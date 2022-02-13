/*******************************************************************************
 * Copyright (c) 2003, 2018 ChoiceMaker LLC and others.
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
package com.choicemaker.cm.aba.util;

import java.util.logging.Logger;

import com.choicemaker.cm.aba.DatabaseAccessor;
import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.core.ChoiceMakerExtensionPoint;
import com.choicemaker.e2.CMConfigurationElement;
import com.choicemaker.e2.CMExtension;
import com.choicemaker.e2.E2Exception;
import com.choicemaker.e2.platform.CMPlatformUtils;
import com.choicemaker.util.StringUtils;

public class DatabaseAccessorUtils {

	private static final Logger logger =
		Logger.getLogger(DatabaseAccessorUtils.class.getName());

	protected static final String DATABASE_ACCESSOR =
		ChoiceMakerExtensionPoint.CM_IO_BLOCKING_AUTOMATED_BASE_DATABASEACCESSOR;

	public static DatabaseAccessor getDatabaseAccessor(String dbaName)
			throws BlockingException {

		final CMExtension dbaExt =
			CMPlatformUtils.getExtension(DATABASE_ACCESSOR, dbaName);
		if (dbaExt == null) {
			String msg = "null DatabaseAccessor extension for: " + dbaName;
			logger.severe(msg);
			throw new IllegalStateException(msg);
		}

		DatabaseAccessor retVal = null;
		try {
			final CMConfigurationElement[] configElements =
				dbaExt.getConfigurationElements();
			if (configElements == null || configElements.length == 0) {
				String msg = "No database accessor configurations: " + dbaName;
				logger.severe(msg);
				throw new IllegalStateException(msg);
			} else if (configElements.length != 1) {
				String msg = "Multiple database accessor configurations for "
						+ dbaName + ": " + configElements.length;
				logger.warning(msg);
			} else {
				assert configElements.length == 1;
			}
			final CMConfigurationElement configElement = configElements[0];
			Object o = configElement.createExecutableExtension("class");
			assert o != null;
			assert o instanceof DatabaseAccessor;
			retVal = (DatabaseAccessor) o;
		} catch (E2Exception e) {
			String msg = "Unable to construct database accessor: " + e;
			logger.severe(msg);
			throw new BlockingException(msg);
		}
		assert retVal != null;
		return retVal;
	}

	private DatabaseAccessorUtils() {
	}

	/**
	 * This method takes the SQL string specified in
	 * <code>SubsetDbRecordCollection</code> and converts that to the condition
	 * string as expected by <code>DatabaseAccessor</code>, particularly by
	 * <code>OraDatabaseAccessor</code>.
	 * <p>
	 * For example: SQL String =
	 * "select mci_id from tb_patient where id_stat_cd = 'A' order by mci_id"
	 * <p>
	 * return = "TB_PATIENT T WHERE B.MCI_ID = T.MCI_ID AND ID_STAT_CD = 'A'"
	 * <p>
	 * 
	 * @author PC (3/27/2007)
	 *
	 * @param input
	 * @param key
	 * @return
	 */
	public static String parseSQL(String input, String key) {
		StringBuffer ret = new StringBuffer();

		// FIXME use ANTLR for more robust parsing

		if (!StringUtils.nonEmptyString(input)) {
			throw new IllegalArgumentException(
					"null or blank SQL record id selection statement");
		}
		if (!StringUtils.nonEmptyString(key)) {
			throw new IllegalArgumentException("null or blank SQL key");
		}

		// get "WHERE" and "FROM"
		input = input.toUpperCase();
		key = key.toUpperCase();
		int w = input.indexOf("WHERE");
		int o = input.indexOf("ORDER");

		// Must be a FROM clause and exactly one TABLE
		int t = input.indexOf("FROM");
		if (t < 0) {
			throw new IllegalArgumentException(
					"Missing FROM clause in SQL record id selection statement: '"
							+ input + "'");
		}
		String str = null;
		if (w < 0 && o < 0) {
			str = input.substring(t + 5);
		} else if (w > 0) {
			str = input.substring(t + 5, w - 1);
		} else if (o > 0) {
			str = input.substring(t + 5, o - 1);
		}
		if (str.indexOf(',') != -1) {
			throw new IllegalArgumentException(
					"cannot have more than 1 table.");
		}

		// Append the (table) name which occurs after the FROM clause
		int i = str.indexOf(' ');
		if (i == -1) {
			ret.append(str);
		} else {
			ret.append(str.substring(0, i));
		}

		ret.append(" T ");
		ret.append("WHERE");
		ret.append(" B.");
		ret.append(key);
		ret.append(" = T.");
		ret.append(key);

		if (w > 0) {
			ret.append(" AND ");
			if (o > 0) {
				ret.append(input.substring(w + 6, o - 1));
			} else {
				ret.append(input.substring(w + 6));
			}
		}

		return ret.toString();
	}

}
