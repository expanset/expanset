package com.expanset.dbmigration;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import com.expanset.hk2.persistence.PersistenceBinder;
import com.expanset.hk2.persistence.PersistenceContextKey;
import com.expanset.hk2.persistence.ThreadScopePersistenceSessionManager;
import com.expanset.hk2.persistence.config.PersistenceConfigurator;
import com.expanset.hk2.persistence.config.PersistenceConfiguratorSettings;
import com.expanset.hk2.persistence.transactions.LocalTransactionsBinder;

/**
 * Parses command line and execute commands for database maintenance.
 */
public class DbMaintenance {

	/**
	 * Command name to print help. Use: &lt;db-command&gt; help.
	 */
	public final static String HELP_COMMAND = "help";

	public final static Map<String, Class<? extends AbstractDbCommand>> COMMANDS = new HashMap<>();
	
	protected final ServiceLocator serviceLocator;	
	
	protected final String changeLogFile;
	
	/**
	 * @param config Configuration with database connection settings.
	 * @param persistenceBinder Configurator for database connection access. 
	 * @param persistenceConfiguratorSettings Configurator for setup database connection access.
	 * @param changeLogFile Name of changelog file (in classpath), optional.
	 */
	public DbMaintenance(
			@Nonnull Configuration config, 
			@Nonnull PersistenceBinder persistenceBinder, 
			@Nonnull PersistenceConfiguratorSettings persistenceConfiguratorSettings,
			@Nullable String changeLogFile) {
		Validate.notNull(config, "config");
		Validate.notNull(persistenceBinder, "persistenceBinder");
		Validate.notNull(persistenceConfiguratorSettings, "persistenceConfiguratorSettings");
		
		this.changeLogFile = changeLogFile;
		
		this.serviceLocator = ServiceLocatorFactory.getInstance().create(null);
		ServiceLocatorUtilities.bind(serviceLocator, new AbstractBinder() {
			@Override
			protected void configure() {
				install(persistenceBinder);
				install(new LocalTransactionsBinder());
				install(new DbMaintenanceBinder());
				
				bind(config).to(Configuration.class);
				bind(DbMaintenance.this).to(DbMaintenance.class);
				bind(persistenceConfiguratorSettings).to(PersistenceConfiguratorSettings.class);
				
				addActiveDescriptor(PersistenceConfigurator.class);
				addActiveDescriptor(ThreadScopePersistenceSessionManager.class);
			}
		});
	}

	/**
	 * @param serviceLocator Initialized services.
	 * @param changeLogFile Name of changelog file (in classpath), optional.
	 */
	public DbMaintenance(
			@Nonnull ServiceLocator serviceLocator,
			@Nullable String changeLogFile) {
		Validate.notNull(serviceLocator, "serviceLocator");
		
		this.serviceLocator = serviceLocator;
		this.changeLogFile = changeLogFile;
		
		ServiceLocatorUtilities.bind(serviceLocator, new AbstractBinder() {
			@Override
			protected void configure() {
				install(new DbMaintenanceBinder());
				
				bind(DbMaintenance.this).to(DbMaintenance.class);
			}
		});
	}
	
	static {
		COMMANDS.put(UpdateDbCommand.DB_COMMAND, UpdateDbCommand.class);
		COMMANDS.put(RollbackDbCommand.DB_COMMAND, RollbackDbCommand.class);
		COMMANDS.put(TagDbCommand.DB_COMMAND, TagDbCommand.class);
		COMMANDS.put(StatusDbCommand.DB_COMMAND, StatusDbCommand.class);
		COMMANDS.put(ChangelogSyncDbCommand.DB_COMMAND, ChangelogSyncDbCommand.class);
		COMMANDS.put(ReleaseLocksDbCommand.DB_COMMAND, ReleaseLocksDbCommand.class);
		COMMANDS.put(ClearCheckSumsDbCommand.DB_COMMAND, ClearCheckSumsDbCommand.class);
	}
	
	/**
	 * @return Name of changelog file (in classpath).
	 */
	public String getChangeLogFile() {
		return changeLogFile;
	}
	
	/**
	 * Test command line for database commands existing.
	 * @param args Command line.
	 * @return true - it is command line for database maintenance.
	 */
	public static boolean isDbCommandLine(@Nullable String[] args) {
		return args != null 
				&& args.length > 0 
				&& COMMANDS.containsKey(args[0]);
	}

	/**
	 * Executes database command.
	 * @param args Command line.
	 * @param key Key to search database settings.
	 * @throws Exception Error when execute command.
	 */
	public void Do(@Nonnull String[] args, @Nonnull PersistenceContextKey key) 
			throws Exception {
		if(!isDbCommandLine(args)) {
			throw new IllegalArgumentException("Illegal command line for db maintenance, db command not found");
		}
		
		Validate.notNull(key, "key");
				
		final AbstractDbCommand dbCommand = serviceLocator.getService(AbstractDbCommand.class, args[0]);
		
		if(args.length > 1 && StringUtils.equalsIgnoreCase(args[1], HELP_COMMAND)) {
			final HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(
					new PrintWriter(createConsoleOutput()), 
					HelpFormatter.DEFAULT_WIDTH,
					null,
					"java -jar <path-to-jar> " + args[0],
					dbCommand.getOptions(),
					HelpFormatter.DEFAULT_LEFT_PAD,
					HelpFormatter.DEFAULT_DESC_PAD,
					null,
					true);
		} else {
			final CommandLineParser parser = new DefaultParser();
			final CommandLine commandLine = parser.parse(
					dbCommand.getOptions(), 
					Arrays.stream(args).skip(1).toArray(String[]::new));
			
			dbCommand.doCommand(commandLine, key);
		}
	}
	
	public OutputStreamWriter createConsoleOutput() 
			throws Exception {
		return new OutputStreamWriter(System.out, "utf-8");
	}	
}
