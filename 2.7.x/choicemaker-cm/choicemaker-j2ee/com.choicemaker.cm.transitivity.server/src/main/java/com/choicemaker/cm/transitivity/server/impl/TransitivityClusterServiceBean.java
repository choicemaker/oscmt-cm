/*
 * Copyright (c) 2001, 2009 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.transitivity.server.impl;

import java.io.IOException;
import java.rmi.RemoteException;
import java.security.AccessControlException;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.logging.Logger;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.sql.DataSource;

import com.choicemaker.cm.core.ChoiceMakerExtensionPoint;
import com.choicemaker.cm.core.DatabaseException;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.InvalidModelException;
import com.choicemaker.cm.core.InvalidProfileException;
import com.choicemaker.cm.core.Profile;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.UnderspecifiedProfileException;
import com.choicemaker.cm.core.XmlConfException;
//import com.choicemaker.cm.core.base.Accessor;
import com.choicemaker.cm.core.base.BeanMatchCandidate;
import com.choicemaker.cm.core.base.MatchCandidate;
import com.choicemaker.cm.core.base.PMManager;
import com.choicemaker.cm.core.base.RecordDecisionMaker;
import com.choicemaker.cm.io.blocking.automated.AutomatedBlocker;
import com.choicemaker.cm.io.blocking.automated.DatabaseAccessor;
import com.choicemaker.cm.io.blocking.automated.UnderspecifiedQueryException;
import com.choicemaker.cm.io.blocking.automated.base.Blocker2;
import com.choicemaker.cm.server.util.CountsUpdate;
import com.choicemaker.cm.server.util.NameServiceLookup;
import com.choicemaker.cm.transitivity.core.TransitivityException;
import com.choicemaker.cm.transitivity.core.TransitivityResult;
import com.choicemaker.cm.transitivity.server.util.MatchBiconnectedIterator;
import com.choicemaker.cm.transitivity.util.CEFromMatchCandidatesBuilder;
import com.choicemaker.cm.transitivity.util.CEFromMatchesBuilder;
import com.choicemaker.e2.CMExtension;
import com.choicemaker.e2.platform.CMPlatformUtils;

/**
 * @author pcheung
 *
 *         ChoiceMaker Technologies, Inc.
 */
@SuppressWarnings({ "rawtypes" })
public class TransitivityClusterServiceBean implements SessionBean {

	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger
			.getLogger(TransitivityClusterServiceBean.class.getName());

	public static final String DATABASE_ACCESSOR =
		ChoiceMakerExtensionPoint.CM_IO_BLOCKING_AUTOMATED_BASE_DATABASEACCESSOR;
	public static final String BLOCKING_SOURCE =
		"java:comp/env/jdbc/blockingSource";

	private transient DataSource blockingSource;
	private static boolean inited;
	private NameServiceLookup nameServiceLookup = new NameServiceLookup();

	/**
	 * This method finds the matches and the cluster for the input record.
	 *
	 * @param profile
	 *            - Profile containing the input record
	 * @param constraint
	 *            - constraint
	 * @param probabilityModel
	 *            - name of the probability accessProvider
	 * @param differThreshold
	 *            - differ threshold
	 * @param matchThreshold
	 *            - match threshold
	 * @param maxNumMatches
	 *            - maximum number of matches to return
	 * @param returnDataFormat
	 *            - return format
	 * @param purpose
	 *            - string purpose identifier
	 * @return TransitivityResult
	 * @throws AccessControlException
	 * @throws InvalidProfileException
	 * @throws RemoteException
	 * @throws InvalidModelException
	 * @throws UnderspecifiedProfileException
	 * @throws DatabaseException
	 */
	public TransitivityResult findClusters(Profile profile, Object constraint,
			String probabilityModel, float differThreshold,
			float matchThreshold, int maxNumMatches, String returnDataFormat,
			String purpose, boolean compact) throws AccessControlException,
			InvalidProfileException, RemoteException, InvalidModelException,
			UnderspecifiedProfileException, DatabaseException {

		logger.info("starting findCluster");

		ImmutableProbabilityModel model = PMManager.getModelInstance(probabilityModel);
		if (model == null) {
			logger.severe("Invalid probability accessProvider: "
					+ probabilityModel);
			throw new InvalidModelException(probabilityModel);
		}
		// 2014-04-24 rphall: Commented out unused local variable
		// Any side effects?
		// Accessor accessor = modelId.getAccessor();
		Record q = profile.getRecord(model);
		RecordDecisionMaker dm = new RecordDecisionMaker();
		DatabaseAccessor databaseAccessor;
		try {
			// FIXME REMOVEME
			// CMPlatformUtils.getExtension(DATABASE_ACCESSOR, (String) model
			// .properties().get(DATABASE_ACCESSOR));
			// END FIXME
			CMExtension dbaExt =
				CMPlatformUtils.getExtension(DATABASE_ACCESSOR,
						model.getDatabaseAccessorName());
			databaseAccessor =
				(DatabaseAccessor) dbaExt.getConfigurationElements()[0]
						.createExecutableExtension("class");
			databaseAccessor.setCondition(constraint);
			databaseAccessor.setDataSource(blockingSource);
		} catch (Exception ex) {
			throw new InvalidModelException(ex.toString());
		}
		SortedSet s;
		AutomatedBlocker rs = new Blocker2(databaseAccessor, model, q);
		try {
			s = dm.getMatches(q, rs, model, differThreshold, matchThreshold);
		} catch (UnderspecifiedQueryException ex) {
			logger.warning(ex.toString());
			throw new UnderspecifiedProfileException("", ex);
		} catch (IOException ex) {
			logger.severe("Database error: " + ex);
			throw new DatabaseException("", ex);
		}

		TransitivityResult tr = null;
		try {
			Iterator ces =
				getCompositeEntities(q, s, probabilityModel, differThreshold,
						matchThreshold);

			if (compact) {
				tr =
					new TransitivityResult(probabilityModel, differThreshold,
							matchThreshold, new MatchBiconnectedIterator(ces));
			} else {
				tr =
					new TransitivityResult(probabilityModel, differThreshold,
							matchThreshold, ces);
			}
		} catch (TransitivityException e) {
			logger.severe(e.toString());
		}

		return tr;
	}

