/*******************************************************************************
 * Copyright (c) 2016, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ChoiceMaker LLC - initial API and implementation
 *******************************************************************************/
package com.choicemaker.cm.urm.client.app;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.choicemaker.cm.urm.client.util.APP_SERVER_VENDOR;
import com.choicemaker.util.Precondition;

public class UrmParams {

	private static final Logger logger =
		Logger.getLogger(UrmParams.class.getName());

	final APP_SERVER_VENDOR appServer;
	final String appName;
	final URM_BMA_APP_COMMAND appCommand;
	final List<Long> jobIds;
	final List<String> errors;

	public UrmParams(APP_SERVER_VENDOR appServer, String appName,
			URM_BMA_APP_COMMAND appCommand, List<Long> jobIds,
			List<String> errors) {
		if (errors == null || errors.isEmpty()) {
			Precondition.assertNonEmptyString(
					"appName must not be null or blank", appName);
			Precondition.assertNonNullArgument(
					"Application command must be non-null", appCommand);
			Precondition.assertNonNullArgument(
					"List of job ids must be non-null", jobIds);
			for (Long jobId : jobIds) {
				Precondition.assertNonNullArgument("Job id must be non-null",
						jobId);
			}
		}

		this.appServer =
			appServer == null ? APP_SERVER_VENDOR.JBOSS : appServer;
		this.appName = appName;
		this.appCommand = appCommand;
		this.jobIds = jobIds;
		this.errors = errors == null ? Collections.emptyList() : errors;

		logger.fine(this.toString());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result =
			prime * result + ((appCommand == null) ? 0 : appCommand.hashCode());
		result = prime * result + ((appName == null) ? 0 : appName.hashCode());
		result =
			prime * result + ((appServer == null) ? 0 : appServer.hashCode());
		result = prime * result + ((jobIds == null) ? 0 : jobIds.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UrmParams other = (UrmParams) obj;
		if (appCommand != other.appCommand)
			return false;
		if (appName == null) {
			if (other.appName != null)
				return false;
		} else if (!appName.equals(other.appName))
			return false;
		if (appServer != other.appServer)
			return false;
		if (jobIds == null) {
			if (other.jobIds != null)
				return false;
		} else if (!jobIds.equals(other.jobIds))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UrmParams [appServer=" + appServer + ", appName=" + appName
				+ ", appCommand=" + appCommand + ", jobIds=" + jobIds + "]";
	}

}
