/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.gui.utils.viewer;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;

import com.choicemaker.cm.core.Descriptor;
import com.choicemaker.cm.core.RecordData;
import com.choicemaker.cm.gui.utils.viewer.event.RecordTableMouseListener;

/**
 * A table displaying the data associated with a single Descriptor on a give MarkedRecordPair.
 * 
 * @author Martin Buechi
 * @author S. Yoakum-Stover
 */
public class RecordTable extends JTable {
	private static final long serialVersionUID = 1L;
	public static final Color TOP_COLOR = new Color(195, 196, 237);
	public static final Color BOTTOM_COLOR = new Color(195, 237, 196);

	private boolean contentEditable;
	private boolean topTable;
	private Color backgroundColor;
	private RecordTableMouseListener recordTableMouseListener;
	private PropertyChangeListener columnListener;

	public RecordTable(boolean contentEditable, boolean topTable) {
		this.contentEditable = contentEditable;
		this.topTable = topTable;
		setAutoCreateColumnsFromModel(false);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setCellSelectionEnabled(true);
		backgroundColor = topTable ? TOP_COLOR : BOTTOM_COLOR;
		setDefaultRenderer(Object.class, new TypedTableCellRenderer(backgroundColor));
		setDefaultEditor(Object.class, new FocusListeningCellEditor(new JTextField()));
		setBackground(backgroundColor);
	}

	public void destroy() {
		RecordTableColumnModel columnModel = (RecordTableColumnModel) getColumnModel();
		columnModel.removePropertyChangeListener(columnListener);
		for (int i = 0; i < 10; ++i) {
			columnModel.removeColumnModelListener(this);
		}
		columnModel.removeColumnModelListener(getTableHeader());
		//		super.setColumnModel(new DefaultTableColumnModel());
	}

	/**
	 * @see javax.swing.JTable#setColumnModel(javax.swing.table.TableColumnModel)
	 */
	public void setColumnModel(RecordTableColumnModel columnModel) {
		super.setColumnModel(columnModel);
		// NOTE: This fixes the "Header not updated" bug.
		columnListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				getTableHeader().repaint();
				updateFromModel();
			}
		};
		columnModel.addPropertyChangeListener(columnListener);
		updateFromModel();
	}

	protected void updateFromModel() {
		if (recordTableMouseListener != null) {
			boolean enableEditing = ((RecordTableColumnModel) getColumnModel()).isEnableEditing();
			recordTableMouseListener.setEnableEditing(enableEditing);
			getTableHeader().setReorderingAllowed(enableEditing);
			getTableHeader().setResizingAllowed(enableEditing);
		}
	}

	public void setDescriptor(Descriptor descriptor) {
		if (recordTableMouseListener != null) {
			removeMouseListener(recordTableMouseListener);
			recordTableMouseListener = null;
		}
		setModel(new RecordTableModel(contentEditable, descriptor, topTable));
		if (descriptor.isStackable()) {
			recordTableMouseListener = new RecordTableMouseListener();
			addMouseListener(recordTableMouseListener);
		}
		updateFromModel();
	}

	public void setRecordData(RecordData recordData) {
		((RecordTableModel) getModel()).setRecordData(recordData);
	}

	protected void configureEnclosingScrollPane() {
		Container p = getParent();
		if (p instanceof JViewport) {
			Container gp = p.getParent();
			if (gp instanceof JScrollPane) {
				JScrollPane scrollPane = (JScrollPane) gp;
				// Make certain we are the viewPort's view and not, for
				// example, the rowHeaderView of the scrollPane -
				// an implementor of fixed columns might do this.
				JViewport viewport = scrollPane.getViewport();
				if (viewport == null || viewport.getView() != this) {
					return;
				}
				if (topTable) {
					scrollPane.setColumnHeaderView(getTableHeader());
				}
				//  scrollPane.getViewport().setBackingStoreEnabled(true);
				Border border = scrollPane.getBorder();
				if (border == null || border instanceof UIResource) {
					scrollPane.setBorder(UIManager.getBorder("Table.scrollPaneBorder"));
				}
			}
		}
	}

	//********************** Inner Classes

	/**
	 * Sets the Default CellEditor for each table to be one that listens to
	 * focus events to stop editing when it looses focus.
	 */
	public static class FocusListeningCellEditor extends DefaultCellEditor implements FocusListener {
		private static final long serialVersionUID = 1L;
		/**
		 * Constructor for FocusListeningCellEditor.
		 * @param textField
		 */
		public FocusListeningCellEditor(JTextField textField) {
			super(textField);
			textField.addFocusListener(this);
		}

		/**
		 * Constructor for FocusListeningCellEditor.
		 * @param checkBox
		 */
		public FocusListeningCellEditor(JCheckBox checkBox) {
			super(checkBox);
			checkBox.addFocusListener(this);
		}

		/**
		 * Constructor for FocusListeningCellEditor.
		 * @param comboBox
		 */
		public FocusListeningCellEditor(JComboBox comboBox) {
			super(comboBox);
			comboBox.addFocusListener(this);
		}

		//***************************** FocusListener Methods

		/**
		 * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
		 */
		public void focusGained(FocusEvent e) {
		}
		/**
		 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
		 */
		public void focusLost(FocusEvent e) {
			stopCellEditing();
		}
	}
}
