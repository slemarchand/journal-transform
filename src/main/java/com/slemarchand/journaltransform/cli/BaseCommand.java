package com.slemarchand.journaltransform.cli;

import java.io.PrintStream;

public abstract class BaseCommand implements Command {
	
	private PrintStream err = System.err;
	
	private PrintStream out = System.out;
	
	protected void logError(String message) {
		err.println(message);
	}
	
	protected void log(String message) {
		out.println(message);
	}
	

}
