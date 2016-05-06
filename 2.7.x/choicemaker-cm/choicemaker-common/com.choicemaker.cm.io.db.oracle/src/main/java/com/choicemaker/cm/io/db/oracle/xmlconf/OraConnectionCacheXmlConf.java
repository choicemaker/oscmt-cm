/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.db.oracle.xmlconf;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.sql.DataSource;

import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;

import org.jdom.Element;

import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.core.xmlconf.XmlConfigurator;
import com.choicemaker.cm.io.db.base.DataSources;

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

	public static final String DEFAULT_CONNECTION_FACTORY_CLASS_NAME =
		"oracle.jdbc.pool.OracleDataSource";

	public static final int MIN_CONNECTION_LIMIT = 1;

	public static final int MIN_PORT_NUMBER = 1;
	public static final int DEFAULT_PORT_NUMBER = 1521;

	private static Map caches = new TreeMap();

	public static void init() {
		try {
			String[] l = list();
			for (int i = 0; i < l.length; ++i) {
				DataSources.addDataSource(l[i], getConnectionCache(l[i]));
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
	public static DataSource getConnectionCache(String name)
			throws java.sql.SQLException, XmlConfException {
		DataSource cc = (DataSource) caches.get(name);
		if (cc != null) {
			return cc;
		} else {
			Element o = XmlConfigurator.getInstance().getPlugin("oracle");
			if (o != null) {
				Iterator i = o.getChildren("OraConnectionCache").iterator();
				while (i.hasNext()) {
					Element x = (Element) i.next();
					if (name.equals(x.getAttributeValue(PV_POOL_NAME))) {
						return getConnectionCache(x);
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
	private static DataSource getConnectionCache(Element e)
			throws java.sql.SQLException {

		// Pool/cache name
		String name = e.getAttributeValue(PV_POOL_NAME);
		logProperty(PV_POOL_NAME, name);

		// Host/server name
		String serverName = e.getChildText(PV_SERVER_NAME);
		logProperty(PV_SERVER_NAME, serverName);

		// Database/SID
		String databaseName = e.getChildText(PV_DATABASE_NAME);
		logProperty(PV_DATABASE_NAME, databaseName);

		// Port number
		String sPortNumber = e.getChildText(PV_PORT_NUMBER);
		logProperty(PV_PORT_NUMBER, sPortNumber);
		int portNumber = DEFAULT_PORT_NUMBER;
		try {
			portNumber = Integer.parseInt(sPortNumber);
		} catch (NumberFormatException x) {
			logger.warning("Invalid port number: '" + sPortNumber + "':"
					+ x.toString());
		}
		if (portNumber < MIN_PORT_NUMBER) {
			logger.info("Resetting connection limit to: "
					+ DEFAULT_PORT_NUMBER);
		}

		// JDBC URL
		String jdbcUrl = e.getChildText(PV_JDBC_URL);
		if (jdbcUrl == null || jdbcUrl.trim().isEmpty()) {
			jdbcUrl = createJdbcUrl(serverName, portNumber, databaseName);
		}
		logProperty(PV_JDBC_URL, jdbcUrl);

		// User name
		String user = e.getChildText(PV_USER_NAME);
		logProperty(PV_USER_NAME, user);

		// Password
		String password = e.getChildText(PV_PASSWORD);
		logProperty(PV_PASSWORD, password);

		// Connection limit
		String sConnectionLimit = e.getChildText(PV_CONNECTION_LIMIT);
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
		String protocol = e.getChildText(PV_NETWORK_PROTOCOL);
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
		caches.put(name, cc);
		return cc;
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
		Element o = XmlConfigurator.getInstance().getPlugin("oracle");
		if (o != null) {
			List l = o.getChildren("OraConnectionCache");
			String[] caches = new String[l.size()];
			int i = 0;
			Iterator iL = l.iterator();
			while (iL.hasNext()) {
				Element e = (Element) iL.next();
				caches[i++] = e.getAttributeValue(PV_POOL_NAME);
			}
			return caches;
		} else {
			return new String[0];
		}
	}

	public static void remove(String name) {
		caches.remove(name);
	}
}
