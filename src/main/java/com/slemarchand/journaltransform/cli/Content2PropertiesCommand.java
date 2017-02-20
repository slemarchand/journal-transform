package com.slemarchand.journaltransform.cli;

import com.slemarchand.journaltransform.cli.Content2PropertiesCommand.CustomBehavior;
import com.slemarchand.journaltransform.properties.Content2PropertiesBatch;
import com.slemarchand.journaltransform.properties.KeyPrefixFactory;
import com.slemarchand.journaltransform.properties.Properties2ContentBatch;
import com.slemarchand.journaltransform.properties.Content2PropertiesBatch.BatchPropertyFilter;

import java.util.List;

public class Content2PropertiesCommand implements Command {

	public static interface CustomBehavior {
		
		public BatchPropertyFilter getPropertyFilter();
		
		public KeyPrefixFactory getKeyPrefixFactory();
		
	}

	@Override
	public void execute(Arguments arguments) throws Exception {
	
		List<String> args = arguments.getStandardArguments();
		
		if(args.size() < 2) {
			throw new CommandArgumentsException(usage());
		}
		
		Content2PropertiesBatch batch = new Content2PropertiesBatch();
		
		batch.articleSourceDirectory(args.get(0));
		
		batch.propertiesTargetDirectory(args.get(1));
		
		
		String customBehaviorArg = arguments.getOptions().get("custom-behavior");
		
		if (customBehaviorArg != null && !customBehaviorArg.trim().isEmpty()) {

			@SuppressWarnings("unchecked")
			Class<? extends Content2PropertiesCommand.CustomBehavior> clazz = (Class<? extends Content2PropertiesCommand.CustomBehavior>) Class
					.forName(customBehaviorArg);

			Content2PropertiesCommand.CustomBehavior behavior = clazz.newInstance();

			BatchPropertyFilter filter = behavior.getPropertyFilter();
			if (filter != null) {
				batch.filter(filter);
			}

			KeyPrefixFactory keyPrefixFactory = behavior.getKeyPrefixFactory();
			if (keyPrefixFactory != null) {
				batch.keyPrefixFactory(keyPrefixFactory);
			}
		}
		
		if(arguments.getOptions().containsKey("continue-on-error")) {
			batch.continueOnError(true);
		}
		
		batch.execute();
	}

	private String usage() {
		return "usage: content2properties" +
				" <contentDirectory> <propertiesDirectory> " + 
				"[--custom-behavior=<customBehaviorClass>]";
	}
}
