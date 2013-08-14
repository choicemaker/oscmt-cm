/*
 * Copyright (c) 2001, 2009 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.io.blocking.automated.base.db;

/**
 * Description
 * 
 * @author  Martin Buechi
 * @version $Revision: 1.1.1.1 $ $Date: 2009/05/03 16:02:47 $
 */
public interface DatabaseAbstraction {
	String EXTENSION_POINT = "com.choicemaker.cm.io.blocking.automated.base.databaseAbstraction";
	
	String getSetDateFormatExpression();
	
	String getSysdateExpression();
	
	String getDateFieldExpression(String field);
}
