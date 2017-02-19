package com.slemarchand.journaltransform.cli;

import com.slemarchand.journaltransform.properties.Properties2ContentBatch;

import java.util.List;

public class Properties2ContentCommand implements Command {

	@Override
	public void execute(Arguments arguments) throws Exception {
	
		List<String> args = arguments.getStandardArguments();
		
		if(args.size() < 2) {
			System.err.println("usage: properties2content <properties-directory> <content-directory>");
		}
		
		Properties2ContentBatch batch = new Properties2ContentBatch();
		
		batch.propertiesSourceDirectory(args.get(0));
		
		batch.articleTargetDirectory(args.get(1));
		
		batch.execute();
		
	}

}
