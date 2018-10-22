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
public class ConsoleHotelService{

	private static final Logger logger = Logger.getLogger(ConsoleHotelService.class);

	private ConsoleEnv env;
	private AccountService accountService;

	public void infoHotel(String accountName, String code) throws Exception{
		Credentials account = accountService.findFromConfigOption(accountName)
				.orElseThrow(() -> new RuntimeException("Account " + accountName + " not found"));
		Hotel2000 hotel2000 = Hotel2000.load(env.getCurrantContract(), env.getWeb3j(), account, new DefaultGasProvider());
		try{
			Hotel hotel = new Hotel(hotel2000.getHotel(code).send());
			logger.info(hotel.toString());
		}catch(Exception e){
			logger.error("Fail", e);
		}
	}


	public void canCreateHotel(String accountName, String code, int nbRoom, BigInteger price) throws Exception{
		Credentials account = accountService.findFromConfigOption(accountName)
				.orElseThrow(() -> new RuntimeException("Account " + accountName + " not found"));
		Hotel2000 hotel2000 = Hotel2000.load(env.getCurrantContract(), env.getWeb3j(), account, new DefaultGasProvider());
		Tuple2<Boolean, String> res = hotel2000.canBuildHotel(code, BigInteger.valueOf(nbRoom), price).send();
		if(res.getValue1()){
			logger.info(accountName + " can create " + code + " hotel");
		}else{
			logger.info(accountName + " can't create " + code + " hotel: " + res.getValue2());
		}

	}

	public void createHotel(String accountName, String code, int nbRoom, BigInteger price) throws Exception{
		Credentials account = accountService.findFromConfigOption(accountName)
				.orElseThrow(() -> new RuntimeException("Account " + accountName + " not found"));
		Hotel2000 hotel2000 = Hotel2000.load(env.getCurrantContract(), env.getWeb3j(), account, new DefaultGasProvider());
		try{
			TransactionReceipt res = hotel2000.buildHotel(code, BigInteger.valueOf(nbRoom), price).send();
			if(res.isStatusOK()){
				logger.info("hotel " + code + " created");
			}
		}catch(Exception e){
			logger.error("fail create hotel", e);
		}


	}
}
