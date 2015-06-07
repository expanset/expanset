package com.expanset.samples.complex.viewmodels;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.FormParam;

import org.apache.commons.lang3.StringUtils;

import com.expanset.samples.complex.entities.DbConstraints;

public class ProfileViewModel {
	
	@NotNull	
	@Size(min=DbConstraints.MIN_PASSWORD_LENGTH, max=DbConstraints.MAX_PASSWORD_LENGTH)
	@FormParam("password")
	private String password;
	
	@FormParam("confirmPassword")
	private String confirmPassword;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	
	@AssertTrue(message="{confirmPassword.message}")
	public boolean isPasswordMatch() {
		return StringUtils.equals(password, confirmPassword);
	}	
}
