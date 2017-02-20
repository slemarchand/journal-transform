package com.slemarchand.journaltransform;

import java.io.File;
import java.io.FileFilter;

public abstract class ContentsDirectoryWalker {
	
	protected File contentsDirectory;

	protected boolean continueOnError;
	
	protected void setContentsDirectory(File directory) {

		if (!directory.isDirectory()) {
			throw new IllegalArgumentException("Not a directory: "
					+ directory.getPath());
		}

		this.contentsDirectory = directory;
	}

	protected void setContentsDirectory(
			String directoryPathname) {

		File directory = new File(directoryPathname);

		this.setContentsDirectory(directory);

	}
	
	protected abstract void processContentFile(final File file) 
			throws JournalTransformException ;

	protected void processDirectory(File directory) throws JournalTransformException {
			
				File[] children = directory.listFiles(new FileFilter() {
			
					@Override
					public boolean accept(File pathname) {
			
						return pathname.isDirectory()
								|| pathname.getName().endsWith(".xml");
					}
			
				});
			
				for (File child : children) {
			
					if (child.isDirectory()) {
						processDirectory(child);
					} else if(continueOnError) {
						try {
							processContentFile(child);
						} catch(Exception e) {
							e.printStackTrace(System.err);
						}
					} else {
						processContentFile(child);
					}
				}
			}
}
