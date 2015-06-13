package com.expanset.samples.getstart;

import javax.ws.rs.FormParam;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Validatable model for web form.
 */
public class SubscribeViewModel {

	@Email
	@NotEmpty
	@FormParam("email")
	private String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
