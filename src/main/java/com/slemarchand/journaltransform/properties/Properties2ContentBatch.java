package com.slemarchand.journaltransform.properties;

import com.slemarchand.journaltransform.ContentsDirectoryWalker;
import com.slemarchand.journaltransform.util.xml.XmlException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public class Properties2ContentBatch extends ContentsDirectoryWalker {

	private File propertiesSourceDirectory;
	
	private Map<Locale, Properties> propertiesByLocale;

	private KeyPrefixFactory keyPrefixFactory;
	
	public Properties2ContentBatch articleSourceDirectory(File directory) {

		this.setContentsDirectory(directory);

		return this;
	}

	public Properties2ContentBatch articleTargetDirectory(
			String directoryPathname) {

		this.setContentsDirectory(directoryPathname);

		return this;
	}

	public Properties2ContentBatch propertiesSourceDirectory(File directory) {

		if (directory.exists() && !directory.isDirectory()) {
			throw new IllegalArgumentException("Not a directory: "
					+ directory.getPath());
		}

		this.propertiesSourceDirectory = directory;

		return this;
	}

	public Properties2ContentBatch propertiesSourceDirectory(
			String directoryPathname) {

		File directory = new File(directoryPathname);

		this.propertiesSourceDirectory(directory);

		return this;
	}
	
	public Properties2ContentBatch keyPrefixFactory(KeyPrefixFactory keyPrefixFactory) {
		
		this.keyPrefixFactory = keyPrefixFactory;

		return this;
	} 
	
	public Properties2ContentBatch continueOnError(boolean continueOnError) {
		
		this.continueOnError = continueOnError;

		return this;
	} 

	public Properties2ContentBatch execute() throws Exception {

		propertiesByLocale = new HashMap<Locale, Properties>();

		final Properties2ContentBatch self = this;
		
		File[] propertiesFiles = this.propertiesSourceDirectory
				.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File f, String name) {
				
				boolean accept = name.endsWith(".properties");
				
				return accept;
			}
		});
		
		if (propertiesFiles.length == 0) {
			throw new IllegalStateException(
					"No .properties file found into directory "
							+ propertiesSourceDirectory);
		}
		
		for (File propertiesFile : propertiesFiles) {
			
			System.out.println("Loading " + propertiesFile + "...");
			
			loadPropertiesFile(propertiesFile);
		}
		
		
		processDirectory(contentsDirectory);

		return this;
	}

	protected void loadPropertiesFile(File file) throws Exception, IOException {
		
		Properties props = new Properties();
		
		props.load(new FileReader(file));
		
		Locale locale;
		
		String fileName = file.getName();
		
		String[] nameParts = fileName.substring(0, fileName.lastIndexOf('.')).split("_");
		
		String localeErrorMessage = "Unrecognized locale for file " + fileName + " ";
		
		if(nameParts.length < 3) {
			
			throw new IllegalArgumentException(localeErrorMessage);
			
		} else {	
			
			String language = nameParts[nameParts.length - 2];
			
			String country = nameParts[nameParts.length - 1];
			
			locale = new Locale(language, country);
			
			if (propertiesByLocale.containsKey(locale)) {
				throw new IllegalArgumentException("Unable to process "
						+ fileName
						+ " because another file was already processed"
						+ " for locale " + locale);
			}
			
			propertiesByLocale.put(locale, props);
			
			System.out.println(props.size() + " properties loaded for " + file);
		}
		
	}

	@Override
	protected void processContentFile(final File file) throws XmlException, IOException {

		System.out.println("Processing " + file);
		
		final String keyPrefix = getKeyPrefix(file);

		Properties2Content properties2Content = new Properties2Content();
		
		properties2Content.propertiesByLocale(propertiesByLocale);
		
		properties2Content.keyPrefix(keyPrefix);

		properties2Content.contentInput(file);
		
		properties2Content.contentOutput(file);

		properties2Content.execute();
	}
	
	/*
	protected String getKeyPrefix(File file) {
		
		String keyPrefix;
		
		String relativePath = file.getPath().replace(
				this.contentsDirectory.getPath() + File.separator,
				"");

		if(keyPrefixFactory != null) {
			
			keyPrefix = this.keyPrefixFactory
					.makeKeyPrefix(file, relativePath);
			
		} else {
			
			keyPrefix = relativePath.;
		}
		
		return keyPrefix;
	}*/
	
	
	public String getKeyPrefix(File file) {	
	
		String relativePath = file.getPath().replace(
				this.contentsDirectory.getPath() + File.separator,
				"").concat(".");
		
		String keyPrefix = relativePath.replaceAll(" ","-").toLowerCase();

		keyPrefix = keyPrefix.replace(".xml", "");
		
		return keyPrefix;
	}	
}
