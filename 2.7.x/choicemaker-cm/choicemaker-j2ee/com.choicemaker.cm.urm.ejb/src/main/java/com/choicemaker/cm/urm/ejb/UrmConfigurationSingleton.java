/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.ejb;

import javax.ejb.Singleton;

import com.choicemaker.cm.core.DatabaseException;
import com.choicemaker.cm.urm.api.UrmConfigurationAdapter;

@Singleton
public class UrmConfigurationSingleton implements UrmConfigurationAdapter {

	// private static final Logger log =
	// Logger.getLogger(UrmConfigurationSingleton.class.getName());

	// -- Injected data

	// @EJB
	// private SqlRecordSourceController sqlRSController;

	// -- Accessors

	// protected final SqlRecordSourceController getSqlRecordSourceController()
	// {
	// return sqlRSController;
	// }

	// /** Map of model configuration names to ABA statistics */
	// private Map<String, AbaStatistics> cachedStats = new Hashtable<>();

	@Override
	public String getCmsConfigurationName(String urmModelConfiguration)
			throws DatabaseException {
		// FIXME always returns the same configuration name, which may
		// or may not exist
		String HACK = "MCI 2012-04-15a (4-thread) /tmp Oracle LINKAGE";
		return HACK;
	}

}
