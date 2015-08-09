package com.expanset.samples.complex;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expanset.dbmigration.DbMaintenance;
import com.expanset.hk2.persistence.config.SingleDatabasePersistenceConfiguratorSettings;
import com.expanset.hk2.persistence.jpa.JpaPersistenceBinder;
import com.expanset.hk2.persistence.jpa.JpaPersistenceContextKey;
import com.expanset.jersey.jetty.EmbeddedJetty;
import com.expanset.jersey.jetty.EmbeddedJettyBinder;
import com.expanset.jersey.persistence.PersistenceFeature;
import com.expanset.logback.LogbackUtils;

/**
 * Project entry point.
 */
public class Startup {
	
	/**
	 * Application service locator.
	 */
	private static final ServiceLocator serviceLocator = ServiceLocatorFactory.getInstance().create(null);
	
	private final static Logger log = LoggerFactory.getLogger(WebApplication.class);
	
	/**
	 * Initializes services.
	 * @param args Program arguments.
	 * @throws Exception Start error.
	 */
	public void init(String[] args) throws Exception {
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
	public void start() 
			throws Exception {
		log.info("Starting...");
		
		serviceLocator.getService(EmbeddedJetty.class).start();
		
		log.info("Started");
	}
	
	/**
	 * Informs the server to stop.
	 * @throws Exception Stop error.
	 */
	public void stop() 
			throws Exception {
		log.info("Stopping...");
		
		serviceLocator.getService(EmbeddedJetty.class).stop();
		
		log.info("Stopped");
	}
	
	/**
	 * Destroys any objects created in 'init'.
	 */
	public void destroy() {
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
    	
    	Startup startup = new Startup();
    	
    	startup.init(args);
    	
		// Database maintenance operation (migrations, rollbacks etc).
    		
		// Need to initialize access to database.
		final Map<String, String> commonProperties = new HashMap<>();
		commonProperties.put(PersistenceFeature.DB_BASE_PATH_PROPERTY, getResourcePath("/WEB-INF/db"));
		
		final SingleDatabasePersistenceConfiguratorSettings settings = 
				new SingleDatabasePersistenceConfiguratorSettings("db");
		settings.setCommonProperties(commonProperties);
		
		final DbMaintenance dbMaintenance = new DbMaintenance(
				// Database connection properties.
				new PropertiesConfiguration(getResourcePath("/WEB-INF/config.properties")),
				// We uses JPA.
				new JpaPersistenceBinder(),
				// We have single database with settings started as 'db.'.
				settings, 
				// Liquibase changeset file.
				"db/main.xml");
    	if(DbMaintenance.isDbCommandLine(args)) {
    		// Setup unit name and starts database command from command line.
    		dbMaintenance.Do(args, new JpaPersistenceContextKey("main"));
		
    		// Do not start web site, only do database maintenance.
    		return;
    	} else {
    		// Do database update.
    		dbMaintenance.Do(new String[] {"db-update" }, new JpaPersistenceContextKey("main"));    		
    	}
    	
    	System.out.println("Starting...");
    	
        try {
        	startup.start();
        
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
        	
        	startup.stop();
        } finally {
        	startup.destroy();	
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
