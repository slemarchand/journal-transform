package com.slemarchand.journaltransform.properties;

import java.io.File;

public interface KeyPrefixFactory {
	
	public String makeKeyPrefix(
			File contentFile,
			String contentFileRelativePath);
	
}