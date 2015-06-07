package com.expanset.dbmigration;

import liquibase.Liquibase;

import org.apache.commons.cli.CommandLine;

/**
 * Removes current checksums from database. On next run checksums will be recomputed.
 */
public class ClearCheckSumsDbCommand extends AbstractDbCommand {

	/**
	 * Command name.
	 */
	public final static String DB_COMMAND = "db-clearchecksums";
	
	@Override
	protected void doComandInternal(CommandLine commandLine, Liquibase liquidbase) 
			throws Exception {		
        liquidbase.clearCheckSums();
	}
}
