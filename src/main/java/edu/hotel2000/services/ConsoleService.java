package edu.hotel2000.services;

import edu.hotel2000.Config;
import edu.hotel2000.models.ConsoleEnv;
import org.apache.log4j.Logger;

import java.math.BigInteger;

public class ConsoleService implements CommandExecuter{

	private static final Logger logger = Logger.getLogger(ConsoleService.class);

	ConsoleEnv env;
	ConsoleHotelService hotelService;
	ConsoleUtilService utilService;

	public ConsoleService(ConsoleEnv env, AccountService accountService){
		this.env = env;
		this.hotelService = new ConsoleHotelService(env, accountService);
		this.utilService = new ConsoleUtilService(env, accountService);
	}

	@Override
	public void evalCommande(String[] commande) throws Exception{
		logger.debug("Run commande : " + String.join(" ", commande));

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
		}else if(commande[0].equalsIgnoreCase("balance")){
			if(commande.length < 2){
				System.out.println(Config.get("hotel2000.commands.balance.help", help));
				return;
			}
			utilService.showBalance(commande[1]);
			return;
		}else if(commande[0].equalsIgnoreCase("hotel")){
			help = Config.get("hotel2000.commands.hotel.help", help);
			if(commande.length < 2){
				System.out.println();
				return;
			}else if(commande[1].equalsIgnoreCase("create")){
				if(commande.length < 6){
					System.out.println(Config.get("hotel2000.commands.hotel.create.help", help));
					return;
				}
				hotelService.createHotel(commande[2], commande[3], Integer.parseInt(commande[4]), new BigInteger(commande[5]));
				return;
			}else if(commande[1].equalsIgnoreCase("canCreate")){
				if(commande.length < 6){
					System.out.println(Config.get("hotel2000.commands.hotel.create.help", help));
					return;
				}
				hotelService.canCreateHotel(commande[2], commande[3], Integer.parseInt(commande[4]), new BigInteger(commande[5]));
				return;
			}else if(commande[1].equalsIgnoreCase("info")){
				if(commande.length < 4){
					System.out.println(Config.get("hotel2000.commands.hotel.create.help", help));
					return;
				}
//				infoHotel(commande[2], commande[3]);
				return;
			}
		}else if(commande[0].equalsIgnoreCase("contract")){
			if(commande.length > 1){
				env.setCurrantContract(commande[1]);
				logger.info("Set Current contract value to : " + commande[1]);

			}else {
				logger.info("Current contract is: " + env.getCurrantContract());
			}
			return;
		}
		System.out.println("Command '" + commande[0] + "' unknown");
		System.out.println(help);
	}
}
