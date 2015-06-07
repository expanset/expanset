package com.expanset.samples.complex.resources;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expanset.common.errors.ExceptionAdapter;
import com.expanset.hk2.persistence.PersistenceSessionManager;
import com.expanset.samples.complex.entities.UserRepository;
import com.expanset.samples.complex.services.SitePrincipal;
import com.expanset.samples.complex.services.StockQuotesService;

@Path("/stock-quotes")
public class StockQuotesResource {

	@Inject
	private Provider<UserRepository> userRepository;
	
	@Inject
	private StockQuotesService stockQuotesService;

	@Inject
	private SecurityContext securityContext;
	
	@Inject
	private PersistenceSessionManager persistenceSessionManager;
	
	private final static Logger log = LoggerFactory.getLogger(StockQuotesResource.class); 
	
	@GET
	@Path("/{symbol}")
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed(value=SitePrincipal.ROLE_USER)
	public void getAsync(
			@PathParam("symbol") final String symbol, 
			@Suspended final AsyncResponse asyncResponse) {	
		CompletableFuture
			.supplyAsync(() -> 
				ExceptionAdapter.get(() -> stockQuotesService.queryGoogle(symbol)))
			.thenAccept(stockQuotes -> {
				persistenceSessionManager.runInScope(() -> {
					userRepository.get().updateStockQuoteDate(
							((SitePrincipal)securityContext.getUserPrincipal()).getId(), 
							new Date());
				});
				
				asyncResponse.resume(stockQuotes);	
			})
			.exceptionally(error -> {
				log.error("Get stock quotes error", error);
				
				asyncResponse.resume(Response
						.status(Status.INTERNAL_SERVER_ERROR)
						.build());
				return null;
			});	
	}	
}
