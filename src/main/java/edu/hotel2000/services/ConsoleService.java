package edu.hotel2000.services;

import edu.hotel2000.Config;
import edu.hotel2000.Util;
import edu.hotel2000.models.ConsoleEnv;
import org.apache.log4j.Logger;

import java.math.BigInteger;
import java.util.Map;
import java.util.Optional;

public class ConsoleService implements CommandExec{

	private static final Logger logger = Logger.getLogger(ConsoleService.class);

	private ConsoleEnv env;
	private ConsoleHotelService hotelService;
	private ConsoleUtilService utilService;
	private CommandeParser commandeParser;
	private ConsoleClientService clientService;

	public ConsoleService(ConsoleEnv env, AccountService accountService){
		this.env = env;
		this.utilService = new ConsoleUtilService(env, accountService);
		this.hotelService = new ConsoleHotelService(env, accountService, utilService, new HotelService());
		this.commandeParser = new CommandeParser();
		this.clientService = new ConsoleClientService(env, accountService, utilService);
	}

	private <T> Optional<T> optionalExclude(T value, T ...excludes){
		if(value == null) return Optional.empty();
		for(T exclude: excludes){
			if(value.equals(exclude)) return Optional.empty();
		}
		return Optional.of(value);
	}

	@Override
	public void evalCommand(String[] commande) throws Exception{
		logger.debug("Run commande : " + String.join(" ", commande));

		String help = Config.get("hotel2000.commands.help");
		if(commande[0].isEmpty()) return;

		Map<String, String> params;
		String acc = env.getAccountName();


		// Help
		params = commandeParser.parse(commande, "help|? [commande]").orElse(null);
		if(params != null){
			if(params.containsKey("commande")){
				System.out.println(Config.get("hotel2000.commands." + commande[1] + ".help", "Command  '" + commande[1] + "' didn't have help"));
			}else{
				System.out.println(help);
			}
			return;
		}


		// Balance
		params = commandeParser.parse(commande, "balance|b [account]=" + acc).orElse(null);
		if(params != null){
			utilService.showBalance(params.get("account"));
			return;
		}


		// Hotel canCreate
		params = commandeParser.parse(commande, "hotel|h canCreate|cc <code> <nbRoom> <price> [account]=" + acc).orElse(null);
		if(params != null){
			hotelService.canCreateHotel(
					params.get("account"),
					params.get("code"),
					Integer.parseInt(params.get("nbRoom")),
					new BigInteger(params.get("price"))
			);
			return;
		}


		// Hotel create
		params = commandeParser.parse(commande, "hotel|h create|c <code> <nbRoom> <price> [account]=" + acc).orElse(null);
		if(params != null){
			hotelService.createHotel(
					params.get("account"),
					params.get("code"),
					Integer.parseInt(params.get("nbRoom")),
					new BigInteger(params.get("price"))
			);
			return;
		}


		// Hotel info
		params = commandeParser.parse(commande, "hotel|h info|i <code> [account]=" + acc).orElse(null);
		if(params != null){
			hotelService.infoHotel(
					params.get("account"),
					params.get("code")
			);
			return;
		}

		// Hotel bookings
		params = commandeParser.parse(commande, "hotel|h bookings|b <code> [room]=-1 [client]=null [account]=" + acc).orElse(null);
		if(params != null){
			hotelService.infoBookings(
					params.get("account"),
					params.get("code"),
					optionalExclude(params.get("room"), "-1").map(Integer::parseInt),
					optionalExclude(params.get("client"), "null")
			);
			return;
		}

		// Hotel activeBookings
		params = commandeParser.parse(commande, "hotel|h activeBookings|ab <code> [room]=-1 [client]=null [account]=" + acc).orElse(null);
		if(params != null){
			hotelService.infoBookings(
					params.get("account"),
					params.get("code"),
					optionalExclude(params.get("room"), "-1").map(Integer::parseInt),
					optionalExclude(params.get("client"), "null")
			);
			return;
		}

		// Hotel withdraw
		params = commandeParser.parse(commande, "hotel|h withdraw|w <code> [account]="+acc).orElse(null);
		if(params != null){
			hotelService.withdraw(
					params.get("account"),
					params.get("code")
			);
			return;
		}


		// Client canBook
		params = commandeParser.parse(commande, "clientAddress|c canBook|cb <code> <start> <end> <roomId> [account]=" + acc).orElse(null);
		if(params != null){
			clientService.canBook(
					params.get("account"),
					params.get("code"),
					Util.computeData(params.get("start")),
					Util.computeData(params.get("end")),
					Integer.parseInt(params.get("roomId"))
			);
			return;
		}


		// Client book
		params = commandeParser.parse(commande, "clientAddress|c book|b <code> <start> <end> <roomId> [money] [account]=" + acc).orElse(null);
		if(params != null){
			clientService.book(
					params.get("account"),
					params.get("code"),
					Util.computeData(params.get("start")),
					Util.computeData(params.get("end")),
					Integer.parseInt(params.get("roomId")),
					Optional.ofNullable(params.get("money")).map(BigInteger::new)
			);
			return;
		}



		// Client priceBook
		params = commandeParser.parse(commande, "clientAddress|c priceBook|pb <code> <start> <end> <roomId> [account]=" + acc).orElse(null);
		if(params != null){
			clientService.priceBook(
					params.get("account"),
					params.get("code"),
					Util.computeData(params.get("start")),
					Util.computeData(params.get("end")),
					Integer.parseInt(params.get("roomId"))
			);
			return;
		}



		// Contract
		params = commandeParser.parse(commande, "contract|c [newValue]").orElse(null);
		if(params != null){
			if(params.containsKey("newValue")){
				env.setContractAddress(params.get("newValue"));
				logger.info("Set Current contract value to : " + params.get("newValue"));
			}else{
				logger.info("Current contract is: " + env.getContractAddress());
			}
			return;
		}


		// Account
		params = commandeParser.parse(commande, "account|a|use [newValue]").orElse(null);
		if(params != null){
			if(params.containsKey("newValue")){
				env.setAccountName(params.get("newValue"));
				logger.info("Set current account value to : " + params.get("newValue"));
			}else{
				logger.info("Current account is: " + acc);
			}
			return;
		}

		// Date
		params = commandeParser.parse(commande, "date|d").orElse(null);
		if(params != null){
			logger.info("Current date : " + Util.datestempToString(System.currentTimeMillis()));
			return;
		}

		System.out.println("Command '" + commande[0] + "' unknown");
		System.out.println(help);
	}

	@Override
	public void onStart(){

	}
}
