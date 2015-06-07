package com.expanset.hk2.persistence.config;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.Validate;

/**
 * Register supporting of multi databases configuration.
 * Information about how to configure multi database environment can be read in the
 * {@link com.expanset.hk2.persistence.PersistenceSessionManager} description.
 */
public class MultipleDatabasesPersistenceConfiguratorBinder extends PersistenceConfiguratorBinder {

	protected final String configPrefixesProperty;
	
	protected final String configDefaultPrefixProperty;
		
	/**
	 * @param configPrefixesProperty Configuration file property that contains a list of database settings prefixes.
	 * @param configDefaultPrefixProperty Configuration file property that contains a name of default database prefix.
	 * @param commonProperties Additional properties for the persistence engine.
	 */
	public MultipleDatabasesPersistenceConfiguratorBinder(
			@Nonnull String configPrefixesProperty, 
			@Nullable String configDefaultPrefixProperty,
			@Nullable Map<String, String> commonProperties) {
		super(commonProperties);
		
		Validate.notEmpty(configPrefixesProperty, "configPrefixesProperty");
		
		this.configPrefixesProperty = configPrefixesProperty;
		this.configDefaultPrefixProperty = configDefaultPrefixProperty;
	}

	@Override
	protected void bindSettings() {	
		bind(new MultipleDatabasesPersistenceConfiguratorSettings(
				configPrefixesProperty, configDefaultPrefixProperty, commonProperties))			
			.to(PersistenceConfiguratorSettings.class);
	} 
}
