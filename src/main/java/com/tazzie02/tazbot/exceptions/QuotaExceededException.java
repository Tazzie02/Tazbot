package com.tazzie02.tazbot.exceptions;

public class QuotaExceededException extends Exception {

	private static final long serialVersionUID = 9008093145014652124L;

	public QuotaExceededException() {
		super();
	}
	
	public QuotaExceededException(String message) {
		super(message);
	}
	
}
