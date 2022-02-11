/*******************************************************************************
 * Copyright (c) 2016 ChoiceMaker LLC and others.
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
import org.apache.maven.project.MavenProject;

import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.core.configure.ChoiceMakerConfiguration;
import com.choicemaker.cm.core.configure.ChoiceMakerConfigurator;

public class MojoConfigurator implements ChoiceMakerConfigurator {

	private final MavenProject project;
	private final File cluemakerDirectory;
	private final File generatedSourceDirectory;
	private final File compiledCodeDirectory;
	private final List<Artifact> artifacts;

	public MojoConfigurator(MavenProject p, File cluemakerDir, File generatedSrcDir, File compiledCodeDir,
			List<Artifact> a) {
		if (p == null) {
			throw new IllegalArgumentException("null maven project");
		}
		if (cluemakerDir == null) {
			throw new IllegalArgumentException("null source directory");
		}
		if (generatedSrcDir == null) {
			throw new IllegalArgumentException("null target directory");
		}
		this.project = p;
		this.cluemakerDirectory = cluemakerDir;
		this.generatedSourceDirectory = generatedSrcDir;
		this.compiledCodeDirectory = compiledCodeDir;
		this.artifacts = a;
	}

	@Override
	public ChoiceMakerConfiguration init() throws XmlConfException {
		return new MojoConfiguration(project, cluemakerDirectory, generatedSourceDirectory, compiledCodeDirectory, artifacts);
	}

	/**
	 * All method parameters are ignored. Equivalent to invoking {@link #init()}
	 * without any parameters.
	 * @param fn may be null
	 */
	@Override
	public ChoiceMakerConfiguration init(String fn, boolean reload,
			boolean initGui) throws XmlConfException {
		return init();
	}

	/**
	 * All method parameters are ignored. Equivalent to invoking {@link #init()}
	 * without any parameters.
	 * @param fn may be null
	 * @param logConfName may be null
	 */
	@Override
	public ChoiceMakerConfiguration init(String fn, String logConfName,
			boolean reload, boolean initGui) throws XmlConfException {
		return init();
	}

	@Override
	public ChoiceMakerConfiguration init(String fn, boolean reload,
			boolean initGui, char[] password) throws XmlConfException {
		return init();
	}

}
