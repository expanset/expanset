package com.expanset.samples.complex.services;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.core.SecurityContext;

import org.glassfish.jersey.server.mvc.Viewable;

import com.expanset.jersey.mvc.templates.TemplatePopulator;

public class RegisteredViewPopulator implements TemplatePopulator {

	@Inject
	protected Provider<SecurityContext> securityContextProvider;
	
	@Override
	public void populate(Viewable viewable, Map<String, Object> model) {
		final SecurityContext securityContext = securityContextProvider.get();
		if(securityContext.getUserPrincipal() instanceof SitePrincipal) {
			model.put("siteUser", securityContext.getUserPrincipal());
		}
	}
}
