/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.xmlenc.gui;

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
public class XmlEncMarkedRecordPairSourceGuiFactory implements SourceGuiFactory {
	public String getName() {
		return "XML ENC";
	}

	public SourceGui createGui(ModelMaker parent, Source s) {
		return new XmlEncMarkedRecordPairSourceGui(parent, (MarkedRecordPairSource)s, false);
	}

	public SourceGui createGui(ModelMaker parent) {
		return createGui(parent, new XmlMarkedRecordPairSource());
	}

	public SourceGui createSaveGui(ModelMaker parent) {
		return new XmlEncMarkedRecordPairSourceGui(parent, new XmlMarkedRecordPairSource(), true);
	}

	public Object getHandler() {
		return this;
	}

	public Class getHandledType() {
		return XmlMarkedRecordPairSource.class;
	}

	public String toString() {
		return "XML EMRPS";
	}

	public boolean hasSink() {
		return true;
	}
}
