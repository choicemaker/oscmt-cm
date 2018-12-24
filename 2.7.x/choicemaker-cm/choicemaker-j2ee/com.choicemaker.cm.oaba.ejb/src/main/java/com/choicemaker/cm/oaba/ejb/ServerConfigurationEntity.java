/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.ejb;

import static com.choicemaker.cm.oaba.ejb.ServerConfigurationJPA.CN_CONFIGNAME;
import static com.choicemaker.cm.oaba.ejb.ServerConfigurationJPA.CN_FILE;
import static com.choicemaker.cm.oaba.ejb.ServerConfigurationJPA.CN_HOSTNAME;
import static com.choicemaker.cm.oaba.ejb.ServerConfigurationJPA.CN_ID;
import static com.choicemaker.cm.oaba.ejb.ServerConfigurationJPA.CN_SVR_MAX_CHUNKCOUNT;
import static com.choicemaker.cm.oaba.ejb.ServerConfigurationJPA.CN_SVR_MAX_CHUNKSIZE;
import static com.choicemaker.cm.oaba.ejb.ServerConfigurationJPA.CN_SVR_MAX_THREADS;
import static com.choicemaker.cm.oaba.ejb.ServerConfigurationJPA.CN_UUID;
import static com.choicemaker.cm.oaba.ejb.ServerConfigurationJPA.ID_GENERATOR_NAME;
import static com.choicemaker.cm.oaba.ejb.ServerConfigurationJPA.ID_GENERATOR_PK_COLUMN_NAME;
import static com.choicemaker.cm.oaba.ejb.ServerConfigurationJPA.ID_GENERATOR_PK_COLUMN_VALUE;
import static com.choicemaker.cm.oaba.ejb.ServerConfigurationJPA.ID_GENERATOR_TABLE;
import static com.choicemaker.cm.oaba.ejb.ServerConfigurationJPA.ID_GENERATOR_VALUE_COLUMN_NAME;
import static com.choicemaker.cm.oaba.ejb.ServerConfigurationJPA.JPQL_SERVERCONFIG_FIND_ALL;
import static com.choicemaker.cm.oaba.ejb.ServerConfigurationJPA.JPQL_SERVERCONFIG_FIND_BY_HOSTNAME;
import static com.choicemaker.cm.oaba.ejb.ServerConfigurationJPA.JPQL_SERVERCONFIG_FIND_BY_NAME;
import static com.choicemaker.cm.oaba.ejb.ServerConfigurationJPA.QN_SERVERCONFIG_FIND_ALL;
import static com.choicemaker.cm.oaba.ejb.ServerConfigurationJPA.QN_SERVERCONFIG_FIND_BY_HOSTNAME;
import static com.choicemaker.cm.oaba.ejb.ServerConfigurationJPA.QN_SERVERCONFIG_FIND_BY_NAME;
import static com.choicemaker.cm.oaba.ejb.ServerConfigurationJPA.TABLE_NAME;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.oaba.api.MutableServerConfiguration;

@NamedQueries({
		@NamedQuery(name = QN_SERVERCONFIG_FIND_ALL,
				query = JPQL_SERVERCONFIG_FIND_ALL),
		@NamedQuery(name = QN_SERVERCONFIG_FIND_BY_HOSTNAME,
				query = JPQL_SERVERCONFIG_FIND_BY_HOSTNAME),
		@NamedQuery(name = QN_SERVERCONFIG_FIND_BY_NAME,
				query = JPQL_SERVERCONFIG_FIND_BY_NAME),
		// @NamedQuery(name = QN_SERVERCONFIG_FIND_ANY_HOST,
		// query = JPQL_SERVERCONFIG_FIND_ANY_HOST)
})
@Entity
@Table(/* schema = "CHOICEMAKER", */name = TABLE_NAME)
public class ServerConfigurationEntity implements MutableServerConfiguration {

	private static final long serialVersionUID = 271L;

	private static final Logger logger =
		Logger.getLogger(ServerConfigurationEntity.class.getName());

	public static long NON_PERSISTENT_ID = 0;

