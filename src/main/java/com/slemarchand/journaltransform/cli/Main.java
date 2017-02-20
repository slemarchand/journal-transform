package com.slemarchand.journaltransform.cli;

import java.util.HashMap;
import java.util.Map;

public class Main {
	
	private final static Map<String,Command> COMMAND_MAP = new  HashMap<String, Command>(); 
	
	static {
		COMMAND_MAP.put("alter-locales", new AlterLocalesCommand());
		COMMAND_MAP.put("properties2content", new Properties2ContentCommand());
		COMMAND_MAP.put("content2properties", new Content2PropertiesCommand());
	}

	public static int main(String[] args) {
		
		Arguments arguments = new Arguments(args);
		
		String commandName = arguments.getCommandName();
		
		Command command = COMMAND_MAP.get(commandName);
		
		int status = 0;
		
		if(command != null) {
			try {
				command.execute(arguments);	
			} catch (Exception e) {
				status = -1;
				e.printStackTrace(System.err);
			}
		} else {
			status = -1;
			System.err.println("Unknown command " + commandName);
		}	
		
		return status;
	}
}
