package com.tazzie02.tazbot.exceptions;

@SuppressWarnings("serial")
public class ProductNotFoundException extends Exception {
	
	public ProductNotFoundException() {
		super();
	}
	
	public ProductNotFoundException(String message) {
		super(message);
	}
}
