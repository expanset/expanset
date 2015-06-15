package com.expanset.samples.mustache;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.server.mvc.Template;

/**
 * Simple web resource. 
 */
@Path("/")
public class HomeResource {
		
	/**
	 * Shows start page.
	 * @return View template information.
	 */
	@GET
	@Template(name="/index.mustache")
	@Produces(MediaType.TEXT_HTML)
	public Map<String, Object> index() {
		final Map<String, Object> model = new HashMap<>();
		model.put("helloFromControler", "Hello from controller!");
		
		return model;
	}	
}
