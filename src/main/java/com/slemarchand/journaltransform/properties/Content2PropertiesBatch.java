package com.slemarchand.journaltransform.properties;

import com.slemarchand.journaltransform.ContentsDirectoryWalker;
import com.slemarchand.journaltransform.properties.Content2Properties.Property;
import com.slemarchand.journaltransform.properties.Content2Properties.PropertyFilter;
import com.slemarchand.journaltransform.util.xml.XmlException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public class Content2PropertiesBatch extends ContentsDirectoryWalker {

	public static interface BatchProperty extends Content2Properties.Property {
		
		public File getContentFile();
		
		public String getKeyPrefix();
	}
	
	public static interface BatchPropertyFilter {
		
		boolean accept(BatchProperty context);
	}

	private File propertiesTargetDirectory;

	private String propertiesBasename = "JournalArticles";

	private Map<Locale, Properties> propertiesByLocale;
		
	private BatchPropertyFilter filter;
	
	private KeyPrefixFactory keyPrefixFactory;
	
	public Content2PropertiesBatch articleSourceDirectory(File directory) {

		this.setContentsDirectory(directory);

		return this;
	}

	public Content2PropertiesBatch articleSourceDirectory(
			String directoryPathname) {

		this.setContentsDirectory(directoryPathname);

		return this;
	}

	public Content2PropertiesBatch propertiesTargetDirectory(File directory) {

		if (directory.exists() && !directory.isDirectory()) {
			throw new IllegalArgumentException("Not a directory: "
					+ directory.getPath());
		}

		this.propertiesTargetDirectory = directory;

		return this;
	}

	public Content2PropertiesBatch propertiesTargetDirectory(
			String directoryPathname) {

		File directory = new File(directoryPathname);

		this.propertiesTargetDirectory(directory);

		return this;
	}

	public Content2PropertiesBatch propertiesBasename(String basename) {

		this.propertiesBasename = basename;

		return this;
	}
	
	public Content2PropertiesBatch filter(BatchPropertyFilter filter) {

		if(filter == null) {
			throw new IllegalArgumentException("filter cannot be null");
		}
		
		this.filter = filter;

		return this;
	} 
	
	public Content2PropertiesBatch keyPrefixFactory(KeyPrefixFactory keyPrefixFactory) {

		if(filter == null) {
			throw new IllegalArgumentException("keyPrefixFactory cannot be null");
		}
		
		this.keyPrefixFactory = keyPrefixFactory;

		return this;
	} 
	
	public Content2PropertiesBatch continueOnError(boolean continueOnError) {
		
		this.continueOnError = continueOnError;

		return this;
	} 

	public Content2PropertiesBatch execute() throws Exception {

		propertiesByLocale = new HashMap<Locale, Properties>();

		processDirectory(contentsDirectory);

		this.propertiesTargetDirectory.mkdirs();

		for (Entry<Locale, Properties> e : propertiesByLocale.entrySet()) {

			Locale locale = e.getKey();

			File propsFile = new File(this.propertiesTargetDirectory,
					this.propertiesBasename + "_" + locale + ".properties");

			FileWriter fw = new FileWriter(propsFile);

			Properties props = e.getValue();
			props.store(fw, "");
	
			fw.flush();
			fw.close();
		}

		return this;
	}

	@Override
	protected void processContentFile(final File file) throws XmlException, FileNotFoundException {

		System.out.println("Processing " + file);
		
		final String keyPrefix = getKeyPrefix(file);
		
		Content2Properties content2properties = new Content2Properties() {

			@Override
			protected PropertyImpl makePropertyImpl() {
				
				BatchPropertyImpl property = new BatchPropertyImpl();
				
				property.setContentFile(file);
				
				property.setKeyPrefix(keyPrefix);
				
				return property;
			}
		};
		
		content2properties.propertiesByLocale(propertiesByLocale);
		
		if(filter != null) {
			content2properties.filter(new FilterAdapter(filter));	
		}
		
		content2properties.keyPrefix(keyPrefix);

		content2properties.content(new FileInputStream(file));

		content2properties.execute();
	}
	
	protected String getKeyPrefix(File file) {
		
		String keyPrefix;
		
		String relativePath = file.getPath().replace(
				this.contentsDirectory.getPath() + File.separator,
				"").concat(".");

		if(keyPrefixFactory != null) {
			
			keyPrefix = this.keyPrefixFactory
					.makeKeyPrefix(file, relativePath);
			
		} else {
			
			keyPrefix = relativePath;
		}
		
		return keyPrefix;
	}

	protected static class BatchPropertyImpl extends Content2Properties.PropertyImpl implements BatchProperty {

		private File contentFile;
		
		private String keyPrefix;

		@Override
		public File getContentFile() {
			return contentFile;
		}

		public void setContentFile(File contentFile) {
			this.contentFile = contentFile;
		}

		@Override
		public String getKeyPrefix() {
			return keyPrefix;
		}

		public void setKeyPrefix(String keyPrefix) {
			this.keyPrefix = keyPrefix;
		}		
	}
	
	protected static class FilterAdapter implements PropertyFilter {
		
		private BatchPropertyFilter targetFilter;
		
		public FilterAdapter(BatchPropertyFilter targetFilter) {
			super();
			this.targetFilter = targetFilter;
		}

		@Override
		public boolean accept(Property context) {
			return targetFilter.accept((BatchProperty) context);
		}
	}
}
