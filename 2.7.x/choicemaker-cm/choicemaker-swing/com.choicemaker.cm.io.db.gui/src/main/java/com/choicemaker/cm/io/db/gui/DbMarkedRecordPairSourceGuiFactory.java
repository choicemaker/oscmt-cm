/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.db.gui;

import com.choicemaker.cm.core.MarkedRecordPairSource;
import com.choicemaker.cm.core.Source;
import com.choicemaker.cm.io.db.oracle.OracleMarkedRecordPairSource2;
import com.choicemaker.cm.modelmaker.gui.ModelMaker;
import com.choicemaker.cm.modelmaker.gui.dialogs.SourceGui;
import com.choicemaker.cm.modelmaker.gui.sources.SourceGuiFactory;

/**
 * Description
 *
 * @author    Martin Buechi
 */
public class DbMarkedRecordPairSourceGuiFactory implements SourceGuiFactory {
	public String getName() {
		return "Db";
	}

	public SourceGui createGui(ModelMaker parent, Source s) {
		return new DbMarkedRecordPairSourceGui(parent, (MarkedRecordPairSource)s);
	}

	public SourceGui createGui(ModelMaker parent) {
		return createGui(parent, new OracleMarkedRecordPairSource2());
	}

	public SourceGui createSaveGui(ModelMaker parent) {
		return null;
	}

	public Object getHandler() {
		return this;
	}

	public Class getHandledType() {
		return OracleMarkedRecordPairSource2.class;
	}

	public String toString() {
		return "Db MRPS";
	}

	public boolean hasSink() {
		return false;
	}
}
