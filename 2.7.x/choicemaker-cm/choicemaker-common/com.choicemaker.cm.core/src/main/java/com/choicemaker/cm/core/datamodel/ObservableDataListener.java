/*
 * Copyright (c) 2001, 2022 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.core.datamodel;

/**
 * Implement this interface when interested in being notified that Items are added and removed from Composites.
 *
 * @author   Arturo Falck
 */
public interface ObservableDataListener {

    
    /**
     * Notification that a new DefaultObservableData item was added to the Composite.
     * 
     * @param event the ObservableDataEvent that contains the details of the addition.
     */	
	public void observableDataAdded(ObservableDataEvent event);
    	
    /**
     * Notification that an DefaultObservableData item was removed from the Composite.
     * 
     * @param event the ObservableDataEvent that contains the details of the removal.
     */	
	public void observableDataRemoved(ObservableDataEvent event);
}
