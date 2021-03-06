/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.modelmaker.gui.listeners;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.SwingPropertyChangeSupport;

public class EventMultiplexer implements PropertyChangeListener {
	private SwingPropertyChangeSupport propertyChangeListeners;

	public EventMultiplexer() {
		propertyChangeListeners = new SwingPropertyChangeSupport(this);
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		propertyChangeListeners.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		propertyChangeListeners.removePropertyChangeListener(l);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		propertyChangeListeners.firePropertyChange(evt);
	}
}
