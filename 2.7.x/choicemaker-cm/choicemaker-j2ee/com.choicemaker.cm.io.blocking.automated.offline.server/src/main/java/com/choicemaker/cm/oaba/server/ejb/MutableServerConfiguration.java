/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.server.ejb;

import java.io.File;

import com.choicemaker.cm.args.ServerConfiguration;

public interface MutableServerConfiguration extends ServerConfiguration {

	/**
	 * Assigns a name for a configuration. A configuration name must be unique
	 * within the database used to store configuration information.
	 */
	void setConfigurationName(String name);

	/** The host machine or logical domain to which a configuration applies */
	void setHostName(String hostName);

	/** The maximum number of ChoiceMaker tasks that should be run in parallel */
	void setMaxChoiceMakerThreads(int maxThreadCount);

	/** The maximum number of records in an OABA chunk file */
	void setMaxOabaChunkFileRecords(int maxChunkSize);

	/** The maximum number of OABA chunk files */
	void setMaxOabaChunkFileCount(int maxChunkCount);

	/**
	 * The parent directory in which job-specific working directories may be
	 * created
	 */
	void setWorkingDirectoryLocation(File location);

}
