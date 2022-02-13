/*******************************************************************************
 * Copyright (c) 2015, 2016 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.db.oracle;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

/**
 * Configure a remote Oracle database for debugging, depending on the values
 * system properties.
 * 
 * References:
 * <ul>
 * <li>Sue Harper, 2006-07-13, http://links.rph.cx/XyfaZ5,
 * "Remote Debugging with SQL Developer"</li>
 * 
 * <li>Sanat Pattanaik, 2011-01-14, http://links.rph.cx/16ER9Dw,
 * "Debugging PL-SQL calls from Java Session Using Eclipse and SQL Developer"</li>
 * </ul>
 */
public class OracleRemoteDebugging {

	/**
	 * A boolean-valued System property that specifies whether to activate
	 * debugging of Oracle stored procedures and functions. Requires appropriate
	 * privileges on the target database. See the OracleRemoteDebugging class in
	 * the com.choicemaker.cm.io.db.oracle module for more details. If not set
	 * to <code>TRUE</code>, Oracle remote debugging is not activated.
	 */
	public static final String ORACLE_DEBUGGING = "choicemakerOracleDebugging";

	/**
	 * A String-valued System property that specifies the address to use for
	 * remote Oracle debugging. If not set, the {@link #DEFAULT_HOST default
	 * host address} is used.
	 */
	public static final String ORACLE_DEBUG_HOST = "choicemakerOracleDebugHost";

	/**
	 * An integer-valued System property that specifies the port to use for
	 * remote Oracle debugging.If not set, the {@link #DEFAULT_PORT default host
	 * port} is used.
	 */
	public static final String ORACLE_DEBUG_PORT = "choicemakerOracleDebugPort";

	private static final Logger logger = Logger
			.getLogger(OracleRemoteDebugging.class.getName());

	/** The default host address */
	public static final String DEFAULT_HOST = "127.0.0.1";

	/** The default host port */
	public static final int DEFAULT_PORT = 4000;

	private static final String SQL_FRAG1 =
		"begin DBMS_DEBUG_JDWP.CONNECT_TCP( '";
	private static final String SQL_FRAG2 = "', ";
	private static final String SQL_FRAG3 = "); end;";

	private static final AtomicBoolean DO_DEBUG = new AtomicBoolean(false);
	private static final AtomicReference HOST = new AtomicReference(
			DEFAULT_HOST);
	private static final AtomicInteger PORT = new AtomicInteger(DEFAULT_PORT);
	private static final AtomicInteger WARNING_COUNT = new AtomicInteger(0);

	/**
	 * Looks up a host address specified by the System property
	 * {@link #ORACLE_DEBUG_HOST}
	 */
	public static String getHost() {
		String s = System.getProperty(ORACLE_DEBUG_HOST, (String) HOST.get());
		s = s.trim();
		if (s.isEmpty()) {
			String msg =
				"System property '" + ORACLE_DEBUG_HOST
						+ "' specifies a blank address. "
						+ "The default value '" + DEFAULT_HOST
						+ "' will be used.";
			logger.fine(msg);
			s = DEFAULT_HOST;
		}
		HOST.set(s);
		return (String) HOST.get();
	}

	/**
	 * Looks up a host port specified by the System property
	 * {@link #ORACLE_DEBUG_PORT}
	 */
	public static int getPort() {
		String s =
			System.getProperty(ORACLE_DEBUG_PORT, String.valueOf(PORT.get()));
		s = s.trim();
		if (s.isEmpty()) {
			s = String.valueOf(DEFAULT_PORT);
		}
		int b = DEFAULT_PORT;
		try {
			b = Integer.valueOf(s).intValue();
			if (b <= 0) {
				String msg =
					"System property '" + ORACLE_DEBUG_PORT
							+ "' specifies a non-positive port '" + s + "'. "
							+ "The default value '" + DEFAULT_PORT
							+ "' will be used.";
				logger.fine(msg);
			}
		} catch (Exception x) {
			String msg =
				"System property '" + ORACLE_DEBUG_PORT
						+ "' specifies an invalid port '" + s + "': "
						+ x.toString() + ". The default value '" + DEFAULT_PORT
						+ "' will be used.";
			logger.warning(msg);
		}
		PORT.set(b);
		return PORT.get();
	}

	/**
	 * Attempts to activate Oracle remote debugging using the specified and host
	 * and port parameters specified by System property values. Equivalent to
	 * invoking
	 * 
	 * <pre>
	 * doDebugging(c, getHost(), getPort())
	 * </pre>
	 */
	public static void doDebugging(Connection c) {
		doDebugging(c, getHost(), getPort());
	}

	/**
	 * Attempts to activate Oracle remote debugging on the specified host and
	 * port using the specified connection. If any parameter is invalid, this
	 * method skips the activation attempt with a warning. Activation may fail
	 * even with valid parameters; for example, sufficient privileges may not be
	 * associated with the specified connection. This method will not throw an
	 * error. Check the application log for warnings, informational messages and
	 * debugging messages if Oracle remote debugging is not working.
	 * 
	 * @param conn
	 *            a non-null connection
	 * @param host
	 *            a valid host address
	 * @param port
	 *            a valid port
	 */
	public static void doDebugging(Connection conn, String host, int port) {
		boolean validParams = true;
		String errors = "";
		if (conn == null) {
			validParams = false;
			errors += "conn: null, ";
		}
		if (host == null || host.trim().isEmpty()) {
			validParams = false;
			errors += "host: null or blank, ";
		}
		if (port <= 0) {
			validParams = false;
			errors += "port: zero or negative";
		}
		if (!validParams) {
			String msg = "Invalid parameters: " + errors;
			if (msg.endsWith(", ")) {
				msg = msg.substring(0, msg.length() - 2);
			}
			msg += ". Oracle debugging will not be activated.";
			logger.warning(msg);
		}

		String s = System.getProperty(ORACLE_DEBUGGING);
		boolean b = Boolean.valueOf(s).booleanValue();
		DO_DEBUG.set(b);

		if (validParams && DO_DEBUG.get()) {
			try {
				s = SQL_FRAG1 + host + SQL_FRAG2 + port + SQL_FRAG3;
				logger.info("Oracle debugging invocation: '" + s + "'");
				CallableStatement stmt = null;
				try {
					stmt = conn.prepareCall(s);
					stmt.execute();
				} finally {
					stmt.close();
				}
				WARNING_COUNT.set(0);
			} catch (Exception _x) {
				if (WARNING_COUNT.get() == 0) {
					String msg =
						"Oracle remote debugging may not be available: "
								+ _x.toString();
					logger.warning(msg);
				}
				WARNING_COUNT.set(1 + WARNING_COUNT.get());
			}
		}
	}

	private OracleRemoteDebugging() {
	}

}