	/**
	 * This method takes the output of findMatches and runs the match result
	 * through the Transitivity Engine.
	 *
	 * @param profile
	 *            - contains the query record
	 * @param candidates
	 *            - match candidates to the query record
	 * @param modelName
	 *            - probability accessProvider name
	 * @param differThreshold
	 *            - differ threshold
	 * @param matchThreshold
	 *            - match threshold
	 * @param compact
	 *            - set this to true if you want the CompositeEntity in the
	 *            TransitivityResult to be compacted before returning.
	 * @return A TransitivityResult object
	 * @throws RemoteException
	 *             If a communication problem occurs.
	 * @throws InvalidProfileException
	 * @throws TransitivityException
	 * @throws InvalidModelException
	 *             if the accessProvider does not exist or is not properly
	 *             configured.
	 */
	public TransitivityResult findClusters(Profile profile,
			MatchCandidate[] candidates, String modelName,
			float differThreshold, float matchThreshold, boolean compact)
			throws RemoteException, InvalidProfileException,
			TransitivityException, InvalidModelException {

		BeanMatchCandidate[] bCandidates =
			new BeanMatchCandidate[candidates.length];
		for (int i = 0; i < candidates.length; i++) {
			bCandidates[i] = (BeanMatchCandidate) candidates[i];
		}

		CEFromMatchCandidatesBuilder ceb =
			new CEFromMatchCandidatesBuilder(profile, bCandidates, modelName,
					differThreshold, matchThreshold);

		TransitivityResult tr = null;
		if (compact) {
			tr =
				new TransitivityResult(modelName, differThreshold,
						matchThreshold, new MatchBiconnectedIterator(
								ceb.getCompositeEntities()));
		} else {
			tr =
				new TransitivityResult(modelName, differThreshold,
						matchThreshold, ceb.getCompositeEntities());
		}

		return tr;
	}

	/**
	 * This takes in a set of Matches and returns an Iterator of
	 * CompositeEntity.
	 *
	 * @param s
	 * @return
	 */
	private Iterator getCompositeEntities(Record q, SortedSet s,
			String modelName, float low, float high)
			throws TransitivityException {

		CEFromMatchesBuilder builder =
			new CEFromMatchesBuilder(q, s.iterator(), modelName, low, high);

		return builder.getCompositeEntities();
	}

	private static synchronized void init(DataSource dataSource)
			throws XmlConfException, RemoteException, DatabaseException {
		if (!inited) {
			new CountsUpdate().cacheCounts(dataSource);
			inited = true;
		}
	}

	public void ejbCreate() throws CreateException {
		try {
			blockingSource =
				(DataSource) nameServiceLookup.lookup(BLOCKING_SOURCE,
						DataSource.class);
			init(blockingSource);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new CreateException(ex.toString());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ejb.SessionBean#ejbActivate()
	 */
	public void ejbActivate() throws EJBException, RemoteException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ejb.SessionBean#ejbPassivate()
	 */
	public void ejbPassivate() throws EJBException, RemoteException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ejb.SessionBean#ejbRemove()
	 */
	public void ejbRemove() throws EJBException, RemoteException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ejb.SessionBean#setSessionContext(javax.ejb.SessionContext)
	 */
	public void setSessionContext(SessionContext arg0) throws EJBException,
			RemoteException {
	}

}
