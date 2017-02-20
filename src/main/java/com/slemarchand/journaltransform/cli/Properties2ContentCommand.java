package com.slemarchand.journaltransform.cli;

import com.slemarchand.journaltransform.properties.Properties2ContentBatch;

import java.util.List;

public class Properties2ContentCommand extends BaseCommand {

	@Override
	public void execute(Arguments arguments) throws Exception {
	
		List<String> args = arguments.getStandardArguments();
		
		if(args.size() < 2) {
			throw new CommandArgumentsException(usage());		
		}
		
		Properties2ContentBatch batch = new Properties2ContentBatch();
		
		batch.propertiesSourceDirectory(args.get(0));
		
		batch.articleTargetDirectory(args.get(1));
		
		batch.execute();
	}
	
	private String usage() {
		return "usage: properties2content" +
				" <propertiesDirectory> <contentDirectory>";
	}

}
