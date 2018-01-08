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
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.choicemaker.cm.args.AbaSettings;
import com.choicemaker.cm.core.Accessor;
import com.choicemaker.cm.core.Constants;
import com.choicemaker.cm.core.DatabaseException;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.base.Match;
import com.choicemaker.cm.core.base.PMManager;
import com.choicemaker.cm.core.base.RecordDecisionMaker;
import com.choicemaker.cm.core.report.ErrorReporter;
import com.choicemaker.cm.core.report.Report;
import com.choicemaker.cm.core.report.ReporterPlugin;
import com.choicemaker.cm.io.blocking.automated.AbaStatistics;
import com.choicemaker.cm.io.blocking.automated.AutomatedBlocker;
import com.choicemaker.cm.io.blocking.automated.DatabaseAccessor;
import com.choicemaker.cm.io.blocking.automated.IncompleteBlockingSetsException;
import com.choicemaker.cm.io.blocking.automated.UnderspecifiedQueryException;
import com.choicemaker.cm.io.blocking.automated.base.Blocker2;
import com.choicemaker.cm.io.blocking.automated.base.BlockingSetReporter;
import com.choicemaker.cm.io.blocking.automated.base.db.DbbCountsCreator;
import com.choicemaker.cm.io.blocking.automated.offline.server.ejb.AbaStatisticsController;
import com.choicemaker.cm.io.blocking.automated.offline.server.impl.AggregateDatabaseAbstractionManager;
import com.choicemaker.cm.io.blocking.automated.util.BlockingConfigurationUtils;
import com.choicemaker.cm.io.blocking.automated.util.DatabaseAccessorUtils;
import com.choicemaker.cm.io.db.base.DatabaseAbstraction;
import com.choicemaker.cm.io.db.base.DatabaseAbstractionManager;
import com.choicemaker.cm.io.db.base.DbAccessor;
import com.choicemaker.cm.io.db.base.DbReaderParallel;
import com.choicemaker.cm.io.xml.base.XmlSingleRecordWriter;
import com.choicemaker.cm.urm.base.DbRecordCollection;
import com.choicemaker.cm.urm.base.Decision3;
import com.choicemaker.cm.urm.base.EvalRecordFormat;
import com.choicemaker.cm.urm.base.EvaluatedRecord;
import com.choicemaker.cm.urm.base.ISingleRecord;
import com.choicemaker.cm.urm.base.MatchScore;
import com.choicemaker.cm.urm.base.RecordRef;
import com.choicemaker.cm.urm.base.RecordType;
import com.choicemaker.cm.urm.base.ScoreType;
import com.choicemaker.cm.urm.base.SubsetDbRecordCollection;
import com.choicemaker.cm.urm.exceptions.ArgumentException;
import com.choicemaker.cm.urm.exceptions.CmRuntimeException;
import com.choicemaker.cm.urm.exceptions.ConfigException;
import com.choicemaker.cm.urm.exceptions.ModelException;
import com.choicemaker.cm.urm.exceptions.RecordCollectionException;
import com.choicemaker.cm.urm.exceptions.RecordException;
import com.choicemaker.cm.urm.exceptions.UrmIncompleteBlockingSetsException;
import com.choicemaker.cm.urm.exceptions.UrmUnderspecifiedQueryException;
import com.choicemaker.e2.CMExtension;
import com.choicemaker.e2.platform.CMPlatformUtils;

/**
 * @author emoussikaev
 * @see
 */
@Stateless
@SuppressWarnings({"rawtypes"})
public class OnlineMatchBaseBean  {

	private static final long serialVersionUID = 271L;
	protected static Logger log = Logger.getLogger(OnlineMatchBaseBean.class.getName());

	// @EJB
	AbaStatisticsController statsController;

	/**
	 * Now a flag for whether counts have been cached in memory.
	 */
	protected static boolean isCountsUpdated = false;

	/**
	 * Returns the modelId that has the specified name.
	 * @exception IllegalArgumentException if the specified name is null
	 * @exception ModelException if a modelId with the specified name
	 * does not exist
	 */
	ImmutableProbabilityModel getProbabilityModel(String modelName)
		throws ModelException {
		if (modelName == null) {
			throw new IllegalArgumentException("null modelId name");
		}
		ImmutableProbabilityModel retVal = PMManager.getModelInstance(modelName);
		if (retVal == null) {
			log.severe("Invalid probability accessProvider: " + modelName);
			throw new ModelException(modelName);
		}
		return retVal;
	}

