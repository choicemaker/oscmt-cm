/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.db.base;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

public class DataSources {
	private static Map<String, DataSource> sources = new HashMap<>();

	public static void addDataSource(String name, DataSource ds) {
		sources.put(name, ds);
	}

	public static DataSource getDataSource(String name) {
		return (DataSource) sources.get(name);
	}

	public static Collection<String> getDataSourceNames() {
		return sources.keySet();
	}
}
