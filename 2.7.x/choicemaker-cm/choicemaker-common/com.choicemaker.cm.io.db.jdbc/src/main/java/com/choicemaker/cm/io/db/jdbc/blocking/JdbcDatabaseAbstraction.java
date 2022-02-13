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
/*
 * Created on Feb 15, 2005
 *
 */
package com.choicemaker.cm.io.db.jdbc.blocking;

import com.choicemaker.cm.aba.base.db.DatabaseAbstraction;

/**
 * @author pcheung
 *
 */
public class JdbcDatabaseAbstraction implements DatabaseAbstraction {

	public String getSetDateFormatExpression() {
//		return "bind @db2ubind.lst datetime ISO blocking all grant public";

		//this is completely not needed since DB by default uses yyyy-mm-dd as input.
		//we just need some valid SQL here.
		return "SELECT current date FROM sysibm.sysdummy1";
	}

	public String getSysdateExpression() {
		return "SYSDATE ()";
	}

	public String getDateFieldExpression(String field) {
		return field;
	}

}
