/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.mmdevtools;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.choicemaker.cm.mmdevtools.gui.DerivedFieldsComputerDialog;
import com.choicemaker.cm.mmdevtools.gui.OpenRsAsMrpsDialog;
import com.choicemaker.cm.mmdevtools.gui.SourceSplitDialog;
import com.choicemaker.cm.modelmaker.gui.menus.ToolsMenu.ToolAction;

/**
 * @author ajwinkel
 *
 */
public class RsUtilsAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	public RsUtilsAction() {
		super("Record Source Utils");
	}

	@Override
	public void actionPerformed(ActionEvent e) { }

	public static class RsSplitAction extends ToolAction {
		private static final long serialVersionUID = 1L;
		public RsSplitAction() {
			super("Split...");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			SourceSplitDialog.showRsSplitDialog(modelMaker);
		}
	}
	
	public static class RsDerivedFieldComputerAction extends ToolAction {
		private static final long serialVersionUID = 1L;
		public RsDerivedFieldComputerAction() {
			super("Derived Fields Computer...");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				DerivedFieldsComputerDialog.showDerivedFieldsComputerDialog(modelMaker, DerivedFieldsComputerDialog.RS);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}	
	}
	
	public static class OpenRsAsMrpsAction extends ToolAction {
		private static final long serialVersionUID = 1L;
		public OpenRsAsMrpsAction() {
			super("Open RS as MRPS...");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				new OpenRsAsMrpsDialog(modelMaker).setVisible(true);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

}
