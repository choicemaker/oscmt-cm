/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.db.base.xmlconf;

import java.util.Iterator;
import java.util.logging.Logger;

import org.jasypt.encryption.StringEncryptor;
import org.jdom2.Document;
import org.jdom2.Element;

import com.choicemaker.cm.core.configure.ConfigurationUtils;
import com.choicemaker.cm.core.xmlconf.XmlConfigurator;
import com.choicemaker.cm.io.db.base.DataSources;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * XML configuration for the connection cache. Uses C3P0 to implement the cache.
 * 
 * See www.mchange.com/projects/c3p0/
 */
public class ConnectionPoolDataSourceXmlConf {

	/** Default limit to retry attempts */
	public static final int DEFAULT_ACQUIRE_RETRY_ATTEMPTS = 10;

	/** Default delay before retrying a connection (msec) */
	public static final int DEFAULT_AQUIRE_RETRY_DELAY = 1500;

	/** Default overall limit to connection checkout (msec) */
	public static final int DEFAULT_CHECKOUT_TIME = DEFAULT_ACQUIRE_RETRY_ATTEMPTS
			* DEFAULT_AQUIRE_RETRY_DELAY;

	private static Logger logger = Logger
			.getLogger(ConnectionPoolDataSourceXmlConf.class.getName());

	private static boolean alreadyInited = false;

	public static void init() {
		StringEncryptor encryptor = XmlConfigurator.getInstance().getStringEncryptor();
		Document d = XmlConfigurator.getInstance().getDocument();
		init(d, encryptor);
	}

	public static void init(StringEncryptor encryptor) {
		Document d = XmlConfigurator.getInstance().getDocument();
		init(d, encryptor);
	}

	public static void init(Document d, StringEncryptor encryptor) {
		Element db = XmlConfigurator.getPlugin(d, "db");
		init(db, encryptor);
	}

	public static void init(Element db, StringEncryptor encryptor) {
		if (db != null) {
			Iterator i = db.getChildren("ConnectionPool").iterator();
			while (i.hasNext()) {
				try {
					Element e = (Element) i.next();
					ComboPooledDataSource cpds = new ComboPooledDataSource();

					// set and load the underlying driver
					final String jdbcDriver = ConfigurationUtils.getChildText(
							e, "driver", encryptor);
					cpds.setDriverClass(jdbcDriver);

					// the URL to connect the underlyng driver with the server
					final String jdbcURL = ConfigurationUtils.getChildText(e,
							"url", encryptor);
					cpds.setJdbcUrl(jdbcURL);

					// security credentials
					final String user = ConfigurationUtils.getChildText(e,
							"user", encryptor);
					cpds.setUser(user);

					final String password = ConfigurationUtils.getChildText(e,
							"password", encryptor);
					cpds.setPassword(password);

					// the initial size of the pool.
					String clearText = ConfigurationUtils.getChildText(e,
							"initialSize", encryptor);
					if (clearText != null) {
						Integer poolInitialSize = Integer.valueOf(clearText);
						cpds.setMinPoolSize(poolInitialSize.intValue());
					}

					// the maximum size the pool can grow to.
					clearText = ConfigurationUtils.getChildText(e, "maxSize",
							encryptor);
					if (clearText != null) {
						Integer poolMaxSize = Integer.valueOf(clearText);
						cpds.setMaxPoolSize(poolMaxSize.intValue());
					}

					// each time the pool grows, it grows by this many
					// connections
					clearText = ConfigurationUtils.getChildText(e, "growBlock",
							encryptor);
					if (clearText != null) {
						Integer poolGrowBack = Integer.valueOf(clearText);
						cpds.setAcquireIncrement(poolGrowBack.intValue());
					}

					cpds.setAcquireRetryAttempts(DEFAULT_ACQUIRE_RETRY_ATTEMPTS);
					cpds.setAcquireRetryDelay(DEFAULT_AQUIRE_RETRY_DELAY);
					cpds.setCheckoutTimeout(DEFAULT_CHECKOUT_TIME);

					final String name = e.getAttributeValue("name");
					DataSources.addDataSource(name, cpds);

				} catch (Exception ex) {
					logger.warning("Error creating connection pool: " + ex);
				}
			}
		}

		alreadyInited = true;
	}

	/**
	 * Conditionally calls init() if it hasn't been called before over the life
	 * of the VM that this process is running in.
	 *
	 * @return whether or not init() was actually called.
	 */
	public static boolean maybeInit() {
		if (!alreadyInited) {
			init();
			return true;
		} else {
			return false;
		}
	}

	public static void close() {
		// JdbcConnectionPoolDriver.shutdownAllConnections();
		throw new RuntimeException("not yet implemented");
	}
}
