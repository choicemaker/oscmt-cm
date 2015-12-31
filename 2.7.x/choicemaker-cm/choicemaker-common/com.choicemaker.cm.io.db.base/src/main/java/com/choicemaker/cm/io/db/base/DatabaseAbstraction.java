/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.db.base;

import com.choicemaker.cm.core.ChoiceMakerExtensionPoint;

/**
 * Description
 * 
 * @author Martin Buechi
 */
public interface DatabaseAbstraction {

	String EXTENSION_POINT =
		ChoiceMakerExtensionPoint.CM_IO_DB_BASE_DATABASEABSTRACTION;

	String getSetDateFormatExpression();

	String getSysdateExpression();

	String getDateFieldExpression(String field);
}
