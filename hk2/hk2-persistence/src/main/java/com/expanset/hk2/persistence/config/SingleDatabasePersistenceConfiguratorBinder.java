package com.expanset.hk2.persistence.config;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.Validate;

/**
 * Register supporting of a single database configuration. It is the simplest configuration.
 * <p>Example below.</p>
 * <p>Configuration file:</p>
 * <pre>
 * db.javax.persistence.jdbc.url=jdbc:h2:db1
 * db.javax.persistence.jdbc.user=username
 * </pre>
 * <p>Single database environment configuration:</p>
 * <pre>
 * install(new JpaPersistenceBinder());
 * install(new LocalTransactionsBinder());
 * install(new SingleDatabasesPersistenceConfiguratorBinder("db", null));
 * bindAsContract(UserDAO.class);
 * </pre>
 * <p>Persistence context injections:</p>
 * <pre>
 * {@literal @}Service
 * {@literal @}Contract
 * {@literal @}PerLookup
 * public class UserDAO {
 *	{@literal @}PersistenceContext(unitName="db")
 *	private EntityManager entityManager;
 * } 
 * </pre>
 * <p>Using sessions:</p>
 * <pre>
 * {@literal @}Inject
 * PersistenceSessionManager sessionManager;
 * try(sessionManager.beginSession()) {
 *	UserDAO userDao =  serviceLocator.getService(UserDAO.class); 
 *	userDao.save(user);
 * };
 * </pre>
 */
public class SingleDatabasePersistenceConfiguratorBinder extends PersistenceConfiguratorBinder {

	protected final String configPrefix;

	/**
	 * @param configPrefix Persistence engine properties prefix.
	 * @param commonProperties Additional properties for the persistence engine.
	 */
	public SingleDatabasePersistenceConfiguratorBinder(
			@Nonnull String configPrefix, 
			@Nullable Map<String, String> commonProperties) {
		super(commonProperties);
		
		Validate.notEmpty(configPrefix, "configPrefix");
		
		this.configPrefix = configPrefix;
	}

	@Override
	protected void bindSettings() {	
		bind(new SingleDatabasePersistenceConfiguratorSettings(configPrefix, commonProperties))			
			.to(PersistenceConfiguratorSettings.class);
	}
}
