/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.api;

import java.util.List;

import javax.sql.DataSource;

import com.choicemaker.cm.args.OabaParameters;
import com.choicemaker.cm.args.PersistableRecordSource;
import com.choicemaker.cm.args.PersistableSqlRecordSource;
import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.core.ISerializableRecordSource;

/**
 * Manages a database of SQL record-source configurations.
 * 
 * @author rphall
 *
 */
public interface SqlRecordSourceController {

	// @Override
	PersistableSqlRecordSource save(PersistableRecordSource rs);

	// @Override
	PersistableSqlRecordSource find(Long id, String type);

	// @Override
	ISerializableRecordSource getRecordSource(Long rsId, String type)
			throws Exception;

	// @Override
	List<PersistableRecordSource> findAll();

	DataSource getStageDataSource(OabaParameters params)
			throws BlockingException;

	DataSource getMasterDataSource(OabaParameters params)
			throws BlockingException;

	DataSource getDataSource(Long id) throws BlockingException;

	DataSource getDataSource(String jndiName) throws BlockingException;

}