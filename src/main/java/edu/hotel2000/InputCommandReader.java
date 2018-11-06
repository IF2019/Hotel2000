package edu.hotel2000;

import com.fasterxml.jackson.databind.deser.DataFormatReaders;
import edu.hotel2000.services.CommandExec;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

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

	private String[] parse(String command){
		Pattern pattern = Pattern.compile("(^|\\ *)(([^ \\\"]+)|(\\\"(((\\\\\\\\)|(\\\\\\\")|[^\"])*)\\\"))");
		Matcher matche = pattern.matcher(command);
		List<String> res = new ArrayList<>();
		while(matche.find()){
			if(matche.group(3) == null){
				res.add(matche.group(5)
						.replaceAll("\\\\\\\"","\"")
						.replaceAll("\\\\\\\\","\\\\"));
			}else {
				res.add(matche.group(3));

			}
		}
		return res.toArray(new String[res.size()]);
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
//		System.out.print("> ");
		readCommand();
	}
}
