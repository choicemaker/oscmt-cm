/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.xmlenc.xmlconf;

import java.util.logging.Logger;

import org.jdom.Document;
import org.jdom.Element;

import com.choicemaker.cm.core.xmlconf.XmlConfigurator;

/**
 * XML configuration for the connection cache. Uses C3P0 to implement the cache.
 * 
 * See www.mchange.com/projects/c3p0/
 */
public class XmlEncInitializer {

	private static Logger logger = Logger
			.getLogger(XmlEncInitializer.class.getName());

	private static boolean alreadyInited = false;
	
	public static void init() {
		Document d = XmlConfigurator.getInstance().getDocument();
		init(d);
	}

	public static void init(Document d) {
		Element db = XmlConfigurator.getPlugin(d, "xmlenc");
		init(db);
	}

	public static void init(Element db) {
		if (db != null) {
			for (Object o : db.getChildren("credentialSet")) {
				try {
					@SuppressWarnings("unused")
					Element cp = (Element) o;
				} catch (Exception ex) {
					logger.warning("Error creating credential set: " + ex);
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
