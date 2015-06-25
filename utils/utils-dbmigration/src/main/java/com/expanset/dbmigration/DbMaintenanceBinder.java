package com.expanset.dbmigration;

import java.util.Map.Entry;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class DbMaintenanceBinder extends AbstractBinder {

	@Override
	protected void configure() {		
		for(Entry<String, Class<? extends AbstractDbCommand>> entry : DbMaintenance.COMMANDS.entrySet()) {
			bind(entry.getValue()).named(entry.getKey()).to(AbstractDbCommand.class);
		}
	}
}
