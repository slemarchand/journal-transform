package com.slemarchand.journaltransform.cli;

import com.slemarchand.journaltransform.locales.AlterLocalesBatch;

import java.util.List;
import java.util.Locale;

public class AlterLocalesCommand implements Command {
	
	@Override
	public void execute(Arguments arguments) throws Exception {
		
		List<String> args = arguments.getStandardArguments();
		
		if(args.size() < 1) {
			
			System.err.println(usage());
			
			return;
		}
		
		AlterLocalesBatch batch = new AlterLocalesBatch();
		
		batch.contentsSourceDirectory(args.get(0));
		
		// Copy locales
		
		String copyLocaleArg = arguments.getOptions().get("copy-locale");
		
		if(copyLocaleArg != null && !copyLocaleArg.trim().isEmpty()) {
		
			String[] copyLocaleArgArray = copyLocaleArg.split(",");
			
			for (String copyLocaleAssignment : copyLocaleArgArray) {
				String[] copyLocaleAssignmentArray = copyLocaleAssignment.split("<-");
				String target = copyLocaleAssignmentArray[0];
				String source = copyLocaleAssignmentArray[1];
				
				Locale targetLocale = toLocale(target);
				Locale sourceLocale = toLocale(source);
				
				batch.copyLocale(sourceLocale, targetLocale);
			}
		
		}
		
		// Remove locales
		
		String removeLocaleArg = arguments.getOptions().get("remove-locale");
		
		if(removeLocaleArg != null && !removeLocaleArg.trim().isEmpty()) {

			String[] removeLocaleArgArray = removeLocaleArg.split(",");
			
			for (String removeLocaleItem: removeLocaleArgArray) {
				Locale localeToRemove = toLocale(removeLocaleItem);
				batch.removeLocale(localeToRemove);
			}
		}
		
		// Default locale
		
		String defaultLocaleArg = arguments.getOptions().get("default-locale");
		
		if(defaultLocaleArg !=  null && !defaultLocaleArg.trim().isEmpty()) {
			
			Locale defaultLocale = toLocale(defaultLocaleArg);
			
			batch.defaultLocale(defaultLocale);
		}
			
		if(arguments.getOptions().containsKey("continue-on-error")) {
			batch.continueOnError(true);
		}
	
		batch.execute();
	}
	
	private Locale toLocale(String localeString) {
		
		Locale locale;
		
		String[] parts = localeString.split("_");
	
		switch (parts.length) {
		case 2:
			locale = new Locale(parts[0], parts[1]);
			break;
		case 3:
			locale = new Locale(parts[0], parts[1], parts[2]);
			break;
		default:
			locale = new Locale(localeString);
			break;
		}
		
		return locale;
	}

	private String usage() {
		return "usage: alterLocales <content-directory>" +
				" [--copy-locale=<target-locale-1><-<source-locale-1>[,<target-locale-n><-<source-locale-n>]*]" + 
				" [--remove-locale=<locale-to-remove-1>[,<locale-to-remove-n>]*]" +
				" [--default-locale=<default-locale>]";
	}
}
