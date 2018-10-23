package edu.hotel2000.services;

import edu.hotel2000.contract.Hotel2000;
import edu.hotel2000.models.ConsoleEnv;
import edu.hotel2000.models.Hotel;
import lombok.AllArgsConstructor;
import org.apache.log4j.Logger;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
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

	Hotel2000 getContract(String accountName) throws IOException, CipherException{
		return accountService.findFromConfigOption(accountName)
				.map(credentials -> Hotel2000.load(env.getContractAddress(), env.getWeb3j(), credentials, new DefaultGasProvider()))
				.orElseThrow(() -> new RuntimeException("Account " + accountName + " not found"));
	}

	void infoHotel(String accountName, String code) throws Exception{
		Hotel2000 hotel2000 = getContract(accountName);
		try{
			Hotel hotel = new Hotel(hotel2000.getHotel(code).send());
			logger.info(hotel.toString());
		}catch(Exception e){
			logger.error("Fail", e);
		}
	}


	void canCreateHotel(String accountName, String code, int nbRoom, BigInteger price) throws Exception{
		Hotel2000 hotel2000 = getContract(accountName);
		Tuple2<Boolean, String> res = hotel2000.canCreateHotel(code, BigInteger.valueOf(nbRoom), price).send();
		if(res.getValue1()){
			logger.info("OK: " + accountName + " can create " + code + " hotel");
		}else{
			logger.info("KO: " + accountName + " can't create " + code + " hotel: " + res.getValue2());
		}

	}

	void createHotel(String accountName, String code, int nbRoom, BigInteger price) throws Exception{
		Hotel2000 hotel2000 = getContract(accountName);
		DefaultGasProvider gasProvider = new DefaultGasProvider();
		try{
			TransactionReceipt res = hotel2000.createHotel(code, BigInteger.valueOf(nbRoom), price).send();
			consoleUtilService.showTransactionReceipt(res, gasProvider.getGasLimit());
			if(consoleUtilService.isSuccess(res, gasProvider.getGasLimit())){
				logger.info("OK: Hotel " + code +" created");
			}else {
				logger.error("KO: fail creating hotel " + code);
			}
		}catch(Exception e){
			logger.error("KO: fail creating hotel " + code, e);
		}


	}

	void withdraw(String accountName, String code) throws IOException, CipherException{
		Hotel2000 hotel2000 = getContract(accountName);
		DefaultGasProvider gasProvider = new DefaultGasProvider();
		try{
			consoleUtilService.showBalance(accountName);
			TransactionReceipt res = hotel2000.withdraw(code).send();
			consoleUtilService.showTransactionReceipt(res, gasProvider.getGasLimit());
			if(consoleUtilService.isSuccess(res, gasProvider.getGasLimit())){
				logger.info("OK: withdraw success");
				consoleUtilService.showBalance(accountName);
			}else {
				logger.error("KO: withdraw fail");
			}
		}catch(Exception e){
			logger.error("KO: withdraw fail", e);
		}
	}

}