	protected void writeDebugInfo(
		ISingleRecord record,
		String probabilityModel,
		float differThreshold,
		float matchThreshold,
		int maxNumMatches,
		EvalRecordFormat returnDataFormat,
		String purpose,
		Level priority) {

		log.log(priority, "record: " + record);
		log.log(priority, "probabilityModel: " + probabilityModel);
		log.log(priority, "differThreshold: " + differThreshold);
		log.log(priority, "matchThreshold: " + matchThreshold);
		log.log(priority, "maxNumMatches: " + maxNumMatches);
		log.log(
			priority,
			"returnData.RecordType: " + returnDataFormat.getRecordType());
		log.log(
			priority,
			"returnData.scoreFormat: " + returnDataFormat.getScoreType());
		log.log(priority, "externalId: " + purpose);

		// Try to dump detail of the query record
		try {
			ImmutableProbabilityModel _model =
				PMManager.getModelInstance(probabilityModel);
			Record _internalRecord = InternalRecordBuilder.getInternalRecord(_model, record);
			boolean _doXmlHeader = false;
			String _details =
				XmlSingleRecordWriter.writeRecord(
					_model,
					_internalRecord,
					_doXmlHeader);
			log.log(
				priority,
				"record detaills: " + Constants.LINE_SEPARATOR + _details);
		} catch (Exception x) {
			log.log(
				priority,
				"Unable to dump details of record '" + record.getId() + "'");
		}
	}

	@SuppressWarnings("null")
	protected List<Match> getMatches(
		long startTime,
		ISingleRecord queryRecord,
		DbRecordCollection masterCollection,
		String modelName,
		float differThreshold,
		float matchThreshold,
		int maxNumMatches,
		String externalId)
		throws
			ModelException,
			ArgumentException,
			RecordException,
			RecordCollectionException,
			CmRuntimeException,
			ConfigException,
			UrmIncompleteBlockingSetsException,
			UrmUnderspecifiedQueryException,
			RemoteException, SQLException, DatabaseException {

		ImmutableProbabilityModel model = null;
		Record q = null;
		AutomatedBlocker recordSource = null;
		List retVal = null;

		try {
			//validate input parameters
			if (maxNumMatches < -1) {
				throw new ConfigException(
					"invalid maxNumMatches:" + maxNumMatches);
			}
			if (maxNumMatches == -1) {
				maxNumMatches = Integer.MAX_VALUE;
			}
			String urlString = masterCollection.getUrl().trim();
			log.fine("url" + urlString);
			if (urlString == null || urlString.length() == 0)
				throw new RecordCollectionException("empty URL");
			Context ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup(urlString);

			if (!isCountsUpdated) {
				// BUG FIX 2009-08-21 rphall
				// It is not the responsibility of this service to update counts.
				// Treat the flag isCountsUpdated as a check for whether
				// counts have been cached in memory.
				DatabaseAbstractionManager mgr = new AggregateDatabaseAbstractionManager();
				DatabaseAbstraction dba = mgr.lookupDatabaseAbstraction(ds);
				DbbCountsCreator countsCreator = new DbbCountsCreator();
				countsCreator.updateAbaStatisticsCache(ds,dba,statsController);
				isCountsUpdated = true;
				// END BUGFIX
			}

			model = getProbabilityModel(modelName);
			// FIXME temporary compilation hack until this class is removed
			String modelDbrName = null;
			// END compilation hack
			if (!modelDbrName.equals(masterCollection.getName()))
				throw new RecordCollectionException("dbConfig should match accessProvider dbConfig attribute");

			q = InternalRecordBuilder.getInternalRecord(model, queryRecord);
			RecordDecisionMaker dm = new RecordDecisionMaker();
			DatabaseAccessor databaseAccessor;
			try {
				// FIXME temporary compilation hack until this class is removed
				String dbaName = null;
				CMExtension dbaExt =
					CMPlatformUtils.getPluginRegistry().getExtension(
							Single.DATABASE_ACCESSOR,
							// model.getDatabaseAccessorName());
							dbaName);
				// END compilation hack
				databaseAccessor =
					(DatabaseAccessor) dbaExt
						.getConfigurationElements()[0]
						.createExecutableExtension("class");

				//PC 3/27/07
				if (masterCollection instanceof SubsetDbRecordCollection) {
					SubsetDbRecordCollection subset =
						(SubsetDbRecordCollection) masterCollection;
					Accessor acc = model.getAccessor();
					DbReaderParallel dbr =
						((DbAccessor) acc).getDbReaderParallel(modelDbrName);

					String masterId = dbr.getMasterId();
					String condition = DatabaseAccessorUtils.parseSQL(subset.getIdsQuery(), masterId);
					log.fine("Condition: " + condition);
					String[] cs = new String[2];
					cs[0] = " ";
					cs[1] = condition;
					databaseAccessor.setCondition(cs);
				} else {
					databaseAccessor.setCondition("");
				}

				databaseAccessor.setDataSource(ds);
			} catch (Exception ex) {
				throw new ModelException(ex.toString());
			}

			String dbConfigName = masterCollection.getName();
			// FIXME temporary HACK
			String blockingConfigName = null;
			AbaSettings FIXME = null;
			// END FIXME
			String bcId = BlockingConfigurationUtils.createBlockingConfigurationId(model, blockingConfigName, dbConfigName);
			AbaStatistics stats = statsController.getStatistics(bcId);
			recordSource =
				new Blocker2(
					databaseAccessor,
					model,
					q,
					FIXME.getLimitPerBlockingSet(),
					FIXME.getSingleTableBlockingSetGraceLimit(),
					FIXME.getLimitSingleBlockingSet(),
					stats,
					dbConfigName,
					blockingConfigName);
			retVal =
				dm.getMatches(
					q,
					recordSource,
					model,
					differThreshold,
					matchThreshold);

			reportSuccessfulQuery(
				startTime,
				q,
				model,
				differThreshold,
				matchThreshold,
				maxNumMatches,
				externalId,
				recordSource,
				retVal);

		} catch (NamingException ex) {
			log.severe(ex.toString());
			throw new RecordCollectionException(ex.toString());

		} catch (RuntimeException ex) {
			log.severe(ex.toString());
			throw new CmRuntimeException(ex.toString());

		} catch (IncompleteBlockingSetsException ex) {
			// This is a data issue, so report it, then throw it.
			log.warning(ex.toString());
			UrmIncompleteBlockingSetsException thrown =
				new UrmIncompleteBlockingSetsException(ex.toString());
			reportUnsuccessfulQuery(
				startTime,
				q,
				model,
				differThreshold,
				matchThreshold,
				maxNumMatches,
				externalId,
				thrown);
			throw thrown;

		} catch (UnderspecifiedQueryException ex) {
			log.warning(ex.toString());
			// This is a data issue, so report it, then throw it.
			UrmUnderspecifiedQueryException thrown =
				new UrmUnderspecifiedQueryException(ex.toString());
			reportUnsuccessfulQuery(
				startTime,
				q,
				model,
				differThreshold,
				matchThreshold,
				maxNumMatches,
				externalId,
				thrown);
			throw thrown;

		} catch (IOException ex) {
			log.severe(ex.toString());
			throw new RecordCollectionException(ex.toString()); //TODO
		}

		return retVal;
	}

