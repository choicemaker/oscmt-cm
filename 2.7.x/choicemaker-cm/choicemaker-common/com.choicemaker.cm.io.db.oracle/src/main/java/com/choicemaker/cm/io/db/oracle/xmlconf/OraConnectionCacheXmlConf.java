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
 * @author Martin Buechi
 */
public class OraConnectionCacheXmlConf {
	private static Logger logger = Logger
			.getLogger(OraConnectionCacheXmlConf.class.getName());

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
					if (name.equals(x.getAttributeValue("name"))) {
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
		String name = e.getAttributeValue("name");
		String driverType = e.getChildText("driverType");
		if (driverType != null) {
			String msg = "Ignoring driverType ('" + driverType + "')";
			logger.fine(msg);
		}
		String serverName = e.getChildText("serverName");
		String networkProtocol = e.getChildText("networkProtocol");
		String databaseName = e.getChildText("databaseName");
		int portNumber = Integer.parseInt(e.getChildText("portNumber"));
		String user = e.getChildText("user");
		String password = e.getChildText("password");
		int connectionLimit =
			Integer.parseInt(e.getChildText("connectionLimit"));
		// DataSource cc = new OracleConnectionCacheImpl();
		PoolDataSource cc = PoolDataSourceFactory.getPoolDataSource();
		// cc.setDriverType(driverType);
		cc.setServerName(serverName);
		cc.setNetworkProtocol(networkProtocol);
		cc.setDatabaseName(databaseName);
		cc.setPortNumber(portNumber);
		cc.setUser(user);
		cc.setPassword(password);
		// cc.setMaxLimit(connectionLimit);
		cc.setMaxPoolSize(connectionLimit);
		cc.setDataSourceName(name);
		// cc.setCacheScheme(OracleConnectionCacheImpl.FIXED_WAIT_SCHEME);
		caches.put(name, cc);
		return cc;
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
				caches[i++] = e.getAttributeValue("name");
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
