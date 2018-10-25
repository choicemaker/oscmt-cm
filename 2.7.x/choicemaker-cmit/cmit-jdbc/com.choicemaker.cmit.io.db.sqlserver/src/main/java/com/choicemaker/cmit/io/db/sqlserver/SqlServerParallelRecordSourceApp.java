package com.choicemaker.cmit.io.db.sqlserver;

import static com.choicemaker.cmit.io.db.sqlserver.SqlServerTestProperties.PN_PROPERTY_FILE;
import static com.choicemaker.e2.platform.InstallablePlatform.INSTALLABLE_PLATFORM;

import java.io.IOException;
import java.sql.SQLException;

import com.choicemaker.cm.core.ModelConfigurationException;
import com.choicemaker.e2.embed.EmbeddedPlatform;
import com.choicemaker.util.SystemPropertyUtils;

public class SqlServerParallelRecordSourceApp<T extends Comparable<T>>
		extends AbstractSqlServerRecordSourceApp<T> {

	public SqlServerParallelRecordSourceApp(String _propertyFileName)
			throws IOException, SQLException, ModelConfigurationException {
		super(_propertyFileName);
	}

	public static <T extends Comparable<T>> void main(String[] args)
			throws Exception {

		String pName = EmbeddedPlatform.class.getName();
		SystemPropertyUtils.setPropertyIfMissing(INSTALLABLE_PLATFORM, pName);

		String propertyFileName = System.getProperty(PN_PROPERTY_FILE);
		SqlServerParallelRecordSourceApp<T> app =
			new SqlServerParallelRecordSourceApp<>(propertyFileName);

		String stats = app.doDownloadReportStats();
		System.out.println(stats);
	}

}
