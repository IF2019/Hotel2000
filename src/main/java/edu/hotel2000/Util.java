package edu.hotel2000;

import edu.hotel2000.models.Money;
import org.web3j.utils.Async;
import org.web3j.utils.Convert;
import rx.Observable;
import rx.functions.Action0;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util{

	private static Pattern dateOffset = Pattern.compile("^(\\+?)([0-9]+)(d?)$");

	public static int TIME_SCALE = 720;

	public static int TIME_IN_DATESTAMP = 86400 / TIME_SCALE;

	public static BigInteger parseData(String date) throws ParseException{
		Matcher matcher = dateOffset.matcher(date);
		if(matcher.matches()){
			long time = Integer.parseInt(matcher.group(2));
			if(matcher.group(3).equals("d")){
				time *= TIME_IN_DATESTAMP;
			}
			if(matcher.group(1).equals("+")){
				time += System.currentTimeMillis()/1000;
			}
			return BigInteger.valueOf(time);
		}
		SimpleDateFormat parser = new SimpleDateFormat("dd-MM-yyyy");
		return BigInteger.valueOf(parser.parse(date).getTime() / 1000 / TIME_SCALE);
	}

	public static String timestempToString(BigInteger dateStamp){
		return timestempToString(dateStamp.longValue());
	}

	public static String timestempToString(long timeStamp){
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		format.setTimeZone(TimeZone.getTimeZone("UTC"));
		return format.format(new Date(timeStamp * TIME_SCALE * 1000));
	}

	public static String datestempToString(BigInteger dateStamp){
		return datestempToString(dateStamp.longValue());
	}

	public static String datestempToString(long dateStamp){
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		format.setTimeZone(TimeZone.getTimeZone("UTC"));
		return format.format(new Date(dateStamp * 86400000));
	}

	public static <T> Observable<List<T>> fork(List<Observable<T>> observables){
		return Observable.create(subscriber -> {
			List<T> res = new ArrayList<>();
			if(observables.isEmpty()){
				subscriber.onNext(res);
				subscriber.onCompleted();
			}
			List<Throwable> errors = new ArrayList<>();
			AtomicReference<Integer> nbObservable = new AtomicReference<>(observables.size());
			Action0 countDown = () -> {
				synchronized(nbObservable){
					nbObservable.set(nbObservable.get() - 1);
					if(nbObservable.get() > 0) return;
					if(nbObservable.get() < 0)
						throw new RuntimeException("BIG FAILURE: nbObservable=" + nbObservable.get());
				}
				if(nbObservable.get() == 0){
					if(errors.isEmpty()){
						subscriber.onNext(res);
						subscriber.onCompleted();
					}else if(errors.size() == 1){
						subscriber.onError(errors.get(0));
					}else{
						Exception e = new Exception("Multiple errors: " + errors){
							List<Throwable> errors_ = errors;
						};
						subscriber.onError(e);
					}

				}
			};


			observables.forEach(observable -> {
				Async.run(() -> observable.subscribe(
						(obj) -> {
							synchronized(res){
								res.add(obj);
							}
						},
						(failure) -> {
							synchronized(errors){
								errors.add(failure);
							}
							countDown.call();
						},
						countDown
				));
			});
		});
	}

	public static Money parseMoney(String value){
		Pattern pattern = Pattern.compile("^(\\-?[0-9\\_]+(\\.[0-9\\_ ]*)?) *([a-zA-Z]*)$");
		Matcher matcher = pattern.matcher(value);
		if(!matcher.matches()) throw new RuntimeException("Value is not ETHER : " + value);
		Convert.Unit unit;
		if(matcher.group(3).isEmpty()) {
			unit = Convert.Unit.WEI;
		}else{
			unit = Convert.Unit.valueOf(matcher.group(3).toUpperCase());
		}
		BigDecimal val = Convert.toWei(matcher.group(1), unit);
		return Money.of(val.toBigInteger());
	}
}
