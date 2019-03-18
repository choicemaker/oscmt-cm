/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.db.oracle.xmlconf;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.jasypt.encryption.StringEncryptor;
import org.jdom2.Element;

import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.core.configure.ConfigurationUtils;
import com.choicemaker.cm.core.xmlconf.XmlConfigurator;
import com.choicemaker.cm.io.db.base.DataSources;

import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;

/**
 * XML configurator for Oracle Connection Cache.
 *
 * @author Martin Buechi (original version)
 * @author rphall (rewrote for ODJBC6/UCP)
 */
public class OraConnectionCacheXmlConf {

	private static Logger logger = Logger
			.getLogger(OraConnectionCacheXmlConf.class.getName());

	public static final String PV_POOL_NAME = "name";

	public static final String PV_DRIVER_TYPE = "driverType";

	public static final String PV_SERVER_NAME = "serverName";

	public static final String PV_NETWORK_PROTOCOL = "networkProtocol";

	public static final String PV_DATABASE_NAME = "databaseName";

	public static final String PV_PORT_NUMBER = "portNumber";

	public static final String PV_USER_NAME = "user";

	public static final String PV_PASSWORD = "password";

	public static final String PV_JDBC_URL = "jdbcURL";

	public static final String PV_CONNECTION_LIMIT = "connectionLimit";

	public static final String DEFAULT_CONNECTION_FACTORY_CLASS_NAME = "oracle.jdbc.pool.OracleDataSource";

	public static final int MIN_CONNECTION_LIMIT = 1;

	public static final int MIN_PORT_NUMBER = 1;
	public static final int DEFAULT_PORT_NUMBER = 1521;

	private static Map<String, DataSource> caches = new TreeMap<>();

	public static void init(StringEncryptor encryptor) {
		try {
			for (String name : list()) {
				DataSource ds = getConnectionCache(name, encryptor);
				DataSources.addDataSource(name, ds);
			}
		} catch (Exception ex) {
			logger.severe(ex.toString());
		}
	}

	/**
	 * Returns a connection corresponding to the specifications.
	 *
	 * @param name
	 *            The name of the configuration.
	 * @throws SQLException
	 *             if an SQLException occurs in the creation of the connection
	 *             cache.
	 * @throws XmlConfException
	 *             if there is a problem with the configuration file.
	 */
	public static DataSource getConnectionCache(String name,
			StringEncryptor encryptor) throws java.sql.SQLException,
			XmlConfException {
		DataSource cc = caches.get(name);
		if (cc != null) {
			return cc;
		} else {
			Element e = XmlConfigurator.getInstance().getPlugin("oracle");
			if (e != null) {
				@SuppressWarnings("rawtypes")
				List children = e.getChildren("OraConnectionCache");
				for (Object o : children) {
					Element x = (Element) o;
					if (name.equals(x.getAttributeValue(PV_POOL_NAME))) {
						return getConnectionCache(x, encryptor);
					}
				}
			}
			throw new XmlConfException(
					"Connection cache not found in configuration file: " + name);
		}
	}

	/**
	 * Returns a connection corresponding to the specifications.
	 *
	 * @param element
	 *            The root element of the configuration cache configuration.
	 * @throws SQLException
	 *             if an SQLException occurs in the creation of the connection
	 *             cache.
	 * @throws XmlConfException
	 *             if there is a problem with the configuration file.
	 */
	private static DataSource getConnectionCache(Element e,
			StringEncryptor encryptor) throws java.sql.SQLException {

		// Pool/cache name
		String name = e.getAttributeValue(PV_POOL_NAME);
		logProperty(PV_POOL_NAME, name);

		// Host/server name
		String serverName = e.getChildText(PV_SERVER_NAME);
		logProperty(PV_SERVER_NAME, serverName);

		// Database/SID
		String databaseName = ConfigurationUtils.getChildText(e,
				PV_DATABASE_NAME, encryptor);
		logProperty(PV_DATABASE_NAME, databaseName);

		// Port number
		String sPortNumber = ConfigurationUtils.getChildText(e, PV_PORT_NUMBER, encryptor);
		logProperty(PV_PORT_NUMBER, sPortNumber);
		int portNumber = DEFAULT_PORT_NUMBER;
		try {
			portNumber = Integer.parseInt(sPortNumber);
		} catch (NumberFormatException x) {
			logger.warning("Invalid port number: '" + sPortNumber + "':"
					+ x.toString());
		}
		if (portNumber < MIN_PORT_NUMBER) {
			logger.info("Resetting connection limit to: " + DEFAULT_PORT_NUMBER);
		}

		// JDBC URL
		String jdbcUrl = ConfigurationUtils.getChildText(e, PV_JDBC_URL, encryptor);
		if (jdbcUrl == null || jdbcUrl.trim().isEmpty()) {
			jdbcUrl = createJdbcUrl(serverName, portNumber, databaseName);
		}
		logProperty(PV_JDBC_URL, jdbcUrl);

		// User name
		String user = ConfigurationUtils.getChildText(e, PV_USER_NAME, encryptor);
		logProperty(PV_USER_NAME, user);

		// Password
		String password = ConfigurationUtils.getChildText(e, PV_PASSWORD, encryptor);
		logProperty(PV_PASSWORD, password);

		// Connection limit
		String sConnectionLimit = ConfigurationUtils.getChildText(e, PV_CONNECTION_LIMIT, encryptor);
		logProperty(PV_CONNECTION_LIMIT, sConnectionLimit);
		int connectionLimit = MIN_CONNECTION_LIMIT;
		try {
			connectionLimit = Integer.parseInt(sConnectionLimit);
		} catch (NumberFormatException x) {
			logger.warning("Invalid connection limit: '" + sConnectionLimit
					+ "':" + x.toString());
		}
		if (connectionLimit < MIN_CONNECTION_LIMIT) {
			logger.info("Resetting connection limit to: "
					+ MIN_CONNECTION_LIMIT);
		}

		// Network protocol
		String protocol = ConfigurationUtils.getChildText(e, PV_NETWORK_PROTOCOL, encryptor);
		logProperty(PV_NETWORK_PROTOCOL, protocol);

		// Ignored -- no longer needed
		logIgnoredProperty(PV_DRIVER_TYPE, e);

		PoolDataSource cc = PoolDataSourceFactory.getPoolDataSource();
		cc.setDataSourceName(name);
		cc.setConnectionFactoryClassName(DEFAULT_CONNECTION_FACTORY_CLASS_NAME);
		cc.setURL(jdbcUrl);
		cc.setServerName(serverName);
		cc.setPortNumber(portNumber);
		cc.setDatabaseName(databaseName);
		cc.setUser(user);
		cc.setPassword(password);
		cc.setMaxPoolSize(connectionLimit);
		cc.setNetworkProtocol(protocol);

		DataSource retVal = new DataSourceWrapper(cc);
		caches.put(name, retVal);
		return retVal;
	}

