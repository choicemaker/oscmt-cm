/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.gui.utils.viewer;

import java.awt.Rectangle;

import com.choicemaker.cm.core.Descriptor;
import com.choicemaker.cm.core.datamodel.DefaultObservableData;

/**
 * 
 * 
 * @author  Arturo Falck
 */
public class CompositeFrameModel extends DefaultObservableData implements InternalFrameModel {
	
	//****************** Fields
	
	private Descriptor descriptor;
	private String alias;
	private Rectangle bounds;
	private boolean enableEditing;
	
	private CompositePaneModel compositePaneModel;
	
	//****************** Constructors
	
	public CompositeFrameModel(Descriptor descriptor, int x, int y) {
		this(descriptor, new CompositePaneModel(descriptor, false), "<New Frame>", new Rectangle(x, y, 300, 300));
	}

	public CompositeFrameModel(
		Descriptor descriptor, 
		CompositePaneModel compositePaneModel,
		String alias,
		Rectangle bounds) {
			
		this.descriptor = descriptor;
		this.alias = alias;
		this.bounds = bounds;
		this.compositePaneModel = compositePaneModel;
	}
	
	/**
	 * Returns the descriptor.
	 * @return Descriptor
	 */
	@Override
	public Descriptor getDescriptor() {
		return descriptor;
	}

	/**
	 * Sets the descriptor.
	 * @param descriptor The descriptor to set
	 */
	@Override
	public void setDescriptor(Descriptor newValue) {
		Descriptor oldValue = descriptor;
		descriptor = newValue;
		firePropertyChange(DESCRIPTOR, oldValue, newValue);
	}

	/**
	 * Returns the alias.
	 * @return String
	 */
	@Override
	public String getAlias() {
		return alias;
	}

	/**
	 * Sets the alias.
	 * @param alias The alias to set
	 */
	@Override
	public void setAlias(String newValue) {
		String oldValue = alias;
		alias = newValue;
		firePropertyChange(ALIAS, oldValue, newValue);
	}

	/**
	 * Returns the bounds.
	 * @return Rectangle
	 */
	@Override
	public Rectangle getBounds() {
		return bounds;
	}

	/**
	 * Sets the bounds.
	 * @param bounds The bounds to set
	 */
	@Override
	public void setBounds(Rectangle newValue) {
		Rectangle oldValue = bounds;
		bounds = newValue;
		firePropertyChange(BOUNDS, oldValue, newValue);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getAlias() == null) ? 0 : getAlias().hashCode());
		result = prime * result + ((getBounds() == null) ? 0 : getBounds().hashCode());
		result = prime
				* result
				+ ((getCompositePaneModel() == null) ? 0 : getCompositePaneModel()
						.hashCode());
		result = prime * result
				+ ((getDescriptor() == null) ? 0 : getDescriptor().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object o){
		if (o instanceof CompositeFrameModel){
			CompositeFrameModel other = (CompositeFrameModel)o;
			boolean returnValue = true;
			
			returnValue &= getDescriptor() == other.getDescriptor();
			returnValue &= getAlias() == other.getAlias();
			returnValue &= getBounds() == other.getBounds();
			
			returnValue &= getCompositePaneModel().equals(other.getCompositePaneModel());
			
			return returnValue;
		}
		else{
			return false;
		}
	}
	
	/**
	 * @return CompositePaneModel
	 */
	public CompositePaneModel getCompositePaneModel() {
		return compositePaneModel;
	}

	/**
	 * @return boolean
	 */
	@Override
	public boolean isEnableEditing() {
		return enableEditing;
	}

	/**
	 * Recursively sets the enableEditing property in the whole Model.
	 * @param enableEditing The enableEditing to set
	 */
	@Override
	public void setEnableEditing(boolean newValue) {
		Boolean oldValue = new Boolean(enableEditing);
		enableEditing = newValue;
		firePropertyChange(ENABLE_EDITING, oldValue, new Boolean(newValue));
		
		getCompositePaneModel().setEnableEditing(newValue);
	}

}
