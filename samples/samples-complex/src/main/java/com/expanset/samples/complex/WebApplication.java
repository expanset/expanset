package com.expanset.samples.complex;

import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.lang3.StringUtils;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.WebConfig;

import com.expanset.hk2.config.ConfiguredFieldsBinder;
import com.expanset.hk2.logging.ProfilerBinder;
import com.expanset.jersey.assets.AssetsBundle;
import com.expanset.jersey.assets.AssetsBundlesFeature;
import com.expanset.jersey.caching.ClientCachingFeature;
import com.expanset.jersey.errorhandling.ForbiddenExceptionMapper;
import com.expanset.jersey.i18n.I18nFeature;
import com.expanset.jersey.jackson.JacksonFeature;
import com.expanset.jersey.mvc.freemarker.FreemarkerMvcFeature;
import com.expanset.jersey.persistence.jpa.JpaPersistenceFeature;
import com.expanset.jersey.security.CookieAuthenticationFeature;
import com.expanset.jersey.session.SessionFeature;
import com.expanset.jersey.validation.ValidationFeature;
import com.expanset.samples.complex.entities.UserRepository;
import com.expanset.samples.complex.resources.AccountResource;
import com.expanset.samples.complex.resources.HomeResource;
import com.expanset.samples.complex.services.AuthenticationService;
import com.expanset.samples.complex.services.StockQuotesService;

/**
 * Web application configuration and lifecycle methods.
 * <p>Contain methods to start or stop embedded web server.</p>
 * <p>You can use 'main' method and run in console or use jsvc to start daemon.</p>
 */
@ApplicationPath("")
public class WebApplication extends ResourceConfig {

	/**
	 * Property in the configuration file with password to encrypt authentication cookie.
	 */
	public static final String MASTER_PASSWORD_PROPERTY = "masterPassword";

	/**
	 * Web application configuration. 
	 * @param webConfig The Web configuration for accessing initialization parameters.
	 * @throws Exception Configure error.
	 */
	@Inject
	public WebApplication(WebConfig webConfig) 
			throws Exception {

		// Web application name.
		property(ServerProperties.APPLICATION_NAME, "WebApplication");
		// Turn on MBean monitoring feature.
		property(ServerProperties.MONITORING_STATISTICS_MBEANS_ENABLED, true);
		
		// Load application configuration file.
		final PropertiesConfiguration appConfig = new PropertiesConfiguration(
				webConfig.getServletContext().getRealPath("/WEB-INF/config.properties"));
		appConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
		
		// Use program internationalization.
		property(I18nFeature.DEFAULT_LOCALE, "en");
		property(I18nFeature.USE_LOCALE_COOKIE, true);
		property(I18nFeature.RESOURCE_BUNDLE, "/WEB-INF/strings");
		register(I18nFeature.class);		
		
		// Use Freemarker template engine
		property(FreemarkerMvcFeature.ERROR_PAGE, "/error.ftl");
		property(FreemarkerMvcFeature.TEMPLATE_BASE_PATH, "/WEB-INF/views");
		register(FreemarkerMvcFeature.class);
		
		// Redirect to login page if authentication needed.
		property(ForbiddenExceptionMapper.REDIRECT_RESOURCE_CLASS, AccountResource.class);
		property(ForbiddenExceptionMapper.REDIRECT_RESOURCE_METHOD, "loginView");
		register(ForbiddenExceptionMapper.class);
		
		// Use Jackson for JSON.
		register(JacksonFeature.class);		
		
		// Use JPA as persistence engine. Single database configuration.
		property(JpaPersistenceFeature.CONFIG_PREFIX, "db");
		property(JpaPersistenceFeature.DB_BASE_PATH, 
				webConfig.getServletContext().getRealPath("/WEB-INF/db"));
		property(JpaPersistenceFeature.DEFAULT_UNUT_NAME, "main");
		register(JpaPersistenceFeature.class);
		
		// Use authentication feature based on encrypted cookie.
        // NOTE More secure to use EnvironmentStringPBEConfig.
		property(CookieAuthenticationFeature.ENCRYPTOR_PASSWORD, 
				appConfig.getString(MASTER_PASSWORD_PROPERTY));
		register(CookieAuthenticationFeature.class);
				
		// Use sessions to store session bound user data.
		register(SessionFeature.class);
		
		// Validate view models.
		register(ValidationFeature.class);

		// Controls cache on client side.
		register(ClientCachingFeature.class);
		
		// Register static assets.
		property(AssetsBundlesFeature.ASSETS, new AssetsBundle[] { 
				new AssetsBundle("/favicon.ico"),
				new AssetsBundle("/robots.txt"),
				new AssetsBundle("/humans.txt"),
				new AssetsBundle("/assets/*"),
		});
		property(AssetsBundlesFeature.CACHE_CONTROL, "max-age=3600");
		register(AssetsBundlesFeature.class);
		
		// Packages with web request handlers.
		packages(StringUtils.join(new String[] { 
				HomeResource.class.getPackage().getName() }, ';'));
		
		// Register application services.
		register(new AbstractBinder() {
			@Override
			protected void configure() {
				
				// Access to Configuration.
				bind(appConfig).to(Configuration.class);		
				
				// Use method profiling.
				install(new ProfilerBinder());
				
				// Use class fields to get access to the configuration data.
				install(new ConfiguredFieldsBinder());
				
				final DozerBeanMapper beanMapper = new DozerBeanMapper();
				bind(beanMapper).to(Mapper.class);
				
				// NOTE We cannot use addActiveDescriptor because @PersistenceContext is not marked 
				// by @InjectionPointIndicator: https://java.net/projects/hk2/lists/users/archive/2014-05/message/9
				bindAsContract(UserRepository.class).in(PerLookup.class);
				
				addActiveDescriptor(StockQuotesService.class);
				addActiveDescriptor(AuthenticationService.class);
			}
		});
	}
}
