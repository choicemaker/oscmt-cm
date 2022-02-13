/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.aba.base;

import com.choicemaker.cm.aba.IBlockingField;
import com.choicemaker.cm.aba.IField;

/**
 * A pair comprised of a {@link QueryField query} and {@link DbField master}
 * which is used for blocking.
 * 
 * @author mbuechi
 */
public class BlockingField extends Field implements IBlockingField {

	private static final long serialVersionUID = 271;

	private final int number;
	private final QueryField queryField;
	private final DbField dbField;
	private final String group;

	public BlockingField(int number, QueryField queryField, DbField dbField,
			String group) {
		this(number, queryField, dbField, group, NN_FIELD);
	}

	public BlockingField(int number, QueryField queryField, DbField dbField,
			String group, IField[][] illegalCombinations) {
		super(illegalCombinations);
		this.number = number;
		this.queryField = queryField;
		this.dbField = dbField;
		this.group = group;
	}

	@Override
	public int getNumber() {
		return number;
	}

	@Override
	public QueryField getQueryField() {
		return queryField;
	}

	@Override
	public DbField getDbField() {
		return dbField;
	}

	@Override
	public String getGroup() {
		return group;
	}

	@Override
	public boolean equals(Object o) {
		boolean retVal = false;
		if (o instanceof BlockingField) {
			IBlockingField that = (IBlockingField) o;
			retVal = this.getNumber() == that.getNumber();
			if (retVal && this.getQueryField() == null) {
				retVal = that.getQueryField() == null;
			} else if (retVal) {
				retVal = this.getQueryField().equals(that.getQueryField());
			}
			if (retVal && this.getDbField() == null) {
				retVal = this.getDbField() == null;
			} else if (retVal) {
				retVal = this.getDbField().equals(that.getDbField());
			}
			if (retVal && this.getGroup() == null) {
				retVal = that.getGroup() == null;
			} else if (retVal) {
				retVal = this.getGroup().equals(that.getGroup());
			}
		}
		return retVal;
	}

	@Override
	public int hashCode() {
		int retVal = this.getNumber();
		if (this.getQueryField() != null) {
			retVal += this.getQueryField().hashCode();
		}
		if (this.getDbField() != null) {
			retVal += this.getDbField().hashCode();
		}
		if (this.getGroup() != null) {
			retVal += this.getGroup().hashCode();
		}
		return retVal;
	}

	@Override
	public String toString() {
		return "BlockingField [number=" + number + ", queryField=" + queryField
				+ ", dbField=" + dbField + "]";
	}

}
