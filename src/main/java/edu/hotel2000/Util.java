package edu.hotel2000;

import org.web3j.utils.Async;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;

import java.math.BigInteger;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util{

	private static Pattern dateOffset = Pattern.compile("^(\\+?)([0-9]+)(d?)$");

	public static int TIME_SCALE = 1000;

	public static int TIME_IN_DATESTAMP = 86400000 / TIME_SCALE;

	public static BigInteger computeData(String date) throws ParseException{
		Matcher matcher = dateOffset.matcher(date);
		if(matcher.matches()){
			long time = Integer.parseInt(matcher.group(2));
			if(matcher.group(3).equals("d")){
				time *= TIME_IN_DATESTAMP;
			}
			if(matcher.group(1).equals("+")){
				time += System.currentTimeMillis();
			}
			return BigInteger.valueOf(time);
		}
		SimpleDateFormat parser = new SimpleDateFormat("dd-MM-yyyy");
		return BigInteger.valueOf(parser.parse(date).getTime() / TIME_SCALE);
	}

	public static String datestempToString(BigInteger dateStamp){
		return datestempToString(dateStamp.longValue());
	}

	public static String datestempToString(long dateStamp){
		Format format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		return format.format(new Date(dateStamp * TIME_SCALE));
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
					if(nbObservable.get() < 0) throw new RuntimeException("BIG FAILURE: nbObservable="+nbObservable.get());
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
						(failure)->{
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
}
