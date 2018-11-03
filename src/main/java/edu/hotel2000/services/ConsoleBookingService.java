package edu.hotel2000.services;

import edu.hotel2000.contract.Hotel2000;
import edu.hotel2000.models.Booking;
import edu.hotel2000.models.ConsoleEnv;
import lombok.AllArgsConstructor;
import org.apache.log4j.Logger;
import org.web3j.crypto.CipherException;
import org.web3j.tx.gas.DefaultGasProvider;
import rx.Observable;

import java.io.IOException;
import java.util.Optional;

@AllArgsConstructor
public class ConsoleBookingService{

	private static final Logger logger = Logger.getLogger(ConsoleBookingService.class);

	private ConsoleEnv env;
	private AccountService accountService;
	private BookingService bookingService;

	Hotel2000 getContract(String accountName) throws IOException, CipherException{
		return accountService.findFromConfigOption(accountName)
				.map(credentials -> Hotel2000.load(env.getContractAddress(), env.getWeb3j(), credentials, new DefaultGasProvider()))
				.orElseThrow(() -> new RuntimeException("Account " + accountName + " not found"));
	}

	void info(String accountName, int id) throws Exception{
		logger.info("See booking info:" +
				" accountName=\"" + accountName + "\"," +
				" bookingId=\"" + id + "\"");
		Hotel2000 hotel2000 = getContract(accountName);
		bookingService.getBooking(hotel2000, id).subscribe(
				booking -> {
					logger.info("OK: " + booking);
					logger.info(booking.getId() + "        " + booking.getStart() + " => (" + (booking.getEnd().getDays() - booking.getStart().getDays()) + " days) => " + booking.getEnd() + "        " + booking.getStatus());
					logger.info("Hotel: " + booking.getHotelCode() + "(room:" + booking.getRoomId() + ")");
					logger.info("Client: " + booking.getClientAddress());
					logger.info("Price: " + booking.getPrice());

				},
				throwable -> logger.error("KO: info fail", throwable)
		);
	}
}
