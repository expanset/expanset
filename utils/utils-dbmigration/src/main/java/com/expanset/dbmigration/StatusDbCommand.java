package com.expanset.dbmigration;

import liquibase.Liquibase;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * Outputs count of unrun change sets.
 */
public class StatusDbCommand extends AbstractDbCommand {

	/**
	 * Command name.
	 */
	public final static String DB_COMMAND = "db-status";
	
	/**
	 * ChangeSet contexts to execute, optional.
	 */
	public final static String CONTEXT_PROPERTY = "context";
	
	@Override
	protected void registerOptions(Options options) {
		options.addOption(Option.builder(CONTEXT_PROPERTY)
				.argName("value1,value2...")
				.hasArg()
				.desc("ChangeSet contexts to execute.")
				.build());		
	}

	@Override
	protected void doComandInternal(CommandLine commandLine, Liquibase liquidbase) 
			throws Exception {
		final String context = commandLine.getOptionValue(CONTEXT_PROPERTY, null);
		
        liquidbase.reportStatus(true, context, dbMaintenance.createConsoleOutput());
	}
}
