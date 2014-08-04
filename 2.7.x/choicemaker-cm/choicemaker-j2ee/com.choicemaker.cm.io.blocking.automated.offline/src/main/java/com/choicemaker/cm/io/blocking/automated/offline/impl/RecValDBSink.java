/*
 * Copyright (c) 2001, 2009 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.io.blocking.automated.offline.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.io.blocking.automated.offline.core.IRecValSink;
import com.choicemaker.util.IntArrayList;

/**
 * @author pcheung
 *
 */
public class RecValDBSink implements IRecValSink {

	public static final String tableName = "CMT_REC_VAL_ID";
	public static final String groupName = "GROUP_ID";
	public static final String recName = "REC_ID";
	public static final String valName = "VAL_ID";
	
	private static final String INSERT_SQL = "insert into " + tableName + 
		" (" + groupName + "," + recName + "," + valName + ") values (?,?,?)";
		
	private static final String CHECK_SQL = "select count(*) from " + tableName + 
		" where " + groupName + " = ?";
		
	private static final String REMOVE_SQL = "delete from " + tableName + " where " +
		groupName + " = ?";


	private DataSource ds;
	private Connection conn;
	private int groupID;
	private boolean exists = false;
	private PreparedStatement insertStmt;
	private int count = 0;
	
	private long [] bufferID = new long [500];
	private IntArrayList [] bufferVal = new IntArrayList [500];
	private int bufferSize = 0;


	/** This constructor takes these two parameters.
	 * 
	 * @param datasource - DB datasource
	 * @param groupID - unique identifier for this object.
	 */
	public RecValDBSink (DataSource ds, int groupID) throws BlockingException {
		this.ds = ds;
		this.groupID = groupID;
		
		try {
			conn = ds.getConnection();
			//check to see if there is any data on the table for this group.
			PreparedStatement stmt = conn.prepareStatement( CHECK_SQL );
			stmt.setInt(1, groupID);
			ResultSet rs = stmt.executeQuery();
			if (rs.next() && rs.getInt(1) > 0) {
				exists = true;
				count = rs.getInt(1);
			} 
			
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException ex) {
			throw new BlockingException ( ex.toString() );
		}
	}


	/* (non-Javadoc)
	 * @see com.choicemaker.cm.io.blocking.automated.offline.core.IRecValSink#writeRecordValue(long, com.choicemaker.cm.core.util.IntArrayList)
	 */
	public void writeRecordValue(long recID, IntArrayList values) throws BlockingException {
/*		try {
			for (int i=0; i< values.size(); i++) {
				insertStmt.setInt(1, groupID);
				insertStmt.setLong(2, recID);
				insertStmt.setInt(3, values.get(i));
				insertStmt.execute();
			
			}
			
			conn.commit();
			count ++;
		} catch (SQLException ex) {
			throw new BlockingException ( ex.toString() );
		}
*/
//using a buffer
		bufferID[bufferSize] = recID;
		bufferVal[bufferSize] = values;
		bufferSize ++;
		if (bufferSize == 500) writeBuffer ();
	}


	private void writeBuffer () throws BlockingException {
		try {
			for (int i=0; i<bufferSize; i++) {
				for (int j=0; j<bufferVal[i].size(); j++) {
					insertStmt.setInt(1, groupID);
					insertStmt.setLong(2, bufferID[i]);
					insertStmt.setInt(3, bufferVal[i].get(j));
					insertStmt.execute();
				}
				count ++;
			}
			conn.commit ();
				
			//reset
			bufferSize = 0;
		} catch (SQLException ex) {
			throw new BlockingException ( ex.toString() );
		}
		
	}


	/* (non-Javadoc)
	 * @see com.choicemaker.cm.io.blocking.automated.offline.core.ISink#exists()
	 */
	public boolean exists() {
		return exists;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.io.blocking.automated.offline.core.ISink#open()
	 */
	public void open() throws BlockingException {
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(false);
			insertStmt = conn.prepareStatement( INSERT_SQL );

			// need to get rid of old data on the data for this group ID.
			delete ();
			
			//start sequence at 0.
			count = 0;
		} catch (SQLException ex) {
			throw new BlockingException (ex.toString());
		}
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.io.blocking.automated.offline.core.ISink#append()
	 */
	public void append() throws BlockingException {
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(false);
			insertStmt = conn.prepareStatement( INSERT_SQL );
		} catch (SQLException ex) {
			throw new BlockingException (ex.toString());
		}
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.io.blocking.automated.offline.core.ISink#close()
	 */
	public void close() throws BlockingException {
		try {
			if (bufferSize > 0) writeBuffer ();
			
			if (insertStmt != null) insertStmt.close();
			if (conn != null) conn.close();
		} catch (SQLException ex) {
			throw new BlockingException (ex.toString());
		}
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.io.blocking.automated.offline.core.ISink#getCount()
	 */
	public int getCount() {
		return count;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.io.blocking.automated.offline.core.ISink#getInfo()
	 */
	public String getInfo() {
		return Integer.toString(groupID);
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.io.blocking.automated.offline.core.ISink#remove()
	 */
	public void remove() throws BlockingException {
		try {
			conn = ds.getConnection();
			delete ();
			conn.close();
		} catch (SQLException ex) {
			throw new BlockingException ( ex.toString() );
		}
	}

	private void delete () throws BlockingException {
		try {
			//check to see if there is any data on the table for this group.
			PreparedStatement stmt = conn.prepareStatement( REMOVE_SQL );
			stmt.setInt(1, groupID);
			stmt.execute();
			conn.commit();
			
			stmt.close();

		} catch (SQLException ex) {
			throw new BlockingException ( ex.toString() );
		}
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.io.blocking.automated.offline.core.ISink#flush()
	 */
	public void flush() throws BlockingException {
	}
		
}