/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.ejb.data;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.ejb.CreateException;
//import javax.jms.Topic;
//import javax.jms.TopicConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * This object contains method to get JMS and EJB objects from the J2EE server.
 *
 * @author pcheung
 * @author rphall (migration to EJB3)
 *
 */
public class EJBConfiguration implements Serializable {

	// private static final Logger log =
	// Logger.getLogger(OabaProcessingControllerBean.class.getName());

	private static final long serialVersionUID = 271;

	// -- Enterprise Naming Context

	// ENC prefix
	public final static String ENV_BASE = "java:comp/env/";

	// ENC Queue names
	public final static String JMS_START_QUEUE = "jms/startQueue";
	public final static String JMS_BLOCKING_QUEUE = "jms/blockQueue";
	public final static String JMS_DEDUP_QUEUE = "jms/dedupQueue";
	public final static String JMS_CHUNK_QUEUE = "jms/chunkQueue";
	public final static String JMS_MATCH_QUEUE = "jms/matchQueue";
	public final static String JMS_MATCH_DEDUP_QUEUE = "jms/matchDedupQueue";
	public final static String JMS_SINGLE_MATCH_QUEUE = "jms/singleMatchQueue";
	public final static String JMS_TRANSITIVITY_QUEUE = "jms/transitivityQueue";
	public final static String JMS_TRANS_MATCH_SCHEDULER_QUEUE =
		"jms/transMatchSchedulerQueue";
	public final static String JMS_TRANS_MATCHER_QUEUE =
		"jms/transMatcherQueue";
	public final static String JMS_TRANS_MATCH_DEDUP_QUEUE =
		"jms/transMatchDedupQueue";
	public final static String JMS_TRANS_MATCH_DEDUP_EACH_QUEUE =
		"jms/transMatchDedupEachQueue";

	// parallelization code
	public final static String JMS_MATCH_SCHEDULER_QUEUE =
		"jms/matchSchedulerQueue";
	public final static String JMS_MATCHER_QUEUE = "jms/matcherQueue";
	public final static String JMS_MATCH_WRITER_QUEUE = "jms/matchWriterQueue";

	// match dedup parallelization
	public final static String JMS_MATCH_DEDUP_EACH_QUEUE =
		"jms/matchDedupEachQueue";

	// ENC DataSource names
	public final static String DATA_SOURCE = "jdbc/OABADS";

	// -- END Enterprise Naming Context

	// Cached data sources
	private transient DataSource ds;

	// Singleton instance
	private static EJBConfiguration config = null;

	private EJBConfiguration() {
	}

	public static EJBConfiguration getInstance() {
		if (config == null)
			config = new EJBConfiguration();
		return config;
	}

	public Connection getConnection() throws RemoteException, CreateException,
			NamingException, SQLException {
		getDataSource();
		Connection retVal = ds.getConnection();
		return retVal;
	}

	public DataSource getDataSource() throws RemoteException, CreateException,
			NamingException, SQLException {
		if (this.ds == null) {
			Context ctx = new InitialContext();
			this.ds = (DataSource) ctx.lookup(ENV_BASE + DATA_SOURCE);
		}
		return this.ds;
	}

}