	void reportSuccessfulQuery(
		long startTime,
		Record q,
		ImmutableProbabilityModel model,
		float differThreshold,
		float matchThreshold,
		int maxNumMatches,
		String externalId,
		AutomatedBlocker recordSource,
		List retVal) {
		ReporterPlugin[] reporterPlugins =
			new ReporterPlugin[] { new BlockingSetReporter(recordSource)};
		try {
			model.report(
				new Report(
					differThreshold,
					matchThreshold,
					maxNumMatches,
					model,
					startTime,
					System.currentTimeMillis(),
					externalId,
					q,
					recordSource.getNumberOfRecordsRetrieved(),
					retVal,
					reporterPlugins));
		} catch (Exception ex) {
			log.severe("reporting: " + ex);
		}
	}

	void reportUnsuccessfulQuery(
		long startTime,
		Record q,
		ImmutableProbabilityModel model,
		float differThreshold,
		float matchThreshold,
		int maxNumMatches,
		String externalId,
		Throwable thrown) {
		final int numRecordsRetrieved = 0;
		final List results = null;
		ReporterPlugin[] reporterPlugins =
			new ReporterPlugin[] { new ErrorReporter(thrown)};
		try {
			model.report(
				new Report(
					differThreshold,
					matchThreshold,
					maxNumMatches,
					model,
					startTime,
					System.currentTimeMillis(),
					externalId,
					q,
					numRecordsRetrieved,
					results,
					reporterPlugins));
		} catch (Exception ex) {
			log.severe("reporting: " + ex);
		}
	}

	/**
	 * @throws IllegalArgumentException if modelId is null
	 */
	static MatchScore getMatchScore(
		ScoreType st,
		Match match,
		ImmutableProbabilityModel model) {
		if (model == null) {
			throw new IllegalArgumentException("null modelId");
		}
		MatchScore ms;
		String note = "";
		if (st.equals(ScoreType.RULE_LIST_NOTE)) {
			String[] notes = match.ac.getNotes(model);
			for (int n = 0; n < notes.length; n++) {
				if (n == 0) {
					note = notes[n];
				} else {
					note = note + "\t" + notes[n];
				}
			}
		}
		ms =
			new MatchScore(
				match.probability,
				Decision3.valueOf(match.decision.toString()),
				note);
		return ms;
	}

	/**
	 * @throws IllegalArgumentException if modelId is null
	 */
	static ISingleRecord getSingleRecord(
		EvalRecordFormat resultFormat,
		Match match,
		ImmutableProbabilityModel model) {
		if (model == null) {
			throw new IllegalArgumentException("null modelId");
		}
		ISingleRecord resRecord;
		if (resultFormat.getRecordType() == RecordType.HOLDER) {
			Object o = model.getAccessor().toRecordHolder(match.m);
			resRecord = (ISingleRecord) o;
		} else if (resultFormat.getRecordType() == RecordType.NONE)
			resRecord = null;
		else
			resRecord = new RecordRef(match.m.getId());

		return resRecord;
	}

	/**
	 * @throws IllegalArgumentException if modelId is null
	 */
	static EvaluatedRecord getEvaluatedRecord(
		EvalRecordFormat resultFormat,
		Match match,
		ImmutableProbabilityModel model) {
		if (model == null) {
			throw new IllegalArgumentException("null modelId");
		}
		return new EvaluatedRecord(
			getSingleRecord(resultFormat, match, model),
			getMatchScore(resultFormat.getScoreType(), match, model));
	}
}
