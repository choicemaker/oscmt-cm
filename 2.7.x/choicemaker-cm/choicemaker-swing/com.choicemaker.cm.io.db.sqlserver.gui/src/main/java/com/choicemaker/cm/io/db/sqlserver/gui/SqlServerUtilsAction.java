/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
/*
 * Created on Jan 9, 2004
 *
 */
package com.choicemaker.cm.io.db.sqlserver.gui;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.choicemaker.cm.modelmaker.gui.ModelMaker;
import com.choicemaker.cm.modelmaker.gui.menus.ToolsMenu.ToolAction;

/**
 * @author ajwinkel
 *
 */
public class SqlServerUtilsAction extends ToolAction implements PropertyChangeListener {

	private static final long serialVersionUID = 271L;
	
	public SqlServerUtilsAction() {
		super("SQL Server Utils");
		setEnabled(false);
	}

	@Override
	public void setModelMaker(ModelMaker m) {
		super.setModelMaker(m);
		m.addPropertyChangeListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) { }

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		setEnabled(modelMaker.haveProbabilityModel() && modelMaker.getProbabilityModel().canEvaluate());		
	}

	public static class SqlServerIdSearchAction extends ToolAction implements PropertyChangeListener {

		private static final long serialVersionUID = 271L;

		public SqlServerIdSearchAction() {
			super("SQL Server Record Search...");
			setEnabled(false);
		}
	
		@Override
		public void setModelMaker(ModelMaker m) {
			super.setModelMaker(m);
			m.addPropertyChangeListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			new SqlServerIdSearchDialog(modelMaker).show();
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			setEnabled(modelMaker.haveProbabilityModel() && modelMaker.getProbabilityModel().canEvaluate());		
		}

	}
	
	public static class SqlServerPairViewerAction extends ToolAction implements PropertyChangeListener {

		private static final long serialVersionUID = 271L;

		public SqlServerPairViewerAction() {
			super("SQL Server Pair Viewer...");
			setEnabled(false);
		}
	
		@Override
		public void setModelMaker(ModelMaker m) {
			super.setModelMaker(m);
			m.addPropertyChangeListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			SqlServerPairViewerDialog.showDialog(modelMaker);
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			setEnabled(modelMaker.haveProbabilityModel() && modelMaker.getProbabilityModel().canEvaluate());		
		}
	}
	
}
