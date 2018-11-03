package edu.hotel2000.services;

import edu.hotel2000.Util;
import edu.hotel2000.contract.Hotel2000;
import edu.hotel2000.models.Booking;
import rx.Observable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BookingService{

	public Observable<Booking> getBooking(Hotel2000 contract, int id){
		return contract.getBooking(BigInteger.valueOf(id)).observable().map(Booking::new);
	}

	public Observable<List<Booking>> getBooking(Hotel2000 contract, int[] ids){

		return Util.fork(Arrays.stream(ids)
				.mapToObj(id -> getBooking(contract, id))
				.collect(Collectors.toCollection(ArrayList::new)));
	}

}
