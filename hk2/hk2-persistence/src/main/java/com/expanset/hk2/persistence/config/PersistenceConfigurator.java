package com.expanset.hk2.persistence.config;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.commons.configuration.Configuration;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.InstanceLifecycleEvent;
import org.glassfish.hk2.api.InstanceLifecycleEventType;
import org.glassfish.hk2.api.InstanceLifecycleListener;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.jvnet.hk2.annotations.Service;

import com.expanset.hk2.config.ConfigurationReloadListener;
import com.expanset.hk2.persistence.PersistenceContextFactoryAccessor;

/**
 * Configure persistence environment based on properties in the configuration file.
 */
@Service
public class PersistenceConfigurator implements 
	InstanceLifecycleListener, ConfigurationReloadListener {

	@Inject
	protected PersistenceConfiguratorSettings settings;
	
	@Inject
	protected Provider<PersistenceContextFactoryAccessor> factoryAccessorProvider;

	@Inject
	protected Configuration config;
	
	@Override
	public Filter getFilter() {
		return BuilderHelper.createContractFilter(PersistenceContextFactoryAccessor.class.getName());
	}

	@Override
	public void lifecycleEvent(InstanceLifecycleEvent lifecycleEvent) {
		if(lifecycleEvent.getEventType() == InstanceLifecycleEventType.POST_PRODUCTION) {
			configure((PersistenceContextFactoryAccessor)lifecycleEvent.getLifecycleObject());
		}
	}

	@Override
	public void configurationReloaded() {
		configure(factoryAccessorProvider.get());
	}
	
	protected void configure(@Nonnull PersistenceContextFactoryAccessor factoryAccessor) {
		assert factoryAccessor != null;
				
		factoryAccessor.resetFactoriesProperties(
				settings.geConfiguration(config), 
				settings.getCommonProperties());	
	}
}
