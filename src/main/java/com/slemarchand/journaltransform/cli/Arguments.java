package com.slemarchand.journaltransform.cli;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Arguments {
	
	protected String commandName;
	
	protected Map<String, String> options = new HashMap<String, String>();
	
	protected List<String> standardArguments = new LinkedList<String>();
	
	public Arguments(String[] args) {
		super();
		parse(args);
	}
	
	public String getCommandName() {
		return commandName;
	}
	
	public Map<String, String>  getOptions() {
		return options;
	}
	
	public List<String> getStandardArguments() {
		return standardArguments;
	}
	
	protected void parse(String[] args) {
		
		if(args.length == 0) {
			
			commandName = "usage";
			
			return;
		}
		
		commandName = args[0];
		
		for (int i = 1; i < args.length; i++) {
			String arg = args[i];
			
			if(arg.startsWith("--")) {
				int sepIndex = arg.indexOf('=');
				
				int optionNameEndIndex =
						sepIndex == -1 ? arg.length() : sepIndex;
				
				String optionName = arg.substring(2, optionNameEndIndex);
				
				String optionValue;
				
				if(sepIndex == -1 || sepIndex == arg.length())  {
					
					String nextArg = args[i+1];
					
					if(!nextArg.startsWith("--")) {
						optionValue = nextArg;
						i++;
					} else {
						optionValue = "";
					}

				} else {
					optionValue = arg.substring(sepIndex+1,arg.length());
				}
				
				this.options.put(optionName, optionValue);
				
			} else {
				this.standardArguments.add(arg);
			}
		}
		
	}
	
	
	
}
