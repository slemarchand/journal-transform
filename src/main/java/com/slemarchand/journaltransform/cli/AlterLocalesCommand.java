package com.slemarchand.journaltransform.cli;

import com.slemarchand.journaltransform.locales.AlterLocalesBatch;
import com.slemarchand.journaltransform.util.LanguageId;

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
				
				Locale targetLocale = LanguageId.toLocale(target);
				Locale sourceLocale = LanguageId.toLocale(source);
				
				batch.copyLocale(sourceLocale, targetLocale);
			}
		
		}
		
		// Remove locales
		
		String removeLocaleArg = arguments.getOptions().get("remove-locale");
		
		if(removeLocaleArg != null && !removeLocaleArg.trim().isEmpty()) {

			String[] removeLocaleArgArray = removeLocaleArg.split(",");
			
			for (String removeLocaleItem: removeLocaleArgArray) {
				Locale localeToRemove = LanguageId.toLocale(removeLocaleItem);
				batch.removeLocale(localeToRemove);
			}
		}
		
		// Default locale
		
		String defaultLocaleArg = arguments.getOptions().get("default-locale");
		
		if(defaultLocaleArg !=  null && !defaultLocaleArg.trim().isEmpty()) {
			
			Locale defaultLocale = LanguageId.toLocale(defaultLocaleArg);
			
			batch.defaultLocale(defaultLocale);
		}
			
		if(arguments.getOptions().containsKey("continue-on-error")) {
			batch.continueOnError(true);
		}
	
		batch.execute();
	}

	private String usage() {
		return "usage: alterLocales <contentDirectory>" +
				" [--copy-locale=<targetLocale1><-<sourceLocale1>[,<targetLocaleN><-<sourceLocaleN>]*]" + 
				" [--remove-locale=<localeToRemove1>[,<localeToRemoveN>]*]" +
				" [--default-locale=<defaultLocale>]";
	}
}
