/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.ml.me.gui;

import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.choicemaker.cm.core.util.ChoiceMakerCoreMessages;
import com.choicemaker.cm.ml.me.base.MaximumEntropy;
import com.choicemaker.cm.modelmaker.gui.dialogs.TrainDialog;
import com.choicemaker.cm.modelmaker.gui.hooks.TrainDialogPlugin;
import com.choicemaker.cm.modelmaker.gui.utils.EnablednessGuard;

/**
 *
 * @author    
 */
public class MeTrainDialogPlugin extends TrainDialogPlugin {
	private static final long serialVersionUID = 1L;
	private JLabel trainingIterationsLabel;
	private JTextField trainingIterations;
	private MaximumEntropy me;
//	private TrainDialog trainDialog;

	public MeTrainDialogPlugin(MaximumEntropy me) {
		this.me = me;
		setBorder(BorderFactory.createTitledBorder(ChoiceMakerCoreMessages.m.formatMessage("ml.me.train.label")));
		trainingIterationsLabel = new JLabel(ChoiceMakerCoreMessages.m.formatMessage("ml.me.train.iterations"));
		trainingIterations = new JTextField(5);
		trainingIterations.setText(String.valueOf(me.getTrainingIterations()));
		setLayout(new FlowLayout(FlowLayout.LEFT));
		add(trainingIterationsLabel);
		add(trainingIterations);
	}
	
	public void setTrainDialog(TrainDialog trainDialog) {
//		this.trainDialog = trainDialog;
		EnablednessGuard dl = new EnablednessGuard(trainDialog);
		trainingIterations.getDocument().addDocumentListener(dl);
	}
	
	public boolean isParametersValid() {
		try {
			return Integer.parseInt(trainingIterations.getText()) > 0;
		} catch (NumberFormatException ex) {
			// ignore
		}
		return false;
	}

	public void set() {
		me.setTrainingIterations(Integer.parseInt(trainingIterations.getText()));
	}
}
