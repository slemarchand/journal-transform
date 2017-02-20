package com.slemarchand.journaltransform.util;

import java.util.Locale;

public class LanguageId {

	public static Locale toLocale(String languageId) {

		Locale locale;

		String[] parts = languageId.split("_");

		switch (parts.length) {
		case 2:
			locale = new Locale(parts[0], parts[1]);
			break;
		case 3:
			locale = new Locale(parts[0], parts[1], parts[2]);
			break;
		default:
			locale = new Locale(languageId);
			break;
		}

		return locale;
	}
}
