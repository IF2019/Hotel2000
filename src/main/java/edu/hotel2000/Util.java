package edu.hotel2000;

import java.math.BigInteger;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.web3j.utils.Async;
import rx.Observable;
import rx.functions.Action1;

import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util{

	private static Pattern dateOffset= Pattern.compile("^(\\+?)([0-9]+)(d?)$");

	public static int TIME_SCALE=1000;

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
		SimpleDateFormat parser=new SimpleDateFormat("dd-MM-yyyy");
		return BigInteger.valueOf(parser.parse(date).getTime()/TIME_SCALE);
	}

	public static String datestempToString(BigInteger dateStamp){
		return datestempToString(dateStamp.longValue());
	}

	public static String datestempToString(long dateStamp){
		Format format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		return format.format(new Date(dateStamp*TIME_SCALE));
	}

	public static Observable fork(Collection<Observable> observables){
		return Observable.create(subscriber -> {
			AtomicReference<Integer> count = new AtomicReference<>(observables.size());
			AtomicReference<Boolean> fail = new AtomicReference<>(false);
			AtomicReference<Throwable> throwable_ = new AtomicReference<>(null);
			if(count.get() == 0){
				subscriber.onNext(null);
				subscriber.onCompleted();
			}
			Action1 countDown = o -> {
				synchronized(count){
					count.set(count.get() - 1);
				}
				if(count.get() == 0 && !fail.get()){
					if(fail.get()){
						subscriber.onError(throwable_.get());
					}else {
						subscriber.onNext(null);
						subscriber.onCompleted();
					}

				}
			};

			Action1<Throwable> failure = throwable -> {
				fail.set(true);
				throwable_.set(throwable);
				countDown.call(null);
			};

			observables.forEach(observable -> {
				Async.run(() -> observable.subscribe(
						countDown,
						failure));
			});
		});
	}
}
