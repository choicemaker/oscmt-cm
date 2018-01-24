/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.aba.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import com.choicemaker.cm.aba.IBlockingConfiguration;
import com.choicemaker.cm.aba.IBlockingField;
import com.choicemaker.cm.aba.IBlockingValue;
import com.choicemaker.cm.aba.IDbField;
import com.choicemaker.cm.aba.IDbTable;
import com.choicemaker.cm.aba.IQueryField;
import com.choicemaker.cm.aba.util.BlockingConfigurationUtils;
import com.choicemaker.cm.core.Record;

public abstract class BlockingConfiguration
		implements IBlockingConfiguration, Serializable {

	private static final long serialVersionUID = 271;

	/**
	 * A misnamed field that holds the id of a configuration. This field is
	 * directly used by generated code, so it should not be changed to something
	 * more appropriate, like <code>id</code>
	 */
	protected String name;

	protected IDbTable[] dbTables;
	protected IDbField[] dbFields;
	protected IBlockingField[] blockingFields;

	private ArrayList<IBlockingValue>[] values;

	public abstract IBlockingValue[] createBlockingValues(Record q);

	@SuppressWarnings("unchecked")
	protected void init(int numFields) {
		values = new ArrayList[numFields];
		for (int i = 0; i < numFields; ++i)
			values[i] = new ArrayList<>();
	}

	protected IBlockingValue[] unionValues() {
		int size = 0;
		for (int i = 0; i < values.length; ++i) {
			size += values[i].size();
		}
		IBlockingValue[] res = new IBlockingValue[size];
		int out = 0;
		for (int i = 0; i < values.length; ++i) {
			ArrayList<IBlockingValue> l = values[i];
			int s = l.size();
			for (int j = 0; j < s; ++j) {
				res[out++] = (IBlockingValue) l.get(j);
			}
		}
		values = null;
		return res;
	}

	protected IBlockingValue addField(int index, String value,
			IBlockingValue[] thisBase) {
		BlockingValue res;
		value = value.intern();
		ArrayList<IBlockingValue> l = values[index];
		int size = l.size();
		int i = 0;
		while (i < size && ((IBlockingValue) l.get(i)).getValue() != value) {
			++i;
		}
		if (i == size) {
			if (thisBase == null) {
				res = new BlockingValue(getBlockingFields()[index], value);
			} else {
				res = new BlockingValue(getBlockingFields()[index], value,
						new IBlockingValue[][] {
								thisBase });
			}
			l.add(res);
		} else {
			res = (BlockingValue) l.get(i);
			if (thisBase != null) {
				int len = res.getBase().length;
				IBlockingValue[][] newBase = new IBlockingValue[len + 1][];
				System.arraycopy(res.getBase(), 0, newBase, 0, len);
				newBase[len] = thisBase;
				res.setBase(newBase);
			}
		}

		return res;
	}

	@Override
	public String getBlockingConfiguationId() {
		return name;
	}

	@Override
	public IDbTable[] getDbTables() {
		return dbTables;
	}

	@Override
	public IDbField[] getDbFields() {
		return dbFields;
	}

	@Override
	public IBlockingField[] getBlockingFields() {
		return blockingFields;
	}

	@Override
	public int hashCode() {
		return BlockingConfigurationUtils.hashCode(this);
	}

	@Override
	public boolean equals(IBlockingConfiguration bc) {
		return BlockingConfigurationUtils.equals(this, bc);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof IBlockingConfiguration))
			return false;
		return equals((IBlockingConfiguration) obj);
	}

	@Override
	public String toString() {
		return "BlockingConfiguration [id=" + name + ", dbTables="
				+ Arrays.toString(dbTables) + ", dbFields="
				+ Arrays.toString(dbFields) + ", blockingFields="
				+ Arrays.toString(blockingFields) + ", values="
				+ Arrays.toString(values) + "]";
	}

	public static class DbConfiguration {
		public String name;
		public IQueryField[] qfs;
		public IDbTable[] dbts;
		public IDbField[] dbfs;
		public IBlockingField[] bfs;

		public DbConfiguration(String name, IQueryField[] qfs, IDbTable[] dbts,
				IDbField[] dbfs, IBlockingField[] bfs) {
			this.name = name;
			this.qfs = qfs;
			this.dbts = dbts;
			this.dbfs = dbfs;
			this.bfs = bfs;
		}

		@Override
		public String toString() {
			return "DbConfiguration [name=" + name + ", qfs="
					+ Arrays.toString(qfs) + ", dbts=" + Arrays.toString(dbts)
					+ ", dbfs=" + Arrays.toString(dbfs) + ", bfs="
					+ Arrays.toString(bfs) + "]";
		}
	}
}
