package com.slemarchand.journaltransform.cli;

import java.util.HashMap;
import java.util.Map;

public class Main {
	
	private final static Map<String,Command> COMMAND_MAP = new  HashMap<String, Command>(); 
	
	static {
		COMMAND_MAP.put("alter-locales", new AlterLocalesCommand());
		COMMAND_MAP.put("properties2content", new Properties2ContentCommand());
	}

	public static void main(String[] args) {
		
		Arguments arguments = new Arguments(args);
		
		String commandName = arguments.getCommandName();
		
		Command command = COMMAND_MAP.get(commandName);
		
		if(command != null) {
			try {
				command.execute(arguments);	
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
		} else {
			System.err.println("Unknown command " + commandName);
		}	
	}
	
}
