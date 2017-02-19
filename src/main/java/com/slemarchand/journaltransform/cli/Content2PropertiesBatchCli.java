package com.slemarchand.journaltransform.cli;

import com.slemarchand.journaltransform.properties.Content2PropertiesBatch;
import com.slemarchand.journaltransform.properties.KeyPrefixFactory;
import com.slemarchand.journaltransform.properties.Content2PropertiesBatch.BatchPropertyFilter;

import java.util.Arrays;



public class Content2PropertiesBatchCli {

	public static interface CustomBehavior {
		
		public BatchPropertyFilter getPropertyFilter();
		
		public KeyPrefixFactory getKeyPrefixFactory();
		
	}
	
	public void run(String[] args) throws Exception {
		
		if(args.length < 2) {
			throw new IllegalArgumentException(usage());
		}
		
		// /Users/sebastien/Work/DANON/danon-liferay/danon-liferay-plugins/danon-automated-actions-hook/src/main/resources/contents/articles/france
		String contentsPathname = args[0];
		String propertiesPath = args[1];
		
		Content2PropertiesBatch batch = new Content2PropertiesBatch();
		batch.articleSourceDirectory(contentsPathname);
		batch.propertiesTargetDirectory(propertiesPath);
		
		if(args.length > 2 && !args[2].startsWith("-")) {
			String customBehaviorClassName = args[2];
			
			@SuppressWarnings("unchecked")
			Class<? extends CustomBehavior> customBehaviorClass = (Class<? extends CustomBehavior>) Class.forName(customBehaviorClassName);
			CustomBehavior customBehavior = customBehaviorClass.newInstance();
			
			BatchPropertyFilter filter = customBehavior.getPropertyFilter();
			if(filter != null) {
				batch.filter(filter);
			}
			
			KeyPrefixFactory keyPrefixFactory = customBehavior.getKeyPrefixFactory();
			if(keyPrefixFactory != null) {
				batch.keyPrefixFactory(keyPrefixFactory);
			}
		}
		
		if(Arrays.asList(args).contains("--continue-on-error")) {
			batch.continueOnError(true);
		}
	
		batch.execute();
	}
	
	private String usage() {
		String usage = "<contentsPathname> <propertiesPath> [<customBehaviorClass>]";
		return usage;
	}

	public static void main(String[] args) throws Exception {
		
		new Content2PropertiesBatchCli().run(args);
		
	}
	
}
