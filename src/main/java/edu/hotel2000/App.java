package edu.hotel2000;

import edu.hotel2000.contract.Hotel2000;
import org.apache.log4j.Logger;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tx.gas.DefaultGasProvider;

import java.io.IOException;
import java.math.BigInteger;

public class App{

	private static final Logger logger = Logger.getLogger(App.class);
	private static final String WEB3J_URL = Config.get("web3jServer");
	private static Web3j web3j;
	private static String hotel2000ContractAddress;

	public static void main(String[] args) throws Exception{

		InputCommandReader reader = new InputCommandReader(System.in, App::receiveCommand);

		reader.start();

		logger.info("Connect Web3j to " + WEB3J_URL + " server");
		web3j = Web3j.build(new HttpService(WEB3J_URL));
		logger.debug("Connect Web3j to " + WEB3J_URL + " server Finish");


		Credentials credentials = AccountService.findFromConfig("deploy");
		logger.info("Deployer Contract Hotel2000 from: " + credentials.getAddress());
		Hotel2000 hotel2000 = Hotel2000.deploy(web3j, credentials, new DefaultGasProvider()).send();
		logger.info("Contract Hotel2000 deployed! ContractAddress: " + hotel2000.getContractAddress());
		hotel2000ContractAddress = hotel2000.getContractAddress();

		logger.info("test: " + hotel2000.test().send());


	}

	private static void showBalance(String account) throws IOException{
		String address = accountNameToAddress(account);
		if(!address.substring(0, 2).equals("0x")) address = "0x" + address;
		BigInteger wei = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send().getBalance();
		logger.info(account + " has " + wei + " wei");
		logger.info(account + " has " + wei.divide(BigInteger.valueOf(10000000000000000L)).intValue() / 100f + " eth");
	}

	private static String accountNameToAddress(String accountName){
		return AccountService.findFromConfigOption(accountName)
				.map(Credentials::getAddress)
				.orElseGet(() -> {
					logger.debug("Account " + accountName + " not found");
					return accountName;
				});
	}

	private static void canCreateHotel(String accountName, String code, int nbRoom, BigInteger prix) throws Exception{
		Credentials account = AccountService.findFromConfigOption(accountName)
				.orElseThrow(() -> new RuntimeException("Account " + accountName + " not found"));
		Hotel2000 hotel2000 = Hotel2000.load(hotel2000ContractAddress, web3j, account, new DefaultGasProvider());
		Tuple2<Boolean, String> res = hotel2000.canBuildHotel(code, BigInteger.valueOf(nbRoom), prix).send();
		if(res.getValue1()){
			logger.info(accountName + " can create " + code + " hotel");
		}else{
			logger.info(accountName + " can't create " + code + " hotel: " + res.getValue2());
		}

	}

	private static void createHotel(String accountName, String code, int nbRoom, BigInteger prix) throws Exception{
		Credentials account = AccountService.findFromConfigOption(accountName)
				.orElseThrow(() -> new RuntimeException("Account " + accountName + " not found"));
		Hotel2000 hotel2000 = Hotel2000.load(hotel2000ContractAddress, web3j, account, new DefaultGasProvider());
		try{
			hotel2000.buildHotel(code, BigInteger.valueOf(nbRoom), prix).send();
			logger.info("hotel " + accountName + " created");
		}catch(Exception e){
			logger.error("fail create hotel", e);
		}


	}

	private static void receiveCommand(String[] commande) throws Exception{


		String help = Config.get("hotel2000.commands.help");
		if(commande[0].isEmpty()) return;
		if(commande[0].equals("?")
				|| commande[0].equalsIgnoreCase("h")
				|| commande[0].equalsIgnoreCase("help")
		){
			if(commande.length > 1){
				System.out.println(Config.get("hotel2000.commands." + commande[1] + ".help", "Command  '" + commande[1] + "' didn't have help"));
				return;
			}
			System.out.println(help);
			return;
		}
		if(commande[0].equalsIgnoreCase("balance")){
			if(commande.length < 2){
				System.out.println(Config.get("hotel2000.commands.balance.help", help));
				return;
			}
			showBalance(commande[1]);
			return;
		}
		if(commande[0].equalsIgnoreCase("hotel")){
			help = Config.get("hotel2000.commands.hotel.help", help);
			if(commande.length < 2){
				System.out.println();
				return;
			}else if(commande[1].equalsIgnoreCase("create")){
				if(commande.length < 6){
					System.out.println(Config.get("hotel2000.commands.hotel.create.help", help));
					return;
				}
				createHotel(commande[2], commande[3], Integer.parseInt(commande[4]), new BigInteger(commande[5]));
				return;
			}else if(commande[1].equalsIgnoreCase("canCreate")){
				if(commande.length < 6){
					System.out.println(Config.get("hotel2000.commands.hotel.create.help", help));
					return;
				}
				canCreateHotel(commande[2], commande[3], Integer.parseInt(commande[4]), new BigInteger(commande[5]));
				return;
			}
		}
		System.out.println("Command '" + commande[0] + "' unknown");
		System.out.println(help);
	}
}
