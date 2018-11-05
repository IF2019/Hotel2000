package edu.hotel2000.services;

import edu.hotel2000.contract.Hotel2000;
import edu.hotel2000.models.ConsoleEnv;
import lombok.AllArgsConstructor;
import org.apache.log4j.Logger;
import org.web3j.crypto.CipherException;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;

import java.io.IOException;
import java.math.BigInteger;

@AllArgsConstructor
public class ConsoleBookingService{

	private static final Logger logger = Logger.getLogger(ConsoleBookingService.class);

	private ConsoleEnv env;
	private AccountService accountService;
	private ConsoleUtilService consoleUtilService;
	private BookingService bookingService;

	Hotel2000 getContract(String accountName) throws IOException, CipherException{
		return accountService.findFromConfigOption(accountName)
				.map(credentials -> Hotel2000.load(env.getContractAddress(), env.getWeb3j(), credentials, env.getGasProvider()))
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

	void canCancel(String accountName, int id) throws Exception{
		logger.info("Check if canCancel booking:" +
				" accountName=\"" + accountName + "\"," +
				" bookingId=" + id);
		Hotel2000 hotel2000 = getContract(accountName);
		Tuple2<Boolean, String> res = hotel2000.canCancelBooking(BigInteger.valueOf(id)).send();
		if(res.getValue1()){
			logger.info("OK: Account \"" + accountName + "\" can cancel booking " + id);
		}else{
			logger.info("KO: Account \"" + accountName + "\" can't cancel booking " + id + "1: " + res.getValue2());
		}

	}

	void cancel(String accountName, int id) throws Exception{
		logger.info("Try cancel booking:" +
				" accountName=\"" + accountName + "\"," +
				" bookingId=" + id);
		Hotel2000 hotel2000 = getContract(accountName);
		try{
			TransactionReceipt res = hotel2000.cancelBooking(BigInteger.valueOf(id)).send();
			consoleUtilService.showTransactionReceipt(res);
			if(consoleUtilService.isSuccess(res)){
				logger.info("OK: Booking " + id + " canceled");
			}else{
				logger.error("KO: fail to cancel booking " + id);
			}
		}catch(Exception e){
			logger.error("KO: cancelBooking crash id:" + id, e);
		}


	}
}
