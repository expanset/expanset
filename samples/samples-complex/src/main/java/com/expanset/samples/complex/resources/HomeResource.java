package com.expanset.samples.complex.resources;

import java.util.Locale;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.mvc.Viewable;

import com.expanset.hk2.i18n.LocaleManager;
import com.expanset.jersey.RememberOptionsInCookie;
import com.expanset.jersey.mvc.templates.PopulateTemplateWith;
import com.expanset.jersey.utils.HttpUtils;
import com.expanset.samples.complex.services.RegisteredViewPopulator;

@Path("/")
@PopulateTemplateWith(RegisteredViewPopulator.class)
public class HomeResource {
	
	@Inject
	private LocaleManager localeManager;

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Object index() {
		return new Viewable("/default.ftl");
	}	

	@GET
	@Path("/language/{language}")
	public Response language(@PathParam(value="language") String language) {
		localeManager.saveLocale(
				new Locale(language), new RememberOptionsInCookie(10000));

		return HttpUtils.seeOther(HomeResource.class)
				.build();
	}	
}
