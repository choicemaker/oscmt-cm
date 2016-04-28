/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.composite.gui;

import com.choicemaker.cm.core.MarkedRecordPairSource;
import com.choicemaker.cm.core.Source;
import com.choicemaker.cm.io.composite.base.CompositeMarkedRecordPairSource;
import com.choicemaker.cm.modelmaker.gui.ModelMaker;
import com.choicemaker.cm.modelmaker.gui.dialogs.SourceGui;
import com.choicemaker.cm.modelmaker.gui.sources.SourceGuiFactory;

/**
 * Description
 *
 * @author    Martin Buechi
 */
public class CompositeMarkedRecordPairSourceGuiFactory implements SourceGuiFactory {
	public String getName() {
		return "Composite";
	}

	public SourceGui createGui(ModelMaker parent, Source s) {
		return new CompositeMarkedRecordPairSourceGui(parent, (MarkedRecordPairSource)s);
	}

	public SourceGui createGui(ModelMaker parent) {
		return createGui(parent, new CompositeMarkedRecordPairSource());
	}

	public SourceGui createSaveGui(ModelMaker parent) {
		return null;
	}

	public Object getHandler() {
		return this;
	}

	public Class getHandledType() {
		return CompositeMarkedRecordPairSource.class;
	}

	public String toString() {
		return "Composite MRPS";
	}

	public boolean hasSink() {
		return false;
	}
}
