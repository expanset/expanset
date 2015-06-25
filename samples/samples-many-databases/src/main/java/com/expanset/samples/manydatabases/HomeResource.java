package com.expanset.samples.manydatabases;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.server.mvc.Template;
import com.expanset.hk2.persistence.PersistenceSessionManager;

/**
 * Simple web resource. 
 */
@Path("/")
public class HomeResource {
	
	@Inject
	private Provider<DaoService> daoService;

	@Inject
	private PersistenceSessionManager persistenceSessionManager;
	
	/**
	 * Shows data from the first database.
	 * @return Model.
	 */
	@GET
	@Template(name="/index.ftl")
	@Produces(MediaType.TEXT_HTML)
	public Object indexDb1() {
		final Map<String, String> model = new HashMap<>();

		final Map<String, String> dbNameOverrides = new HashMap<>();
		dbNameOverrides.put("", "db01");
		persistenceSessionManager.runInScope(
				() -> model.put("helloString", daoService.get().getFromDatabase()), 
				dbNameOverrides);
				
		return model;
	}	

	/**
	 * Shows data from the second database.
	 * @return Model.
	 */
	@GET
	@Path("/db2")
	@Template(name="/index.ftl")
	@Produces(MediaType.TEXT_HTML)
	public Object indexDb2() {
		final Map<String, String> model = new HashMap<>();
		
		final Map<String, String> dbNameOverrides = new HashMap<>();
		dbNameOverrides.put("", "db02");
		persistenceSessionManager.runInScope(
				() -> model.put("helloString", daoService.get().getFromDatabase()), 
				dbNameOverrides);
		
		return model;
	}	
}
