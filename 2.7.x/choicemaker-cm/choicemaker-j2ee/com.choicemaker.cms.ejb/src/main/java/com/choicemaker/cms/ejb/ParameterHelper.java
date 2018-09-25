package com.choicemaker.cms.ejb;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.choicemaker.cm.aba.AbaStatistics;
import com.choicemaker.cm.aba.AutomatedBlocker;
import com.choicemaker.cm.aba.DatabaseAccessor;
import com.choicemaker.cm.aba.base.Blocker2;
import com.choicemaker.cm.aba.base.db.DbbCountsCreator;
import com.choicemaker.cm.aba.util.BlockingConfigurationUtils;
import com.choicemaker.cm.aba.util.DatabaseAccessorUtils;
import com.choicemaker.cm.args.AbaSettings;
import com.choicemaker.cm.core.Accessor;
import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.core.DatabaseException;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.base.PMManager;
import com.choicemaker.cm.io.db.base.DatabaseAbstraction;
import com.choicemaker.cm.io.db.base.DatabaseAbstractionManager;
import com.choicemaker.cm.io.db.base.DbAccessor;
import com.choicemaker.cm.io.db.base.DbReaderParallel;
import com.choicemaker.cm.oaba.api.AbaStatisticsController;
import com.choicemaker.cm.oaba.ejb.AggregateDatabaseAbstractionManager;
import com.choicemaker.cms.api.AbaParameters;
import com.choicemaker.util.Precondition;
import com.choicemaker.util.StringUtils;

public class ParameterHelper {

	private static final Logger logger =
		Logger.getLogger(ParameterHelper.class.getName());

	private static AtomicBoolean areCountsCached = new AtomicBoolean();

	public static void cacheAbaStatics(
			final AbaStatisticsController statsController, final DataSource ds)
			throws BlockingException {
		if (areCountsCached.compareAndSet(false, true)) {
			cacheAbaStatistics(statsController, ds);
		}
	}

	/** Cache ABA statistics for field-value counts from a reference source */
	public static void cacheAbaStatistics(
			final AbaStatisticsController statsController, final DataSource ds)
			throws BlockingException {

		logger.info("Caching ABA statistics for reference records..");
		logger.fine("cacheAbaStatistics: areCountsCached: "
				+ areCountsCached.get());
		logger.finer("cacheAbaStatistics: thread: "
				+ Thread.currentThread().getName() + ": "
				+ Thread.currentThread().getId());
		Precondition.assertNonNullArgument("null ABA statistics",
				statsController);
		Precondition.assertNonNullArgument("null data source", ds);

		try {
			DatabaseAbstractionManager mgr =
				new AggregateDatabaseAbstractionManager();
			DatabaseAbstraction dba = mgr.lookupDatabaseAbstraction(ds);
			DbbCountsCreator cc = new DbbCountsCreator();
			cc.updateAbaStatisticsCache(ds, dba, statsController);
		} catch (SQLException | DatabaseException e) {
			areCountsCached.set(false);
			String msg = "Unable to cache master ABA statistics: " + e;
			logger.severe(msg);
			logger.fine("cacheAbaStatistics: areCountsCached: "
					+ areCountsCached.get());
			logger.finer("cacheAbaStatistics: thread: "
					+ Thread.currentThread().getName() + ": "
					+ Thread.currentThread().getId());
			throw new BlockingException(msg);
		}
		areCountsCached.set(true);
		logger.info(
				"... finished caching ABA statistics for reference records.");
		logger.fine("cacheAbaStatistics: areCountsCached: "
				+ areCountsCached.get());
		logger.finer("cacheAbaStatistics: thread: "
				+ Thread.currentThread().getName() + ": "
				+ Thread.currentThread().getId());
	}

