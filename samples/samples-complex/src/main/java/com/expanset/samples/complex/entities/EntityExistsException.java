package com.expanset.samples.complex.entities;

import java.sql.SQLException;

import javax.annotation.Nonnull;
import javax.persistence.PersistenceException;

import org.apache.commons.lang.Validate;
import org.h2.api.ErrorCode;

@SuppressWarnings("serial")
public class EntityExistsException extends Exception {
	
	public EntityExistsException() {
		super();
	}
	
	public EntityExistsException(String message, Throwable cause) {
		super(message, cause);
	}

	public EntityExistsException(String message) {
		super(message);
	}

	public EntityExistsException(Throwable cause) {
		super(cause);
	}
	
	public static void throwIfExist(@Nonnull PersistenceException e) 
			throws EntityExistsException {
		Validate.notNull(e, "e");
		
		if(e.getCause() != null && e.getCause().getCause() instanceof SQLException) {
			SQLException sqlException = (SQLException)e.getCause().getCause();
			if(sqlException.getErrorCode() == ErrorCode.DUPLICATE_KEY_1) {
				throw new EntityExistsException(e);
			}
		}
	}
}
