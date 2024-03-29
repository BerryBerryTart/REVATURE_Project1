package com.berry.exception;

public class BadParameterException extends Exception {
	
	private static final long serialVersionUID = 8771492278053319613L;

	public BadParameterException() {
		super();
	}

	public BadParameterException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public BadParameterException(String message, Throwable cause) {
		super(message, cause);
	}

	public BadParameterException(String message) {
		super(message);
	}

	public BadParameterException(Throwable cause) {
		super(cause);
	}

}