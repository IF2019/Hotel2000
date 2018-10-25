package edu.hotel2000.models;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class Money extends BigInteger{

	private String stringValue;
	private static final Map<Integer, String> units = new HashMap<Integer, String>(){{
		put(0, "Wei");
		put(3, "KWei");
		put(6, "MWei");
		put(9, "GWei");
		put(12, "Szabo");
		put(15, "Finney");
		put(18, "Ether");
		put(21, "KEther");
		put(24, "MEther");
		put(27, "GEther");
	}};

	public Money(byte[] val){
		super(val);
	}

	public Money(int signum, byte[] magnitude){
		super(signum, magnitude);
	}

	public Money(String val, int radix){
		super(val, radix);
	}

	public Money(String val){
		super(val);
	}

	public Money(int numBits, Random rnd){
		super(numBits, rnd);
	}

	public Money(int bitLength, int certainty, Random rnd){
		super(bitLength, certainty, rnd);
	}

	public Money(BigInteger val){
		super(val.toString());
	}

	public static Money of(BigInteger value){
		return new Money(value);
	}

	public String toString(){
		if(stringValue != null) return stringValue;
		String data = super.toString();
		int nbDec = Math.min(Math.max(((data.length() - 1) / 3) * 3, 0), 27);
		String unit = units.get(nbDec);
		if(nbDec == 0) return stringValue = data + unit;
		int subPos = data.length() - nbDec;
		return stringValue = data.substring(0, subPos) + "." + data.substring(subPos, subPos + 3) + ' ' + unit;
	}

	public String toWeyString(){
		String data = super.toString();
		int nbSpace = (data.length()-1) / 3;
		int offset = data.length() - nbSpace * 3;
		String res = data.substring(0,offset);
		for(int i = 0; i < nbSpace; i++){
			res += ' ' + data.substring(offset + i *3, offset + i *3+3);
		}
		return res;
	}
}
