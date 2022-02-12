/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package com.choicemaker.cm.io.db.sqlserver.gui;

import com.choicemaker.cm.core.Source;
import com.choicemaker.cm.io.db.base.xmlconf.ConnectionPoolDataSourceXmlConf;
import com.choicemaker.cm.io.db.sqlserver.SqlServerMarkedRecordPairSource;
import com.choicemaker.cm.modelmaker.gui.ModelMaker;
import com.choicemaker.cm.modelmaker.gui.dialogs.SourceGui;
import com.choicemaker.cm.modelmaker.gui.sources.SourceGuiFactory;

/**
 * Description
 *
 * @author   Adam Winkel
 */
public class SqlServerMarkedRecordPairSourceGuiFactory implements SourceGuiFactory {

	@Override
	public String getName() {
		return "SQL Server";
	}

	@Override
	public SourceGui createGui(ModelMaker parent, Source s) {
		return new SqlServerMarkedRecordPairSourceGui(parent, (SqlServerMarkedRecordPairSource)s);
	}

	@Override
	public SourceGui createGui(ModelMaker parent) {
		return createGui(parent, new SqlServerMarkedRecordPairSource());
	}

	@Override
	public boolean hasSink() {
		return false;
	}

	@Override
	public SourceGui createSaveGui(ModelMaker parent) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getHandler() {
		return this;
	}

	@Override
	public Class getHandledType() {
		return SqlServerMarkedRecordPairSource.class;
	}

	@Override
	public String toString() {
		return "SQL Server MRPS";
	}
	
	static {
		ConnectionPoolDataSourceXmlConf.maybeInit();
	}

}
