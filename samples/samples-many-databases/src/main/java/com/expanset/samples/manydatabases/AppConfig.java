package com.expanset.samples.manydatabases;

import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spi.Container;
import org.glassfish.jersey.server.spi.ContainerLifecycleListener;
import org.glassfish.jersey.servlet.WebConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expanset.dbmigration.DbMaintenance;
import com.expanset.hk2.persistence.PersistenceContextFactoryKey;
import com.expanset.hk2.persistence.PersistenceContextKey;
import com.expanset.jersey.mvc.freemarker.FreemarkerMvcFeature;
import com.expanset.jersey.persistence.ormlite.OrmlitePersistenceFeature;
import com.expanset.logback.LogbackUtils;

/**
 * Web application configuration.
 */
@ApplicationPath("")
public class AppConfig extends ResourceConfig {

	/**
	 * @param webConfig The Web configuration for accessing initialization parameters.
	 * @param serviceLocator Services. 
	 * @throws Exception Configuration error.
	 */
	@Inject
	public AppConfig(final WebConfig webConfig, final ServiceLocator serviceLocator) 
			throws Exception {
		// Setup logback logging engine.
		LogbackUtils.install(webConfig.getServletContext().getRealPath("/WEB-INF/logback.xml"));		

		// Load application configuration file.
		final PropertiesConfiguration appConfig = new PropertiesConfiguration(
				webConfig.getServletContext().getRealPath("/WEB-INF/config.properties"));
		appConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
		
		// Use Ormlite as persistence engine. Many databases configuration.
		property(OrmlitePersistenceFeature.CONFIG_PREFIXES_PROPERTY, "dbPrefixes");
		property(OrmlitePersistenceFeature.CONFIG_DEFAULT_PREFIX, "db01");
		property(OrmlitePersistenceFeature.DB_BASE_PATH, 
				webConfig.getServletContext().getRealPath("/WEB-INF/db"));
		register(OrmlitePersistenceFeature.class);		
		
		// Use Freemarker template engine
		property(FreemarkerMvcFeature.ERROR_PAGE, "/error.ftl");
		property(FreemarkerMvcFeature.TEMPLATE_BASE_PATH, "/WEB-INF/views");
		register(FreemarkerMvcFeature.class);
				
		// Packages with web request handlers.
		packages(StringUtils.join(new String[] { 
				HomeResource.class.getPackage().getName() }, ';'));
		
		// Register application services.
		register(new AbstractBinder() {
			@Override
			protected void configure() {
				
				// Enable access to application configuration.
				bind(appConfig).to(Configuration.class);		
				
				addActiveDescriptor(DaoService.class);
			}
		});
		
		// Listening container events.
		registerInstances(new ContainerLifecycleListener() {
			
			private final Logger log = LoggerFactory.getLogger(AppConfig.class);

			@Override
			public void onStartup(Container conainer) {
				try {
		    		// Databases migration.
					
					final DbMaintenance dbMaintenance = new DbMaintenance(serviceLocator, null);
		    		
		    		// Migrate first database.
		    		dbMaintenance.Do(
		    				new String[] {"db-update", "-changelog", "db01.xml" }, 
		    				new PersistenceContextKey(new PersistenceContextFactoryKey("db01")));
		    		// Migrate second database.
		    		dbMaintenance.Do(
		    				new String[] {"db-update", "-changelog", "db02.xml" }, 
		    				new PersistenceContextKey(new PersistenceContextFactoryKey("db02")));
				} 
				catch (Throwable e) {
					log.error("Db migration error", e);
				}
			}

			@Override
			public void onShutdown(Container conainer) {
				LogbackUtils.uninstall();
			}
			
			@Override
			public void onReload(Container conainer) {
			}
		});
	}
}
