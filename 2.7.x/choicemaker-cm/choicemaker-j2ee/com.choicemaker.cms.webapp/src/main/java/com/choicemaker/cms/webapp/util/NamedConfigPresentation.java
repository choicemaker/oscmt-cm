/*******************************************************************************
 * Copyright (c) 2003, 2021 ChoiceMaker LLC and others.
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
package com.choicemaker.cms.webapp.util;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;

public class NamedConfigPresentation {

	static final Logger logger = Logger.getLogger(NamedConfigPresentation.class
			.getName());

	private static final Set<NameType> _s = new LinkedHashSet<>();
	static {
		_s.add(new NameType("Id", long.class));
		_s.add(new NameType("ConfigurationName", String.class));
		_s.add(new NameType("ConfigurationDescription", String.class));
		_s.add(new NameType("ModelName", String.class));
		_s.add(new NameType("LowThreshold", float.class));
		_s.add(new NameType("HighThreshold", float.class));
		_s.add(new NameType("Task", String.class));
		_s.add(new NameType("Rigor", String.class));
		_s.add(new NameType("RecordSourceType", String.class));
		_s.add(new NameType("DataSource", String.class));
		_s.add(new NameType("JdbcDriverClassName", String.class));
		_s.add(new NameType("BlockingConfiguration", String.class));
		_s.add(new NameType("QuerySelection", String.class));
		_s.add(new NameType("QueryDatabaseConfiguration", String.class));
		_s.add(new NameType("QueryDeduplicated", boolean.class));
		_s.add(new NameType("ReferenceSelection", String.class));
		_s.add(new NameType("ReferenceDatabaseConfiguration",
				String.class));
		_s.add(new NameType("ReferenceDatabaseAccessor",
				String.class));
		_s.add(new NameType("ReferenceDatabaseReader",
				String.class));
		_s.add(new NameType("TransitivityFormat", String.class));
		_s.add(new NameType("TransitivityGraph", String.class));
		_s.add(new NameType("AbaMaxMatches", int.class));
		_s.add(new NameType("AbaLimitPerBlockingSet", int.class));
		_s.add(new NameType("AbaLimitSingleBlockingSet", int.class));
		_s.add(new NameType("AbaSingleTableBlockingSetGraceLimit",
				int.class));
		_s.add(new NameType("OabaMaxSingle", int.class));
		_s.add(new NameType("OabaMaxBlockSize", int.class));
		_s.add(new NameType("OabaMaxChunkSize", int.class));
		_s.add(new NameType("OabaMaxOversized", int.class));
		_s.add(new NameType("OabaMaxMatches", int.class));
		_s.add(new NameType("OabaMinFields", int.class));
		_s.add(new NameType("OabaInterval", int.class));
		_s.add(new NameType("ServerMaxThreads", int.class));
		_s.add(new NameType("ServerMaxFileEntries", int.class));
		_s.add(new NameType("ServerMaxFilesCount", int.class));
		_s.add(new NameType("ServerFileURI", String.class));
	}

	/**
	 * Returns an ordered set of property names and types for NamedConfiguration
	 * implementations
	 */
	public Set<NameType> getNCPropertyNameTypes() {
		return Collections.unmodifiableSet(_s);
	}

	public NamedConfigPresentation() {
	}

}
