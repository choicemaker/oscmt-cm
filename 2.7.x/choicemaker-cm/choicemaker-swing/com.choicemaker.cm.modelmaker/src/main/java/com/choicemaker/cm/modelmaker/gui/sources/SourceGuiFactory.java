/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.modelmaker.gui.sources;

import com.choicemaker.cm.core.DynamicDispatchHandler;
import com.choicemaker.cm.core.Source;
import com.choicemaker.cm.modelmaker.gui.ModelMaker;
import com.choicemaker.cm.modelmaker.gui.dialogs.SourceGui;
/**
 * Description
 *
 * @author    Martin Buechi
 */
public interface SourceGuiFactory extends DynamicDispatchHandler {
	String getName();

	SourceGui createGui(ModelMaker parent, Source s);

	SourceGui createGui(ModelMaker parent);

	SourceGui createSaveGui(ModelMaker parent);

	boolean hasSink();

	@Override
	Class getHandledType();
}
