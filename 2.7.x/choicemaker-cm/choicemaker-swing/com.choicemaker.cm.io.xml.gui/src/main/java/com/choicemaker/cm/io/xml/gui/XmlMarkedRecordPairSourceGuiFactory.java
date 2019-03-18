/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.xml.gui;

import com.choicemaker.cm.core.MarkedRecordPairSource;
import com.choicemaker.cm.core.Source;
import com.choicemaker.cm.io.xml.base.XmlMarkedRecordPairSource;
import com.choicemaker.cm.modelmaker.gui.ModelMaker;
import com.choicemaker.cm.modelmaker.gui.dialogs.SourceGui;
import com.choicemaker.cm.modelmaker.gui.sources.SourceGuiFactory;

/**
 * Description
 *
 * @author    Martin Buechi
 */
public class XmlMarkedRecordPairSourceGuiFactory implements SourceGuiFactory {
	@Override
	public String getName() {
		return "XML";
	}

	@Override
	public SourceGui createGui(ModelMaker parent, Source s) {
		return new XmlMarkedRecordPairSourceGui(parent, (MarkedRecordPairSource)s, false);
	}

	@Override
	public SourceGui createGui(ModelMaker parent) {
		return createGui(parent, new XmlMarkedRecordPairSource());
	}

	@Override
	public SourceGui createSaveGui(ModelMaker parent) {
		return new XmlMarkedRecordPairSourceGui(parent, new XmlMarkedRecordPairSource(), true);
	}

	@Override
	public Object getHandler() {
		return this;
	}

	@Override
	public Class getHandledType() {
		return XmlMarkedRecordPairSource.class;
	}

	@Override
	public String toString() {
		return "XML MRPS";
	}

	@Override
	public boolean hasSink() {
		return true;
	}
}
