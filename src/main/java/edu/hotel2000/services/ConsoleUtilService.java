package edu.hotel2000.services;

import edu.hotel2000.models.ConsoleEnv;
import edu.hotel2000.models.Money;
import lombok.AllArgsConstructor;
import org.apache.log4j.Logger;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
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
			logger.info("==================== Transaction: SUCCESS ====================");
			logger.info("From: " +tr.getFrom());
			logger.info("To: " +tr.getTo());
			logger.info("Block: " + tr.getBlockNumber() + " ("+ tr.getBlockHash() +")");
			logger.info("GasUsed: " +tr.getGasUsed());
			logger.info("==================== Transaction: SUCCESS ====================");
		}else {
			logger.warn("==================== Transaction: FAIL =======================");
			logger.warn("From: " +tr.getFrom());
			logger.warn("To: " +tr.getTo());
			logger.warn("Block: " + tr.getBlockNumber() + " ("+ tr.getBlockHash() +")");
			logger.warn("GasUsed: " +tr.getGasUsed());
			logger.warn("==================== Transaction: FAIL =======================");
		}
	}

	public void showBalance(String accountName) throws IOException, CipherException{
		String address = accountService.getAddress(accountName)
				.orElseGet(() -> {
					logger.debug("Account " + accountName + " not found");
					return accountName;
				});
		if(!address.substring(0, 2).equals("0x")) address = "0x" + address;
		Money money = Money.of(env.getWeb3j().ethGetBalance(address, DefaultBlockParameterName.LATEST).send().getBalance());
		logger.info(accountName + ": " + money + " = " + money.toWeyString() + " wei");
	}

	public void pay(String fromS, String toS, Money amount) throws Exception{
		logger.info("pay: from:"+fromS+" to:"+toS + " amount:" + amount );
		logger.info("Before pay" );
		showBalance(fromS);
		showBalance(toS);

		Credentials credentials = accountService.findFromConfig(fromS);

		TransactionReceipt test = Transfer.sendFunds(
				env.getWeb3j(),
				credentials,
				accountService.getAddress(toS).orElse(toS),
				new BigDecimal(amount),
				Convert.Unit.WEI
		).send();

		showTransactionReceipt(test, env.getGasProvider().getGasLimit());

		logger.info("After Pay" );
		showBalance(fromS);
		showBalance(toS);
	}
}
