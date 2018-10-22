package edu.hotel2000.services;

import edu.hotel2000.contract.Hotel2000;
import edu.hotel2000.models.ConsoleEnv;
import edu.hotel2000.models.Hotel;
import lombok.AllArgsConstructor;
import org.apache.log4j.Logger;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;

@AllArgsConstructor
class ConsoleHotelService{

	private static final Logger logger = Logger.getLogger(ConsoleHotelService.class);

	private ConsoleEnv env;
	private AccountService accountService;
	private ConsoleUtilService consoleUtilService;

	void infoHotel(String accountName, String code) throws Exception{
		Credentials account = accountService.findFromConfigOption(accountName)
				.orElseThrow(() -> new RuntimeException("Account " + accountName + " not found"));
		Hotel2000 hotel2000 = Hotel2000.load(env.getContractAddress(), env.getWeb3j(), account, new DefaultGasProvider());
		try{
			Hotel hotel = new Hotel(hotel2000.getHotel(code).send());
			logger.info(hotel.toString());
		}catch(Exception e){
			logger.error("Fail", e);
		}
	}


	void canCreateHotel(String accountName, String code, int nbRoom, BigInteger price) throws Exception{
		Credentials account = accountService.findFromConfigOption(accountName)
				.orElseThrow(() -> new RuntimeException("Account " + accountName + " not found"));
		Hotel2000 hotel2000 = Hotel2000.load(env.getContractAddress(), env.getWeb3j(), account, new DefaultGasProvider());
		Tuple2<Boolean, String> res = hotel2000.canCreateHotel(code, BigInteger.valueOf(nbRoom), price).send();
		if(res.getValue1()){
			logger.info(accountName + " can create " + code + " hotel");
		}else{
			logger.info(accountName + " can't create " + code + " hotel: " + res.getValue2());
		}

	}

	void createHotel(String accountName, String code, int nbRoom, BigInteger price) throws Exception{
		Credentials account = accountService.findFromConfigOption(accountName)
				.orElseThrow(() -> new RuntimeException("Account " + accountName + " not found"));
		DefaultGasProvider gasProvider = new DefaultGasProvider();
		Hotel2000 hotel2000 = Hotel2000.load(env.getContractAddress(), env.getWeb3j(), account, gasProvider);
		try{
			TransactionReceipt res = hotel2000.createHotel(code, BigInteger.valueOf(nbRoom), price).send();
			consoleUtilService.showTransactionReceipt(res, gasProvider.getGasLimit());
			if(consoleUtilService.isSuccess(res, gasProvider.getGasLimit())){
				logger.error("Hotel " + code +" created");
			}else {
				logger.error("fail create hotel " + code);
			}
		}catch(Exception e){
			logger.error("fail create hotel " + code, e);
		}


	}
}
