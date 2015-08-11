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
 * @version   $Revision: 1.1.1.1 $ $Date: 2009/05/03 16:02:58 $
 */
public class FlatFileMarkedRecordPairSourceGuiFactory implements SourceGuiFactory {
	public String getName() {
		return "FlatFile";
	}

	public SourceGui createGui(ModelMaker parent, Source s) {
		return new FlatFileMarkedRecordPairSourceGui(parent, (MarkedRecordPairSource)s, false);
	}

	public SourceGui createGui(ModelMaker parent) {
		return createGui(parent, new FlatFileMarkedRecordPairSource());
	}

	public SourceGui createSaveGui(ModelMaker parent) {
		return new FlatFileMarkedRecordPairSourceGui(parent, new FlatFileMarkedRecordPairSource(), true);
	}

	public Object getHandler() {
		return this;
	}

	public Class getHandledType() {
		return FlatFileMarkedRecordPairSource.class;
	}

	public String toString() {
		return "FlatFile MRPS";
	}

	public boolean hasSink() {
		return true;
	}
}
