package com.tazzie02.tazbot.exceptions;

public class NoVoiceChannelException extends Exception {
	
	private static final long serialVersionUID = -6955871131210947057L;

	public NoVoiceChannelException() {
		super();
	}
	
	public NoVoiceChannelException(String message) {
		super(message);
	}
}
