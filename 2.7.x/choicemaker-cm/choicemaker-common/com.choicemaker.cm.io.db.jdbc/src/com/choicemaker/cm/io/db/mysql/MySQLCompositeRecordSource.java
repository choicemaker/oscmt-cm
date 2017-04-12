/*
 * Created on Sep 8, 2004
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
import com.choicemaker.cm.io.composite.base.CompositeRecordSource;
import com.choicemaker.cm.io.db.base.DataSources;
import com.choicemaker.cm.io.db.base.DbAccessor;
import com.choicemaker.cm.io.db.base.DbReaderSequential;
import com.choicemaker.cm.io.db.mysql.blocking.MySQLDatabaseAccessor;

/**
 * This version is a workaround for DB2RecordSource.  The DB2RecordSource encounters 
 * OutOfMemoryException when it tries to bring a lot of data.  This solution is the chop up the query into
 * smaller chunks.  It uses a CompositeRecordSource to store the smaller chunks.
 * 
 * This version works well with numeric primary key, but it is not tuned for String primary key.
 * 
 * @author pcheung
 *
 */
public class MySQLCompositeRecordSource implements RecordSource {
	
	private static final Logger log = Logger.getLogger(MySQLCompositeRecordSource.class);

	
	private CompositeRecordSource compositeSource;
	private ImmutableProbabilityModel model;
	private String dbConfiguration;
	private String idsQuery;
	private String dsName;
	private DataSource ds;
	private int maxSize;
	private Statement stmt;
	private Connection connection;
	private ResultSet rs;

	
	/**
	 * Default Constructor that does nothing.
	 */
	public MySQLCompositeRecordSource (){
	}
	
	
	/** This constructor takes these arguments
	 * 
	 * @param ds - data source 
	 * @param accessProvider - probabiliyt accessProvider
	 * @param idsQuery - the query to get the ids.
	 * @param dbConfiguration - db configuration name in the schema file.
	 * @param maxSize - maximum number of records in each of the composite.
	 */
	public MySQLCompositeRecordSource(DataSource ds, ImmutableProbabilityModel model, String idsQuery,
		String dbConfiguration, int maxSize) {
		this.model = model;
		this.idsQuery = idsQuery;
		this.dbConfiguration = dbConfiguration;
		this.maxSize = maxSize;
		this.ds = ds;
		this.dsName = "DS";
	}


	/** This constructor takes these arguments
	 * 
	 * @param dsName - data source name
	 * @param accessProvider - probabiliyt accessProvider
	 * @param idsQuery - the query to get the ids.
	 * @param dbConfiguration - db configuration name in the schema file.
	 * @param maxSize - maximum number of records in each of the composite.
	 */
	public MySQLCompositeRecordSource(String dsName, ImmutableProbabilityModel model, String idsQuery,
		String dbConfiguration, int maxSize) {
		this.model = model;
		this.idsQuery = idsQuery;
		this.dbConfiguration = dbConfiguration;
		this.maxSize = maxSize;
		setDataSourceName (dsName);
	}
	
	public String getDataSourceName() {
		return dsName;
	}

	private void setDataSourceName(String dsName) {
		DataSource ds = DataSources.getDataSource(dsName);
		setDataSource(dsName, ds);
	}
		
	public DataSource getDataSource() {
		return ds;
	}
	
