/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.flatfile.gui;

import com.choicemaker.cm.core.MarkedRecordPairSource;
import com.choicemaker.cm.core.Source;
import com.choicemaker.cm.io.flatfile.base.FlatFileMarkedRecordPairSource;
import com.choicemaker.cm.modelmaker.gui.ModelMaker;
import com.choicemaker.cm.modelmaker.gui.dialogs.SourceGui;
import com.choicemaker.cm.modelmaker.gui.sources.SourceGuiFactory;

/**
 * Description
 *
 * @author    Martin Buechi
 */
public class FlatFileMarkedRecordPairSourceGuiFactory implements SourceGuiFactory {
	@Override
	public String getName() {
		return "FlatFile";
	}

	@Override
	public SourceGui createGui(ModelMaker parent, Source s) {
		return new FlatFileMarkedRecordPairSourceGui(parent, (MarkedRecordPairSource)s, false);
	}

	@Override
	public SourceGui createGui(ModelMaker parent) {
		return createGui(parent, new FlatFileMarkedRecordPairSource());
	}

	@Override
	public SourceGui createSaveGui(ModelMaker parent) {
		return new FlatFileMarkedRecordPairSourceGui(parent, new FlatFileMarkedRecordPairSource(), true);
	}

	@Override
	public Object getHandler() {
		return this;
	}

	@Override
	public Class getHandledType() {
		return FlatFileMarkedRecordPairSource.class;
	}

	@Override
	public String toString() {
		return "FlatFile MRPS";
	}

	@Override
	public boolean hasSink() {
		return true;
	}
}
