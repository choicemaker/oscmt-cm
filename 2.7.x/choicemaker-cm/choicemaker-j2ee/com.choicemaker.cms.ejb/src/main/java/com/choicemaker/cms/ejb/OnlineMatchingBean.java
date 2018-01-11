package com.choicemaker.cms.ejb;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.choicemaker.client.api.DataAccessObject;
import com.choicemaker.client.api.EvaluatedPair;
import com.choicemaker.client.api.IGraphProperty;
import com.choicemaker.client.api.MatchCandidates;
import com.choicemaker.client.api.TransitiveCandidates;
import com.choicemaker.cm.core.Accessor;
import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.core.DatabaseException;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Match;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.base.PMManager;
import com.choicemaker.cm.core.base.RecordDecisionMaker;
import com.choicemaker.cm.io.blocking.automated.AbaStatistics;
import com.choicemaker.cm.io.blocking.automated.AutomatedBlocker;
import com.choicemaker.cm.io.blocking.automated.DatabaseAccessor;
import com.choicemaker.cm.io.blocking.automated.base.Blocker2;
import com.choicemaker.cm.io.blocking.automated.base.db.DbbCountsCreator;
import com.choicemaker.cm.io.blocking.automated.offline.server.ejb.AbaStatisticsController;
import com.choicemaker.cm.io.blocking.automated.offline.server.impl.AggregateDatabaseAbstractionManager;
import com.choicemaker.cm.io.blocking.automated.util.BlockingConfigurationUtils;
import com.choicemaker.cm.io.blocking.automated.util.DatabaseAccessorUtils;
import com.choicemaker.cm.io.db.base.DatabaseAbstraction;
import com.choicemaker.cm.io.db.base.DatabaseAbstractionManager;
import com.choicemaker.cm.io.db.base.DbAccessor;
import com.choicemaker.cm.io.db.base.DbReaderParallel;
import com.choicemaker.cms.api.AbaParameters;
import com.choicemaker.cms.api.AbaServerConfiguration;
import com.choicemaker.cms.api.AbaSettings;
import com.choicemaker.cms.api.OnlineMatching;
import com.choicemaker.cms.beans.MatchCandidatesBean;
import com.choicemaker.util.Precondition;
import com.choicemaker.util.StringUtils;

