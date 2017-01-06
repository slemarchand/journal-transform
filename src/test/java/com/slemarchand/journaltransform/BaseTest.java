package com.slemarchand.journaltransform;

import java.io.InputStream;

public abstract class BaseTest {
	
	protected InputStream getResourceAsStream(String name) {
		String path = "/"
				+ this.getClass().getPackage().getName().replaceAll("\\.", "/")
				+ name;
		return this.getClass().getResourceAsStream(path);

	}
}
