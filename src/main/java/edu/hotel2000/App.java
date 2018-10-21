package edu.hotel2000;

import edu.hotel2000.contract.Hotel2000;
import org.apache.log4j.Logger;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

import java.io.IOException;
import java.math.BigInteger;

public class App{

	private static final Logger logger = Logger.getLogger(App.class);
	private static final String WEB3J_URL = Config.get("web3jServer");
	private static Web3j web3j;

	public static void main(String[] args) throws Exception{

		InputCommandReader reader = new InputCommandReader(System.in, App::receiveCommand);

		reader.start();

		logger.info("Connect Web3j to " + WEB3J_URL + " server");
		web3j = Web3j.build(new HttpService(WEB3J_URL));
		logger.debug("Connect Web3j to " + WEB3J_URL + " server Finish");


		Credentials credentials = AccountService.findFromConfig("deploy");
		logger.info("Deployer Contract Hotel2000 from: " + credentials.getAddress());
		Hotel2000 hotel2000 = Hotel2000.deploy(web3j, credentials, new DefaultGasProvider()).send();

		logger.info("test: " + hotel2000.test().send());


	}

	public static void showBalance(String account) throws IOException{
		String address = AccountService.findFromConfigOption("deploy")
				.map(Credentials::getAddress)
				.orElseGet(() -> {
					logger.info("Account " + account + " not found");
					return account;
				});
		if(!address.substring(0, 2).equals("0x")) address = "0x" + address;
		BigInteger wei = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send().getBalance();
		logger.info(account + " has " + wei + " wei");
		logger.info(account + " has " + wei.divide(new BigInteger("10000000000000000")).intValue()/100f + " eth");
	}

	private static void receiveCommand(String[] commande) throws Exception{
		String help = Config.get("hotel2000.commands.help");
		if(commande[0].isEmpty())return;
		if(commande[0].equalsIgnoreCase("balance")){
			if(commande.length < 2){
				System.out.println(Config.get("hotel2000.commands.balance.help", help));
				return;
			}
			showBalance(commande[1]);
			return;
		}
		System.err.println("Command '" + commande[0] + "' unknown");
		System.out.println(help);
	}
}
