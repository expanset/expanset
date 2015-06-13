package com.expanset.samples.getstart;

import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.server.mvc.Template;
import org.glassfish.jersey.server.mvc.Viewable;

import com.expanset.jersey.mvc.templates.PopulateTemplateWith;
import com.expanset.jersey.utils.HttpUtils;
import com.expanset.jersey.validation.ValidationResult;

/**
 * Simple web resource. 
 */
@Path("/")
@PopulateTemplateWith(MasterTemplatePopulator.class)
public class HomeResource {
		
	/**
	 * Shows start page.
	 * @return View template information.
	 */
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Viewable index() {
		return new Viewable("/index.ftl");
	}	
	
	/**
	 * Validates user input. 
	 * @param model Model to validate.
	 * @param result Validation result.
	 * @return View with validation errors or do redirection to start page.
	 */
	@POST
	@Path("/subscribe")
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Template(name="/index.ftl")
	public Object login(
			@BeanParam @Valid SubscribeViewModel model, 
			@Context ValidationResult result) {
		if(result.isSuccess()) {
			return HttpUtils.seeOther(HomeResource.class).build();
		}
		
		return model;
	}	
}
