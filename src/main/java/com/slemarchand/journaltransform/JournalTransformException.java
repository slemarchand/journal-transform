package com.slemarchand.journaltransform;

public class JournalTransformException extends Exception {

	public JournalTransformException() {
		super();
	}

	public JournalTransformException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public JournalTransformException(String message, Throwable cause) {
		super(message, cause);
	}

	public JournalTransformException(String message) {
		super(message);
	}

	public JournalTransformException(Throwable cause) {
		super(cause);
	}

}
