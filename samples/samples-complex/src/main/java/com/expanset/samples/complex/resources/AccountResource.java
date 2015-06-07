package com.expanset.samples.complex.resources;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.glassfish.jersey.server.mvc.Template;
import org.glassfish.jersey.server.mvc.Viewable;

import com.expanset.hk2.config.ConfiguredInteger;
import com.expanset.hk2.security.AuthenticationManager;
import com.expanset.jersey.RememberOptionsInCookie;
import com.expanset.jersey.caching.ClientCacheMaxAge;
import com.expanset.jersey.caching.ClientNoCache;
import com.expanset.jersey.mvc.templates.PopulateTemplateWith;
import com.expanset.jersey.utils.HttpUtils;
import com.expanset.jersey.validation.ValidationResult;
import com.expanset.samples.complex.entities.EntityExistsException;
import com.expanset.samples.complex.entities.User;
import com.expanset.samples.complex.entities.UserRepository;
import com.expanset.samples.complex.services.AuthenticationService;
import com.expanset.samples.complex.services.RegisteredViewPopulator;
import com.expanset.samples.complex.services.SitePrincipal;
import com.expanset.samples.complex.viewmodels.LoginUserViewModel;
import com.expanset.samples.complex.viewmodels.ProfileViewModel;
import com.expanset.samples.complex.viewmodels.RegisterUserViewModel;

@ClientNoCache
@Path("/account")
public class AccountResource {
	
	@Inject
	private UserRepository userRepository;

	@Inject
	private AuthenticationService authenticationService;

	@Inject
	private AuthenticationManager authenticationManager;	
	
	@Inject
	private SecurityContext securityContext;	
	
	@Inject
	private ResourceBundle resources;
	
	@Inject
	private Mapper mapper;
	
	@ConfiguredInteger("authenticationMaxAge")
	private int authenticationMaxAge;
	
	@GET
	@Path("/register")
	@Produces(MediaType.TEXT_HTML)
	@ClientCacheMaxAge(time=10, unit=TimeUnit.SECONDS)
	public Viewable registerView() {
		return new Viewable("/register.ftl");
	}	

	@POST
	@Path("/register")
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Template(name="/register.ftl")
	public Object register(
			@BeanParam @Valid RegisterUserViewModel model, 
			@Context ValidationResult result) 
					throws Exception {
		if(result.isSuccess()) {
			try {
				final User user = mapper.map(model, User.class);
				userRepository.createUser(user, model.getPassword());
				
				authenticationManager.saveAuthentication(
						authenticationService.generateAuthenticationToken(user),
						new RememberOptionsInCookie(authenticationMaxAge));
			} catch (EntityExistsException e) {
				result.addError("login", resources.getString("userAlreadyExists"));
				return model;	
			}

			return HttpUtils.seeOther(HomeResource.class).build();
		}
		
		return model;
	}	
	
	@GET
	@Path("/login")
	@Produces(MediaType.TEXT_HTML)
	@Template(name="/login.ftl")
	public Map<String, Object> loginView(@QueryParam("return") String returnUrl) {
		final Map<String, Object> model = new HashMap<>();
		model.put("returnUrl", returnUrl);
		return model;
	}		

	@POST
	@Path("/login")
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Template(name="/login.ftl")
	public Object login(
			@BeanParam @Valid LoginUserViewModel model, 
			@Context ValidationResult result) 
					throws Exception {
		if(result.isSuccess()) {
			final Optional<User> user = authenticationService.authenticate(model.getLogin(), model.getPassword());
			if(user.isPresent()) {
				authenticationManager.saveAuthentication(
						authenticationService.generateAuthenticationToken(user.get()),
						new RememberOptionsInCookie(authenticationMaxAge));
				if(StringUtils.isNotEmpty(model.getReturnUrl())) {
					final URI returnUrl = new URI(model.getReturnUrl());
					return Response.seeOther(returnUrl).build();
				}
				
				return HttpUtils.seeOther(HomeResource.class).build();
			}

			result.addError("authenticate", resources.getString("invalidLoginOrPassword"));
		}
		
		return model;
	}
	
	@GET
	@Path("/logout")	
	public Response logout(@Context HttpSession session) 
			throws Exception {
		session.invalidate();
		authenticationManager.removeAuthentication(null);
		
		return HttpUtils.seeOther(HomeResource.class).build();
	}

	@GET
	@Path("/profile")	
	@Produces(MediaType.TEXT_HTML)
	@RolesAllowed(value=SitePrincipal.ROLE_USER)
	@PopulateTemplateWith(RegisteredViewPopulator.class)
	public Viewable profileView() 
			throws Exception {
		return new Viewable("/profile.ftl");
	}
	
	@POST
	@Path("/profile")
	@Template(name="/profile.ftl")
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@RolesAllowed(value=SitePrincipal.ROLE_USER)
	@PopulateTemplateWith(RegisteredViewPopulator.class)
	public Object profile(
			@BeanParam @Valid ProfileViewModel model, 
			@Context ValidationResult result) 
					throws Exception {
		if(result.isSuccess()) {
			final User user = userRepository.updateUser(
					((SitePrincipal)securityContext.getUserPrincipal()).getId(),
					model.getPassword());
			
			// Need because password is changed.
			authenticationManager.saveAuthentication(
					authenticationService.generateAuthenticationToken(user),
					new RememberOptionsInCookie(authenticationMaxAge));

			return HttpUtils.seeOther(AccountResource.class, "profile").build();
		}
		
		return model;
	}		
}
