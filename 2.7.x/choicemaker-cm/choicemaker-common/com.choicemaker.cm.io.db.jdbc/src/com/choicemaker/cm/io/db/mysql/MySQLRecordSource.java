/*
 * Created on Feb 5, 2004
 *
 */
package com.choicemaker.cm.io.db.mysql;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.RecordSource;
import com.choicemaker.cm.core.Sink;
import com.choicemaker.cm.core.util.ChainedIOException;
import com.choicemaker.cm.io.db.base.DataSources;
import com.choicemaker.cm.io.db.base.DbAccessor;
import com.choicemaker.cm.io.db.base.DbReaderSequential;
import com.choicemaker.cm.io.db.mysql.blocking.MySQLDatabaseAccessor;
import com.choicemaker.cm.io.db.mysql.dbom.MySQLDbObjectMaker;

/**
 * The object reads the record data from DB2.
 * 
 * @author pcheung
 *
 */
public class MySQLRecordSource implements RecordSource {

	private static Logger log = Logger.getLogger(MySQLDatabaseAccessor.class);

	private String fileName;

	private ImmutableProbabilityModel model;
	private String dbConfiguration;
	private String idsQuery;

	private String dsName;
	private DataSource ds;
	private Connection connection;
	private Statement stmt;
	
	private DbReaderSequential dbr;
	
	public MySQLRecordSource() {
		// do nothing...
	}
	
	public MySQLRecordSource(String fileName, ImmutableProbabilityModel model, String dsName, String dbConfiguration, String idsQuery) {
		this.model = model;
		setDataSourceName(dsName);
		this.dbConfiguration = dbConfiguration;
		this.idsQuery = idsQuery;
	}

	public void open() throws IOException {
		DbAccessor accessor = (DbAccessor) model.getAccessor();
		dbr = accessor.getDbReaderSequential(dbConfiguration);
		
		try {
			if (connection == null) {
				connection = ds.getConnection();
				connection.setAutoCommit(false);
			}

			//first create the temp table.
			stmt = connection.createStatement();
			String str = MySQLDatabaseAccessor.getCreateTemp (dbr);
			log.debug (str);
			stmt.executeUpdate( str );
			connection.commit();
			stmt.close();

			
			//create the index for the temp table
			stmt = connection.createStatement();
			str = MySQLDatabaseAccessor.getCreateTempIndex();
			log.debug (str);
			stmt.executeUpdate( str );
			connection.commit();
			stmt.close();


			//second load the ids
			stmt = connection.createStatement();
			str = "INSERT INTO session.ids " + idsQuery;
			log.debug(str);
			stmt.executeUpdate(str);
			connection.commit();
			stmt.close();

			//third gets the sql query
			stmt = connection.createStatement();
			stmt.setFetchSize(100);
			str = MySQLDbObjectMaker.getMultiQuery(model, dbConfiguration);
			log.debug(str);
			ResultSet rs = stmt.executeQuery(str);
			
			dbr.open(rs, stmt);
		} catch (SQLException ex) {
			Logger.getLogger(getClass()).error(ex.toString());
			
			throw new ChainedIOException("", ex);
		}
	}

	public boolean hasNext() throws IOException {
		return dbr.hasNext();
	}

	public Record getNext() throws IOException {
		return dbr.getNext();
	}

	public void close() throws IOException {
		try {
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
			
			if (connection != null) {
				connection.commit();
				connection.close();
				connection = null;
			}
		} catch (SQLException ex) {
			throw new ChainedIOException("Problem closing the statement or connection.", ex);
		} finally {
			//free the memory from dbr
			dbr = null;
		}
	}

	public ImmutableProbabilityModel getModel() {
		return model;
	}

	public void setModel(ImmutableProbabilityModel model) {
		this.model = model;
	}
		
	public String getDataSourceName() {
		return dsName;
	}

	public void setDataSourceName(String dsName) {
		DataSource ds = DataSources.getDataSource(dsName);
		setDataSource(dsName, ds);
	}
		
	public DataSource getDataSource() {
		return ds;
	}
	
	public void setDataSource(String name, DataSource ds) {
		this.dsName = name;
		this.ds = ds;
	}
	
	public void setConnection(Connection connection) {
		this.dsName = null;
		this.ds = null;
		
		this.connection = connection;
	}
	
	public String getDbConfiguration() {
		return dbConfiguration;
	}
	
	public void setDbConfiguration(String dbConfiguration) {
		this.dbConfiguration = dbConfiguration;
	}
	
	public String getIdsQuery() {
		return idsQuery;
	}
	
	public void setIdsQuery(String idsQuery) {
		this.idsQuery = idsQuery;
	}
	
	public String getName() {
		return "DB2 Record Source";
	}

	public void setName(String name) {
		throw new UnsupportedOperationException();
	}

	public boolean hasSink() {
		return false;
	}

	public Sink getSink() {
		throw new UnsupportedOperationException();
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}
	
}
