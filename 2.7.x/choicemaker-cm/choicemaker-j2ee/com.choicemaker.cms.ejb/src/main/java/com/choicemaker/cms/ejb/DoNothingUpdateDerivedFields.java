/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cms.ejb;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.choicemaker.cm.urm.exceptions.UrmIncompleteSpecificationException;

/**
 * A default implementation of IUpdateDerivedFields.
 * Logs a warning when the
 * {@line #updateDerivedFields(DataSource) updateDerivedFields}
 * method is invoked.
 * @author rphall
 */
@SuppressWarnings({"rawtypes"})
public class DoNothingUpdateDerivedFields extends AbstractUpdateDerivedFields {

	private static final long serialVersionUID = 1L;
	private static Logger log =
		Logger.getLogger(DoNothingUpdateDerivedFields.class.getName());

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.urm.IUpdateDerivedFields#updateDirtyDerivedFields(javax.sql.DataSource)
	 */
	public int updateDirtyDerivedFields(DataSource dataSource)
		throws SQLException, IOException {

		if (dataSource == null) {
			// Normally, an IllegalArgumentException
			log.severe("null data source");
		}
		final int retVal = 0;
		log.warning(
			"Default updateDirtyDerivedFields does nothing; "
				+ retVal
				+ " records updated.");
		return retVal;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.urm.IUpdateDerivedFields#updateAllDerivedFields(javax.sql.DataSource)
	 */
	public int updateAllDerivedFields(DataSource dataSource)
		throws SQLException {

		if (dataSource == null) {
			// Normally, an IllegalArgumentException
			log.severe("null data source");
		}
		final int retVal = 0;
		log.warning(
			"Default updateAllDerivedFields does nothing; "
				+ retVal
				+ " records updated.");
		return retVal;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.urm.IUpdateDerivedFields#updateDerivedFields(javax.sql.DataSource,java.lang.Comparable)
	 */
	public int updateDerivedFields(DataSource dataSource, Comparable id)
		throws SQLException, IOException {

		if (dataSource == null) {
			// Normally, an IllegalArgumentException
			log.severe("null data source");
		}
		final int retVal = 0;
		log.warning(
			"Default updateDerivedFields does nothing; "
				+ retVal
				+ " records updated.");
		return retVal;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.urm.ejb.AbstractUpdateDerivedFields#checkProperties(java.util.Properties)
	 */
	protected void checkProperties(Properties p)
		throws UrmIncompleteSpecificationException {
		log.fine("properties: " + p.toString());
	}

}
