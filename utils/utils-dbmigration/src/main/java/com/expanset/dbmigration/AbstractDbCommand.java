package com.expanset.dbmigration;

import java.util.Map;
import java.util.Properties;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import com.expanset.hk2.persistence.ConnectionProvider;
import com.expanset.hk2.persistence.PersistenceContextKey;
import com.expanset.hk2.persistence.PersistenceSessionManager;

/**
 * Base class for database maintenance commands.
 */
public abstract class AbstractDbCommand {

	/**
	 * The changelog file (in classpath) to use, required.
	 */
	public final static String CHANGELOG_PROPERTY = "changelog";
	
	/**
	 * Default database catalog name, optional.
	 */
	public final static String CATALOG_PROPERTY = "catalog";
	
	/**
	 * Default database schema name, optional.
	 */
	public final static String SCHEMA_PROPERTY = "schema";
	
	/**
	 * Changelog variables, optional.
	 */
	public final static String CHANGELOG_PROPERTIES = "P";
	
	@Inject
	protected DbMaintenance dbMaintenance;
	
	@Inject
	protected PersistenceSessionManager sessionManager;
	
	/**
	 * @return list of command options.
	 */
	public Options getOptions() {
		Options options = new Options();
		if(StringUtils.isEmpty(dbMaintenance.getChangeLogFile())) {
			options.addOption(Option.builder(CHANGELOG_PROPERTY)
					.argName("value")
					.hasArg()
					.required()
					.desc("The changelog file (in classpath) to use, required.")
					.build());			
		}
		options.addOption(Option.builder(CATALOG_PROPERTY)
				.argName("value")
				.hasArg()
				.desc("Default database catalog name, optional.")
				.build());
		options.addOption(Option.builder(SCHEMA_PROPERTY)
				.argName("value")
				.hasArg()
				.desc("Default database schema name, optional.")
				.build());		
		options.addOption(Option.builder(CHANGELOG_PROPERTIES)
				.argName("property=value")
				.numberOfArgs(2)
				.valueSeparator()
				.desc("Changelog variables.")
				.build());		
		registerOptions(options);
		return options;
	}

	/**
	 * Executes database command.
	 * @param commandLine Parsed options from command line.
	 * @param key Key to search database settings.
	 * @throws Exception Error when command execution.
	 */
	public void doCommand(@Nonnull CommandLine commandLine, @Nonnull PersistenceContextKey key) 
			throws Exception {
		Validate.notNull(commandLine, "commandLine");
		Validate.notNull(key, "key");
		
		try(
				final AutoCloseable scope = 
						sessionManager.beginSession();
				final ConnectionProvider connectionProvider = 
						sessionManager.getPersistenceContext(key, ConnectionProvider.class, null)) {
			final Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(
					new JdbcConnection(connectionProvider.provide()) {
						@Override
						public void close() {		
							// Do not close connection here.
						}
					});
			String changeLogFile = commandLine.getOptionValue(CHANGELOG_PROPERTY);
			if(StringUtils.isEmpty(changeLogFile)) {
				changeLogFile = dbMaintenance.getChangeLogFile();
			}
			final String catalogName = commandLine.getOptionValue(CATALOG_PROPERTY, null);
			if(database.supportsCatalogs() && StringUtils.isNotEmpty(catalogName)) {
				database.setDefaultCatalogName(catalogName);
				database.setOutputDefaultCatalog(true);
			}				
			final String schemaName = commandLine.getOptionValue(SCHEMA_PROPERTY, null);
			if(database.supportsSchemas() && StringUtils.isNotEmpty(schemaName)) {
				database.setDefaultSchemaName(schemaName);
				database.setOutputDefaultSchema(true);
			}				
			
			try {
				final Liquibase liquibase = new Liquibase(
						changeLogFile, 
						new ClassLoaderResourceAccessor(), 
						database);
				
				final Properties expressionVars = commandLine.getOptionProperties(CHANGELOG_PROPERTIES);
				if (expressionVars != null) {
	                for (Map.Entry<Object, Object> var : expressionVars.entrySet()) {
	                    liquibase.setChangeLogParameter(var.getKey().toString(), var.getValue());
	                }
	            }
				
				doComandInternal(commandLine, liquibase);
			} finally {
				database.close();
			}
		}
	}

	protected void registerOptions(@Nonnull Options options) {
	}	
	
	protected abstract void doComandInternal(@Nonnull CommandLine commandLine, @Nonnull Liquibase liquidbase)
			throws Exception;
}