	public static DataSource getDataSource(String jndiName)
			throws BlockingException {
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

	public static ImmutableProbabilityModel getModel(AbaParameters parameters) {
		Precondition.assertNonNullArgument("null parameters", parameters);
		String modelName = parameters.getModelConfigurationName();
		ImmutableProbabilityModel model =
			PMManager.getImmutableModelInstance(modelName);
		return model;
	}

	private final AbaParameters parameters;

	// Cached values
	// FIXME: not thread safe. Use Atomic values.
	private ImmutableProbabilityModel model;
	private DatabaseAccessor databaseAccessor;
	private DbReaderParallel dbReaderParallel;
	private DataSource dataSource;
	private AbaStatistics abaStatistics;
	private String blockingConfigurationId;

	public ParameterHelper(AbaParameters parameters) {
		Precondition.assertNonNullArgument("null AbaParameters", parameters);
		this.parameters = parameters;
	}

	public AbaStatistics getAbaStatistics(AbaStatisticsController controller)
			throws BlockingException {
		Precondition.assertNonNullArgument("null controller", controller);
		if (abaStatistics == null) {
			cacheAbaStatics(controller, getDataSource());
			final String bcId = this.getBlockingConfigurationId();
			abaStatistics = controller.getStatistics(bcId);
			if (abaStatistics == null) {
				String msg =
					"Abastatistics are not available for blocking configuration: "
							+ bcId;
				logger.severe(msg);
				throw new BlockingException(msg);
			}
		}
		return abaStatistics;
	}

	public <T extends Comparable<T> & Serializable> AutomatedBlocker getAutomatedBlocker(
			final Record<T> q, AbaSettings settings,
			AbaStatisticsController statsController) throws BlockingException {
		Precondition.assertNonNullArgument("null record", q);
		Precondition.assertNonNullArgument("null settings", settings);
		Precondition.assertNonNullArgument("null controller", statsController);

		final int limitPBS = settings.getLimitPerBlockingSet();
		final int stbsgl = settings.getSingleTableBlockingSetGraceLimit();
		final int limitSBS = settings.getLimitSingleBlockingSet();
		final String databaseConfiguration =
			parameters.getReferenceDatabaseConfiguration();
		final String blockingConfiguration =
			parameters.getQueryToReferenceBlockingConfiguration();

		final AbaStatistics stats = this.getAbaStatistics(statsController);
		AutomatedBlocker retVal = new Blocker2(getDatabaseAccessor(),
				getModel(), q, limitPBS, stbsgl, limitSBS, stats,
				databaseConfiguration, blockingConfiguration);
		logger.fine(q.getId() + " " + retVal + " " + model);

		return retVal;
	}

	public String getBlockingConfigurationId() {
		if (blockingConfigurationId == null) {
			ImmutableProbabilityModel m = getModel();
			String bc = parameters.getQueryToReferenceBlockingConfiguration();
			String dbc = parameters.getReferenceDatabaseConfiguration();
			blockingConfigurationId = BlockingConfigurationUtils
					.createBlockingConfigurationId(m, bc, dbc);
		}
		return blockingConfigurationId;
	}

	public DatabaseAccessor getDatabaseAccessor() throws BlockingException {
		if (databaseAccessor == null) {
			// Get an accessor
			String dbaName = parameters.getDatabaseAccessorName();
			databaseAccessor =
				DatabaseAccessorUtils.getDatabaseAccessor(dbaName);

			// Set the data source that the accessor will use
			databaseAccessor.setDataSource(getDataSource());

			// Set the selection criteria that the accessor will use
			final DbReaderParallel dbr = getDbReaderParallel();
			final String masterId = dbr.getMasterId();
			final String referenceQuery =
				parameters.getReferenceSelectionViewAsSQL();
			final String condition =
				DatabaseAccessorUtils.parseSQL(referenceQuery, masterId);
			logger.fine("Condition: " + condition);
			if (condition != null && StringUtils.nonEmptyString(condition)) {
				String[] cs = new String[2];
				cs[0] = " ";
				cs[1] = condition;
				databaseAccessor.setCondition(cs);
			} else {
				databaseAccessor.setCondition("");
			}
		}
		return databaseAccessor;
	}

	public DataSource getDataSource() throws BlockingException {
		if (dataSource == null) {
			String jndiName = parameters.getReferenceDatasource();
			dataSource = getDataSource(jndiName);
		}
		return dataSource;
	}

	public DbReaderParallel getDbReaderParallel() {
		if (dbReaderParallel == null) {
			String dbrName = parameters.getDatabaseReaderName();
			Accessor acc = model.getAccessor();
			dbReaderParallel = ((DbAccessor) acc).getDbReaderParallel(dbrName);
		}
		return dbReaderParallel;
	}

	public ImmutableProbabilityModel getModel() {
		if (model == null) {
			String modelName = parameters.getModelConfigurationName();
			model = PMManager.getImmutableModelInstance(modelName);
		}
		return model;
	}

}
