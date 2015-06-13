package com.expanset.samples.getstart;

import javax.inject.Inject;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.WebConfig;
import com.expanset.hk2.config.ConfiguredFieldsBinder;
import com.expanset.jersey.assets.AssetsBundle;
import com.expanset.jersey.assets.AssetsBundlesFeature;
import com.expanset.jersey.freemarker.FreemarkerMvcFeature;
import com.expanset.jersey.validation.ValidationFeature;

/**
 * Web application configuration.
 */
public class AppConfig extends ResourceConfig {

	/**
	 * @param webConfig The Web configuration for accessing initialization parameters.
	 * @throws Exception Configuration error.
	 */
	@Inject
	public AppConfig(WebConfig webConfig) 
			throws Exception {

		// Load application configuration file.
		final PropertiesConfiguration appConfig = new PropertiesConfiguration(
				webConfig.getServletContext().getRealPath("/WEB-INF/config.properties"));
		appConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
		
		// Use Freemarker template engine
		property(FreemarkerMvcFeature.ERROR_PAGE, "/error.ftl");
		property(FreemarkerMvcFeature.TEMPLATE_BASE_PATH, "/WEB-INF/views");
		register(FreemarkerMvcFeature.class);

		// Validate view models.
		register(ValidationFeature.class);
		
		// Register static assets.
		property(AssetsBundlesFeature.ASSETS, new AssetsBundle[] { 
				new AssetsBundle("/assets/*"),
		});
		property(AssetsBundlesFeature.CACHE_CONTROL, "max-age=3600"); // Use client caching.
		register(AssetsBundlesFeature.class);
		
		// Packages with web request handlers.
		packages(StringUtils.join(new String[] { 
				HomeResource.class.getPackage().getName() }, ';'));
		
		// Register application services.
		register(new AbstractBinder() {
			@Override
			protected void configure() {
				
				// Enable access to application configuration.
				bind(appConfig).to(Configuration.class);		
				
				// Use class fields to get access to the configuration data.
				install(new ConfiguredFieldsBinder());
			}
		});
	}
}
