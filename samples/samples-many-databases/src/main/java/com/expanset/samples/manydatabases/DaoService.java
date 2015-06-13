package com.expanset.samples.manydatabases;

import java.sql.SQLException;

import org.glassfish.hk2.api.PerLookup;
import org.jvnet.hk2.annotations.Contract;
import org.jvnet.hk2.annotations.Service;

import com.expanset.hk2.persistence.ormlite.OrmlitePersistenceContext;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

/**
 * Simple DAO.
 */
@Contract
@Service
@PerLookup
public class DaoService {
	
	@OrmlitePersistenceContext
	private ConnectionSource connection;

	private final static long KEY = 1;
	
	/**
	 * Returns string from default database.
	 * @return String from default database.
	 * @throws SQLException Error.
	 */
	public String getFromDatabase() 
			throws SQLException {
		Dao<SampleTable, Long> sampleDao =
			     DaoManager.createDao(connection, SampleTable.class);		
		SampleTable sampleTableRecord = sampleDao.queryForId(KEY);

		return sampleTableRecord.getHello();
	}
}
