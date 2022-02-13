/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
/*
 * Created on Feb 5, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.choicemaker.cm.io.db.postgres2;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.choicemaker.cm.core.Accessor;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.RecordSource;
import com.choicemaker.cm.io.db.base.AbstractParallelRecordSource;
import com.choicemaker.cm.io.db.base.DbView;

/**
 * @author pcheung
 */
public class PostgresParallelRecordSource2 extends AbstractParallelRecordSource
	implements RecordSource {

	private static Logger logger =
			Logger.getLogger(PostgresParallelRecordSource2.class.getName());

	private static final String SOURCE = PostgresParallelRecordSource2.class.getSimpleName();

	public PostgresParallelRecordSource2(String fileName,
			ImmutableProbabilityModel model, String dsName,
			String dbConfiguration, String idsQuery) {
		super(fileName, model, dsName, dbConfiguration, idsQuery);
	}

	protected void createView(Connection conn) throws SQLException {
		Statement view = conn.createStatement();
		String s = "DROP VIEW IF EXISTS " + getDataView();
		logger.fine(s);
		view.execute(s);

		s = "create view " + getDataView() + " as " + getIdsQuery();
		logger.fine(s);
		view.execute(s);
		view.close();
	}

	protected void dropView(Connection conn) throws SQLException {
		Statement view = conn.createStatement();
		String s = "DROP VIEW IF EXISTS " + getDataView();
		logger.fine(s);
		view.execute(s);
		view.close();
	}

	protected ResultSet[] retrieveResultSets() throws SQLException {
		Accessor accessor = getModel().getAccessor();
		String viewBase0 = "vw_cmt_%s_r_%s";
		String viewBase = String.format(viewBase0, accessor.getSchemaName(), getDbConfiguration());
		DbView[] views = getDatabaseReader().getViews();
		String masterId = getDatabaseReader().getMasterId();

		int numViews = views.length;
		Statement[] selects = new Statement[numViews];

		List<ResultSet> resultSets = new ArrayList<>();
		for (int i = 0; i < numViews; ++i) {
			String viewName = viewBase + i;
			logger.finest("view: " + viewName);
			DbView v = views[i];

			StringBuffer sb = new StringBuffer("select * from ");
			sb.append(viewBase);
			sb.append(i);
			sb.append(" where ");
			sb.append(masterId);
			sb.append(" in (select id from ");
			sb.append(getDataView());
			sb.append(")");

			if (v.orderBy.length > 0) {
				sb.append(" ORDER BY ");
				sb.append(getOrderBy(v));
			}

			String queryString = sb.toString();

			logger.fine("Query: " + queryString);

			selects[i] = getConnection().prepareStatement(queryString);
			logger.fine("Prepared statement");
			selects[i].setFetchSize(100);
			logger.fine("Changed Fetch Size to 100");
			ResultSet resultSet = ((PreparedStatement) selects[i]).executeQuery();
			logger.fine("Executed query " + i);
			resultSets.add(resultSet);
		}
		
	closeSelectStatements(selects);

	ResultSet[] retVal = resultSets.toArray(new ResultSet[resultSets.size()]);	
	return retVal;
	}

	private void closeSelectStatements(Statement[] selects) {

		List<String> exceptionMessages = new ArrayList<>();
		if (selects != null) {
			int s = selects.length;
			for (int i = 0; i < s; i++) {
				if (selects[i] != null) {
					try {
						selects[i].close();
					} catch (SQLException e) {
						String msg0 = "Problem closing statement [%d]: %s";
						String msg = String.format(msg0, i, e.toString());
						exceptionMessages.add(msg);
					}
				}
				selects[i] = null;
			}
			selects = null;
		}

		// Log any exception messages as warnings
		if (!exceptionMessages.isEmpty()) {
			final int count = exceptionMessages.size();
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			String msg0 = "Problem(s) closing select statements: %d";
			String msg = String.format(msg0, count);
			pw.println(msg);
			for (int i = 0; i < count; i++) {
				msg = exceptionMessages.get(i);
				pw.println(msg);
			}
			msg = sw.toString();
			logger.warning(msg);
		}

		// Postconditions
		assert selects == null;
	}

	@Override
	protected void closeOwnedResources(List<String> exceptionMessages) {
	}

	@Override
	protected String getSource() {
		return SOURCE;
	}

}
