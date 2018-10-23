package edu.hotel2000;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util{

	private static Pattern dateOffset= Pattern.compile("^(\\+?)([0-9]+)([h]?)$");

	public static int timeInDatestemp = 8640;

	public static BigInteger computeData(String date) throws ParseException{
		Matcher matcher = dateOffset.matcher(date);
		if(matcher.matches()){
			long time = Integer.parseInt(matcher.group(2));
			if(matcher.group(3).equals("h")){
				time *= timeInDatestemp;
			}
			if(matcher.group(1).equals("+")){
				time += System.currentTimeMillis();
			}
			return BigInteger.valueOf(time);
		}
		SimpleDateFormat parser=new SimpleDateFormat("dd-MM-yyyy");
		return BigInteger.valueOf(parser.parse(date).getTime());
	}
}
