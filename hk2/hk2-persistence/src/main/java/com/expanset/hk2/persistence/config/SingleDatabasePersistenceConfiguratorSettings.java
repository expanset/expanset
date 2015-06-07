package com.expanset.hk2.persistence.config;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.jvnet.hk2.annotations.Service;

import com.expanset.common.ConfigurationUtils;

/**
 * Implementation of {@link PersistenceConfiguratorSettings} for a single database environment.
 */
@Service
public class SingleDatabasePersistenceConfiguratorSettings extends PersistenceConfiguratorSettings {

	protected final String configPrefix;
	
	public SingleDatabasePersistenceConfiguratorSettings(
			@Nonnull String configPrefix, 
			@Nullable Map<String, String> commonProperties) {
		super(commonProperties);
		
		Validate.notEmpty(configPrefix, "configPrefix");
				
		this.configPrefix = configPrefix;
	}

	@Override
	public Map<String, Map<String, String>> geConfiguration(Configuration config) {
		Validate.notNull(config, "config");
		
		final Map<String, Map<String, String>> result = new HashMap<>();
		result.put(StringUtils.EMPTY, ConfigurationUtils.getMap((config.subset(configPrefix))));

		return result;
	}
}
