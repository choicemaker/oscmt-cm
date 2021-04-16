/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.db.oracle;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.choicemaker.client.api.Decision;

/**
 * Client proxy of taking a snapshot of a record pair.
 * <p>
 * FIXME: rename and move this Oracle-specific implementation
 * to the com.choicemaker.cm.io.db.oracle package
 * </p>
 *
 * @author    Martin Buechi
 */
public class OracleTakeSnapshot {
	private static Logger logger = Logger.getLogger(OracleTakeSnapshot.class.getName());

	private DataSource ds;

	/**
	 * Constructor.
	 *
	 * @param   ds  Data source for getting a connection to the database.
	 */
	public OracleTakeSnapshot(DataSource ds) {
		this.ds = ds;
	}

	/**
	 * Takes a snapshot of the two records (including all stacked data) and inserts
	 * them along with a decision record into the training database.
	 *
	 * @param   baseId  The ID of the first record.
	 * @param   matchId  The ID of the second record.
	 * @param   d  The human decision.
	 * @param   source  The source of the decision.
	 * @param   comments  Comments regarding the source of the pair or decision.
	 * @param   date  The date when the decision was made.
	 * @param   user  The user who made the decision.
	 */
	public void takeSnapshot(
		String conf,
		Object baseId,
		Object matchId,
		Decision d,
		String source,
		String comments,
		java.util.Date date,
		String user)
		throws SQLException {
		Connection conn = ds.getConnection();
		if (logger.isLoggable(Level.FINE)) {
			logger.fine(
				"call TAKE_SNAPSHOT ("
					+ baseId.toString()
					+ ", "
					+ matchId.toString()
					+ ", "
					+ d.toSingleCharString()
					+ ", "
					+ source
					+ ", "
					+ comments
					+ ", "
					+ new java.sql.Date(date.getTime())
					+ ", "
					+ user
					+ ")");
		}
		CallableStatement stmt = conn.prepareCall("call TAKE_SNAPSHOT (?,?,?,?,?,?,?)");
		//stmt.setString(1, conf);
		stmt.setString(1, baseId.toString());
		stmt.setString(2, matchId.toString());
		stmt.setString(3, d.toSingleCharString());
		stmt.setString(4, source);
		stmt.setString(5, comments);
		stmt.setDate(6, new java.sql.Date(date.getTime()));
		stmt.setString(7, user);
		stmt.execute();
		conn.close();
	}
}
