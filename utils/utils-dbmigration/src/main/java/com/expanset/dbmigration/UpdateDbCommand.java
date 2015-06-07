package com.expanset.dbmigration;

import liquibase.Liquibase;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * Updates database to current version.
 */
public class UpdateDbCommand extends AbstractDbCommand {

	/**
	 * Command name.
	 */
	public final static String DB_COMMAND = "db-update";
	
	/**
	 * ChangeSet contexts to execute, optional.
	 */
	public final static String CONTEXT_PROPERTY = "context";
	
	/**
	 * Applies the next &lt;value&gt; change sets, optional.
	 */
	public final static String COUNT_PROPERTY = "count";
	
	/**
	 * Writes SQL to update database to current version to STDOUT, optional.
	 */
	public final static String PRINT_SCRIPT_ONLY_PROPERTY = "sql";
	
	/**
	 * Updates the database, then rolls back changes before updating again, optional.
	 */
	public final static String TEST_ROLLBACK_PROPERTY = "testrollback";

	@Override
	protected void registerOptions(Options options) {
		options.addOption(Option.builder(CONTEXT_PROPERTY)
				.argName("value1,value2...")
				.hasArg()
				.desc("ChangeSet contexts to execute.")
				.build());
		OptionGroup optionGroup = new OptionGroup();
		optionGroup.addOption(Option.builder(PRINT_SCRIPT_ONLY_PROPERTY)
				.desc("Writes SQL to update database to current version to STDOUT.")
				.build());
		optionGroup.addOption(Option.builder(TEST_ROLLBACK_PROPERTY)
				.desc("Updates the database, then rolls back changes before updating again.")
				.build());
		options.addOptionGroup(optionGroup);
		
		options.addOption(Option.builder(COUNT_PROPERTY)
				.argName("value")
				.hasArg()
				.desc("Applies the next <value> change sets.")
				.build());
	}

	@Override
	protected void doComandInternal(CommandLine commandLine, Liquibase liquidbase) 
			throws Exception {
		final String context = commandLine.getOptionValue(CONTEXT_PROPERTY, null);
        final Integer count = NumberUtils.createInteger(commandLine.getOptionValue(COUNT_PROPERTY, null));
        final boolean printScriptOnly = commandLine.hasOption(PRINT_SCRIPT_ONLY_PROPERTY);
        final boolean testRollback = commandLine.hasOption(TEST_ROLLBACK_PROPERTY);
        
        if(testRollback) {
        	liquidbase.updateTestingRollback(context);
        } else if (printScriptOnly) {
        	if (count != null) {
        		liquidbase.update(count, context, dbMaintenance.createConsoleOutput());
        	} else {
        		liquidbase.update(context, dbMaintenance.createConsoleOutput());
        	}
        } else {
        	if (count != null) {
        		liquidbase.update(count, context);	
        	} else {
        		liquidbase.update(context);
        	}
        }
	}
}
