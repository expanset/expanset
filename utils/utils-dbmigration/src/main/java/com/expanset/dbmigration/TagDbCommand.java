package com.expanset.dbmigration;

import liquibase.Liquibase;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * "Tags" the current database state for future rollback.
 */
public class TagDbCommand extends AbstractDbCommand {

	/**
	 * Command name.
	 */
	public final static String DB_COMMAND = "db-tag";
	
	/**
	 * 'Tags' the current database state for future rollback, required.
	 */
	public final static String TAG_PROPERTY = "tag";
	
	@Override
	protected void registerOptions(Options options) {
		options.addOption(Option.builder(TAG_PROPERTY)
				.argName("tag")
				.hasArg()
				.required()
				.desc("'Tags' the current database state for future rollback.")
				.build());
	}

	@Override
	protected void doComandInternal(CommandLine commandLine, Liquibase liquidbase) 
			throws Exception {
		final String tag = commandLine.getOptionValue(TAG_PROPERTY);
		
        liquidbase.tag(tag);
	}
}
