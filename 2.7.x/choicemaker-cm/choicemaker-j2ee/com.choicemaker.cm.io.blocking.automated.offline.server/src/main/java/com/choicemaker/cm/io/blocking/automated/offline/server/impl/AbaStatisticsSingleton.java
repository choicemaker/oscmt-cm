/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.blocking.automated.offline.server.impl;

import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;

import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.choicemaker.cm.args.OabaParameters;
import com.choicemaker.cm.args.PersistableSqlRecordSource;
import com.choicemaker.cm.core.DatabaseException;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.io.blocking.automated.AbaStatistics;
import com.choicemaker.cm.io.blocking.automated.IBlockingConfiguration;
import com.choicemaker.cm.io.blocking.automated.base.db.DbbCountsCreator;
import com.choicemaker.cm.io.blocking.automated.offline.server.ejb.AbaStatisticsController;
import com.choicemaker.cm.io.blocking.automated.offline.server.ejb.SqlRecordSourceController;
import com.choicemaker.cm.io.blocking.automated.util.BlockingConfigurationUtils;
import com.choicemaker.cm.io.db.base.DatabaseAbstraction;
import com.choicemaker.cm.io.db.base.DatabaseAbstractionManager;

@Singleton
public class AbaStatisticsSingleton implements AbaStatisticsController {

	private static final Logger log =
		Logger.getLogger(AbaStatisticsSingleton.class.getName());

	// -- Injected data

	@EJB
	private SqlRecordSourceController sqlRSController;

	// -- Accessors

	protected final SqlRecordSourceController getSqlRecordSourceController() {
		return sqlRSController;
	}

	/** Map of model configuration names to ABA statistics */
	private Map<String, AbaStatistics> cachedStats = new Hashtable<>();

	@Override
	public void updateReferenceStatistics(OabaParameters params)
			throws DatabaseException {
		final long rsId = params.getReferenceRsId();
		final String type = PersistableSqlRecordSource.TYPE;
		PersistableSqlRecordSource rs =
			this.getSqlRecordSourceController().find(rsId, type);
		String dsJndiUrl = rs.getDataSource();
		updateReferenceStatistics(dsJndiUrl);
	}

	@Override
	@TransactionAttribute(REQUIRES_NEW)
	public void updateReferenceStatistics(String urlString)
			throws DatabaseException {
		DataSource ds = null;
		log.fine("url" + urlString);
		if (urlString == null || urlString.length() == 0)
			throw new IllegalArgumentException("empty DataSource url");
		Context ctx;
		try {
			ctx = new InitialContext();
			Object o = ctx.lookup(urlString);
			ds = (DataSource) o;
		} catch (NamingException e) {
			String msg = "Unable to acquire DataSource from JNDI URL '"
					+ urlString + "': " + e;
			log.severe(msg);
			throw new DatabaseException(msg, e);
		}
		DatabaseAbstractionManager mgr =
			new AggregateDatabaseAbstractionManager();
		DatabaseAbstraction dba = mgr.lookupDatabaseAbstraction(ds);
		DbbCountsCreator countsCreator = new DbbCountsCreator();
		try {
			countsCreator.installAbaMetaData(ds);
			final boolean onlyUncomputed = false;
			final boolean commitChanges = false;
			countsCreator.computeAbaStatistics(ds, dba, onlyUncomputed,
					commitChanges);
			countsCreator.updateAbaStatisticsCache(ds, dba, this);
		} catch (SQLException e) {
			String msg = "Unable to compute ABA statistics for '" + urlString
					+ "': " + e;
			log.severe(msg);
			throw new DatabaseException(msg, e);
		}
	}

	@Override
	public String computeBlockingConfigurationId(ImmutableProbabilityModel m,
			String blockingConfiguration, String databaseConfiguration) {
		return BlockingConfigurationUtils.createBlockingConfigurationId(m,
				blockingConfiguration, databaseConfiguration);
	}

	@Override
	public void putStatistics(String blockingConfigurationId,
			AbaStatistics counts) {
		this.cachedStats.put(blockingConfigurationId, counts);
	}

	@Override
	public void putStatistics(IBlockingConfiguration bc, AbaStatistics counts) {
		String blockingConfigurationId = bc.getBlockingConfiguationId();
		putStatistics(blockingConfigurationId, counts);
	}

	@Override
	public AbaStatistics getStatistics(String blockingConfigurationId) {
		AbaStatistics retVal = this.cachedStats.get(blockingConfigurationId);
		return retVal;
	}

	@Override
	public AbaStatistics getStatistics(IBlockingConfiguration bc) {
		String blockingConfigurationId = bc.getBlockingConfiguationId();
		return getStatistics(blockingConfigurationId);
	}

}
