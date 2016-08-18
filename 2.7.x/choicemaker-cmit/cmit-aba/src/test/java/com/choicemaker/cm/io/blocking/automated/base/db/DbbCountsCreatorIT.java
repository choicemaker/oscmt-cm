package com.choicemaker.cm.io.blocking.automated.base.db;

import static com.choicemaker.cm.io.blocking.automated.base.db.ConfigurationUtils.SQLSERVER_CONFIG_FILE;
import static com.choicemaker.cm.io.blocking.automated.base.db.ConfigurationUtils.TARGET_NAME;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.core.base.PMManager;
import com.choicemaker.cm.io.db.base.DataSources;
import com.choicemaker.cm.io.db.base.xmlconf.ConnectionPoolDataSourceXmlConf;
import com.choicemaker.e2.CMExtension;
import com.choicemaker.e2.embed.EmbeddedPlatform;
import com.choicemaker.e2.platform.CMPlatformUtils;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DbbCountsCreatorIT {

	private static final Logger logger = Logger
			.getLogger(DbbCountsCreatorIT.class.getName());

	public static final String PN_SQLSERVER_DB_JDBCURL =
		"sqlserver_db_jdbcUrl";

	public static final String PN_SQLSERVER_DB_USER =
		"sqlserver_db_user";

	public static final String PN_SQLSERVER_DB_PASSWORD =
		"sqlserver_db_password";
	
	public static final String DB_PLUGIN = "db";
	
	public static final String SPM_PLUGIN_ID =
		"com.choicemaker.cm.simplePersonMatching";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		EmbeddedPlatform.install();
		CMExtension[] exts = CMPlatformUtils.getPluginExtensions(SPM_PLUGIN_ID);
		logger.info("DEBUG simple person matching extension count: " + exts.length);
		int count = PMManager.loadModelPlugins();
		if (count == 0) {
			logger.warning("No probability models loaded");
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	
	public static String createPasswordHint(String password) {
		final int PASSWORD_HINT_LENGTH = 3;
		final String ELLIPSIS = "...";

		final String retVal;
		if (password == null) {
			retVal = "null";
		} else if (password.length() < PASSWORD_HINT_LENGTH) {
			retVal = ELLIPSIS;
		} else {
			retVal =
				password.substring(0, PASSWORD_HINT_LENGTH) + ELLIPSIS;
		}
		return retVal;
	}

	private String jdbcUrl;
	private String user;
	private String password;
	private DataSource dataSource;

	@Before
	public void setUp() throws Exception {

		jdbcUrl = System.getProperty(PN_SQLSERVER_DB_JDBCURL);
		user = System.getProperty(PN_SQLSERVER_DB_USER);
		password = System.getProperty(PN_SQLSERVER_DB_PASSWORD);

		final String hint0 = createPasswordHint(password);
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.println("JDBC parameters");
		pw.println("    jdbcUrl: " + jdbcUrl);
		pw.println("       user: " + user);
		pw.println("   password: " + hint0);
		pw.println();
		logger.info(sw.toString());
		
		Document d = null;
		try {
			d = ConfigurationUtils.readConfigurationFromResource(SQLSERVER_CONFIG_FILE);
		} catch (XmlConfException | JDOMException | IOException e) {
			logger.severe(e.toString());
			// fail(e.toString());
			throw e;
		}
		ConnectionPoolDataSourceXmlConf.init(d, null);
		
		int count = 0;
		for (Object o : DataSources.getDataSourceNames()) {
			String dsName = (String) o;
			logger.info("DataSource name: " + dsName);
			if (TARGET_NAME.equals(dsName)) {
				++count;
				dataSource = DataSources.getDataSource(dsName);
				assertTrue(dataSource instanceof ComboPooledDataSource);
				ComboPooledDataSource cpds = (ComboPooledDataSource) dataSource;
				
				// Update the data source parameters from the System properties
				if (user != null) {
					cpds.setUser(user);
				}
				if (password != null) {
					cpds.setPassword(password);
				}
				if (jdbcUrl != null) {
					cpds.setJdbcUrl(jdbcUrl);
				}

				String hint = createPasswordHint(cpds.getPassword());
				sw = new StringWriter();
				pw = new PrintWriter(sw);
				pw.println("DataSource parameters");
				pw.println("    jdbcUrl: " + cpds.getJdbcUrl());
				pw.println("       user: " + cpds.getUser());
				pw.println("   password: " + hint);
				logger.info(sw.toString());
			}
		}
		if (count == 0) {
			logger.warning("DataSource '" + TARGET_NAME + "' not found");
		} else if (count > 1) {
			String msg = "Multiple data sources named '" + TARGET_NAME + "'";
			logger.severe(msg);
			throw new Error(msg);
		} else {
			logger.info("Updated DataSource '" + TARGET_NAME + "'");
		}
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testInstall() {
		DbbCountsCreator cc = new DbbCountsCreator();
		try {
			cc.install(dataSource);
		} catch (SQLException e) {
			logger.severe(e.toString());
			fail(e.toString());
		}
	}

//	 @Test
//	 public void testCreateDataSourceBoolean() {
//	 }
	
	// @Test
	// public void testCreateDataSourceDatabaseAbstractionBoolean() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testSetCacheCountSources() {
	// fail("Not yet implemented");
	// }

}
