/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.mmdevtools;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;

import com.choicemaker.cm.mmdevtools.gui.MrpsFlattenDialog;
import com.choicemaker.cm.modelmaker.gui.ModelMaker;
import com.choicemaker.cm.modelmaker.gui.menus.ToolsMenu.ToolAction;

/**
 * @author Adam Winkel
 */
public class MrpsUtilsAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	public MrpsUtilsAction() {
		super("Marked Record Pair Source Utils");
	}

	@Override
	public void actionPerformed(ActionEvent e) { }

	public static class MrpsFlattenAction extends ToolAction {
		private static final long serialVersionUID = 1L;
		public MrpsFlattenAction() {
			super("Flatten...");
			setEnabled(false);
		}
		@Override
		public void setModelMaker(final ModelMaker m) {
			m.addPropertyChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent e) {
					setEnabled(m.haveSourceList());
				}
			});
			super.setModelMaker(m);
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			MrpsFlattenDialog.showMrpsFlattenDialog(modelMaker);
		}
	}
	
}
