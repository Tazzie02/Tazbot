package com.tazzie02.tazbot.exceptions;

public class NotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = -8836756251043242953L;

	public NotFoundException() {
		super();
	}
	
	public NotFoundException(String message) {
		super(message);
	}
}
