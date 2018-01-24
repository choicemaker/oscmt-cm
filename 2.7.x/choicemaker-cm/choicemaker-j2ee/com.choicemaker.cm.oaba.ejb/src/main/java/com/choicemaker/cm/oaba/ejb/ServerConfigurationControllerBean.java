/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.ejb;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.oaba.api.DefaultServerConfiguration;
import com.choicemaker.cm.oaba.api.MutableServerConfiguration;
import com.choicemaker.cm.oaba.api.OabaJobController;
import com.choicemaker.cm.oaba.api.ServerConfigurationController;
import com.choicemaker.cm.oaba.api.ServerConfigurationException;
import com.choicemaker.util.SystemPropertyUtils;

@Stateless
public class ServerConfigurationControllerBean implements
		ServerConfigurationController {

	private static final Logger logger = Logger
			.getLogger(ServerConfigurationEntity.class.getName());

	protected static final String GENERIC_NAME_PREFIX = "GENERIC_";

	protected static final String UNKNOWN_HOSTNAME = "UKNOWN";

	public static final long INVALID_ID = 0;

	public static final int DEFAULT_MAX_CHUNK_SIZE = 1000000;

	public static final int DEFAULT_MAX_CHUNK_COUNT = 2000;

	public static int computeAvailableProcessors() {
		int retVal = Runtime.getRuntime().availableProcessors();
		return retVal;
	}

	public static String computeHostName() {
		// A hack to an unsolvable problem. See StackOverflow,
		// "How do I get the local hostname if unresolvable through DNS?"
		// http://links.rph.cx/1szjiIc
		String retVal = null;
		if (System.getProperty("os.name").startsWith("Windows")) {
			// Windows will always set the 'COMPUTERNAME' variable
			retVal = System.getenv("COMPUTERNAME");
		}
		if (retVal == null) {
			retVal = System.getenv("HOSTNAME");
		}
		if (retVal == null) {
			try {
				InetAddress localhost = java.net.InetAddress.getLocalHost();
				retVal = localhost.getHostName();
			} catch (UnknownHostException e) {
				assert retVal == null;
				logger.warning(e.toString());
			}
		}
		if (retVal == null) {
			retVal = "UNKNOWN_HOSTNAME";
		}
		assert retVal != null;
		retVal = ServerConfigurationEntity.standardizeHostName(retVal);
		return retVal;
	}

	public static String computeUniqueGenericName() {
		String retVal = GENERIC_NAME_PREFIX + UUID.randomUUID().toString();
		return retVal;
	}

	public static File computeGenericLocation() {
		String home = System.getProperty(SystemPropertyUtils.PN_USER_HOME);
		File retVal = new File(home);
		return retVal;
	}

	@PersistenceContext(unitName = "oaba")
	private EntityManager em;

	@EJB
	private OabaJobController jobController;

	@Override
	public ServerConfiguration findServerConfiguration(long id) {
		ServerConfigurationEntity retVal =
			em.find(ServerConfigurationEntity.class, id);
		return retVal;
	}

	@Override
	public ServerConfiguration findServerConfigurationByName(String configName) {
		Query query =
			em.createNamedQuery(ServerConfigurationJPA.QN_SERVERCONFIG_FIND_BY_NAME);
		query.setParameter(
				ServerConfigurationJPA.PN_SERVERCONFIG_FIND_BY_NAME_P1,
				configName);
		@SuppressWarnings("unchecked")
		List<ServerConfigurationEntity> beans = query.getResultList();

		ServerConfiguration retVal = null;
		if (beans.size() > 1) {
			throw new IllegalStateException("non-unique configuration name: "
					+ configName);
		} else if (beans.size() == 1) {
			retVal = beans.get(0);
		} else {
			assert beans == null || beans.size() == 0;
			assert retVal == null;
		}

		return retVal;
	}

	@Override
	public List<ServerConfiguration> findAllServerConfigurations() {
		Query query =
			em.createNamedQuery(ServerConfigurationJPA.QN_SERVERCONFIG_FIND_ALL);
		@SuppressWarnings("unchecked")
		List<ServerConfigurationEntity> beans = query.getResultList();
		List<ServerConfiguration> retVal = new LinkedList<>();
		if (beans != null) {
			retVal.addAll(beans);
		}
		return retVal;
	}

	@Override
	public List<ServerConfiguration> findServerConfigurationsByHostName(
			String hostName) {
		return findServerConfigurationsByHostName(hostName, false);
	}

	@Override
	public List<ServerConfiguration> findServerConfigurationsByHostName(
			String hostName, boolean strict) {
		List<ServerConfiguration> retVal =
			findServerConfigurationsByHostNameStrict(hostName);
		if (strict == false) {
			retVal.addAll(findServerConfigurationsForAnyHost());
		}
		return retVal;
	}

	protected List<ServerConfiguration> findServerConfigurationsByHostNameStrict(
			String hostName) {
		hostName = ServerConfigurationEntity.standardizeHostName(hostName);
		Query query =
			em.createNamedQuery(ServerConfigurationJPA.QN_SERVERCONFIG_FIND_BY_HOSTNAME);
		query.setParameter(
				ServerConfigurationJPA.PN_SERVERCONFIG_FIND_BY_HOSTNAME_P1,
				hostName);
		@SuppressWarnings("unchecked")
		List<ServerConfigurationEntity> beans = query.getResultList();
		List<ServerConfiguration> retVal = new LinkedList<>();
		if (beans != null) {
			retVal.addAll(beans);
		}
		return retVal;
	}

	protected List<ServerConfiguration> findServerConfigurationsForAnyHost() {
		return findServerConfigurationsByHostNameStrict(ServerConfiguration.ANY_HOST);
	}

	@Override
	public ServerConfiguration findServerConfigurationByJobId(long jobId) {
		ServerConfiguration retVal = null;
		BatchJob batchJob = jobController.findBatchJob(jobId);
		if (batchJob != null) {
			long serverId = batchJob.getServerId();
			retVal = findServerConfiguration(serverId);
		}
		return retVal;
	}

	@Override
	public MutableServerConfiguration computeGenericConfiguration() {
		MutableServerConfiguration retVal = new ServerConfigurationEntity();
		retVal.setConfigurationName(computeUniqueGenericName());
		retVal.setHostName(computeHostName());
		retVal.setMaxChoiceMakerThreads(computeAvailableProcessors());
		retVal.setMaxOabaChunkFileCount(DEFAULT_MAX_CHUNK_COUNT);
		retVal.setMaxOabaChunkFileRecords(DEFAULT_MAX_CHUNK_SIZE);
		retVal.setWorkingDirectoryLocation(computeGenericLocation());
		return retVal;
	}

	@Override
	public MutableServerConfiguration computeGenericConfiguration(
			String hostName) {
		if (hostName == null || !hostName.trim().equals(hostName)
				|| hostName.isEmpty()) {
			throw new IllegalArgumentException("invalid host name: '"
					+ hostName + "'");
		}
		MutableServerConfiguration retVal = new ServerConfigurationEntity();
		retVal.setConfigurationName(computeUniqueGenericName());
		retVal.setHostName(hostName);
		retVal.setMaxChoiceMakerThreads(computeAvailableProcessors());
		retVal.setMaxOabaChunkFileCount(DEFAULT_MAX_CHUNK_COUNT);
		retVal.setMaxOabaChunkFileRecords(DEFAULT_MAX_CHUNK_SIZE);
		retVal.setWorkingDirectoryLocation(computeGenericLocation());
		return retVal;
	}

	@Override
	public MutableServerConfiguration clone(ServerConfiguration sc) {
		MutableServerConfiguration retVal = new ServerConfigurationEntity(sc);
		return retVal;
	}

	@Override
	public ServerConfiguration save(ServerConfiguration sc)
			throws ServerConfigurationException {
		logger.fine("Saving " + sc);
		if (sc == null) {
			throw new IllegalArgumentException("null configuration");
		}

		ServerConfiguration retVal = null;

		ServerConfigurationEntity scb = null;
		if (!(sc instanceof ServerConfigurationEntity)) {
			scb = new ServerConfigurationEntity(sc);
		} else {
			scb = (ServerConfigurationEntity) sc;
		}
		assert scb != null;

		final String name = sc.getName();
		if (name == null || name.trim().isEmpty()) {
			throw new IllegalArgumentException(
					"null or blank configuration name");
		}
		ServerConfiguration duplicate = findServerConfigurationByName(name);
		if (duplicate != null) {
			assert duplicate instanceof ServerConfigurationEntity;
			if (scb.equalsIgnoreIdUuid(duplicate)) {
				retVal = duplicate;
			} else {
				// Beans have the same name but other fields are different
				assert sc.getName().equals(duplicate.getName());
				String msg = "Duplicate server name: " + sc.getName();
				throw new ServerConfigurationException(msg);
			}
		}

		if (retVal == null) {
			em.persist(scb);
			retVal = scb;
			logger.fine("Saved " + retVal);
		}

		assert retVal != null;
		return retVal;
	}

	@Override
	public ServerConfiguration setDefaultConfiguration(String host,
			ServerConfiguration sc) {
		if (host == null) {
			throw new IllegalArgumentException("null host name");
		}
		if (sc == null) {
			throw new IllegalArgumentException("null configuration");
		}
		if (!host.equalsIgnoreCase(sc.getHostName())
				&& !ServerConfiguration.ANY_HOST.equals(sc.getHostName())) {
			String msg =
				"Host name '"
						+ host
						+ "' is inconsistent with the configuration host name '"
						+ sc.getHostName() + "'";
			throw new IllegalArgumentException(msg);
		}

		ServerConfiguration retVal = null;
		DefaultServerConfiguration old =
			em.find(DefaultServerConfigurationBean.class, host);
		if (old != null) {
			long id = old.getServerConfigurationId();
			retVal = em.find(ServerConfigurationEntity.class, id);
			if (retVal == null) {
				logger.warning("missing server configuration: " + id);
			}
		} else {
			retVal = sc;
			if (ServerConfigurationEntity.isNonPersistentId(retVal.getId())) {
				em.persist(retVal);
				assert !ServerConfigurationEntity.isNonPersistentId(retVal
						.getId());
			}
			DefaultServerConfiguration dsc =
				new DefaultServerConfigurationBean(host, retVal.getId());
			em.persist(dsc);
		}
		assert retVal != null;

		return retVal;
	}

	@Override
	public DefaultServerConfiguration findDefaultServerConfiguration(
			String hostName) {
		DefaultServerConfiguration retVal = null;
		if (hostName != null) {
			hostName = hostName.trim();
			if (!hostName.isEmpty()) {
				retVal =
					em.find(DefaultServerConfigurationBean.class, hostName);
			}
		}
		return retVal;
	}

	/**
	 * Gets the default configuration for a particular host.
	 * <ol>
	 * <li>If default configuration for a host has been set previously, returns
	 * this configuration</li>
	 * <li>If only one configuration exists for a host, including any
	 * configuration marked {@link ServerConfiguration#ANY_HOST ANY_HOST}, this
	 * configuration is returned</li>
	 * <li>If neither of the previous conditions hold, then a generic
	 * configuration is computed and returned.
	 * </ol>
	 * Equivalent to
	 * 
	 * <pre>
	 * getDefaultConfiguration(hostName, true)
	 * </pre>
	 */
	@Override
	public ServerConfiguration getDefaultConfiguration(String hostName) {
		return getDefaultConfiguration(hostName, true);
	}

	/**
	 * Gets the default configuration for a particular host.
	 * <ol>
	 * <li>If default configuration for a host has been set previously, returns
	 * this configuration</li>
	 * <li>If only one configuration exists for a host, including any
	 * configuration marked {@link ServerConfiguration#ANY_HOST ANY_HOST}, this
	 * configuration is returned</li>
	 * <li>If neither of the previous conditions hold, and if the
	 * <code>computeFallback</code> flag is true, then a generic configuration
	 * is computed and returned.
	 * <li>Otherwise returns null.
	 * </ol>
	 */
	@Override
	public ServerConfiguration getDefaultConfiguration(String host,
			boolean computeFallback) {
		if (host == null) {
			throw new IllegalArgumentException("null host name");
		}
		host = host.trim();

		ServerConfiguration retVal = null;
		DefaultServerConfiguration dscb =
			em.find(DefaultServerConfigurationBean.class, host);
		if (dscb != null) {
			long id = dscb.getServerConfigurationId();
			retVal = em.find(ServerConfigurationEntity.class, id);
			if (retVal == null) {
				logger.warning("missing server configuration: " + id);
			}
		}
		if (retVal == null) {
			List<ServerConfiguration> configs =
				findServerConfigurationsByHostName(host);
			if (configs.size() == 1) {
				retVal = configs.get(0);
			}
		}
		if (retVal == null && computeFallback) {
			MutableServerConfiguration mutable = computeGenericConfiguration();
			mutable.setHostName(host);
			try {
				retVal = save(mutable);
			} catch (ServerConfigurationException e) {
				// The mutable instance is created with a unique name,
				// so a duplicate name exception should never occur
				new IllegalStateException(e.getMessage());
			}
			assert retVal.getId() != ServerConfigurationEntity.NON_PERSISTENT_ID;
			setDefaultConfiguration(host, mutable);
		}

		if (computeFallback) {
			assert retVal != null;
		}
		return retVal;
	}

	@Override
	public List<DefaultServerConfiguration> findAllDefaultServerConfigurations() {
		Query query =
			em.createNamedQuery(DefaultServerConfigurationJPA.QN_DSC_FIND_ALL);
		@SuppressWarnings("unchecked")
		List<DefaultServerConfiguration> retVal = query.getResultList();
		if (retVal == null) {
			retVal = new ArrayList<>();
		}
		return retVal;
	}

}
