package edu.hotel2000;

import java.math.BigInteger;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
}
