/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.xml.gui;

import com.choicemaker.cm.core.RecordSource;
import com.choicemaker.cm.core.Source;
import com.choicemaker.cm.io.xml.base.XmlRecordSource;
import com.choicemaker.cm.modelmaker.gui.ModelMaker;
import com.choicemaker.cm.modelmaker.gui.dialogs.SourceGui;
import com.choicemaker.cm.modelmaker.gui.sources.SourceGuiFactory;

/**
 * Description
 *
 * @author    Martin Buechi
 */
public class XmlRecordSourceGuiFactory implements SourceGuiFactory {
	@Override
	public String getName() {
		return "XML";
	}

	@Override
	public SourceGui createGui(ModelMaker parent, Source s) {
		return new XmlRecordSourceGui(parent, (RecordSource)s);
	}

	@Override
	public SourceGui createGui(ModelMaker parent) {
		return createGui(parent, new XmlRecordSource());
	}

	@Override
	public SourceGui createSaveGui(ModelMaker parent) {
		return createGui(parent, new XmlRecordSource());
	}

	@Override
	public Object getHandler() {
		return this;
	}

	@Override
	public Class getHandledType() {
		return XmlRecordSource.class;
	}

	@Override
	public String toString() {
		return "XML";
	}

	@Override
	public boolean hasSink() {
		return true;
	}
}
