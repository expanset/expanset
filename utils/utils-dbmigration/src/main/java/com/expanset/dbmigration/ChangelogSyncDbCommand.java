package com.expanset.dbmigration;

import liquibase.Liquibase;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * Mark all changes as executed in the database.
 */
public class ChangelogSyncDbCommand extends AbstractDbCommand {

	/**
	 * Command name.
	 */
	public final static String DB_COMMAND = "db-sync";
	
	/**
	 * ChangeSet contexts to execute, optional.
	 */
	public final static String CONTEXT_PROPERTY = "context";
	
	/**
	 * Mark the next change set as executed in the database, optional.
	 */
	public final static String ONLY_NEXT_PROPERTY = "onlynext";
	
	/**
	 * Writes SQL to mark all changes as executed in the database to STDOUT, optional.
	 */
	public final static String PRINT_SCRIPT_ONLY_PROPERTY = "sql";
	
	@Override
	protected void registerOptions(Options options) {
		options.addOption(Option.builder(CONTEXT_PROPERTY)
				.argName("value1,value2...")
				.hasArg()
				.desc("ChangeSet contexts to execute.")
				.build());
		
		options.addOption(Option.builder(ONLY_NEXT_PROPERTY)
				.desc("Mark the next change set as executed in the database.")
				.build());
		options.addOption(Option.builder(PRINT_SCRIPT_ONLY_PROPERTY)
				.desc("Writes SQL to mark all changes as executed in the database to STDOUT.")
				.build());
	}

	@Override
	protected void doComandInternal(CommandLine commandLine, Liquibase liquidbase) 
			throws Exception {
		final String context = commandLine.getOptionValue(CONTEXT_PROPERTY, null);
		final boolean printScriptOnly = commandLine.hasOption(PRINT_SCRIPT_ONLY_PROPERTY);
		final boolean onlyNext = commandLine.hasOption(ONLY_NEXT_PROPERTY);
		
		if(printScriptOnly) {
			if(onlyNext) {
		        liquidbase.changeLogSync(context, dbMaintenance.createConsoleOutput());
			} else {
		        liquidbase.markNextChangeSetRan(context, dbMaintenance.createConsoleOutput());
			}
		} else {
			if(onlyNext) {
		        liquidbase.changeLogSync(context);
			} else {
		        liquidbase.markNextChangeSetRan(context);
			}
		}
	}
}
