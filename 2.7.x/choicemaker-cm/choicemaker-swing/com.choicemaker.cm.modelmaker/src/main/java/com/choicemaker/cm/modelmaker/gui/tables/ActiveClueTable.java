/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.modelmaker.gui.tables;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.choicemaker.cm.core.MutableMarkedRecordPair;
import com.choicemaker.cm.core.RepositoryChangeEvent;
import com.choicemaker.cm.core.RepositoryChangeListener;
import com.choicemaker.cm.modelmaker.gui.ModelMaker;
import com.choicemaker.cm.modelmaker.gui.listeners.ClueNameCellListener;
import com.choicemaker.cm.modelmaker.gui.listeners.TableColumnListener;

/**
 * The active clues for a particular MarkedRecordPair.  Displayed on the DefaultPairReviewPanel.
 * 
 * @author S. Yoakum-Stover
 */
public class ActiveClueTable extends JTable implements RepositoryChangeListener {
	private static final long serialVersionUID = 1L;
	private ModelMaker modelMaker;
	private ActiveClueTableModel myModel;
	private MutableMarkedRecordPair markedRecordPair;

	public ActiveClueTable(ModelMaker modelMaker) {
		super();
		this.modelMaker = modelMaker;
		init();
	}

	private void init() {
		myModel = new ActiveClueTableModel(modelMaker.getProbabilityModel());
		setAutoCreateColumnsFromModel(false);
		setModel(myModel);

		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		TableColumn[] columns = myModel.getColumns();
		for (int i = 0; i < columns.length; i++) {
			addColumn(columns[i]);
		}

		JTableHeader header = getTableHeader();
		header.setUpdateTableInRealTime(true);
		header.addMouseListener(new TableColumnListener(this, myModel));
		addMouseListener(new ClueNameCellListener(modelMaker, this));
	}

	/**
	 * Asks the myModel to rebuild its rows, then refresh itself.
	 */
	public void markedRecordPairSelected(int index) {
		if(markedRecordPair != null) {
			markedRecordPair.removeRepositoryChangeListener(this);
		}
		markedRecordPair = (MutableMarkedRecordPair) modelMaker.getSourceList().get(index);
		markedRecordPair.addRepositoryChangeListener(this);
		myModel.setMarkedRecordPair(markedRecordPair);
		refresh();
	}

	public void reset() {
		if(markedRecordPair != null) {
			markedRecordPair.removeRepositoryChangeListener(this);
		}
		markedRecordPair = null;
		myModel.reset();
		tableChanged(new TableModelEvent(myModel));
		repaint();
	}

	private void refresh() {
		//refresh the column headings
		TableColumnModel colModel = getColumnModel();
		for (int i = 0; i < myModel.getColumnCount(); i++) {
			TableColumn tCol = colModel.getColumn(i);
			tCol.setHeaderValue(myModel.getColumnName(tCol.getModelIndex()));
		}
		getTableHeader().repaint();

		//repaint the table
		tableChanged(new TableModelEvent(myModel));
		repaint();
	}
	/**
	 * @see com.choicemaker.cm.train.gui.listeners.RepositoryChangeListener#setChanged(com.choicemaker.cm.train.gui.listeners.RepositoryChangeEvent)
	 */
	@Override
	public void setChanged(RepositoryChangeEvent evt) {
	}
	/**
	 * @see com.choicemaker.cm.train.gui.listeners.RepositoryChangeListener#recordDataChanged(com.choicemaker.cm.train.gui.listeners.RepositoryChangeEvent)
	 */
	@Override
	public void recordDataChanged(RepositoryChangeEvent evt) {
		myModel.setMarkedRecordPair(markedRecordPair);
		refresh();
	}
	/**
	 * @see com.choicemaker.cm.train.gui.listeners.RepositoryChangeListener#markupDataChanged(com.choicemaker.cm.train.gui.listeners.RepositoryChangeEvent)
	 */
	@Override
	public void markupDataChanged(RepositoryChangeEvent evt) {
	}
}
