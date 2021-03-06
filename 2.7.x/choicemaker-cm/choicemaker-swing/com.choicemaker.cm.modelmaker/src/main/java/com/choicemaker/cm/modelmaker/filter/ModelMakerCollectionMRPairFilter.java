/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.modelmaker.filter;

import java.beans.PropertyChangeEvent;
import java.util.Collection;

import com.choicemaker.cm.analyzer.filter.CollectionMarkedRecordPairFilter;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.modelmaker.ModelMakerEventNames;
import com.choicemaker.cm.modelmaker.gui.ModelMaker;

/**
 * @author	Adam Winkel
 */
public class ModelMakerCollectionMRPairFilter extends
		CollectionMarkedRecordPairFilter implements
		ListeningMarkedRecordPairFilter {

	private static final long serialVersionUID = 1L;
	private final ModelMaker parent;

	public ModelMakerCollectionMRPairFilter(ModelMaker modelMaker) {
		this(modelMaker, null);
		// assert useCollection == false;
	}

	public ModelMakerCollectionMRPairFilter(ModelMaker modelMaker, Collection pairs) {
		super(null);
		this.parent = modelMaker;
		parent.addPropertyChangeListener(this);
		parent.getProbabilityModelEventMultiplexer().addPropertyChangeListener(this);
		reset();
		setAcceptedPairs(pairs);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();
		Object source = evt.getSource();
		if (source == parent) {
			if (propertyName == ModelMakerEventNames.PROBABILITY_MODEL) {
				setModel(parent.getProbabilityModel());
			}
		} else if (source == parent.getProbabilityModel() && propertyName == null) {
			setModel(parent.getProbabilityModel());
		}
	}

	public void setModel(ImmutableProbabilityModel model) {
		reset();
	}

}
