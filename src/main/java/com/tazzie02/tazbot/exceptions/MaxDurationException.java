package com.tazzie02.tazbot.exceptions;

public class MaxDurationException extends Exception {
	
	private static final long serialVersionUID = 6238478030811049501L;

	public MaxDurationException() {
		super();
	}
	
	public MaxDurationException(String message) {
		super(message);
	}
	
}
