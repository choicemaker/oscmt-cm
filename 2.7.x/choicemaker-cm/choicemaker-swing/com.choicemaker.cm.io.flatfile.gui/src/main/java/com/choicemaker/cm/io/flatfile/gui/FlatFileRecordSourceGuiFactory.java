/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.flatfile.gui;

import com.choicemaker.cm.core.RecordSource;
import com.choicemaker.cm.core.Source;
import com.choicemaker.cm.io.flatfile.base.FlatFileRecordSource;
import com.choicemaker.cm.modelmaker.gui.ModelMaker;
import com.choicemaker.cm.modelmaker.gui.dialogs.SourceGui;
import com.choicemaker.cm.modelmaker.gui.sources.SourceGuiFactory;

/**
 * Description
 *
 * @author    Martin Buechi
 */
public class FlatFileRecordSourceGuiFactory implements SourceGuiFactory {
	@Override
	public String getName() {
		return "FlatFile";
	}

	@Override
	public SourceGui createGui(ModelMaker parent, Source s) {
		return new FlatFileRecordSourceGui(parent, (RecordSource)s);
	}

	@Override
	public SourceGui createGui(ModelMaker parent) {
		return createGui(parent, new FlatFileRecordSource());
	}

	@Override
	public SourceGui createSaveGui(ModelMaker parent) {
		return new FlatFileRecordSourceGui(parent, new FlatFileRecordSource());
	}

	@Override
	public Object getHandler() {
		return this;
	}

	@Override
	public Class getHandledType() {
		return FlatFileRecordSource.class;
	}

	@Override
	public String toString() {
		return "FlatFile";
	}

	@Override
	public boolean hasSink() {
		return true;
	}
}
