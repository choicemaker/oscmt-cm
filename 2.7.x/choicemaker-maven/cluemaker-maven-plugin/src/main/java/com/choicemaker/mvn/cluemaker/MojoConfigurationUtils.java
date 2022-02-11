/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker LLC - initial API and implementation
 *******************************************************************************/
package com.choicemaker.mvn.cluemaker;

import java.io.File;
import java.util.List;

import org.apache.maven.artifact.Artifact;

import com.choicemaker.util.SystemPropertyUtils;

class MojoConfigurationUtils {

	private static final String PATH_SEPARATOR = System
			.getProperty(SystemPropertyUtils.PN_PATH_SEPARATOR);

	private MojoConfigurationUtils() {
	}

	static String computeClasspath(List<Artifact> artifacts)
			throws IllegalStateException {
		assert artifacts != null;
		int count = artifacts.size();
		StringBuilder sb = new StringBuilder();
		for (Artifact a : artifacts) {
			File f = a.getFile();
			assert f != null;
			String fileName = f.getAbsolutePath();
			sb.append(fileName);
			--count;
			if (count > 1) {
				sb.append(PATH_SEPARATOR);
			}
		}
		final String retVal = sb.toString();
		assert retVal != null;
		return retVal;
	}

}
