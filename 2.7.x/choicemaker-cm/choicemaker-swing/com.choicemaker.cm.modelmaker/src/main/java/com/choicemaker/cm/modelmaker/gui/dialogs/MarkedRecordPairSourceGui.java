/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.modelmaker.gui.dialogs;

import com.choicemaker.cm.core.Constants;
import com.choicemaker.cm.modelmaker.gui.ModelMaker;

/**
 * Description
 * 
 * @author  Martin Buechi
 */
public abstract class MarkedRecordPairSourceGui extends SourceGui {
	private static final long serialVersionUID = 1L;

	protected MarkedRecordPairSourceGui(ModelMaker parent, String title) {
		super(parent, title);
	}

	protected String getFileTypeDescription() {
		return "Marked record pair descriptors (*.mrps)";
	}
	
	protected String getExtension() {
		return Constants.MRPS_EXTENSION;
	}
}
