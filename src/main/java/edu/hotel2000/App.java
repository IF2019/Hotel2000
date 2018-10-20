package edu.hotel2000;

import org.apache.log4j.Logger;

public class App{

	private final static Logger logger = Logger.getLogger(App.class);
	private static final String NAME = Config.get("hotel2000.name");
	private static final String PASSWORD = Config.get("hotel2000.password");

	public static void main(String[] args){

		if(PASSWORD == null){
			logger.info("Hello " + NAME + "! You are not specified your password in config file :(");
		}else{
			logger.info("Hello " + NAME + " your password is " + PASSWORD);
		}



	}
}
