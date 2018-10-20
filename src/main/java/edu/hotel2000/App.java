package edu.hotel2000;

import org.apache.log4j.Logger;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;

public class App{

	private final static Logger logger = Logger.getLogger(App.class);
	private static final String DEPLOYER_ADDRESS = Config.get("hotel2000.account.deploy.address");
	private static final String DEPLOYER_PASSPHRASE = Config.get("hotel2000.account.deploy.passphrase");
	private static final String DEPLOYER_FILE = Config.get("hotel2000.account.deploy.path");
	private static final String WEB3J_URL = Config.get("web3jServer");

	public static void main(String[] args) throws IOException, CipherException{

		logger.info("Create Web3j HTTP service");
		Web3j web3j = Web3j.build(new HttpService(WEB3J_URL));
		logger.debug("Create Web3j HTTP service Finish");


		logger.debug("Create deployer credentials");
		Credentials credentials = WalletUtils.loadCredentials(DEPLOYER_PASSPHRASE, DEPLOYER_FILE);



	}
}
