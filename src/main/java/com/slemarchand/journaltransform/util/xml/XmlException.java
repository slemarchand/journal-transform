package com.slemarchand.journaltransform.util.xml;

import com.slemarchand.journaltransform.JournalTransformException;

public class XmlException extends JournalTransformException {

	public XmlException() {
		super();
	}

	public XmlException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public XmlException(String message, Throwable cause) {
		super(message, cause);
	}

	public XmlException(String message) {
		super(message);
	}

	public XmlException(Throwable cause) {
		super(cause);
	}

}
