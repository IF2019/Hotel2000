package edu.hotel2000;

import edu.hotel2000.contract.Hotel2000;
import edu.hotel2000.models.ConsoleEnv;
import edu.hotel2000.services.AccountService;
import edu.hotel2000.services.ConsoleService;
import org.apache.log4j.Logger;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

public class App{

	private static final Logger logger = Logger.getLogger(App.class);
	private static final String WEB3J_URL = Config.get("web3jServer");
	private static final String DEPLOY_ACCOUNT = Config.get("hotel2000.deployAccount");
	private static final String TEST_ACOUNT = Config.get("hotel2000.testAccount");
	private static final String CONSOLE_ACOUNT = Config.get("hotel2000.consoleAccount");
	private static final boolean SHOW_DATE = Config.get("hotel2000.showDate", "false").equalsIgnoreCase("true");

	public static void main(String[] args) throws Exception{

		ConsoleEnv env;
		AccountService accountService = new AccountService();

		Credentials credentialsDeploy = accountService.findFromConfigOption(DEPLOY_ACCOUNT).orElseGet(() -> {
			logger.error("No deploy count defined");
			System.exit(1);
			return null;
		});

		Credentials credentialsTest = accountService.findFromConfigOption(TEST_ACOUNT).orElseGet(() -> {
			logger.error("No deploy count defined");
			System.exit(1);
			return null;
		});


		logger.info("Connect Web3j to " + WEB3J_URL + " server");
		Web3j web3j = Web3j.build(new HttpService(WEB3J_URL));
		logger.debug("Connect Web3j to " + WEB3J_URL + " server Finish");

		Hotel2000 hotel2000;
		String hotel2000ContractAddress;
		if(args.length > 0){
			hotel2000ContractAddress = args[0];
		}else{
			logger.info("No address specified for Hotel2000 contract (add address in first parameter)");
			logger.info("Account \"" + DEPLOY_ACCOUNT + "\" deploy Contract Hotel2000 ... ");
			Hotel2000 res = Hotel2000.deploy(web3j, credentialsDeploy, new DefaultGasProvider()).send();
			logger.info("Contract Hotel2000 deployed! ContractAddress: " + res.getContractAddress());
			hotel2000ContractAddress = res.getContractAddress();
		}
		logger.info("Account \"" + TEST_ACOUNT + "\" load Hotel2000 contract: " + hotel2000ContractAddress);
		hotel2000 = Hotel2000.load(hotel2000ContractAddress, web3j, credentialsTest, new DefaultGasProvider());
		logger.info("test: " + hotel2000.test().send());

		env = ConsoleEnv.builder()
				.web3j(web3j)
				.contractAddress(hotel2000ContractAddress)
				.accountName(CONSOLE_ACOUNT)
				.build();

		if(SHOW_DATE){
			logger.info("Current date : " + Util.timestempToString(System.currentTimeMillis()));
			new Thread(() -> {
				try{
					while(true){
						long whiteTime = Util.TIME_IN_DATESTAMP - (System.currentTimeMillis() % Util.TIME_IN_DATESTAMP);
						Thread.sleep(whiteTime+1);
						logger.info("Current date : " + Util.timestempToString(System.currentTimeMillis()));
					}
				}catch(InterruptedException e){
					logger.error("SHOW_DATE: InterruptedException",e);
				}
			}).start();
		}

		ConsoleService consoleService = new ConsoleService(env, accountService);

		InputCommandReader reader = new InputCommandReader(System.in, consoleService);

		reader.start();


	}


}
