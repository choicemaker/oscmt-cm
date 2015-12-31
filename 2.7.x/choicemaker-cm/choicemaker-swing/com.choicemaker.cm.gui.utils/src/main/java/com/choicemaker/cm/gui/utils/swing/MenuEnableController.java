/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.gui.utils.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 * .
 *
 * @author   Arturo Falck
 */
public class MenuEnableController implements PropertyChangeListener {
		
		private Vector childActions = new Vector();
		private Action menu;
		
		public MenuEnableController(Action menu){
			this.menu = menu;
		}
		
		public void addChildAction(AbstractAction action){
			action.addPropertyChangeListener(this);
			childActions.addElement(action);
		}
		
		/**
		 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
		 */
		public void propertyChange(PropertyChangeEvent evt) {
			menu.setEnabled(isAnyReviewActionEnabled());
		}
		
		private boolean isAnyReviewActionEnabled(){
			boolean returnValue = false;
			
			Enumeration e = childActions.elements();
			while (e.hasMoreElements() && returnValue == false) {
				Action element = (Action) e.nextElement();
				returnValue = element.isEnabled();
			}
			
			return returnValue;
		}
	};
