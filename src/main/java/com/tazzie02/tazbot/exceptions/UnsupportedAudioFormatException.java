package com.tazzie02.tazbot.exceptions;

@SuppressWarnings("serial")
public class UnsupportedAudioFormatException extends Exception {
	
	public UnsupportedAudioFormatException() {
		super();
	}
	
	public UnsupportedAudioFormatException(String message) {
		super(message);
	}
	
}
