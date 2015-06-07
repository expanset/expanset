package com.expanset.samples.complex;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.lang3.StringUtils;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.WebConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expanset.dbmigration.DbMaintenance;
import com.expanset.hk2.config.ConfiguredFieldsBinder;
import com.expanset.hk2.persistence.config.SingleDatabasePersistenceConfiguratorBinder;
import com.expanset.hk2.persistence.jpa.JpaPersistenceBinder;
import com.expanset.hk2.persistence.jpa.JpaPersistenceContextKey;
import com.expanset.jersey.assets.AssetsBundle;
import com.expanset.jersey.assets.AssetsBundlesFeature;
import com.expanset.jersey.caching.ClientCachingFeature;
import com.expanset.jersey.errorhandling.ForbiddenExceptionMapper;
import com.expanset.jersey.freemarker.FreemarkerMvcFeature;
import com.expanset.jersey.i18n.I18nFeature;
import com.expanset.jersey.jackson.JacksonFeature;
import com.expanset.jersey.jetty.EmbeddedJetty;
import com.expanset.jersey.jetty.EmbeddedJettyBinder;
import com.expanset.jersey.persistence.PersistenceFeature;
import com.expanset.jersey.persistence.jpa.JpaPersistenceFeature;
import com.expanset.jersey.security.CookieAuthenticationFeature;
import com.expanset.jersey.session.SessionFeature;
import com.expanset.jersey.validation.ValidationFeature;
import com.expanset.logback.LogbackUtils;
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
	 * Application service locator.
	 */
	private static final ServiceLocator serviceLocator = ServiceLocatorFactory.getInstance().create(null);
	
	private final static Logger log = LoggerFactory.getLogger(WebApplication.class);

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

	/**
	 * Initializes services.
	 * @param args Program arguments.
	 * @throws Exception Start error.
	 */
	public static void init(String[] args) throws Exception {
		// Setup logback logging engine.
		LogbackUtils.install(getResourcePath("/WEB-INF/logback.xml"));

		// Setup embedded web server.
		ServiceLocatorUtilities.bind(serviceLocator, new EmbeddedJettyBinder(
				getResourcePath("."), 
				getResourcePath("/WEB-INF/webserver.xml"),
				WebApplication.class,
				true));		// Use sessions (for SessionFeature).
	}

	/**
	 * Starts the server, accepts incoming connections.
	 * @throws Exception Start error.
	 */
	public static void start() 
			throws Exception {
		log.info("Starting...");
		
		serviceLocator.getService(EmbeddedJetty.class).start();
		
		log.info("Started");
	}
	
	/**
	 * Informs the server to stop.
	 * @throws Exception Stop error.
	 */
	public static void stop() 
			throws Exception {
		log.info("Stopping...");
		
		serviceLocator.getService(EmbeddedJetty.class).stop();
		
		log.info("Stopped");
	}
	
	/**
	 * Destroys any objects created in 'init'.
	 */
	public static void destroy() {
		log.info("Exiting...");
		
		// Closes all services.
		ServiceLocatorFactory.getInstance().destroy(serviceLocator);
		
		log.info("Done");
		
		// Releases logback resources.
		LogbackUtils.uninstall();
	}
	
	private static final CharSequence STOP_COMMAND = "stop";	
	
    public static void main(String[] args) 
    		throws Exception {
    	final String workingdirectory = System.getProperty("user.dir");
    	System.out.println(String.format("Working directory is %s", workingdirectory));
    	
        init(args);
    	
    	if(DbMaintenance.isDbCommandLine(args)) {
    		// Database maintenance operation (migrations, rollbacks etc).
    		
    		// Need to initialize access to database.
    		final Map<String, String> commonProperties = new HashMap<>();
    		commonProperties.put(PersistenceFeature.DB_BASE_PATH_PROPERTY, getResourcePath("/WEB-INF/db"));
    		
    		final DbMaintenance dbMaintenance = new DbMaintenance(
    				// Database connection properties.
    				new PropertiesConfiguration(getResourcePath("/WEB-INF/config.properties")),
    				// We uses JPA.
    				new JpaPersistenceBinder(),
    				// We have single database with settings started as 'db.'.
    				new SingleDatabasePersistenceConfiguratorBinder("db", commonProperties), 
    				// Liquibase changeset file.
    				"db/main.xml");
    		// Setup unit name and starts database command from command line.
    		dbMaintenance.Do(args, new JpaPersistenceContextKey("main"));
    		
    		// Do not start web site, only do database maintenance.
    		return;
    	}
    	
    	System.out.println("Starting...");
    	
        try {
        	start();
        
        	System.out.printf("Started, to stop print '%s'", STOP_COMMAND);
        	System.out.println();
        	
        	while(true) {
            	BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        		String line = reader.readLine();
        		if(StringUtils.equalsIgnoreCase(line, STOP_COMMAND)) {
        			break;
        		}
        	}
        
        	System.out.println("Stopping...");
        	
        	stop();
        } finally {
        	destroy();	
        }
        
        System.out.println("Stopped");
    }
    
	/**
	 * Returns full path to the required resource. Working directory will be used. 
	 * @param resourcePath Relative resource path.
	 * @return Full path to the required resource.
	 */
	private static String getResourcePath(String resourcePath) {
		return Paths.get(".", resourcePath).toAbsolutePath().toString();
	} 
}