	private void setDataSource(String name, DataSource ds) {
		this.dsName = name;
		this.ds = ds;
	}
	
	
	/** This method chops up the data and populates the CompositeRecordSource.
	 * 
	 *
	 */
	private void init () throws IOException{
		DbAccessor accessor = (DbAccessor) model.getAccessor();
		DbReaderSequential dbr = accessor.getDbReaderSequential(dbConfiguration);
		String idName = dbr.getMasterId();
		boolean isString = (dbr.getMasterIdType().indexOf("CHAR") > -1) ||
			(dbr.getMasterIdType().indexOf("STR") > -1);
						
		log.debug (dbr.getMasterIdType() + " isString " + isString);
		log.debug ("master id: " + idName);
		log.debug ("idsQuery: " + idsQuery);

		try {
			if (connection == null) {
				connection = ds.getConnection();
				connection.setAutoCommit(false);
			}
		
			if (isString) compositeSource = handleString (connection, dbr, idName);
			else  compositeSource = handleNumber (connection, dbr, idName);

			shutDown ();
		} catch (SQLException ex) {
			log.error(ex.toString());
			
			throw new ChainedIOException("", ex);
		}
	}
	
	
	//this handles numeric primary key
	private CompositeRecordSource handleNumber (Connection conn, DbReaderSequential dbr, String idName) 
	throws SQLException {
		CompositeRecordSource compositeSource  = new CompositeRecordSource ();
		compositeSource.setModel(model);

		rs = getStats (conn, dbr);

		if (rs.next()) {
			long min = rs.getLong(1);
			long max = rs.getLong(2);
			int count = rs.getInt(3);
				
			log.debug ("handleNumber min " + min + " max " + max + " count " + count);
				
			while (min < max) {
				String subQuery = setMinMax (idsQuery, idName, min, min + maxSize);
					
				//since between is inclusive, we need to add another one.
				min += maxSize + 1;
					
				log.debug (subQuery); 
					
				MySQLRecordSource srs = new MySQLRecordSource ();
				srs.setDataSource(dsName,ds);
				srs.setModel(model);
				srs.setDbConfiguration(dbConfiguration);
				srs.setIdsQuery(subQuery);
					
				compositeSource.add(srs);
			}
				
			log.debug ("number of sources: " + compositeSource.getNumSources());
		}
			
		return compositeSource;
	}
	
	
	//this handles String primary key
	private CompositeRecordSource handleString (Connection conn, DbReaderSequential dbr, String idName) 
	throws SQLException {
		
		CompositeRecordSource compositeSource  = new CompositeRecordSource ();
		compositeSource.setModel(model);

		rs = getStats (conn, dbr);

		if (rs.next()) {
			String min = rs.getString(1);
			String max = rs.getString(2);
			int count = rs.getInt(3);
				
			log.debug ("handleString min " + min + " max " + max + " count " + count);
			
			if (count < maxSize) {
				String subQuery = setMinMax (idsQuery, idName, min, max);

				MySQLRecordSource srs = new MySQLRecordSource ();
				srs.setDataSource(dsName,ds);
				srs.setModel(model);
				srs.setDbConfiguration(dbConfiguration);
				srs.setIdsQuery(subQuery);
					
				compositeSource.add(srs);
				
			} else {
				int factor = 8 - (count / maxSize);
				if (factor < 0) factor = 1;
				
				log.debug("factor " + factor);
				
				while (min.compareTo(max) < 0) {
					char c = min.charAt(0);
					int c2 = c + factor;
					char c3 = (char) c2;
					while (!Character.isLetterOrDigit(c3)) {
						c2 = c3 + 1;
						c3 = (char) c2;
					}
					String min2 = Character.toString(c3);
					if (min.length() > 1) min2 = min2 + min.substring(1);
				
					String subQuery = setMinMax (idsQuery, idName, min, min2);
					
					//since between is inclusive, we need to add another one.
					min = min2 + "0";
					
					log.debug (subQuery); 
					
					MySQLRecordSource srs = new MySQLRecordSource ();
					srs.setDataSource(dsName,ds);
					srs.setModel(model);
					srs.setDbConfiguration(dbConfiguration);
					srs.setIdsQuery(subQuery);
					
					compositeSource.add(srs);
				}
				
				log.debug ("number of sources: " + compositeSource.getNumSources());
				
			}
			
		}
		return compositeSource;
	}
	
	
	/** This method finds out about min, max, and count record id of idsQuery.
	 * 
	 *
	 */
	private ResultSet getStats (Connection connection, DbReaderSequential dbr) throws SQLException {
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

		//second load the ids in.
		stmt = connection.createStatement();
		str = "INSERT INTO ids " + idsQuery;
		log.debug (str);
		stmt.executeUpdate( str );
		connection.commit();
		stmt.close();
		
		//third read min, max, and count		
		stmt = connection.createStatement();
		str = "SELECT MIN(ID), MAX(ID), COUNT(*) FROM IDS";
		log.debug (str);
		rs = stmt.executeQuery(str);

		return rs;
	}
	
	
	private String setMinMax (String idsQuery, String idName, String min, String max) {
		StringBuffer sb = new StringBuffer ();
		
		int ind = idsQuery.toUpperCase().indexOf("WHERE");

		if ( ind > 0) {
			sb.append(idsQuery.substring(0, ind + 6));
			sb.append ("(");
			sb.append (idName);
			sb.append (" between '");
			sb.append(min);
			sb.append("' and '");
			sb.append(max);
			sb.append ("')");
			sb.append (" and ");
			sb.append(idsQuery.substring(ind + 6));
		} else {
			sb.append(idsQuery);
			sb.append(" where ");
			sb.append ("(");
			sb.append (idName);
			sb.append (" between '");
			sb.append(min);
			sb.append("' and '");
			sb.append(max);
			sb.append ("')");
		}

		return sb.toString();
	}
	
	
	private String setMinMax (String idsQuery, String idName, long min, long max) {
		StringBuffer sb = new StringBuffer ();
		
		int ind = idsQuery.toUpperCase().indexOf("WHERE");

		if ( ind > 0) {
			sb.append(idsQuery.substring(0, ind + 6));
			sb.append ("(");
			sb.append (idName);
			sb.append (" between ");
			sb.append(min);
			sb.append(" and ");
			sb.append(max);
			sb.append (")");
			sb.append (" and ");
			sb.append(idsQuery.substring(ind + 6));
		} else {
			sb.append(idsQuery);
			sb.append(" where ");
			sb.append ("(");
			sb.append (idName);
			sb.append (" between ");
			sb.append(min);
			sb.append(" and ");
			sb.append(max);
			sb.append (")");
		}

		return sb.toString();
	}
	
	
	
