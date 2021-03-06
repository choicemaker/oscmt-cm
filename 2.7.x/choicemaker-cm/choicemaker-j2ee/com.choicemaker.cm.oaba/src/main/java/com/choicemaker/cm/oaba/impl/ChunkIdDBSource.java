/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.oaba.core.IChunkRecordIdSource;

/**
 * @author pcheung
 *
 */
public class ChunkIdDBSource implements IChunkRecordIdSource {

	public static final String tableName = "CMT_CHUNK_ID";
	public static final String groupName = "GROUP_ID";
	public static final String seqName = "SEQ_ID";
	public static final String fieldName = "CHUNK_ID";

	private static final String CHECK_SQL = "select count(*) from " + tableName
			+ " where " + groupName + " = ?";

	private static final String SELECT_SQL = "select " + fieldName + " from "
			+ tableName + " where " + groupName + " = ? order by " + seqName;

	private static final String REMOVE_SQL = "delete from " + tableName
			+ " where " + groupName + " = ?";

	private DataSource ds;
	private Connection conn;
	private int groupID;
	private boolean exists = false;
	private PreparedStatement selectStmt;
	private ResultSet selectRS;
	// private int count = 0;
	private long nextID;

	/**
	 * This constructor takes these two parameters.
	 * 
	 * @param ds
	 *            - datasource
	 * @param groupID
	 *            - unique identifier for this object.
	 */
	public ChunkIdDBSource(DataSource ds, int groupID) throws BlockingException {
		this.ds = ds;
		this.groupID = groupID;

		try {
			conn = ds.getConnection();
			// check to see if there is any data on the table for this group.
			PreparedStatement stmt = conn.prepareStatement(CHECK_SQL);
			stmt.setInt(1, groupID);
			ResultSet rs = stmt.executeQuery();
			if (rs.next() && rs.getInt(1) > 0)
				exists = true;

			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException ex) {
			throw new BlockingException(ex.toString());
		}
	}

	@Override
	public Long next() {
		return nextID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.oaba.core.ISource#exists()
	 */
	@Override
	public boolean exists() {
		return exists;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.oaba.core.ISource#open()
	 */
	@Override
	public void open() throws BlockingException {
		try {
			conn = ds.getConnection();
			conn.setReadOnly(true);
			selectStmt = conn.prepareStatement(SELECT_SQL);
			selectStmt.setInt(1, groupID);
			selectRS = selectStmt.executeQuery();
		} catch (SQLException ex) {
			throw new BlockingException(ex.toString());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.oaba.core.ISource#hasNext()
	 */
	@Override
	public boolean hasNext() throws BlockingException {
		boolean ret = false;
		try {
			ret = selectRS.next();
			if (ret)
				nextID = selectRS.getLong(1);
		} catch (SQLException ex) {
			throw new BlockingException(ex.toString());
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.oaba.core.ISource#close()
	 */
	@Override
	public void close() throws BlockingException {
		try {
			if (selectRS != null)
				selectRS.close();
			if (selectStmt != null)
				selectStmt.close();
			if (conn != null)
				conn.close();
		} catch (SQLException ex) {
			throw new BlockingException(ex.toString());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.oaba.core.ISource#getInfo()
	 */
	@Override
	public String getInfo() {
		return Integer.toString(groupID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.oaba.core.ISource#remove()
	 */
	@Override
	public void delete() throws BlockingException {
		try {
			conn = ds.getConnection();
			// check to see if there is any data on the table for this group.
			PreparedStatement stmt = conn.prepareStatement(REMOVE_SQL);
			stmt.setInt(1, groupID);
			stmt.execute();
			conn.commit();

			stmt.close();
			conn.close();
		} catch (SQLException ex) {
			throw new BlockingException(ex.toString());
		}
	}

}
