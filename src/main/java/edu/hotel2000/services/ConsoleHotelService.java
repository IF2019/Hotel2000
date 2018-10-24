package edu.hotel2000.services;

import edu.hotel2000.contract.Hotel2000;
import edu.hotel2000.models.ConsoleEnv;
import lombok.AllArgsConstructor;
import org.apache.log4j.Logger;
import org.web3j.crypto.CipherException;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tx.gas.DefaultGasProvider;

import java.io.IOException;
import java.math.BigInteger;

@AllArgsConstructor
class ConsoleHotelService{

	private static final Logger logger = Logger.getLogger(ConsoleHotelService.class);

	private ConsoleEnv env;
	private AccountService accountService;
	private ConsoleUtilService consoleUtilService;
	private HotelService hotelService;

	Hotel2000 getContract(String accountName) throws IOException, CipherException{
		return accountService.findFromConfigOption(accountName)
				.map(credentials -> Hotel2000.load(env.getContractAddress(), env.getWeb3j(), credentials, new DefaultGasProvider()))
				.orElseThrow(() -> new RuntimeException("Account " + accountName + " not found"));
	}

	void infoHotel(String accountName, String code) throws Exception{
		logger.info("See hotel info:" +
				" accountName=\"" + accountName + "\"," +
				" hotelCode=\"" + code + "\"");
		Hotel2000 hotel2000 = getContract(accountName);
		try{
			hotelService.getHotel(hotel2000, code).toBlocking().subscribe(
					hotel -> logger.info("OK: " + hotel.toString()),
					throwable -> logger.error("KO: getHotel fail", throwable)
			);
		}catch(Exception e){
			logger.error("KO: getHotel fail", e);
		}
	}


	void canCreateHotel(String accountName, String code, int nbRoom, BigInteger price) throws Exception{
		logger.info("Check if canCreateHotel:" +
				" accountName=\"" + accountName + "\"," +
				" hotelCode=\"" + code + "\"" +
				" nbRoom=" + nbRoom + "," +
				" price=" + price);
		Hotel2000 hotel2000 = getContract(accountName);
		Tuple2<Boolean, String> res = hotel2000.canCreateHotel(code, BigInteger.valueOf(nbRoom), price).send();
		if(res.getValue1()){
			logger.info("OK: " + accountName + " can create " + code + " hotel");
		}else{
			logger.info("KO: " + accountName + " can't create " + code + " hotel: " + res.getValue2());
		}

	}

	void createHotel(String accountName, String code, int nbRoom, BigInteger price) throws Exception{
		logger.info("Try create hotel:" +
				" accountName=\"" + accountName + "\"," +
				" hotelCode=\"" + code + "\"" +
				" nbRoom=" + nbRoom + "," +
				" price=" + price);
		Hotel2000 hotel2000 = getContract(accountName);
		DefaultGasProvider gasProvider = new DefaultGasProvider();
		try{
			TransactionReceipt res = hotel2000.createHotel(code, BigInteger.valueOf(nbRoom), price).send();
			consoleUtilService.showTransactionReceipt(res, gasProvider.getGasLimit());
			if(consoleUtilService.isSuccess(res, gasProvider.getGasLimit())){
				logger.info("OK: Hotel " + code + " created");
			}else{
				logger.error("KO: fail creating hotel " + code);
			}
		}catch(Exception e){
			logger.error("KO: createHotel fail " + code, e);
		}


	}

	void withdraw(String accountName, String code) throws IOException, CipherException{
		logger.info("Try withdraw:" +
				" accountName=\"" + accountName + "\"," +
				" hotelCode=\"" + code + "\"");
		Hotel2000 hotel2000 = getContract(accountName);
		DefaultGasProvider gasProvider = new DefaultGasProvider();
		try{
			consoleUtilService.showBalance(accountName);
			TransactionReceipt res = hotel2000.withdraw(code).send();
			consoleUtilService.showTransactionReceipt(res, gasProvider.getGasLimit());
			if(consoleUtilService.isSuccess(res, gasProvider.getGasLimit())){
				logger.info("OK: withdraw success");
				consoleUtilService.showBalance(accountName);
			}else{
				logger.error("KO: withdraw fail");
			}
		}catch(Exception e){
			logger.error("KO: withdraw fail", e);
		}
	}

}
