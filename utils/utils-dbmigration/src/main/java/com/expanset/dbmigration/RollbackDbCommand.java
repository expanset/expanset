package com.expanset.dbmigration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import liquibase.Liquibase;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * Rollback the database to the state it was in at the given point.
 */
public class RollbackDbCommand extends AbstractDbCommand {

	/**
	 * Command name.
	 */
	public final static String DB_COMMAND = "db-rollback";
	
	/**
	 * ChangeSet contexts to execute, optional.
	 */
	public final static String CONTEXT_PROPERTY = "context";

	/**
	 * Rollback the database to the state it was in when the tag was applied.
	 */
	public final static String TAG_PROPERTY = "tag";

	/**
	 * Rollback the database to the state it was in at the given date.
	 */
	public final static String DATE_PROPERTY = "date";
	
	/**
	 * Rollback the last &lt;value&gt; change sets, optional.
	 */
	public final static String COUNT_PROPERTY = "count";
	
	/**
	 * Writes SQL to rollback the database to the current state after the changes in the changeslog have been applied, optional.
	 */
	public final static String FUTURE_SQL_PROPERTY = "future";
			
	/**
	 * Writes SQL to rollback database to STDOUT, optional.
	 */
	public final static String PRINT_SCRIPT_ONLY_PROPERTY = "sql";

	@Override
	protected void registerOptions(Options options) {
		final DateFormat format = DateFormat.getDateInstance();

		options.addOption(Option.builder(CONTEXT_PROPERTY)
				.argName("value1,value2...")
				.hasArg()
				.desc("ChangeSet contexts to execute.")
				.build());
		options.addOption(Option.builder(PRINT_SCRIPT_ONLY_PROPERTY)
				.desc("Writes SQL to rollback database to STDOUT.")
				.build());
		
		final OptionGroup optionGroup = new OptionGroup();
		optionGroup.addOption(Option.builder(TAG_PROPERTY)
				.argName("tag")
				.hasArg()
				.desc("Rollback the database to the state it was in when the tag was applied.")
				.build());
		optionGroup.addOption(Option.builder(DATE_PROPERTY)
				.argName(((SimpleDateFormat)format).toPattern())
				.hasArg()
				.desc("Rollback the database to the state it was in at the given date.")
				.build());		
		optionGroup.addOption(Option.builder(COUNT_PROPERTY)
				.argName("value")
				.hasArg()
				.desc("Rolls back the last <value> change sets.")
				.build());		
		optionGroup.addOption(Option.builder(FUTURE_SQL_PROPERTY)
				.desc("Writes SQL to rollback the database to the current state after the changes in the changeslog have been applied.")
				.build());		
		options.addOptionGroup(optionGroup);
	}

	@Override
	protected void doComandInternal(CommandLine commandLine, Liquibase liquidbase) 
			throws Exception {
        final DateFormat format = DateFormat.getDateInstance();
        final String context = commandLine.getOptionValue(CONTEXT_PROPERTY, null);
        final String tag = commandLine.getOptionValue(TAG_PROPERTY, null);
        final String date = commandLine.getOptionValue(DATE_PROPERTY, null);
        final Integer count = NumberUtils.createInteger(commandLine.getOptionValue(COUNT_PROPERTY, null));
        final boolean printScriptOnly = commandLine.hasOption(PRINT_SCRIPT_ONLY_PROPERTY);
                
        if (printScriptOnly) {
        	if (StringUtils.isNotEmpty(tag)) {
        		liquidbase.rollback(tag, context, dbMaintenance.createConsoleOutput());
        	} else if (StringUtils.isNotEmpty(date)) {
        		liquidbase.rollback(format.parse(date), context, dbMaintenance.createConsoleOutput());
        	} else if (count != null) {
        		liquidbase.rollback(count, context, dbMaintenance.createConsoleOutput());
        	} else {
        		liquidbase.futureRollbackSQL(context, dbMaintenance.createConsoleOutput());
        	}
        } else {
        	if (StringUtils.isNotEmpty(tag)) {
        		liquidbase.rollback(tag, context);
        	} else if (StringUtils.isNotEmpty(date)) {
        		liquidbase.rollback(format.parse(date), context);
        	} else if (count != null) {
        		liquidbase.rollback(count, context);
        	} else {
        		liquidbase.futureRollbackSQL(context, dbMaintenance.createConsoleOutput());
        	}
        }
	}
}
