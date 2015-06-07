package com.expanset.samples.complex.services;

import java.security.Principal;
import java.util.Date;

import javax.annotation.Nonnull;

import com.expanset.samples.complex.entities.User;

public class SitePrincipal implements Principal {

	public final static String ROLE_USER = "user";
	
	protected final long id;
	
	protected final String name;
	
	protected final Date stockQuoteDate;
	
	public SitePrincipal(@Nonnull User user) {
		this.id = user.getId();
		this.name = user.getLogin();
		this.stockQuoteDate = user.getStockQuoteDate();
	}
	
	@Override
	public String getName() {
		return name;
	}

	public long getId() {
		return id;
	}
	
	public Date getStockQuoteDate() {
		return stockQuoteDate;
	}
}
