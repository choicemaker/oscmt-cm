/*
 * Copyright (c) 2001, 2009 ChoiceMaker Technologies, Inc. and others.
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
import java.beans.PropertyChangeSupport;

import org.apache.log4j.Logger;

/**
 * Part of the Model in the MVC framework for ReviewMaker.
 *
 * Abstraction for a data element that notifies listeners when its data changes.
 *
 * @author   Arturo Falck
 * @version  $Revision: 1.2 $ $Date: 2010/03/27 21:03:06 $
 */
public abstract class DefaultObservableData implements ObservableData {

	private static final Logger logger = Logger.getLogger(DefaultObservableData.class);

	private PropertyChangeSupport support = null;

	public DefaultObservableData() {
		support = new PropertyChangeSupport(this);
	}

	/**
	 * @see java.beans.PropertyChangeSupport#addPropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		support.addPropertyChangeListener(listener);
	}

	/**
	 * @see java.beans.PropertyChangeSupport#getPropertyChangeListeners()
	 */
	public PropertyChangeListener[] getPropertyChangeListeners() {
		try {
			// return support.getPropertyChangeListeners();
			return (PropertyChangeListener[]) support.getClass().getMethod(
				"getPropertyChangeListeners",
				new Class[0]).invoke(
				this,
				new Object[0]);

		} catch (Exception ex) {
			logger.info("Caught Exception", ex);
		}
		throw new UnsupportedOperationException("Operation DefaultObservableData.etPropertyChangeListeners() not supported under JDK 1.3");
	}

	/**
	 * @see java.beans.PropertyChangeSupport#removePropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		support.removePropertyChangeListener(listener);
	}

	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		support.firePropertyChange(propertyName, oldValue, newValue);
	}

	public void removeAllPropertyChangeListeners() {
		support = new PropertyChangeSupport(this);
	}
}