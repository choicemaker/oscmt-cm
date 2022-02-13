/*******************************************************************************
 * Copyright (c) 2003, 2020 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc  - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
package com.choicemaker.cm.args;

import java.io.File;
import java.io.Serializable;

public interface ServerConfiguration extends Serializable {

	/**
	 * A special value for the {@link #getHostName() hostName} field that
	 * indicates a configuration is not specific to a particular host machine.
	 */
	String ANY_HOST = "**ANY HOST**";

	long getId();

	boolean isPersistent();

	/**
	 * A memorable name for a configuration. A configuration name must be unique
	 * within the database used to store configuration information.
	 * @return the name of this configuration
	 */
	String getName();

	/**
	 * A universally, unique identifier for a configuration, automatically
	 * assigned.
	 * @return the unique id for this configuration
	 */
	String getUUID();

	/** @return The host machine or logical domain to which a configuration applies */
	String getHostName();

	/**
	 * @return The maximum number of ChoiceMaker tasks that should be run in parallel
	 */
	int getMaxChoiceMakerThreads();

	/** @return The maximum number of entries in an OABA or Transitivity result file */
	int getMaxFileEntries();

	/** @return A deprecated alias for {@link #getMaxFileEntries()}
	 * @deprecated
	 */
	@Deprecated
	int getMaxOabaChunkFileRecords();

	/** @return A (fuzzy) maximum number of result files produced during matching or
	 * transitivity analysis
	 */
	int getMaxFilesCount();

	/** @return A deprecated alias for {@link #getMaxFilesCount()}
	 * @deprecated
	 */
	@Deprecated
	int getMaxOabaChunkFileCount();

	/**
	 * Checks whether the URI returned by
	 * {@link #getWorkingDirectoryLocationUriString()} represents an exiting and
	 * valid directory. Since this class is persistent, the URI may be valid in
	 * the context in which it was first saved, but be invalid in a subsequent
	 * context when it is retrieved (for example, on a completely different
	 * host).
	 * @return true if the working directory is valid, false otherwise
	 */
	boolean isWorkingDirectoryLocationValid();

	/**
	 * Returns the parent directory in which job-specific working directories
	 * may be created. This method is preferred over
	 * {@link #getWorkingDirectoryLocationUriString()} when the location must
	 * exist for an application to work successfully.
	 * 
	 * @return never null
	 * @throws IllegalStateException
	 *             if the location doesn't exist or isn't valid
	 */
	File getWorkingDirectoryLocation() throws IllegalStateException;

	/**
	 * Returns a String represent the URI of the parent directory in which
	 * job-specific working directories may be created. This method should never
	 * throw an exception, and it is therefore preferred over
	 * {@link #getWorkingDirectoryLocation()} in certain applications where the
	 * existence or validity of the location is not critical, such as equality
	 * checks between two server configurations.
	 * 
	 * @return possibly null
	 */
	String getWorkingDirectoryLocationUriString();

}
