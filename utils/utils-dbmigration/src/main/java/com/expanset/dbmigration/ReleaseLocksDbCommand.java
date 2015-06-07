package com.expanset.dbmigration;

import liquibase.Liquibase;

import org.apache.commons.cli.CommandLine;

/**
 * Releases all locks on the database changelog.
 */
public class ReleaseLocksDbCommand extends AbstractDbCommand {

	/**
	 * Command name.
	 */
	public final static String DB_COMMAND = "db-releaselocks";
	
	@Override
	protected void doComandInternal(CommandLine commandLine, Liquibase liquidbase) 
			throws Exception {		
        liquidbase.forceReleaseLocks();
	}
}
