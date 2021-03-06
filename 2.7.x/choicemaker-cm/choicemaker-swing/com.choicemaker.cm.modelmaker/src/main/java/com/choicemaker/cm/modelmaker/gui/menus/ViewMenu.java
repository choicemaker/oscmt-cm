/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.modelmaker.gui.menus;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.choicemaker.cm.core.util.ChoiceMakerCoreMessages;
import com.choicemaker.cm.modelmaker.gui.ModelMaker;

/**
 * 
 * @author S. Yoakum-Stover
 */
public class ViewMenu extends JMenu {
	private static final long serialVersionUID = 1L;
	private ModelMaker parent;
	private static final String VIEW_MENU = ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.menu.view");

	public ViewMenu(ModelMaker g) {
		super(VIEW_MENU);
		parent = g;
		this.setMnemonic(KeyEvent.VK_V);
		buildMenu();
	}

	public void buildMenu() {
		// Show toolbar
		ImageIcon showToolbarIcon = null;
		//new ImageIcon(MaximumEntropyShowTotaler.class.getResource("images/showToolbar.gif"));
		Action showToolbarAction =
			new AbstractAction(
				ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.menu.view.toolbar"),
				showToolbarIcon) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
				parent.showToolbar(item.isSelected());
			}
		};
		JMenuItem showToolbarItem = add(new JCheckBoxMenuItem(showToolbarAction));
		showToolbarItem.setSelected(true);
		showToolbarItem.setIcon(null);

		// Pair indices
		ImageIcon showPairIndicesIcon = null;
		//new ImageIcon(MaximumEntropyShowTotaler.class.getResource("images/showPairIndices.gif"));
		Action showPairIndicesAction =
			new AbstractAction(
				ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.menu.view.pairindices"),
				showPairIndicesIcon) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
				parent.showPairIndices(item.isSelected());
			}
		};
		JMenuItem showPairIndicesItem = add(new JCheckBoxMenuItem(showPairIndicesAction));
		showPairIndicesItem.setSelected(true);
		showPairIndicesItem.setIcon(null);
		
		Action showStatusMessagesAction = new AbstractAction("Status Messages") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
				parent.showStatusMessages(item.isSelected());
			}
		};
		JMenuItem showStatusMessagesItem = add(new JCheckBoxMenuItem(showStatusMessagesAction));
		showStatusMessagesItem.setSelected(true);

		addSeparator();

		// ShowTotal
		ImageIcon showSummaryIcon = null;
		//new ImageIcon(MaximumEntropyShowTotaler.class.getResource("images/showSummary.gif"));
		Action showSummaryAction =
			new AbstractAction(
				ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.menu.view.cluesummary"),
				showSummaryIcon) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
				parent.getTrainingControlPanel().showCluePerformancePanel(item.isSelected());
			}
		};
		JMenuItem showSummaryItem = add(new JCheckBoxMenuItem(showSummaryAction));
		showSummaryItem.setIcon(null);
		
		addSeparator();
		
		// Active Clue Table
		Action showActiveCluesAction = new AbstractAction("Active Clue Table") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
				parent.getHumanReviewPanel().showActiveCluesPanel(item.isSelected());
			}
		};
		JMenuItem showActiveCluesItem = add(new JCheckBoxMenuItem(showActiveCluesAction));
		showActiveCluesItem.setSelected(true);
	}
}
