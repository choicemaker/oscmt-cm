/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.aba.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;

import javax.sql.DataSource;

import com.choicemaker.cm.aba.AutomatedBlocker;
import com.choicemaker.cm.aba.DatabaseAccessor;
import com.choicemaker.cm.aba.IBlockingField;
import com.choicemaker.cm.aba.IBlockingSet;
import com.choicemaker.cm.aba.IBlockingValue;
import com.choicemaker.cm.aba.IDbField;
import com.choicemaker.cm.aba.IDbTable;
import com.choicemaker.cm.aba.IGroupTable;
import com.choicemaker.cm.core.Record;

/**
 *
 * @author
 * @deprecated Write a JUnit test instead of trying to use this one
 */
@Deprecated
public class TestDatabaseAccessor implements DatabaseAccessor {
	private Connection connection;

	@Override
	public void setDataSource(DataSource dataSource) {
	}

	@Override
	public void setCondition(Object condition) {
	}

	public TestDatabaseAccessor(Connection connection) {
		this.connection = connection;
	}

	@Override
	public DatabaseAccessor cloneWithNewConnection()
			throws CloneNotSupportedException {
		throw new CloneNotSupportedException("not yet implemented");
	}

	private int getActualCount(IBlockingSet bs) {
		if (connection == null) {
			return -1;
		}
		try {
			StringBuffer from = new StringBuffer();
			StringBuffer where = new StringBuffer();
			String join = null;
			String uniqueId = null;
			int numTables = bs.getNumTables();
			for (int i = 0; i < numTables; ++i) {
				IGroupTable gt = bs.getTable(i);
				String alias = " v" + gt.getTable().getNum() + gt.getGroup();
				if (join == null) {
					join = alias;
					uniqueId = gt.getTable().getUniqueId();
				} else {
					from.append(", ");
					if (where.length() > 0)
						where.append(" and ");
					where.append(join + "." + uniqueId + " = " + alias + "."
							+ uniqueId);
				}
				from.append(gt.getTable().getName() + alias);
			}
			int numValues = bs.numFields();
			for (int i = 0; i < numValues; ++i) {
				IBlockingValue bv = bs.getBlockingValue(i);
				IBlockingField bf = bv.getBlockingField();
				IDbField dbf = bf.getDbField();
				IDbTable dbt = dbf.getTable();
				if (where.length() > 0)
					where.append(" and ");
				where.append("v" + dbt.getNum() + bf.getGroup() + "."
						+ dbf.getName() + " = '" + bv.getValue() + "'");
			}
			String query = "SELECT COUNT(DISTINCT " + join + "." + uniqueId
					+ ") FROM " + from + " where " + where;
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			int cnt = rs.getInt(1);
			rs.close();
			return cnt;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return -1;
	}

	@Override
	public void open(AutomatedBlocker blocker, String databaseConfiguration)
			throws IOException {
		System.out.println("=============== open");
		Iterator<IBlockingSet> iBlockingSets =
			blocker.getBlockingSets().iterator();
		int i = 0;
		while (iBlockingSets.hasNext()) {
			IBlockingSet bs = iBlockingSets.next();

			System.out.println("------------- Blocking Set " + i + ": "
					+ bs.getExpectedCount() + " : " + getActualCount(bs));
			int numValues = bs.numFields();
			for (int j = 0; j < numValues; ++j) {
				IBlockingValue bv = bs.getBlockingValue(i);
				IBlockingField bf = bv.getBlockingField();
				IDbField dbf = bf.getDbField();
				System.out.println("bf: " + bf.getNumber() + ", table: "
						+ dbf.getTable().getName() + ", field: " + dbf.getName()
						+ ", val: " + bv.getValue() + ", count: "
						+ bv.getCount() + ", table size: " + bv.getTableSize());
			}
			++i;
		}
	}

	@Override
	public void close() {
		System.out.println("close");
		try {
			connection.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public boolean hasNext() {
		System.out.println("hasNext");
		return false;
	}

	@Override
	public Record getNext() {
		return null;
	}
}
