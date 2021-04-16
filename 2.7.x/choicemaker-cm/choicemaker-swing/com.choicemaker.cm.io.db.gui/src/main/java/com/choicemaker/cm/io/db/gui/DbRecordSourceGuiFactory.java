/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.db.gui;

import com.choicemaker.cm.core.Source;
import com.choicemaker.cm.io.db.oracle.OracleRecordSource;
import com.choicemaker.cm.modelmaker.gui.ModelMaker;
import com.choicemaker.cm.modelmaker.gui.dialogs.SourceGui;
import com.choicemaker.cm.modelmaker.gui.sources.SourceGuiFactory;

/**
 * Description
 *
 * @author   Adam Winkel
 */
public class DbRecordSourceGuiFactory implements SourceGuiFactory {
	@Override
	public String getName() {
		return "DB";
	}

	@Override
	public SourceGui createGui(ModelMaker parent, Source s) {
		return new DbRecordSourceGui(parent, (OracleRecordSource)s);
	}

	@Override
	public SourceGui createGui(ModelMaker parent) {
		return createGui(parent, new OracleRecordSource());
	}

	@Override
	public SourceGui createSaveGui(ModelMaker parent) {
		throw new UnsupportedOperationException("No such thing as a DB Record Sink.");
	}

	@Override
	public Object getHandler() {
		return this;
	}

	@Override
	public Class getHandledType() {
		return OracleRecordSource.class;
	}

	@Override
	public String toString() {
		return "DB RS";
	}

	@Override
	public boolean hasSink() {
		return false;
	}
}
