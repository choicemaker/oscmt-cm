/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.mmdevtools;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.AbstractAction;

import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.base.MutableMarkedRecordPair;
import com.choicemaker.cm.mmdevtools.gui.MciNameParserExportDialog;
import com.choicemaker.cm.modelmaker.gui.ModelMaker;
import com.choicemaker.cm.modelmaker.gui.menus.ToolsMenu.ToolAction;
import com.choicemaker.util.Precondition;

/**
 * @author Adam Winkel
 */
public class MciNameParserExportDialogAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger
			.getLogger(MciNameParserExportDialogAction.class.getName());

	public static final String MCI_RECORDS_BASE =
//		"com.choicemaker.cm.custom.mci.gend.MciRecords.PatientBase";
	 "com.choicemaker.cm.custom.mci.gend.internal.MciRecords.PatientImpl";

	public static Class getMciRecordsClass(ClassLoader cl) {
		Class retVal = null;
		logger.fine("MCI record class name: '" + MCI_RECORDS_BASE + "'");
		try {
			final boolean initialize = true;
			retVal = Class.forName(MCI_RECORDS_BASE, initialize, cl);
		} catch (Throwable t) {
			String msg =
				"Unable to resolve class '" + MCI_RECORDS_BASE + "': "
						+ t.toString();
			logger.info(msg);
		}
		return retVal;
	}

	public static boolean isMciRecords(Record r) {
		boolean retVal = false;
		if (r != null) {
			ClassLoader cl = r.getClass().getClassLoader();
			Class c = getMciRecordsClass(cl);
			logger.fine("Record class: " + r.getClass().getName());
			if (c != null) {
				retVal = c.isInstance(r);
			}
		} else {
			logger.fine("Null record");
		}
		return retVal;
	}

	public static boolean isMciRecords(final ModelMaker m) {
		Precondition.assertNonNullArgument("null ModelMaker", m);
		boolean retVal = false;
		if (m.haveSourceList()) {
			List sourceList = m.getSourceList();
			for (int i = 0; i < sourceList.size();) {
				MutableMarkedRecordPair mrp =
					(MutableMarkedRecordPair) sourceList.get(i);
				Record r = mrp.getQueryRecord();
				retVal = isMciRecords(r);
				break;
			}
		}
		return retVal;
	}

	public MciNameParserExportDialogAction() {
		super("MCI NameParser Export");
	}

	public void actionPerformed(ActionEvent e) {
	}

	public static class MciNameParserExportAction extends ToolAction {
		private static final long serialVersionUID = 271L;

		public MciNameParserExportAction() {
			super("Export...");
//			setEnabled(false);
			setEnabled(true);
		}

		public void setModelMaker(final ModelMaker m) {
			m.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent e) {
					boolean enabled =
						m.haveSourceList()
								&& MciNameParserExportDialogAction
										.isMciRecords(m);
					setEnabled(enabled);
				}
			});
			super.setModelMaker(m);
		}

		public void actionPerformed(ActionEvent e) {
			MciNameParserExportDialog.showExportDialog(modelMaker);
		}
	}

}
