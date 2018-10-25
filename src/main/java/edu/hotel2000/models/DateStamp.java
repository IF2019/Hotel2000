package edu.hotel2000.models;

import edu.hotel2000.Util;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
public class DateStamp{

	private static Map<Long, DateStamp> values = new HashMap<>();

	long days;

	public static DateStamp of(long value){
		DateStamp res = values.get(value);
		if(res != null)return res;
		res = new DateStamp(value);
		values.put(value, res);
		return res;
	}

	public static DateStamp of(BigInteger value){
		return of(value.longValue());
	}

	@Override
	public String toString(){
		return Util.datestempToString(days);
	}

}
