package com.expanset.hk2.persistence.config;

import java.util.Map;

import javax.annotation.Nullable;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

/**
 * Simple persistence configuration.
 */
public class PersistenceConfiguratorBinder extends AbstractBinder {

	protected final Map<String, String> commonProperties;	
	
	/**
	 * @param commonProperties Additional properties for the persistence engine.
	 */
	public PersistenceConfiguratorBinder(
			@Nullable Map<String, String> commonProperties) {
		
		this.commonProperties = commonProperties;
	}

	@Override
	protected void configure() {
		bindSettings();
		
		addActiveDescriptor(PersistenceConfigurator.class);
	}
	
	protected void bindSettings() {
		bind(new PersistenceConfiguratorSettings(commonProperties))			
			.to(PersistenceConfiguratorSettings.class);
	}
}
