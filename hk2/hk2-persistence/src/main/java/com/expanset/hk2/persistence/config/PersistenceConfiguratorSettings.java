package com.expanset.hk2.persistence.config;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.configuration.Configuration;
import org.jvnet.hk2.annotations.Contract;
import org.jvnet.hk2.annotations.Service;

/**
 * Base class for settings for a single or multi database environment. 
 */
@Service
@Contract
public class PersistenceConfiguratorSettings {
	
	protected final Map<String, String> commonProperties;
	
	/**
	 * @param commonProperties Additional properties for the persistence engine.
	 */
	public PersistenceConfiguratorSettings(@Nullable Map<String, String> commonProperties) {
		this.commonProperties = commonProperties;
	}

	/**
	 * @return Additional properties for the persistence engine.
	 */
	public Map<String, String> getCommonProperties() {
		return commonProperties;
	}

	public Map<String, Map<String, String>> geConfiguration(@Nonnull Configuration config) {
		return null;
	}
}
