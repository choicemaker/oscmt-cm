/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.modelmaker.gui.abstraction;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;

import com.choicemaker.cm.core.RepositoryChangeEvent;
import com.choicemaker.cm.core.RepositoryChangeListener;
import com.choicemaker.cm.modelmaker.gui.ModelMaker;
import com.choicemaker.cm.modelmaker.gui.listeners.EvaluationEvent;
import com.choicemaker.cm.modelmaker.gui.listeners.EvaluationListener;
import com.choicemaker.cm.module.swing.AbstractTabbedPanel;

/**
 * The panel from which training is initiated.  Users may turn clues on and off, 
 * manually set weights, evaluate the clues on the source to get the counts 
 * statistics, and train the model.
 * 
 * @author S. Yoakum-Stover
 */
public abstract class AbstractModelReviewPanel
	extends AbstractTabbedPanel
	implements
		RepositoryChangeListener,
		PropertyChangeListener,
		EvaluationListener {

	private static final long serialVersionUID = 1L;
	private ModelMaker parent;
//	private Trainer trainer;

//	private JPanel controlsPanel;
//	private JScrollPane cluePerformancePanel;
//	private CluePerformanceTable performanceTable;

//	private ClueTablePanel clueTablePanel;

//	private boolean dirty;

	public AbstractModelReviewPanel(ModelMaker g) {
		super();
		parent = g;
		setBorder(BorderFactory.createEmptyBorder(15, 5, 5, 5));
		buildPanel();
		parent.addPropertyChangeListener(this);
		parent.addEvaluationListener(this);
		parent.getProbabilityModelEventMultiplexer().addPropertyChangeListener(
			this);
		parent.addMarkedRecordPairDataChangeListener(this);
	}

	/**
	 * Invoked when the panel is displayed (true)
	 * or hidden (false) from the user
	 */
	@Override
	public abstract void setVisible(boolean b);

	protected abstract void buildPanel();

	/**
	 * Invoked to display (true) or hide (false) a clue
	 * performance table from the user
	 */
	public abstract void showCluePerformancePanel(boolean b);

	/**
	 * Handles PropertyChangeEvent notices from the application or the
	 * model that require the clue performance table to be reset:<ul>
	 * <li/>if the source is the application and<ul>
	 * <li/>the event name is
	 * {@link com.choicemaker.cm.modelmaker.ModelMakerEventNames#MARKED_RECORD_PAIR_SOURCE MARKED_RECORD_PAIR_SOURCE}
	 * <li/>the event name is
	 * {@link com.choicemaker.cm.modelmaker.ModelMakerEventNames#PROBABILITY_MODEL PROBABILITY_MODEL}
	 * </ul><li/>or the source is the current model</ul>
	 */
	@Override
	public abstract void propertyChange(PropertyChangeEvent evt);

	/** Handle evaluation events from the current model trainer */
	@Override
	public abstract void evaluated(EvaluationEvent evt);

	/** Handle data source changes */
	@Override
	public abstract void setChanged(RepositoryChangeEvent evt);

	/** Handle changes to data */
	@Override
	public abstract void recordDataChanged(RepositoryChangeEvent evt);

	/** Handle markup changes to data */
	@Override
	public abstract void markupDataChanged(RepositoryChangeEvent evt);

}
