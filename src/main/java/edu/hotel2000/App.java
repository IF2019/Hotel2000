package edu.hotel2000;

import edu.hotel2000.contract.Hotel2000;
import org.apache.log4j.Logger;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

import java.io.Console;

public class App{

	private final static Logger logger = Logger.getLogger(App.class);
	private static final String DEPLOYER_ADDRESS = Config.get("hotel2000.account.deploy.address");
	private static final String DEPLOYER_PASSPHRASE = Config.get("hotel2000.account.deploy.passphrase");
	private static final String DEPLOYER_FILE = Config.get("hotel2000.account.deploy.path");
	private static final String WEB3J_URL = Config.get("web3jServer");

	public static void main(String[] args) throws Exception{

		logger.info("Create Web3j HTTP service");
		Web3j web3j = Web3j.build(new HttpService(WEB3J_URL));
		logger.debug("Create Web3j HTTP service Finish");


		Credentials credentials = WalletUtils.loadCredentials(DEPLOYER_PASSPHRASE, DEPLOYER_FILE);
		logger.info("Deployer Contract Hotel2000 from: " + DEPLOYER_ADDRESS);
		Hotel2000 hotel2000 = Hotel2000.deploy(web3j, credentials, new DefaultGasProvider()).send();

		logger.info("test: " + hotel2000.test().send());


	}
}
