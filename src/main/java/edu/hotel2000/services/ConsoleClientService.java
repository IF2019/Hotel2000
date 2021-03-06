package edu.hotel2000.services;

import edu.hotel2000.Util;
import edu.hotel2000.contract.Hotel2000;
import edu.hotel2000.models.ConsoleEnv;
import edu.hotel2000.models.Money;
import lombok.AllArgsConstructor;
import org.apache.log4j.Logger;
import org.web3j.crypto.CipherException;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tx.gas.DefaultGasProvider;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;

@AllArgsConstructor
public class ConsoleClientService{

	private static final Logger logger = Logger.getLogger(ConsoleClientService.class);

	private ConsoleEnv env;
	private AccountService accountService;
	private ConsoleUtilService consoleUtilService;

	Hotel2000 getContract(String accountName) throws IOException, CipherException{
		return accountService.findFromConfigOption(accountName)
				.map(credentials -> Hotel2000.load(env.getContractAddress(), env.getWeb3j(), credentials, new DefaultGasProvider()))
				.orElseThrow(() -> new RuntimeException("Account " + accountName + " not found"));
	}


	void canBook(String accountName, String code, BigInteger start, BigInteger end, int room) throws Exception{
		logger.info("Check if canBook:" +
				" accountName=\"" + accountName + "\"," +
				" hotelCode=\"" + code + "\"," +
				" start=\"" + Util.timestempToString(start) + "\"," +
				" end=\"" + Util.timestempToString(end) + "\"," +
				" roomId=" + room );
		Hotel2000 hotel2000 = getContract(accountName);
		Tuple2<Boolean, String> res = hotel2000.canBook(code, start, end, BigInteger.valueOf(room)).send();
		if(res.getValue1()){
			logger.info("OK: Account \"" + accountName + "\" can book this roomId");
		}else{
			logger.info("KO: Account \"" + accountName + "\" can't book this roomId: " + res.getValue2());
		}

	}


	void priceBook(String accountName, String code, BigInteger start, BigInteger end, int room) throws Exception{
		logger.info("Try to view priceBook:" +
				" accountName=\"" + accountName + "\"," +
				" hotelCode=\"" + code + "\"," +
				" start=\"" + Util.timestempToString(start) + "\"," +
				" end=\"" + Util.timestempToString(end) + "\"," +
				" roomId=" + room );
		Hotel2000 hotel2000 = getContract(accountName);
		Tuple2<Boolean, BigInteger> res = hotel2000.getBookingPrice(code, start, end).send();
		if(res.getValue1()){
			logger.info("OK: price :  " + Money.of(res.getValue2()));
		}else{
			logger.error("KO: fail to compute price!");
		}

	}

	void book(String accountName, String code, BigInteger start, BigInteger end, int room, Optional<Money> weiO) throws Exception{
		logger.info("Try to book:" +
				" accountName=\"" + accountName + "\"," +
				" hotelCode=\"" + code + "\"," +
				" start=\"" + Util.timestempToString(start) + "\"," +
				" end=\"" + Util.timestempToString(end) + "\"," +
				" roomId=" + room + "," +
				" wei=" + weiO.map(Objects::toString).orElse("auto") );
		Hotel2000 hotel2000 = getContract(accountName);
		DefaultGasProvider gasProvider = new DefaultGasProvider();
		try{
			Money wei;
			if(weiO.isPresent()){
				wei = weiO.get();
			} else {
				logger.info("Compute price ...");
				Tuple2<Boolean, BigInteger> res = hotel2000.getBookingPrice(code, start, end).send();
				if(!res.getValue1()){
					logger.error("KO: Compute price failed");
					return;
				}
				wei = Money.of(res.getValue2());
				logger.info("price:" + wei);
			}
			logger.info("Compute booking ...");
			TransactionReceipt res = hotel2000.book(code, start, end, BigInteger.valueOf(room), wei).send();
			consoleUtilService.showTransactionReceipt(res, gasProvider.getGasLimit());
			if(consoleUtilService.isSuccess(res, gasProvider.getGasLimit())){
				logger.info("OK: Booking success");
			}else {
				logger.error("KO: Booking fail (use canBook to view error)");
			}
		}catch(Exception e){
			logger.error("KO: Booking fail!", e);
		}


	}

}
