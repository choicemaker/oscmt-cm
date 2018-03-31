/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.ejb;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.choicemaker.cm.aba.base.db.DbbCountsCreator;
import com.choicemaker.cm.core.DatabaseException;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.base.PMManager;
import com.choicemaker.cm.core.configure.xml.NotFoundException;
import com.choicemaker.cm.core.configure.xml.XmlConfigurablesRegistry;
import com.choicemaker.cm.io.db.base.DatabaseAbstraction;
import com.choicemaker.cm.io.db.base.DatabaseAbstractionManager;
import com.choicemaker.cm.oaba.api.AbaStatisticsController;
import com.choicemaker.cm.oaba.ejb.AggregateDatabaseAbstractionManager;
import com.choicemaker.cm.urm.api.CmServerAdmin;
import com.choicemaker.cm.urm.api.IUpdateDerivedFields;
import com.choicemaker.cm.urm.base.DbRecordCollection;
import com.choicemaker.cm.urm.exceptions.ConfigException;
import com.choicemaker.cm.urm.exceptions.ModelException;
import com.choicemaker.cm.urm.exceptions.RecordCollectionException;
import com.choicemaker.cms.ejb.DefaultUpdateDerivedFieldsRegistry;
import com.choicemaker.util.Precondition;

@Stateless
@Remote(CmServerAdmin.class)
public class CmServerAdminBean implements CmServerAdmin {

	private static final String VERSION = "2.7.1";

	private static final Logger logger =
		Logger.getLogger(CmServerAdminBean.class.getName());

	/** Returns the DataSource specified by the database record collection */
	public static DataSource getDataSource(DbRecordCollection rc)
			throws RecordCollectionException {
		String uri = null;
		DataSource retVal = null;
		try {
			uri = rc.getUrl();
			logger.fine("DataSource URI: " + uri);
			Context ctx = new InitialContext();
			Object o = ctx.lookup(uri);
			retVal = (DataSource) o;
			if (logger.isLoggable(Level.FINE)) {
				Connection conn = retVal.getConnection();
				String connUrl = conn.getMetaData().getURL();
				logger.fine("DB connection URL: " + connUrl);
				conn.close();
			}
		} catch (NamingException x) {
			String msg =
				"Unable to obtain JNDI context or lookup up database URI '"
						+ uri + "': " + x.toString();
			logger.severe(msg);
			throw new RecordCollectionException(msg, x);
		} catch (SQLException x) {
			String msg = "Unable to check JDBC connection of datasource";
			logger.warning(msg);
		}
		return retVal;
	}

	/** Returns the update agent specified by the modelId */
	public static IUpdateDerivedFields getUpdator(String modelName)
			throws ModelException, ConfigException {
		ImmutableProbabilityModel model = PMManager.getModelInstance(modelName);
		if (model == null) {
			logger.severe("Invalid probability accessProvider: " + modelName);
			throw new ModelException(modelName);
		}
		// HACK compilation hack until this class is eliminated
		String delegateExtension =
			// (String) model.properties().get(
			// IUpdateDerivedFields.PN_MODEL_CONFIGURATION_UPDATOR_DELEGATE);
			null;
		// END compilation hack
		XmlConfigurablesRegistry registry =
			DefaultUpdateDerivedFieldsRegistry.getInstance();
		IUpdateDerivedFields retVal;
		try {
			retVal = (IUpdateDerivedFields) registry.get(delegateExtension);
		} catch (NotFoundException x) {
			String msg = x.getMessage();
			logger.severe(msg);
			throw new ConfigException(msg);
		}
		return retVal;
	}

	// @EJB
	AbaStatisticsController statsController;

	@Override
	public String getVersion(Object context) throws RemoteException {
		return VERSION;
	}

	@Override
	public void updateAllDerivedFields(String probabilityModel,
			DbRecordCollection rc) throws ConfigException,
			RecordCollectionException, ModelException, RemoteException {
		Precondition.assertNonNullArgument("null modelId", probabilityModel);
		Precondition.assertNonNullArgument("null record collection", rc);
		try {
			DataSource ds = getDataSource(rc);
			IUpdateDerivedFields updator = getUpdator(probabilityModel);
			updator.updateDirtyDerivedFields(ds);
		} catch (IOException x) {
			String msg = "Unable to access records: " + x.toString();
			logger.severe(msg);
			throw new RecordCollectionException(msg);
		} catch (SQLException x) {
			String msg = "Unable to query records: " + x.toString();
			logger.severe(msg);
			throw new RecordCollectionException(msg, x);
		}
	}

	@Override
	public void updateCounts(String probabilityModel, String urlString)
			throws RecordCollectionException, ConfigException, RemoteException {
		DataSource ds = null;
		try {
			logger.fine("url" + urlString);
			if (urlString == null || urlString.length() == 0)
				throw new RecordCollectionException("empty url");
			Context ctx = new InitialContext();
			Object o = ctx.lookup(urlString);
			ds = (DataSource) o;
			DatabaseAbstractionManager mgr =
				new AggregateDatabaseAbstractionManager();
			DatabaseAbstraction dba = mgr.lookupDatabaseAbstraction(ds);
			DbbCountsCreator countsCreator = new DbbCountsCreator();
			countsCreator.installAbaMetaData(ds);
			final boolean onlyUncomputed = false;
			final boolean commitChanges = false;
			countsCreator.computeAbaStatistics(ds, dba, onlyUncomputed,
					commitChanges);
			countsCreator.updateAbaStatisticsCache(ds, dba, statsController);
		} catch (NamingException e) {
			logger.severe(e.toString());
			throw new ConfigException(e.toString());
		} catch (DatabaseException | SQLException e) {
			logger.severe(e.toString());
			throw new RecordCollectionException(e.toString());
		}
	}

	@Override
	public void updateDerivedFields(String probabilityModel,
			DbRecordCollection rc) throws ConfigException,
			RecordCollectionException, ModelException, RemoteException {
		Precondition.assertNonNullArgument("null modelId", probabilityModel);
		Precondition.assertNonNullArgument("null record collection", rc);
		try {
			DataSource ds = getDataSource(rc);
			IUpdateDerivedFields updator = getUpdator(probabilityModel);
			updator.updateDirtyDerivedFields(ds);
		} catch (IOException x) {
			String msg = "Unable to access records: " + x.toString();
			logger.severe(msg);
			throw new RecordCollectionException(msg);
		} catch (SQLException x) {
			String msg = "Unable to query records: " + x.toString();
			logger.severe(msg);
			throw new RecordCollectionException(msg);
		}
	}

}
