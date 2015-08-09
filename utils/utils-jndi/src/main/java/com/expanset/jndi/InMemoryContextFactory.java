package com.expanset.jndi;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

public class InMemoryContextFactory implements InitialContextFactory {

	@Override
	public Context getInitialContext(Hashtable<?, ?> environment) 
			throws NamingException {
		final Context initialContext = new InMemoryContext(environment, null, null);
		return initialContext;	
	}
}
