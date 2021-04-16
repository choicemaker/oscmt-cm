/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.gui.utils.viewer;

import java.awt.BorderLayout;
import java.awt.Dimension;

import com.choicemaker.cm.core.RecordData;


/**
 * Description
 * 
 * @author  Martin Buechi
 */
public class CompositeFrame extends InternalFrame{

	private static final long serialVersionUID = 1L;
	private boolean contentEditable;
	private CompositePane pane;
	private boolean pair;

	public CompositeFrame(boolean pair, boolean contentEditable) {
		super("", true, true, false, false);
		this.pair = pair;
		this.contentEditable = contentEditable;
		setFrameIcon(null);
		setMinimumSize(new Dimension(10, 10));
		
		getContentPane().setLayout(new BorderLayout());
	}
	
	@Override
	public void destroy() {
		super.destroy();
		pane.destroy();
	}

	/**
	 * Sets the recordPairFrameModel.
	 * @param recordPairFrameModel The recordPairFrameModel to set
	 */
	@Override
	public void initInternalFrameModel() {
		
		pane = new CompositePane(pair, contentEditable);
		pane.setCompositePaneModel(((CompositeFrameModel)getInternalFrameModel()).getCompositePaneModel());
		getContentPane().add(pane);
		
	}
	

	@Override
	public void setRecordData(RecordData recordData) {
		pane.setRecordData(recordData);
	}
}
