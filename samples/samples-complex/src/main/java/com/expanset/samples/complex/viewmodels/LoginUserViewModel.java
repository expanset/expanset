package com.expanset.samples.complex.viewmodels;

import javax.ws.rs.FormParam;

import org.hibernate.validator.constraints.NotEmpty;

public class LoginUserViewModel {
	
	@NotEmpty
	@FormParam("login")
	private String login;
	
	@NotEmpty	
	@FormParam("password")
	private String password;

	@FormParam("returnUrl")
	private String returnUrl;
	
	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}
}
