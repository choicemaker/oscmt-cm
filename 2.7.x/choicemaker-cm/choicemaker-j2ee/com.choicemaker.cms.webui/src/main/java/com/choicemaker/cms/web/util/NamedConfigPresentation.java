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
package com.choicemaker.cms.web.util;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @deprecated see com.choicemaker.cms.webapp.util.NamedConfigPresentation
 */
@Deprecated
public class NamedConfigPresentation {

	static final Logger logger = Logger.getLogger(NamedConfigPresentation.class
			.getName());

	private static final Set<PropertyNameType> _s = new LinkedHashSet<>();
	static {
		_s.add(new PropertyNameType("Id", long.class));
		_s.add(new PropertyNameType("ConfigurationName", String.class));
		_s.add(new PropertyNameType("ConfigurationDescription", String.class));
		_s.add(new PropertyNameType("ModelName", String.class));
		_s.add(new PropertyNameType("LowThreshold", float.class));
		_s.add(new PropertyNameType("HighThreshold", float.class));
		_s.add(new PropertyNameType("Task", String.class));
		_s.add(new PropertyNameType("Rigor", String.class));
		_s.add(new PropertyNameType("RecordSourceType", String.class));
		_s.add(new PropertyNameType("DataSource", String.class));
		_s.add(new PropertyNameType("JdbcDriverClassName", String.class));
		_s.add(new PropertyNameType("BlockingConfiguration", String.class));
		_s.add(new PropertyNameType("QuerySelection", String.class));
		_s.add(new PropertyNameType("QueryDatabaseConfiguration", String.class));
		_s.add(new PropertyNameType("QueryDeduplicated", boolean.class));
		_s.add(new PropertyNameType("ReferenceSelection", String.class));
		_s.add(new PropertyNameType("ReferenceDatabaseConfiguration",
				String.class));
		_s.add(new PropertyNameType("ReferenceDatabaseAccessor",
				String.class));
		_s.add(new PropertyNameType("ReferenceDatabaseReader",
				String.class));
		_s.add(new PropertyNameType("TransitivityFormat", String.class));
		_s.add(new PropertyNameType("TransitivityGraph", String.class));
		_s.add(new PropertyNameType("AbaMaxMatches", int.class));
		_s.add(new PropertyNameType("AbaLimitPerBlockingSet", int.class));
		_s.add(new PropertyNameType("AbaLimitSingleBlockingSet", int.class));
		_s.add(new PropertyNameType("AbaSingleTableBlockingSetGraceLimit",
				int.class));
		_s.add(new PropertyNameType("OabaMaxSingle", int.class));
		_s.add(new PropertyNameType("OabaMaxBlockSize", int.class));
		_s.add(new PropertyNameType("OabaMaxChunkSize", int.class));
		_s.add(new PropertyNameType("OabaMaxOversized", int.class));
		_s.add(new PropertyNameType("OabaMaxMatches", int.class));
		_s.add(new PropertyNameType("OabaMinFields", int.class));
		_s.add(new PropertyNameType("OabaInterval", int.class));
		_s.add(new PropertyNameType("ServerMaxThreads", int.class));
		_s.add(new PropertyNameType("ServerMaxFileEntries", int.class));
		_s.add(new PropertyNameType("ServerMaxFilesCount", int.class));
		_s.add(new PropertyNameType("ServerFileURI", String.class));
	}

	/**
	 * Returns an ordered set of property names and types for NamedConfiguration
	 * implementations
	 */
	public static Set<PropertyNameType> getNCPropertyNameTypes() {
		return Collections.unmodifiableSet(_s);
	}

	private NamedConfigPresentation() {
	}

}
