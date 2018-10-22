package edu.hotel2000;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@SuppressWarnings("WeakerAccess")
public class Config{

	private final static Logger logger = Logger.getLogger(Config.class);

	private static Properties prop = new Properties();
	private static boolean isLoaded = false;

	private static void load(String file){
		logger.debug("load config " + file);
		InputStream input = null;

		try{

			input = Config.class.getClassLoader().getResourceAsStream(file);

			if(input == null){ // ignore if file is not found
				logger.info("Config " + file + " not loaded: File not found");
				return;
			}

			// load a properties file
			prop.load(input);

		}catch(IOException ex){
			ex.printStackTrace();
		}finally{
			if(input != null){
				try{
					logger.debug("Config " + file + " loaded");
					input.close();
				}catch(IOException e){
					logger.error("Config " + file + " not loaded", e);
				}
			}
		}
	}

	private static void load(){
		logger.debug("load configs");
		load("config.properties");
		load("config-local.properties");
		logger.debug("load configs finish");
		isLoaded = true;
	}

	public static String get(String key){
		if(!isLoaded){
			load();
		}
		return prop.getProperty(key);
	}

	public static String get(String key, String defaultValue){
		String res = get(key);
		return res == null ? defaultValue : res;
	}
}
