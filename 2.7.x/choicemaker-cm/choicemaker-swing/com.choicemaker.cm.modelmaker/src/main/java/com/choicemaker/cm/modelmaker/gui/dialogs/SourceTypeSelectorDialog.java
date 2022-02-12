/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.modelmaker.gui.dialogs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.choicemaker.cm.core.ChoiceMakerExtensionPoint;
import com.choicemaker.cm.core.Source;
import com.choicemaker.cm.core.util.ChoiceMakerCoreMessages;
import com.choicemaker.cm.gui.utils.ExtensionHolder;
import com.choicemaker.cm.gui.utils.JavaHelpUtils;
import com.choicemaker.cm.modelmaker.gui.ModelMaker;
import com.choicemaker.cm.modelmaker.gui.sources.SourceGuiFactory;
import com.choicemaker.e2.CMExtension;
import com.choicemaker.e2.CMExtensionPoint;
import com.choicemaker.e2.platform.CMPlatformUtils;

/**
 * Description
 *
 * @author S. Yoakum-Stover
 */
public class SourceTypeSelectorDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(SourceTypeSelectorDialog.class.getName());

	public static int MRPS = -1;
	public static int RS = -2;

	protected ModelMaker parent;
	private JPanel content;
	private JList sourceTypeList;
	private JScrollPane sourceTypeScrollPane;
	private JLabel sourceTypeLabel;
	private JButton okayButton;
	private JButton cancelButton;
	private boolean save;
	private Source source;

	private int type;

	public SourceTypeSelectorDialog(ModelMaker g, boolean save) {
		this(g, MRPS, save);
	}

	public SourceTypeSelectorDialog(ModelMaker g, int type, boolean save) {
		super(g, ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.dialog.source.typeselector.label"), true);
		parent = g;
		this.type = type;
		this.save = save;
		buildContent();
		addContentListeners();
		layoutContent();
		this.setContentPane(content);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		pack();
		setLocationRelativeTo(parent);
		setEnabledness();
	}

	public Source define() {
		setVisible(true);
		setEnabledness();
		return source;
	}

	private void setEnabledness() {
		okayButton.setEnabled(!sourceTypeList.isSelectionEmpty());
	}

	private void buildContent() {
		content = new JPanel();
		sourceTypeLabel =
			new JLabel(ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.dialog.source.typeselector.available"));
		CMExtensionPoint extensionPoint = null;
		if (type == RS) {
			extensionPoint = CMPlatformUtils.getExtensionPoint(ChoiceMakerExtensionPoint.CM_MODELMAKER_RSREADERGUI);
		} else {
			extensionPoint = CMPlatformUtils.getExtensionPoint(ChoiceMakerExtensionPoint.CM_MODELMAKER_MRPSREADERGUI);
		}
		if (save) {
			Vector l = new Vector();
			CMExtension[] extensions = extensionPoint.getExtensions();
			for (int i = 0; i < extensions.length; i++) {
				CMExtension ext = extensions[i];
				if(Boolean.valueOf(ext.getConfigurationElements()[0].getAttribute("hasSink")).booleanValue()) {
					l.add(new ExtensionHolder(ext));
				}
			}
			sourceTypeList = new JList(l);
		} else {
			sourceTypeList =
				new JList(
					ExtensionHolder.getExtensionsOfExtensionPoint(extensionPoint
						));
		}
		sourceTypeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sourceTypeList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				setEnabledness();
			}
		});
		sourceTypeScrollPane = new JScrollPane();
		sourceTypeScrollPane.getViewport().add(sourceTypeList);

		okayButton = new JButton(ChoiceMakerCoreMessages.m.formatMessage("new.elipsis"));
		cancelButton = new JButton(ChoiceMakerCoreMessages.m.formatMessage("cancel"));
	}

	private void showSource() {
		SourceGuiFactory sourceGuiFactory = null;
		try {
			sourceGuiFactory = (SourceGuiFactory) ((ExtensionHolder) sourceTypeList.getSelectedValue()).getInstance();
			dispose();
			SourceGui sourceGui = save ? sourceGuiFactory.createSaveGui(parent) : sourceGuiFactory.createGui(parent);
			source = sourceGui.define();
		} catch (Exception e) {
			logger.severe(e.toString());;
		}
	}

	private void addContentListeners() {
		sourceTypeList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					showSource();
				}
			}
		});

		//cancelButton
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				dispose();
			}
		});
		//okayButton
		okayButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showSource();
			}
		});

		JavaHelpUtils.enableHelpKey(this, "train.gui.dialog.sourcetypeselector");
	}

	private void layoutContent() {
		GridBagLayout layout = new GridBagLayout();
		content.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(2, 2, 5, 5);

		//Row 1......................................
		//sourceTypeLabel
		c.gridy = 0;
		c.gridx = 0;
		c.anchor = GridBagConstraints.WEST;
		content.add(sourceTypeLabel, c);

		//Row 2 ........................................
		//sourceTypeScrollPane
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		layout.setConstraints(sourceTypeScrollPane, c);
		content.add(sourceTypeScrollPane);

		JPanel buttons = new JPanel(new GridLayout(1, 2, 10, 10));
		buttons.add(okayButton);
		buttons.add(cancelButton);
		c.gridy = 2;
		c.weightx = 0;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		content.add(buttons, c);
	}
}
