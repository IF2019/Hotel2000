package edu.hotel2000.models;

import edu.hotel2000.Util;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DateStamp{
	long days;

	public static DateStamp of(BigInteger value){
		return new DateStamp(value.longValue());
	}

	@Override
	public String toString(){
		return Util.datestempToString(days);
	}

}
