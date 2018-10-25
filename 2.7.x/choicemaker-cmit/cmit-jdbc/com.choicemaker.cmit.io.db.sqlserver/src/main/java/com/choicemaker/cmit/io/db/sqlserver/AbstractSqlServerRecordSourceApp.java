package com.choicemaker.cmit.io.db.sqlserver;

import static com.choicemaker.cmit.io.db.oracle.JdbcTestUtils.loadProperties;
import static com.choicemaker.cmit.io.db.oracle.JdbcTestUtils.logProperty;
import static com.choicemaker.cmit.io.db.sqlserver.C3P0_DataSource.PN_POOLNAME;
import static com.choicemaker.cmit.io.db.sqlserver.C3P0_DataSource.configureDatasource;
import static com.choicemaker.cmit.io.db.sqlserver.SqlServerTestProperties.PN_DATABASE_CONFIGURATION;
import static com.choicemaker.cmit.io.db.sqlserver.SqlServerTestProperties.PN_MODEL_NAME;
import static com.choicemaker.cmit.io.db.sqlserver.SqlServerTestProperties.PN_SQL_RECORD_SELECTION;

import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.ModelConfigurationException;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.RecordSource;
import com.choicemaker.cm.core.base.PMManager;
import com.choicemaker.cm.io.db.base.DataSources;
import com.choicemaker.util.Precondition;

public abstract class AbstractSqlServerRecordSourceApp<T extends Comparable<T>> {

	private static final Logger log =
		Logger.getLogger(AbstractSqlServerRecordSourceApp.class.getName());

	private static final String SOURCE = "AbstractSqlServerRecordSourceApp";

	/** Number of records between when debug print of the current record id */
	public static final int COUNT_RECORDS_BETWEEN_DEBUG_PRINTS = 5000;
	/** Number of records between when info print of the current record id */
	public static final int COUNT_RECORDS_BETWEEN_INFO_PRINTS = 50000;

	public static <T extends Comparable<T>> void logRecord(int count,
			Record<T> r) {
		AbstractSqlServerRecordSourceApp.logRecord(count, r,
				log.isLoggable(Level.FINEST));
	}

	public static <T extends Comparable<T>> void logRecord(int count,
			Record<T> r, boolean isLoggable) {
		if (isLoggable) {
			String sid = r == null ? null : r.getId().toString();
			System.err.println(String.format("%8d: %s", count, sid));
		}
	}

	public static ImmutableProbabilityModel lookupProbabilityModel(
			String modelName) throws ModelConfigurationException, IOException {

		Precondition.assertNonEmptyString("Empty modelName", modelName);
		PMManager.loadModelPlugins();
		ImmutableProbabilityModel retVal =
			PMManager.getImmutableModelInstance(modelName);
		if (retVal == null) {
			String msg = String.format("missing model named '%s'", modelName);
			log.warning(msg);
		}

		return retVal;
	}

	private final String propertyFileName;

	private final Properties properties;

	private final String datasourceName;

	private final String modelName;

	private final String databaseConfigurationName;

	private final String sqlRecordSelectionQuery;

	public AbstractSqlServerRecordSourceApp(String _propertyFileName)
			throws IOException, SQLException, ModelConfigurationException {

		Precondition.assertNonEmptyString("missing name of property file",
				_propertyFileName);
		this.propertyFileName = _propertyFileName;

		FileReader fr = new FileReader(propertyFileName);
		this.properties = loadProperties(fr);

		this.datasourceName = properties.getProperty(PN_POOLNAME);
		Precondition.assertNonEmptyString("missing name for data source",
				propertyFileName);
		logProperty(PN_MODEL_NAME, datasourceName);

		final DataSource ds = configureDatasource(properties);
		assert ds != null;
		final DataSource ds2 = DataSources.getDataSource(datasourceName);
		Precondition.assertBoolean(
				String.format("misconfigured data source '%s'", datasourceName),
				ds == ds2);

		this.modelName = properties.getProperty(PN_MODEL_NAME);
		Precondition.assertNonEmptyString("missing name for model", modelName);
		logProperty(PN_MODEL_NAME, modelName);

		this.databaseConfigurationName =
			properties.getProperty(PN_DATABASE_CONFIGURATION);
		Precondition.assertNonEmptyString("missing database configuration name",
				databaseConfigurationName);
		logProperty(PN_DATABASE_CONFIGURATION, databaseConfigurationName);

		this.sqlRecordSelectionQuery =
			properties.getProperty(PN_SQL_RECORD_SELECTION);
		Precondition.assertNonEmptyString("missing SQL for record selection",
				sqlRecordSelectionQuery);
		logProperty(PN_SQL_RECORD_SELECTION, sqlRecordSelectionQuery);
	}

