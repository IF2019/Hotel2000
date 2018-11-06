package edu.hotel2000.services;

import edu.hotel2000.Config;
import edu.hotel2000.Util;
import edu.hotel2000.models.ConsoleEnv;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.Optional;

public class ConsoleService implements CommandExec{

	private static final Logger logger = Logger.getLogger(ConsoleService.class);

	private ConsoleEnv env;
	private ConsoleHotelService hotelService;
	private ConsoleUtilService utilService;
	private CommandeParser commandeParser;
	private ConsoleClientService clientService;
	private ConsoleBookingService bookingService;

	public ConsoleService(ConsoleEnv env, AccountService accountService){
		this.env = env;

		BookingService bookingService = new BookingService();
		HotelService hotelService = new HotelService(bookingService);

		this.utilService = new ConsoleUtilService(env, accountService);
		this.hotelService = new ConsoleHotelService(env, accountService, utilService, hotelService, bookingService);
		this.commandeParser = new CommandeParser();
		this.clientService = new ConsoleClientService(env, accountService, utilService);
		this.bookingService = new ConsoleBookingService(env, accountService, utilService, bookingService);
	}

	private <T> Optional<T> optionalExclude(T value, T... excludes){
		if(value == null) return Optional.empty();
		for(T exclude : excludes){
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
					Util.parseMoney(params.get("price"))
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
					Util.parseMoney(params.get("price"))
			);
			return;
		}


		// Hotel setTitle
		params = commandeParser.parse(commande, "hotel|h title|t <code> <title> [account]=" + acc).orElse(null);
		if(params != null){
			hotelService.setTitle(
					params.get("account"),
					params.get("code"),
					params.get("title")
			);
			return;
		}


		// Hotel setDescription
		params = commandeParser.parse(commande, "hotel|h description|d <code> <description> [account]=" + acc).orElse(null);
		if(params != null){
			hotelService.setDescription(
					params.get("account"),
					params.get("code"),
					params.get("description")
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
			hotelService.infoActiveBookings(
					params.get("account"),
					params.get("code"),
					optionalExclude(params.get("room"), "-1").map(Integer::parseInt),
					optionalExclude(params.get("client"), "null")
			);
			return;
		}

		// Hotel withdraw
		params = commandeParser.parse(commande, "hotel|h withdraw|w <code> [account]=" + acc).orElse(null);
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
					Util.parseData(params.get("start")),
					Util.parseData(params.get("end")),
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
					Util.parseData(params.get("start")),
					Util.parseData(params.get("end")),
					Integer.parseInt(params.get("roomId")),
					Optional.ofNullable(params.get("money")).map(Util::parseMoney)
			);
			return;
		}


		// Client priceBook
		params = commandeParser.parse(commande, "clientAddress|c priceBook|pb <code> <start> <end> <roomId> [account]=" + acc).orElse(null);
		if(params != null){
			clientService.priceBook(
					params.get("account"),
					params.get("code"),
					Util.parseData(params.get("start")),
					Util.parseData(params.get("end")),
					Integer.parseInt(params.get("roomId"))
			);
			return;
		}


		// Booking info
		params = commandeParser.parse(commande, "booking|b info|i <bookingId> [account]=" + acc).orElse(null);
		if(params != null){
			bookingService.info(
					params.get("account"),
					Integer.parseInt(params.get("bookingId"))
			);
			return;
		}

		// Booking canCancel
		params = commandeParser.parse(commande, "booking|b canCancel|cc <bookingId> [account]=" + acc).orElse(null);
		if(params != null){
			bookingService.canCancel(
					params.get("account"),
					Integer.parseInt(params.get("bookingId"))
			);
			return;
		}

		// Booking cancel
		params = commandeParser.parse(commande, "booking|b cancel|c <bookingId> [account]=" + acc).orElse(null);
		if(params != null){
			bookingService.cancel(
					params.get("account"),
					Integer.parseInt(params.get("bookingId"))
			);
			return;
		}


		// Contract
		params = commandeParser.parse(commande, "contract|c [newValue]").orElse(null);
		if(params != null){
			if(params.containsKey("newValue")){
				env.setContractAddress(params.get("newValue"));
				logger.info("Set current contract value to : " + params.get("newValue"));
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
			logger.info("Current date : " + Util.timestempToString(System.currentTimeMillis()));
			return;
		}

		// Pay
		params = commandeParser.parse(commande, "[from]=" + acc + " pay|p <to> <qte>").orElse(null);
		if(params != null){
			utilService.pay(
					params.get("from"),
					params.get("to"),
					Util.parseMoney(params.get("qte"))
			);
			return;
		}

		System.out.println("Command '" + commande[0] + "' unknown");
		System.out.println(help);
	}

	@Override
	public void onStart(){

	}
}
