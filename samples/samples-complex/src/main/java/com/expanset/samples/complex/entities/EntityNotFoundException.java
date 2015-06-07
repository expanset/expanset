package com.expanset.samples.complex.entities;

@SuppressWarnings("serial")
public class EntityNotFoundException extends Exception {
	
	public EntityNotFoundException() {
		super();
	}
	
	public EntityNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public EntityNotFoundException(String message) {
		super(message);
	}

	public EntityNotFoundException(Throwable cause) {
		super(cause);
	}
}
