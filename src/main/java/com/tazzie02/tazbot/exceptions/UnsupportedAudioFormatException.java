package com.tazzie02.tazbot.exceptions;

public class UnsupportedAudioFormatException extends Exception {
	
	private static final long serialVersionUID = -6699927713412519156L;

	public UnsupportedAudioFormatException() {
		super();
	}
	
	public UnsupportedAudioFormatException(String message) {
		super(message);
	}
	
}