	private void shutDown () {
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			} 
		} catch (SQLException e) {
			log.error(e.toString(), e);
		}

		try {
			if (stmt != null) {
				stmt.close();
				stmt = null;
			} 
		} catch (SQLException e) {
			log.error(e.toString(), e);
		}

		try {
			if (connection != null) {
				connection.commit();
				connection.close();
				connection = null;
			} 
		} catch (SQLException e) {
			log.error(e.toString(), e);
		}
	}
	
	
	
	

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.RecordSource#getNext()
	 */
	public Record getNext() throws IOException {
		return compositeSource.getNext();
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.Source#open()
	 */
	public void open() throws IOException {
		init ();
		compositeSource.open();
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.Source#close()
	 */
	public void close() throws IOException {
		compositeSource.close();
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.Source#hasNext()
	 */
	public boolean hasNext() throws IOException {
		return compositeSource.hasNext();
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.Source#getName()
	 */
	public String getName() {
		return "DB2 Composite Record Source";
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.Source#setName(java.lang.String)
	 */
	public void setName(String name) {
		//do nothing
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.Source#getModel()
	 */
	public ImmutableProbabilityModel getModel() {
		return model;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.Source#setModel(com.choicemaker.cm.core.ProbabilityModel)
	 */
	public void setModel(ImmutableProbabilityModel m) {
		this.model = m;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.Source#hasSink()
	 */
	public boolean hasSink() {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.Source#getSink()
	 */
	public Sink getSink() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.Source#getFileName()
	 */
	public String getFileName() {
		throw new UnsupportedOperationException();
	}
	
	
}