	public abstract RecordSource createRecordSource(
			ImmutableProbabilityModel model)
			throws ModelConfigurationException, IOException;
	// {
	// Precondition.assertNonNullArgument("null model", model);
	//
	// final String unusedFileName = null;
	// RecordSource rs =
	// new SqlServerRecordSource(unusedFileName, model, datasourceName,
	// databaseConfigurationName, sqlRecordSelectionQuery);
	// return rs;
	// }

	public String doDownloadReportStats()
			throws BlockingException, ModelConfigurationException, IOException {
		final String modelName = this.getModelName();
		final ImmutableProbabilityModel model =
			lookupProbabilityModel(modelName);
		final RecordSource rs = this.createRecordSource(model);

		final long start = System.currentTimeMillis();
		int count = this.downloadRecords(rs, model);

		final long duration = System.currentTimeMillis() - start;
		final float seconds = duration / 1000f;
		final float rate = duration == 0 ? 0f : count / seconds;

		final String appName = this.getClass().getSimpleName();
		String msg = String.format(
				"App: %s, Records: %d, duration: %f (sec), rate: %f (recs/sec)",
				appName, count, seconds, rate);
		log.info(msg);
		return msg;
	}

	public int downloadRecords(RecordSource rs, ImmutableProbabilityModel model)
			throws BlockingException {

		final String METHOD = "downloadRecords";
		log.entering(SOURCE, METHOD, new Object[] {
				rs, model });
		assert rs != null;
		assert model != null;

		int retVal = 0;
		try {
			rs.setModel(model);
			rs.open();

			while (rs.hasNext()) {
				@SuppressWarnings("unchecked")
				Record<T> r = rs.getNext();
				Comparable<T> id = r.getId();
				boolean isCheckpoint = (retVal
						% AbstractSqlServerRecordSourceApp.COUNT_RECORDS_BETWEEN_DEBUG_PRINTS == 0)
						|| (retVal
								% AbstractSqlServerRecordSourceApp.COUNT_RECORDS_BETWEEN_INFO_PRINTS == 0);
				if (isCheckpoint) {
					String msg =
						String.format("Record %s / count %d", id, retVal);
					if (log.isLoggable(Level.INFO)) {
						log.info(msg);
					} else if (log.isLoggable(Level.FINE)) {
						log.fine(msg);
					}
				}
				logRecord(retVal, r);
				retVal++;
			}

		} catch (Exception ex) {
			throw new BlockingException(ex.toString());
		} finally {
			try {
				rs.close();
			} catch (IOException e) {
				log.warning(String.format(
						"Unable to close record source (%s): %s", rs, e));
			}
		}

		return retVal;
	}

	public String getDatabaseConfigurationName() {
		return databaseConfigurationName;
	}

	public String getDatasourceName() {
		return datasourceName;
	}

	public String getModelName() {
		return modelName;
	}

	public Properties getProperties() {
		return properties;
	}

	public String getPropertyFileName() {
		return propertyFileName;
	}

	public String getSqlRecordSelectionQuery() {
		return sqlRecordSelectionQuery;
	}

}