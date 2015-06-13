package com.expanset.samples.manydatabases;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Simple table.
 */
@DatabaseTable(tableName = "sample")
public class SampleTable {
	
	@DatabaseField(id = true)
    private long id;
	
	@DatabaseField()
    private String hello;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getHello() {
		return hello;
	}

	public void setHello(String hello) {
		this.hello = hello;
	}	
}