	public static String createJdbcUrl(String host, int port, String database) {
		// jdbc:oracle:thin:@<host>:<port>:<database>
		StringBuilder sb = new StringBuilder("jdbc:oracle:thin:@");
		sb.append(host).append(":").append(port).append(":").append(database);
		String retVal = sb.toString();
		return retVal;
	}

	/** Log a property value */
	private static void logProperty(String pn, String pv) {
		if (pn == null || pn.trim().isEmpty()) {
			String msg = "Null or blank property name for value '" + pv + "'";
			logger.warning(msg);
		} else {
			String msg = "Property '" + pn + "': value '" + pv + "'";
			logger.info(msg);
		}
	}

	/** Log if a property is ignored */
	private static void logIgnoredProperty(String pn, Element e) {
		if (pn != null && !pn.isEmpty() && e != null) {
			String pv = e.getChildText(pn);
			logIgnoredProperty(pn, pv);
		}
	}

	/** Log if non-null, non-empty values of a property is ignored */
	private static void logIgnoredProperty(String pn, String pv) {
		if (pv != null && !pv.isEmpty()) {
			String msg = "Ignoring property '" + pv + "': value '" + pn + "'";
			logger.info(msg);
		}
	}

	public static String[] list() throws XmlConfException {
		Element e = XmlConfigurator.getInstance().getPlugin("oracle");
		if (e != null) {
			@SuppressWarnings("rawtypes")
			List children = e.getChildren("OraConnectionCache");
			String[] caches = new String[children.size()];
			int i = 0;
			for (Object o : children) {
				Element x = (Element) o;
				caches[i++] = x.getAttributeValue(PV_POOL_NAME);
			}
			return caches;
		} else {
			return new String[0];
		}
	}

	public static void remove(String name) {
		caches.remove(name);
	}

	/**
	 * Wraps an existing data source to provide connections that have autoCommit
	 * set to false, by default.
	 */
	public static class DataSourceWrapper implements DataSource {
		private final DataSource ds;

		public DataSourceWrapper(DataSource ds) {
			if (ds == null) {
				throw new IllegalArgumentException("null datasource");
			}
			this.ds = ds;
		}

		@Override
		public PrintWriter getLogWriter() throws SQLException {
			return ds.getLogWriter();
		}

		@Override
		public <T> T unwrap(Class<T> iface) throws SQLException {
			return ds.unwrap(iface);
		}

		@Override
		public void setLogWriter(PrintWriter out) throws SQLException {
			ds.setLogWriter(out);
		}

		@Override
		public boolean isWrapperFor(Class<?> iface) throws SQLException {
			return isWrapperFor(iface);
		}

		@Override
		public Connection getConnection() throws SQLException {
			Connection retVal = ds.getConnection();
			assert retVal != null;
			retVal.setAutoCommit(false);
			return retVal;
		}

		@Override
		public void setLoginTimeout(int seconds) throws SQLException {
			ds.setLoginTimeout(seconds);
		}

		@Override
		public Connection getConnection(String username, String password)
				throws SQLException {
			return ds.getConnection(username, password);
		}

		@Override
		public int getLoginTimeout() throws SQLException {
			return ds.getLoginTimeout();
		}

		@Override
		public Logger getParentLogger() throws SQLFeatureNotSupportedException {
			return ds.getParentLogger();
		}

	}

}
