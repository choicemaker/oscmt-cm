/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.db.base;

/**
 *
 * @author    Martin Buechi
 * @version   $Revision: 1.1.1.1 $ $Date: 2009/05/03 16:02:55 $
 */
public class DbField {
	public final String table;
	public final String name;
 	public final String baseName;	

	public DbField(String table, String name) {
		this(table,name,name);
	}

	public DbField(String table, String name, String baseName) {
		this.table = table;
		this.name = name;
		this.baseName = baseName;
	}
}
