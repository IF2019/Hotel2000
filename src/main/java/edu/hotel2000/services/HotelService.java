package edu.hotel2000.services;

import edu.hotel2000.Util;
import edu.hotel2000.contract.Hotel2000;
import edu.hotel2000.models.Booking;
import edu.hotel2000.models.Hotel;
import org.apache.log4j.Logger;
import rx.Observable;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class HotelService{
	private static final Logger logger = Logger.getLogger(HotelService.class);

	private void applieBookingInRooms(Hotel hotel, Collection<Booking> bookings){
		bookings.forEach(booking -> {
			Map<Long, Integer> roomBookins = hotel.getRooms()[booking.getRoomId()].getBooking();
			for(long i = booking.getStart(); i < booking.getEnd(); i++){
				roomBookins.put(i, booking.getId());
			}
		});
	}

	public Observable<Booking> getBooking(Hotel2000 contract, int id){
		return contract.getBooking(BigInteger.valueOf(id)).observable().map(Booking::new);
	}

	public Map<Integer, Booking> indexBooking(List<Booking> bookings){
		return bookings.stream().collect(Collectors.toMap(Booking::getId, booking -> booking));
	}

	public Observable<List<Booking>> getBooking(Hotel2000 contract, int[] ids){

		return Util.fork(Arrays.stream(ids)
				.mapToObj(id -> getBooking(contract, id))
				.collect(Collectors.toCollection(ArrayList::new)));
	}

	public Observable<Hotel> insertHotelBookingsId(Hotel2000 contract, Hotel hotel){

		List<Observable<Integer>> observables = new ArrayList<>();
		int[] bookingsId = hotel.getBookingsId();
		for(int i = 0; i < bookingsId.length; i++){
			logger.debug("Search id for bookingsId[" + i + "]");
			int index = i;
			observables.add(
					contract.getHotelBookingId(hotel.getCode(), BigInteger.valueOf(i)).observable()
							.map(BigInteger::intValue)
							.doOnNext(id -> bookingsId[index] = id)
			);
		}
		return Util.fork(observables).map(o -> hotel);
	}

	public Observable<Hotel> insertHotelActiveBookingsId(Hotel2000 contract, Hotel hotel){

		List<Observable<Integer>> observables = new ArrayList<>();
		int[] activeBookingsId = hotel.getActiveBookingsId();

		for(int i = 0; i < activeBookingsId.length; i++){
			logger.debug("Search id for activeBookingsId[" + i + "]");
			int index = i;
			observables.add(
					contract.getHotelBookingId(hotel.getCode(), BigInteger.valueOf(i)).observable()
							.map(BigInteger::intValue)
							.doOnNext(id -> activeBookingsId[index] = id)
			);
		}
		return Util.fork(observables).map(o -> hotel);
	}

	public Observable<Hotel> getHotel(Hotel2000 contract, String code, boolean withActiveBookingsId, boolean withBookingsId, boolean withRoomBookings){
		Observable<Hotel> res = contract.getHotel(code).observable()
				.map(Hotel::new);
		if(withActiveBookingsId)res =res.flatMap(hotel -> insertHotelActiveBookingsId(contract, hotel));
		if(withBookingsId){
			res=res.flatMap(hotel -> insertHotelBookingsId(contract, hotel));
			if(withRoomBookings){
				res = res.flatMap(hotel -> getBooking(contract, hotel.getBookingsId())
						.doOnNext(bookings -> applieBookingInRooms(hotel, bookings))
						.map(bookingMap -> hotel)
				);
			}
		}
		return res;
	}

	public Observable<Hotel> getHotel(Hotel2000 contract, String code){
		return getHotel(contract, code, true, true, true);
	}
}
