package edu.hotel2000;

import edu.hotel2000.services.CommandExec;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class InputCommandReader extends Thread{

	private final static Logger logger = Logger.getLogger(InputCommandReader.class);

	private BufferedReader buffer;
	private CommandExec commandExecuter;

	InputCommandReader(InputStream in, CommandExec commandExecuter){
		buffer = new BufferedReader(new InputStreamReader(in));
		this.commandExecuter = commandExecuter;
	}

	private void readCommand() throws IOException{
		String line = buffer.readLine();

		try{
			commandExecuter.evalCommand(parse(line));
		}catch(Exception e){
			logger.error("Execute commande " + line + " failed", e);
		}

	}

	private String[] parse(String commande){
		return commande.split(" ");
	}

	@Override
	public void run(){
		{
			commandExecuter.onStart();
			try{
				//noinspection InfiniteLoopStatement
				while(true) loop();
			}catch(IOException e){
				logger.error("ReadCommand Failed : ", e);
			}
		}
	}

	private void loop() throws IOException{
		System.out.print("> ");
		readCommand();
	}
}
