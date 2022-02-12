/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.modelmaker.gui.tables;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;

import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.util.ChoiceMakerCoreMessages;
import com.choicemaker.cm.modelmaker.ModelMakerEventNames;
import com.choicemaker.cm.modelmaker.gui.ModelMaker;

/**
 * Description
 * 
 * @author  Martin Buechi
 */
public class ActiveClueTablePanel extends JScrollPane implements PropertyChangeListener {
	private static final long serialVersionUID = 1L;
	private ModelMaker modelMaker;
	private ActiveClueTable clueTable;

	public ActiveClueTablePanel(ModelMaker modelMaker) {
		this.modelMaker = modelMaker;
		setBorder(
			BorderFactory.createTitledBorder(
				ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.panel.humanreview.activeclues")));
		setPreferredSize(new Dimension(800, 140));
		modelMaker.addPropertyChangeListener(this);
		modelMaker.getProbabilityModelEventMultiplexer().addPropertyChangeListener(this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();
		Object source = evt.getSource();
		if (source == modelMaker) {
			if (propertyName == ModelMakerEventNames.PROBABILITY_MODEL) {
				replaceClueTable();
			} else if (propertyName == ModelMakerEventNames.MARKED_RECORD_PAIR) {
				if(clueTable != null) {
					clueTable.markedRecordPairSelected(((Integer) evt.getNewValue()).intValue());
				}
			} else if(propertyName == ModelMakerEventNames.MARKED_RECORD_PAIR_SOURCE) {
				resetData();
			}
		} else if (
			source == modelMaker.getProbabilityModel()
				&& (propertyName == null || propertyName == ImmutableProbabilityModel.MACHINE_LEARNER)) {
			replaceClueTable();
		}
	}
	
	public void resetData() {
		if(clueTable != null) {
			clueTable.reset();
		}
	}

	private void replaceClueTable() {
		if (clueTable != null) {
			getViewport().remove(clueTable);
		}
		ImmutableProbabilityModel model = modelMaker.getProbabilityModel();
		if (model != null) {
			clueTable = new ActiveClueTable(modelMaker);
			getViewport().add(clueTable);
		}
	}
}
