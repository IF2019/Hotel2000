package edu.hotel2000.services;

import edu.hotel2000.Util;
import edu.hotel2000.contract.Hotel2000;
import edu.hotel2000.models.Booking;
import edu.hotel2000.models.Hotel;
import edu.hotel2000.models.Room;
import org.apache.log4j.Logger;
import org.web3j.protocol.Web3j;
import rx.Observable;
import sun.security.krb5.Credentials;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HotelService{
	private static final Logger logger = Logger.getLogger(HotelService.class);

	public Hotel getHotel(Web3j web3j, Hotel2000 contract, String code) throws Exception{
		Hotel hotel = new Hotel(contract.getHotel(code).send());
		Room[] rooms = hotel.getRooms();
		int[] bookingsId = hotel.getBookingsId();
		int[] activeBookingsId = hotel.getActiveBookingsId();

		final Throwable[] e = {null};
		List<Observable> observables = new ArrayList<>();

		Map<Integer, Booking> bookingMap = new HashMap<>();

		for(int i = 0; i < rooms.length; i++){
			rooms[i] = new Room();
		}

		for(int i = 0; i < bookingsId.length; i++){
			logger.debug("Search id for bookingsId[" + i + "]");
			int index = i;
			observables.add(
					contract.getHotelBookingId(code, BigInteger.valueOf(i)).observable()
							.map(BigInteger::intValue)
							.flatMap(id -> {
								bookingsId[index] = id;
								logger.debug("bookingsId[" + index + "]=" + id);
								logger.debug("Search booking for id[" + index + "]=" + id);
								return contract.getBooking(BigInteger.valueOf(id)).observable();
							})
							.map(Booking::new)
							.doOnNext(booking -> {
								logger.debug("booking found: " + booking);
								bookingMap.put(booking.getId(), booking);
								for(long day = booking.getStart(); day < booking.getEnd(); day++){
									rooms[booking.getRoomId()].getBooking().put(day, booking.getId());
								}
							})
							.doOnError(throwable -> logger.error("bookingsId " + index + " failed", e[0] = throwable))
			);
		}

		for(int i = 0; i < activeBookingsId.length; i++){
			logger.debug("Search id for activeBookingsId[" + i + "]");
			int index = i;
			observables.add(
					contract.getHotelBookingId(code, BigInteger.valueOf(i)).observable()
							.map(BigInteger::intValue)
							.doOnNext(id -> {
								activeBookingsId[index] = id;
							})
							.doOnError(throwable -> logger.error("bookingsId " + index + " failed", e[0] = throwable))
			);
		}


		logger.debug("Fork and subscribe all Observable");
		Util.fork(observables).toBlocking().subscribe(); // Wait all response
		logger.debug("Wait all response Finish");


		return hotel;
	}
}
