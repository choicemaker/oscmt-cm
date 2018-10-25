package com.choicemaker.cmit.io.db.sqlserver;

import static com.choicemaker.cmit.io.db.oracle.JdbcTestUtils.loadProperties;
import static com.choicemaker.cmit.io.db.oracle.JdbcTestUtils.logProperty;
import static com.choicemaker.cmit.io.db.sqlserver.C3P0_DataSource.PN_POOLNAME;
import static com.choicemaker.cmit.io.db.sqlserver.C3P0_DataSource.configureDatasource;
import static com.choicemaker.cmit.io.db.sqlserver.SqlServerTestProperties.PN_DATABASE_CONFIGURATION;
import static com.choicemaker.cmit.io.db.sqlserver.SqlServerTestProperties.PN_MODEL_NAME;
import static com.choicemaker.cmit.io.db.sqlserver.SqlServerTestProperties.PN_PROPERTY_FILE;
import static com.choicemaker.cmit.io.db.sqlserver.SqlServerTestProperties.PN_SQL_RECORD_SELECTION;
import static com.choicemaker.e2.platform.InstallablePlatform.INSTALLABLE_PLATFORM;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.RecordSource;
import com.choicemaker.cm.core.base.PMManager;
import com.choicemaker.cm.io.db.base.DataSources;
import com.choicemaker.cm.io.db.sqlserver.SqlServerRecordSource;
import com.choicemaker.e2.embed.EmbeddedPlatform;
import com.choicemaker.util.Precondition;
import com.choicemaker.util.SystemPropertyUtils;

public class SqlServerRecordSourceApp {

	private static final Logger log =
		Logger.getLogger(SqlServerRecordSourceApp.class.getName());

	/** Number of records between when debug print of the current record id */
	public static final int COUNT_RECORDS_BETWEEN_DEBUG_PRINTS = 5000;

	/** Number of records between when info print of the current record id */
	public static final int COUNT_RECORDS_BETWEEN_INFO_PRINTS = 50000;

	protected static final String SOURCE = "SqlServerRecordSourceApp";

	public static <T extends Comparable<T>> void main(String[] args)
			throws Exception {

		String pName = EmbeddedPlatform.class.getName();
		SystemPropertyUtils.setPropertyIfMissing(INSTALLABLE_PLATFORM, pName);

		String propertyFileName = System.getProperty(PN_PROPERTY_FILE);
		Precondition.assertNonEmptyString("missing name of property file",
				propertyFileName);
		FileReader fr = new FileReader(propertyFileName);
		Properties p = loadProperties(fr);

		final String dsName = p.getProperty(PN_POOLNAME);
		Precondition.assertNonEmptyString("missing name for data source",
				propertyFileName);
		logProperty(PN_MODEL_NAME, dsName);

		final DataSource ds = configureDatasource(p);
		assert ds != null;
		final DataSource ds2 = DataSources.getDataSource(dsName);
		Precondition.assertBoolean(
				String.format("misconfigured data source '%s'", dsName),
				ds == ds2);

		String modelName = p.getProperty(PN_MODEL_NAME);
		Precondition.assertNonEmptyString("missing name for model", modelName);
		logProperty(PN_MODEL_NAME, modelName);

		PMManager.loadModelPlugins();
		ImmutableProbabilityModel model =
			PMManager.getImmutableModelInstance(modelName);
		Precondition.assertNonEmptyString(
				String.format("missing model named '%s'", modelName),
				modelName);

		String databaseConfiguration = p.getProperty(PN_DATABASE_CONFIGURATION);
		Precondition.assertNonEmptyString("missing database configuration name",
				databaseConfiguration);
		logProperty(PN_DATABASE_CONFIGURATION, databaseConfiguration);

		String selection = p.getProperty(PN_SQL_RECORD_SELECTION);
		Precondition.assertNonEmptyString("missing SQL for record selection",
				selection);
		logProperty(PN_SQL_RECORD_SELECTION, selection);

		// START
		String unusedFileName = null;
		RecordSource rs = new SqlServerRecordSource(unusedFileName, model,
				dsName, databaseConfiguration, selection);

		final long start = System.currentTimeMillis();
		int count = downloadRecords(rs, model);

		final long duration = System.currentTimeMillis() - start;
		final float seconds = duration / 1000f;
		final float rate = duration == 0 ? 0f : count / seconds;

		String msg = String.format(
				"Records: %d, duration: %f (sec), rate: %f (recs/sec)", count,
				seconds, rate);
		log.info(msg);
		System.out.println(msg);
	}

	public static <T extends Comparable<T>> int downloadRecords(RecordSource rs,
			ImmutableProbabilityModel model) throws BlockingException {

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
				Record<T> r = (Record<T>) rs.getNext();
				Comparable<T> id = r.getId();
				boolean isCheckpoint = (retVal
						% COUNT_RECORDS_BETWEEN_DEBUG_PRINTS == 0)
						|| (retVal % COUNT_RECORDS_BETWEEN_INFO_PRINTS == 0);
				if (isCheckpoint) {
					String msg =
						String.format("Record %s / count %d", id, retVal);
					if (log.isLoggable(Level.INFO)) {
						log.info(msg);
					} else if (log.isLoggable(Level.FINE)) {
						log.fine(msg);
					}
				}
				logRecord(retVal,r);
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

	public static <T extends Comparable<T>> void logRecord(int count,
			Record<T> r) {
		if (log.isLoggable(Level.FINEST)) {
			String sid = r == null ? null : r.getId().toString();
			System.err.println(String.format("%8d: %s", count, sid));
		}
	}

}