@Stateless
public class OnlineMatchingBean<T extends Comparable<T> & Serializable>
		implements OnlineMatching<T> {

	private static final Logger logger =
		Logger.getLogger(OnlineMatchingBean.class.getName());

	/**
	 * Checks for any specifications required for a complete online matching
	 * configuration.
	 *
	 * @param configuration
	 *            a non-null online matching context
	 * @return a non-null list of any missing specifications. The list will be
	 *         empty if the configuration is a complete specification for online
	 *         matching.
	 */
	public static List<String> listIncompleteSpecifications(
			AbaParameters parameters, AbaSettings settings,
			AbaServerConfiguration configuration) {
		logger.warning("listIncompleteSpecifications is not implemented");
		return Collections.emptyList();
	}

	/** Creates a diagnostic suitable for logging or display to a user. */
	public static String createIncompleteSpecificationMessage(
			AbaParameters parameters, AbaSettings settings,
			AbaServerConfiguration configuration) {
		logger.warning("createIncompleteSpecificationMessage is not implemented");
		return "".intern();
	}

	@EJB
	private AbaStatisticsController statsController;

	boolean areCountsCached;

	@Override
	public MatchCandidates<T> getMatchCandidates(final DataAccessObject<T> query,
			final AbaParameters parameters, final AbaSettings settings,
			final AbaServerConfiguration configuration)
			throws IOException, BlockingException {

		Precondition.assertNonNullArgument("null query", query);
		Precondition.assertNonNullArgument("null configuration", configuration);
		List<String> missingSpecs =
			listIncompleteSpecifications(parameters, settings, configuration);
		if (missingSpecs.size() > 0) {
			String msg = createIncompleteSpecificationMessage(parameters,
					settings, configuration);
			logger.severe(msg);
			throw new IllegalArgumentException(msg);
		}

		String modelName = parameters.getModelConfigurationName();
		ImmutableProbabilityModel model =
			PMManager.getImmutableModelInstance(modelName);

		String dbaName = parameters.getDatabaseAccessorName();
		DatabaseAccessor databaseAccessor =
			DatabaseAccessorUtils.getDatabaseAccessor(dbaName);

		String dbrName = parameters.getDatabaseReaderName();
		Accessor acc = model.getAccessor();
		DbReaderParallel dbr =
			((DbAccessor) acc).getDbReaderParallel(dbrName);

		String masterId = dbr.getMasterId();
		String referenceQuery = parameters.getReferenceSelectionViewAsSQL();
		String condition = DatabaseAccessorUtils.parseSQL(referenceQuery, masterId);
		logger.fine("Condition: " + condition);
		if (condition != null && StringUtils.nonEmptyString(condition)) {
			String[] cs = new String[2];
			cs[0] = " ";
			cs[1] = condition;
			databaseAccessor.setCondition(cs);
		} else {
			databaseAccessor.setCondition("");
		}

		int limitPBS = settings.getLimitPerBlockingSet();
		int stbsgl = settings.getSingleTableBlockingSetGraceLimit();
		int limitSBS = settings.getLimitSingleBlockingSet();
		String databaseConfiguration =
			parameters.getReferenceDatabaseConfiguration();
		String blockingConfiguration =
			parameters.getQueryToReferenceBlockingConfiguration();

		RecordDecisionMaker dm = new RecordDecisionMaker();
		float lowThreshold = parameters.getLowThreshold();
		float highThreshold = parameters.getHighThreshold();

		String jndiName = parameters.getReferenceDatasource();
		final DataSource masterDS = getDataSource(jndiName);
		databaseAccessor.setDataSource(masterDS);

		if (!areCountsCached) {
			cacheAbaStatistics(masterDS);
			areCountsCached = true;
		}

		final String bcId =
			BlockingConfigurationUtils.createBlockingConfigurationId(model,
					blockingConfiguration, databaseConfiguration);
		final AbaStatistics stats = statsController.getStatistics(bcId);
		if (stats == null) {
			String msg =
				"Abastatistics are not available for blocking configuration: "
						+ bcId;
			logger.severe(msg);
			throw new IllegalStateException(msg);
		}

		@SuppressWarnings("unchecked")
		final Record<T> q = model.getAccessor().toImpl(query);

		AutomatedBlocker rs =
			new Blocker2(databaseAccessor, model, q, limitPBS, stbsgl, limitSBS,
					stats, databaseConfiguration, blockingConfiguration);
		logger.fine(q.getId() + " " + rs + " " + model);

		List<Match> matches =
			dm.getMatches(q, rs, model, lowThreshold, highThreshold);
		assert matches != null;

		List<EvaluatedPair<T>> pairs = new ArrayList<>(matches.size());
		for (Match match : matches) {
			String[] notes = match.ac.getNotes(model);
			@SuppressWarnings("unchecked")
			DataAccessObject<T> m =
				(DataAccessObject<T>) model.getAccessor().toRecordHolder(match.m);
			EvaluatedPair<T> p = new EvaluatedPair<T>(query, m,
					match.probability, match.decision, notes);
			pairs.add(p);
		}

		MatchCandidates<T> retVal = new MatchCandidatesBean<T>(query, pairs);
		return retVal;
	}

	@Override
	public TransitiveCandidates<T> getTransitiveCandidates(
			DataAccessObject<T> query, AbaParameters parameters,
			AbaSettings settings, AbaServerConfiguration configuration,
			IGraphProperty mergeConnectivity) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public DataSource getDataSource(String jndiName) throws BlockingException {
		if (jndiName == null || !jndiName.trim().equals(jndiName)
				|| jndiName.isEmpty()) {
			String msg = "Invalid JNDI name '" + jndiName + "'";
			throw new IllegalArgumentException(msg);
		}
		DataSource retVal = null;
		try {
			Context ctx = new InitialContext();
			retVal = (DataSource) ctx.lookup(jndiName);
		} catch (NamingException ex) {
			String msg =
				"Unable to locate DataSource '" + jndiName + "': " + ex;
			logger.severe(ex.toString());
			throw new BlockingException(msg, ex);
		}
		assert retVal != null;

		return retVal;
	}

	/** Cache ABA statistics for field-value counts from a reference source */
	protected void cacheAbaStatistics(DataSource ds) throws BlockingException {
		logger.info("Caching ABA statistics for reference records..");
		try {
			DatabaseAbstractionManager mgr =
				new AggregateDatabaseAbstractionManager();
			DatabaseAbstraction dba = mgr.lookupDatabaseAbstraction(ds);
			DbbCountsCreator cc = new DbbCountsCreator();
			cc.updateAbaStatisticsCache(ds, dba, statsController);
		} catch (SQLException | DatabaseException e) {
			String msg = "Unable to cache master ABA statistics: " + e;
			logger.severe(msg);
			throw new BlockingException(msg);
		}
		logger.info(
				"... finished caching ABA statistics for reference records.");
	}

}
