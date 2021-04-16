package com.choicemaker.cmit.io.db.sqlserver;

import static com.choicemaker.cmit.io.db.sqlserver.SqlServerTestProperties.PN_PROPERTY_FILE;
import static com.choicemaker.e2.platform.InstallablePlatform.INSTALLABLE_PLATFORM;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.ModelConfigurationException;
import com.choicemaker.cm.core.RecordSource;
import com.choicemaker.cm.io.db.sqlserver.SqlServerParallelRecordSource;
import com.choicemaker.e2.embed.EmbeddedPlatform;
import com.choicemaker.util.Precondition;
import com.choicemaker.util.SystemPropertyUtils;

public class SqlServerParallelRecordSourceApp<T extends Comparable<T>>
		extends AbstractSqlServerRecordSourceApp<T> {

	private static final Logger log =
		Logger.getLogger(SqlServerParallelRecordSourceApp.class.getName());

	protected static final String SOURCE = "SqlServerParallelRecordSourceApp";

	public static <T extends Comparable<T>> void main(String[] args)
			throws Exception {

		final String METHOD = "main";
		log.entering(SOURCE, METHOD, args);

		String pName = EmbeddedPlatform.class.getName();
		SystemPropertyUtils.setPropertyIfMissing(INSTALLABLE_PLATFORM, pName);

		String propertyFileName = System.getProperty(PN_PROPERTY_FILE);
		SqlServerParallelRecordSourceApp<T> app =
			new SqlServerParallelRecordSourceApp<>(propertyFileName);

		DownloadStats stats = app.doDownloadReportStats();
		System.err.println(stats.toString());
		System.out.println(stats.toCSV());

		log.exiting(SOURCE, METHOD);
	}

	public SqlServerParallelRecordSourceApp(String _propertyFileName)
			throws IOException, SQLException, ModelConfigurationException {
		super(_propertyFileName);
	}

	@Override
	public RecordSource createRecordSource(ImmutableProbabilityModel model)
			throws ModelConfigurationException, IOException {
		Precondition.assertNonNullArgument("null model", model);

		final String unusedFileName = null;
		RecordSource rs = new SqlServerParallelRecordSource(unusedFileName,
				model, getDatasourceName(), getDatabaseConfigurationName(),
				getSqlRecordSelectionQuery());
		return rs;
	}

}
