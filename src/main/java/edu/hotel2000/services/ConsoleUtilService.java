package edu.hotel2000.services;

import edu.hotel2000.models.ConsoleEnv;
import lombok.AllArgsConstructor;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.web3j.crypto.CipherException;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.IOException;
import java.math.BigInteger;

@AllArgsConstructor
public class ConsoleUtilService{

	private static final Logger logger = Logger.getLogger(ConsoleUtilService.class);

	private ConsoleEnv env;
	private AccountService accountService;

	public boolean isSuccess(TransactionReceipt tr, BigInteger gasLimit){
		return tr.isStatusOK() && !tr.getGasUsed().equals(gasLimit);
	}

	public void showTransactionReceipt(TransactionReceipt tr, BigInteger gasLimit){

		if(isSuccess(tr, gasLimit)){
			logger.info("Transaction ==================== SUCCESS ====================");
			logger.info("Status: " +tr.getStatus());
			logger.info("ContractAddress: " +tr.getContractAddress());
			logger.info("GasUsed: " +tr.getGasUsed());
			logger.info("All Data: " + tr.toString());
			logger.info("Transaction ==================== SUCCESS ====================");
		}else {
			logger.warn("Transaction ==================== FAIL =======================");
			logger.warn("Status: " +tr.getStatus());
			logger.warn("ContractAddress: " +tr.getContractAddress());
			logger.warn("GasUsed: " +tr.getGasUsed());
			logger.warn("All Data: " + tr.toString());
			logger.warn("Transaction ==================== FAIL =======================");
		}
	}

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
