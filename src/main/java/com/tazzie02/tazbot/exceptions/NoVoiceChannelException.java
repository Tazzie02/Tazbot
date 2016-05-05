package com.tazzie02.tazbot.exceptions;

@SuppressWarnings("serial")
public class NoVoiceChannelException extends Exception {
	
	public NoVoiceChannelException() {
		super();
	}
	
	public NoVoiceChannelException(String message) {
		super(message);
	}
}
