/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.gui.utils.viewer;
import java.awt.Rectangle;

import com.choicemaker.cm.core.Descriptor;
import com.choicemaker.cm.core.datamodel.ObservableData;
/**
 * Common interface to .
 *
 * @author   Arturo Falck
 */
public interface InternalFrameModel extends ObservableData {
	
	//****************** Constants
	
	public static final String ENABLE_EDITING = "ENABLE_EDITING";
	public static final String BOUNDS = "BOUNDS";
	public static final String ALIAS = "ALIAS";
	public static final String DESCRIPTOR = "DESCRIPTOR";
	
	//****************** Accessor Methods
	
	Descriptor getDescriptor();
	void setDescriptor(Descriptor newValue);
	
	String getAlias();
	void setAlias(String newValue);
	
	Rectangle getBounds();
	void setBounds(Rectangle newValue);
	
	boolean isEnableEditing();
	void setEnableEditing(boolean newValue);
}
