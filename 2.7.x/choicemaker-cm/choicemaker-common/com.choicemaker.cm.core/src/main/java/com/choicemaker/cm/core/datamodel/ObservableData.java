/*
 * Copyright (c) 2001, 2015 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.core.datamodel;
import java.beans.PropertyChangeListener;
/**
 * .
 *
 * @author   Arturo Falck
 */
public interface ObservableData {
	void addPropertyChangeListener(PropertyChangeListener listener);
	PropertyChangeListener[] getPropertyChangeListeners();
	void removePropertyChangeListener(PropertyChangeListener listener);
}
