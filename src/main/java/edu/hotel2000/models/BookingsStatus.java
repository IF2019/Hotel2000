package edu.hotel2000.models;

import java.math.BigInteger;

public enum BookingsStatus{
	ACTIVE, CANCELLED, CLOSED;

	public static BookingsStatus valueOf(BigInteger val){
		return valueOf(val.intValue());
	}
	public static BookingsStatus valueOf(int val){
		return BookingsStatus.values()[val];
	}
}
