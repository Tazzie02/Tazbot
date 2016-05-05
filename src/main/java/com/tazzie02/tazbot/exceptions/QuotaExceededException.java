package com.tazzie02.tazbot.exceptions;

@SuppressWarnings("serial")
public class QuotaExceededException extends Exception {

	public QuotaExceededException() {
		super();
	}
	
	public QuotaExceededException(String message) {
		super(message);
	}
	
}
