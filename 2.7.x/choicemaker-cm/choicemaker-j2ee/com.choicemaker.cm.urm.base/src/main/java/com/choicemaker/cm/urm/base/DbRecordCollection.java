/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.base;

import com.choicemaker.cm.urm.exceptions.RecordCollectionException;

/**
 * A record collection in a database.
 * <p>
 *
 * @author emoussikaev
 * @see
 */
public class DbRecordCollection extends RefRecordCollection {

	/** As of 2010-11-12 */
	private static final long serialVersionUID = -6426979517126949280L;

	public static final int DEFAULT_REC_COLLETION_BUFFER_SIZE = 100000;
	private Integer    bufferSize;

	private String name;

	/**
	 * Constructs a <code>DbRecordCollection</code> using the data source JNDI name, user name, password and the query that provides a list of records ids.
	 * The record collection will consist of the records with the specified IDs.
	 *
	 * @param   url
	 * @param 	name
	 * @param	bufferSize

	 */
	public DbRecordCollection(String url, String name, int bufferSize) throws RecordCollectionException{
		super(url);
		this.name = name;
		if(bufferSize >0)
			this.bufferSize = new Integer(bufferSize);
		else
			throw new RecordCollectionException("invalid buffer size "+ bufferSize );
	}


	/**
	 * Constructs a <code>DbRecordCollection</code> using the data source JNDI name, user name, password and the query that provides a list of records ids.
	 * The record collection will consist of the records with the specified IDs.
	 *
	 * @param   dbConfig
	 * @param 	idsQuery The query that provides a list of records ids.
	 * @param	maxSize

	 */
	public DbRecordCollection(String url,String name) {
		super(url);
		this.name = name;
		this.bufferSize = new Integer(DEFAULT_REC_COLLETION_BUFFER_SIZE);
	}
	public String getName() {
		return name;
	}


	public void setName(String n) {
		name = n;
	}

	@Override
	public void accept(IRecordCollectionVisitor ext)throws RecordCollectionException{
		ext.visit(this);
	}

	/**
	 * @return
	 */
	public Integer getBufferSize() {
		return bufferSize;
	}

	/**
	 * @param long1
	 */
	public void setBufferSize(Integer sz) {
		if(sz.intValue() >0)
			bufferSize = sz;
	}

	@Override
	public String toString() {
		return super.toString()+"|"+this.name;
	}
}


///** URL
// * Constructs a <code>DbRecordSource</code> using the data source JNDI name and the query that provides a list of record ids.
// * The record collection will consist of the records with the specified IDs.
// *
// * @param   dataSourceName  The JNDI name of the data source.
// * @param 	idsQuery The query that provides a list of records ids.
// */
//public DbRecordSource(String DriverClass, String idsQuery) {
//	this.dataSourceName = dataSourceName;
//	this.idsQuery = idsQuery;
//}
