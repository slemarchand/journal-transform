package com.slemarchand.journaltransform.locales;

import com.slemarchand.journaltransform.ContentsDirectoryWalker;
import com.slemarchand.journaltransform.JournalTransformException;
import com.slemarchand.journaltransform.util.xml.XmlHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

public class AlterLocalesBatch extends ContentsDirectoryWalker {
	
	protected  AlterLocales acl = new AlterLocales(new XmlHelper());

	public AlterLocalesBatch contentsSourceDirectory(File directory) {

		this.setContentsDirectory(directory);

		return this;
	}

	public AlterLocalesBatch contentsSourceDirectory(
			String directoryPathname) {

		this.setContentsDirectory(directoryPathname);

		return this;
	}
	
	public AlterLocalesBatch copyLocale(Locale sourceLocale,
			Locale targetLocale) {

		this.acl.copyLocale(sourceLocale, targetLocale);

		return this;
	}

	public AlterLocalesBatch removeLocale(Locale locale) {

		this.acl.removeLocale(locale);

		return this;
	}

	public AlterLocalesBatch defaultLocale(Locale defaultLocale) {

		this.acl.defaultLocale(defaultLocale);

		return this;
	}
	
	public AlterLocalesBatch continueOnError(boolean continueOnError) {
		
		this.continueOnError = continueOnError;

		return this;
	} 

	public void execute() throws Exception {

		
		
		processDirectory(contentsDirectory);

	}

	protected void processContentFile(final File file) throws JournalTransformException, IOException {

		System.out.println("Processing " + file);
		

		acl
		.contentInput(file)
		.contentOutput(file);

		acl.execute();
	}
}