	protected static boolean isNonPersistentId(long id) {
		return id == NON_PERSISTENT_ID;
	}

	public static boolean isPersistent(ServerConfiguration sc) {
		boolean retVal = false;
		if (sc != null) {
			retVal = !isNonPersistentId(sc.getId());
		}
		return retVal;
	}

	public static String dump(ServerConfiguration c) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);

		pw.println("Server configuration (SC)");
		if (c == null) {
			pw.println("SC: null server configuration");
		} else {
			pw.println("SC: Server configuration id: " + c.getId());
			pw.println("SC: Server configuration name: " + c.getName());
			pw.println("SC: Server configuration UUI: " + c.getUUID());
			pw.println("SC: Server host name: " + c.getHostName());
			pw.println("SC: Max ChoiceMaker threads: "
					+ c.getMaxChoiceMakerThreads());
			pw.println("SC: Max Chunk files: " + c.getMaxOabaChunkFileCount());
			pw.println("SC: Max Records per chunk file: "
					+ c.getMaxOabaChunkFileRecords());
		}
		String retVal = sw.toString();
		return retVal;
	}

	public static String standardizeHostName(String s) {
		String retVal = null;
		if (s != null) {
			retVal = s.trim().toUpperCase();
		}
		return retVal;
	}

	// -- Instance data

	@Id
	@Column(name = CN_ID)
	@TableGenerator(name = ID_GENERATOR_NAME, table = ID_GENERATOR_TABLE,
			pkColumnName = ID_GENERATOR_PK_COLUMN_NAME,
			valueColumnName = ID_GENERATOR_VALUE_COLUMN_NAME,
			pkColumnValue = ID_GENERATOR_PK_COLUMN_VALUE)
	@GeneratedValue(strategy = GenerationType.TABLE,
			generator = ID_GENERATOR_NAME)
	private long id;

	@Column(name = CN_CONFIGNAME)
	private String name;

	@Column(name = CN_UUID)
	private final String uuid;

	@Column(name = CN_HOSTNAME)
	private String hostName;

	@Column(name = CN_SVR_MAX_THREADS)
	private int maxThreads;

	@Column(name = CN_SVR_MAX_CHUNKSIZE)
	private int maxChunkSize;

	@Column(name = CN_SVR_MAX_CHUNKCOUNT)
	private int maxChunkCount;

	@Column(name = CN_FILE)
	private String fileURI;

	public ServerConfigurationEntity() {
		this.uuid = UUID.randomUUID().toString();
	}

	public ServerConfigurationEntity(ServerConfiguration sc) {
		this.uuid = UUID.randomUUID().toString();
		this.name =
			ServerConfigurationControllerBean.computeUniqueGenericName();
		File f = sc.getWorkingDirectoryLocation();
		if (f == null) {
			throw new IllegalArgumentException("null working directory");
		} else {
			this.fileURI = f.toURI().toString();
		}
		setHostName(sc.getHostName());
		setMaxOabaChunkFileCount(sc.getMaxOabaChunkFileCount());
		setMaxOabaChunkFileRecords(sc.getMaxOabaChunkFileRecords());
		setMaxChoiceMakerThreads(sc.getMaxChoiceMakerThreads());
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public boolean isPersistent() {
		return isPersistent(this);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getUUID() {
		return uuid;
	}

	@Override
	public String getHostName() {
		assert hostName == null
				|| hostName.trim().toUpperCase().equals(hostName);
		return hostName;
	}

	@Override
	public int getMaxChoiceMakerThreads() {
		return maxThreads;
	}

	@Override
	public int getMaxOabaChunkFileRecords() {
		return maxChunkSize;
	}

	@Override
	public int getMaxOabaChunkFileCount() {
		return maxChunkCount;
	}

	@Override
	public boolean isWorkingDirectoryLocationValid() {
		boolean retVal = false;
		try {
			File f = new File(new URI(fileURI));
			if (f != null && f.exists() && f.isDirectory() && f.canWrite()
					&& f.canRead()) {
				retVal = true;
			}
		} catch (Exception x) {
			String msg =
				"Invalid file location: " + fileURI + ": " + x.toString();
			logger.warning(msg);
			assert retVal == false;
		}
		return retVal;
	}

	@Override
	public String getWorkingDirectoryLocationUriString() {
		return fileURI;
	}

	@Override
	public File getWorkingDirectoryLocation() {
		File retVal = null;
		if (fileURI != null) {
			try {
				retVal = new File(new URI(fileURI));
			} catch (URISyntaxException e) {
				String msg =
					"Invalid file location: " + fileURI + ": " + e.toString();
				logger.severe(msg);
				new IllegalStateException(msg);
			}
		}
		assert retVal != null;
		return retVal;
	}

	public static boolean equalsIgnoreIdUuid(ServerConfiguration sc1,
			ServerConfiguration sc2) {
		boolean retVal = false;
		check: if (sc1 != null && sc2 != null) {
			if (sc1 == sc2) {
				retVal = true;
				break check;
			}
			if (sc1.getName() == null) {
				if (sc2.getName() != null) {
					break check;
				}
			} else if (!sc1.getName().equals(sc2.getName())) {
				break check;
			}
			if (sc1.getHostName() == null) {
				if (sc2.getHostName() != null) {
					break check;
				}
			} else if (!sc1.getHostName().equalsIgnoreCase(sc2.getHostName())) {
				break check;
			}
			if (sc1.getWorkingDirectoryLocationUriString() == null) {
				if (sc2.getWorkingDirectoryLocationUriString() != null) {
					break check;
				}
			} else if (!sc1.getWorkingDirectoryLocationUriString()
					.equals(sc2.getWorkingDirectoryLocationUriString())) {
				break check;
			}
			if (sc1.getMaxOabaChunkFileCount() != sc2
					.getMaxOabaChunkFileCount()) {
				break check;
			}
			if (sc1.getMaxOabaChunkFileRecords() != sc2
					.getMaxOabaChunkFileRecords()) {
				break check;
			}
			if (sc1.getMaxChoiceMakerThreads() != sc2
					.getMaxChoiceMakerThreads()) {
				break check;
			}
			retVal = true;
		} // end check
		return retVal;
	}

	public boolean equalsIgnoreIdUuid(ServerConfiguration sc2) {
		return equalsIgnoreIdUuid(this, sc2);
	}

	@Override
	public String toString() {
		return "ServerConfigurationEntity [id=" + id + ", name=" + name
				+ ", uuid=" + uuid + "]";
	}

	@Override
	public void setConfigurationName(String name) {
		if (name == null || name.trim().isEmpty()) {
			throw new IllegalArgumentException("null or blank name");
		}
		this.name = name.trim();
	}

	@Override
	public void setHostName(String name) {
		if (name == null || name.trim().isEmpty()) {
			throw new IllegalArgumentException("null or blank name");
		}
		this.hostName = standardizeHostName(name);
	}

	@Override
	public void setMaxChoiceMakerThreads(int value) {
		if (value < 0) {
			throw new IllegalArgumentException("negative thread limit");
		}
		this.maxThreads = value;
	}

	@Override
	public void setMaxOabaChunkFileRecords(int value) {
		if (value < 0) {
			throw new IllegalArgumentException("negative chunk size");
		}
		this.maxChunkSize = value;
	}

	@Override
	public void setMaxOabaChunkFileCount(int value) {
		if (value < 0) {
			throw new IllegalArgumentException("negative chunk count");
		}
		this.maxChunkCount = value;
	}

	@Override
	public void setWorkingDirectoryLocation(File location) {
		if (location == null) {
			throw new IllegalArgumentException("null location");
		}
		if (!location.exists()) {
			throw new IllegalArgumentException(
					"location does not exist: " + location);
		}
		if (!location.isDirectory()) {
			throw new IllegalArgumentException(
					"location is not a directory: " + location);
		}
		if (!location.canWrite()) {
			throw new IllegalArgumentException(
					"location can not be written: " + location);
		}
		if (!location.canRead()) {
			throw new IllegalArgumentException(
					"location can not be read: " + location);
		}
		this.fileURI = location.toURI().toString();
	}

}
