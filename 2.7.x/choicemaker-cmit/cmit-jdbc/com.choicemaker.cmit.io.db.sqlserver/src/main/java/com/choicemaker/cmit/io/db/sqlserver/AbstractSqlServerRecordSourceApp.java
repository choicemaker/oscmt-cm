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
		if (log.isLoggable(Level.FINEST)) {
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

	public DownloadStats doDownloadReportStats()
			throws BlockingException, ModelConfigurationException, IOException {
		final String modelName = this.getModelName();
		final ImmutableProbabilityModel model =
			lookupProbabilityModel(modelName);
		final RecordSource rs = this.createRecordSource(model);

		DownloadStats retVal = this.downloadRecords(rs, model);
		log.info(retVal.toString());

		return retVal;
	}

	public DownloadStats downloadRecords(RecordSource rs,
			ImmutableProbabilityModel model) throws BlockingException {

		final String appName = this.getClass().getSimpleName();

		final String METHOD = "downloadRecords";
		log.entering(SOURCE, METHOD, new Object[] {
				rs, model });
		assert rs != null;
		assert model != null;

		DownloadStats retVal = null;
		int count = 0;
		try {
			rs.setModel(model);
			final long start_0 = System.currentTimeMillis();
			rs.open();
			final long acquireMsecs = System.currentTimeMillis() - start_0;
			log.fine(String.format("Connection acquisition (msecs): %d",
					acquireMsecs));

			final long start_1 = System.currentTimeMillis();
			while (rs.hasNext()) {
				@SuppressWarnings("unchecked")
				Record<T> r = rs.getNext();
				Comparable<T> id = r.getId();
				boolean isCheckpoint = (count
						% AbstractSqlServerRecordSourceApp.COUNT_RECORDS_BETWEEN_DEBUG_PRINTS == 0)
						|| (count
								% AbstractSqlServerRecordSourceApp.COUNT_RECORDS_BETWEEN_INFO_PRINTS == 0);
				if (isCheckpoint) {
					String msg =
						String.format("Record %s / count %d", id, count);
					if (log.isLoggable(Level.INFO)) {
						log.info(msg);
					} else if (log.isLoggable(Level.FINE)) {
						log.fine(msg);
					}
				}
				logRecord(count, r);
				count++;
			}
			final long downloadMsecs = System.currentTimeMillis() - start_1;
			log.fine(String.format("Download duration (msecs): %d",
					downloadMsecs));

			retVal =
				new DownloadStats(appName, acquireMsecs, count, downloadMsecs);

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