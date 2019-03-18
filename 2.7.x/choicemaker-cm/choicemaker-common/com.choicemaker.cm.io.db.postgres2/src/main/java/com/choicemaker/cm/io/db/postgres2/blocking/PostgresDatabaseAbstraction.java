/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.db.postgres2.blocking;

import com.choicemaker.cm.io.db.base.DatabaseAbstraction;

/**
 * Description
 * 
 * @author  Martin Buechi
 */
public class PostgresDatabaseAbstraction implements DatabaseAbstraction {

	/**
	 * @see com.choicemaker.cm.io.db.base.plugin.automatedblocking.db.DatabaseAbstraction#getSetDateFormatExpression()
	 */
	@Override
	public String getSetDateFormatExpression() {
		return "SET datestyle = \"ISO, YMD\";";
	}

	/**
	 * @see com.choicemaker.cm.io.db.base.plugin.automatedblocking.db.DatabaseAbstraction#getSysdateExpression()
	 */
	@Override
	public String getSysdateExpression() {
		return "now()";
	}

	/**
	 * @see com.choicemaker.cm.io.db.base.plugin.automatedblocking.db.DatabaseAbstraction#getDateFieldExpression(java.lang.String)
	 */
	@Override
	public String getDateFieldExpression(String field) {
		return "to_char(" + field + ", 'YYYY-MM-DD')";
	}
}
