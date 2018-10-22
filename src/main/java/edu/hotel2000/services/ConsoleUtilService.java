package edu.hotel2000.services;

import edu.hotel2000.models.ConsoleEnv;
import lombok.AllArgsConstructor;
import org.apache.log4j.Logger;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.core.DefaultBlockParameterName;

import java.io.IOException;
import java.math.BigInteger;

@AllArgsConstructor
public class ConsoleUtilService{

	private static final Logger logger = Logger.getLogger(ConsoleUtilService.class);

	private ConsoleEnv env;
	private AccountService accountService;

	public void showBalance(String accountName) throws IOException, CipherException{
		String address = accountService.getAddress(accountName)
				.orElseGet(() -> {
					logger.debug("Account " + accountName + " not found");
					return accountName;
				});
		if(!address.substring(0, 2).equals("0x")) address = "0x" + address;
		BigInteger wei = env.getWeb3j().ethGetBalance(address, DefaultBlockParameterName.LATEST).send().getBalance();
		logger.info(accountName + " has " + wei + " wei");
		logger.info(accountName + " has " + wei.divide(BigInteger.valueOf(10000000000000000L)).intValue() / 100f + " eth");
	}
}
