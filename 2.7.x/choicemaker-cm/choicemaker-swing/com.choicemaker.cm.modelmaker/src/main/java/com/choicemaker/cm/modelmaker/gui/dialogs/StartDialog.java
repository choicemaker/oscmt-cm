/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.modelmaker.gui.dialogs;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.choicemaker.cm.core.util.ChoiceMakerCoreMessages;
import com.choicemaker.cm.gui.utils.JavaHelpUtils;
import com.choicemaker.cm.gui.utils.dialogs.FileChooserFactory;
import com.choicemaker.cm.modelmaker.gui.ModelMaker;
import com.choicemaker.cm.modelmaker.gui.utils.Enable;
import com.choicemaker.cm.modelmaker.gui.utils.EnablednessGuard;

/**
 *
 * @author Martin Buechi
 */
public class StartDialog extends JFrame implements Enable {
	private static final long serialVersionUID = 1L;

	private JPanel content;
	private JLabel configurationLabel;
	private JTextField configuration;
	private JButton configurationBrowse;
	private JLabel passwordLabel;
	private JPasswordField password;
	private JButton ok;
	private JButton cancel;
	private boolean res;

	public StartDialog(String confText) {
		super();
		setTitle(ChoiceMakerCoreMessages.m
				.formatMessage("train.gui.modelmaker.dialog.launchpad.label"));
		setIconImage(new ImageIcon(
				ModelMaker.class.getResource("images/cmIcon.gif")).getImage());
		content = new JPanel();
		configurationLabel = new JLabel(
				ChoiceMakerCoreMessages.m
						.formatMessage("train.gui.modelmaker.dialog.launchpad.configurationfile"));
		configuration = new JTextField(confText);
		configurationBrowse = new JButton(
				ChoiceMakerCoreMessages.m.formatMessage("browse.elipsis"));
		passwordLabel = new JLabel(
				ChoiceMakerCoreMessages.m
						.formatMessage("train.gui.modelmaker.dialog.launchpad.password"));
		password = new JPasswordField();
		ok = new JButton(ChoiceMakerCoreMessages.m.formatMessage("ok"));
		cancel = new JButton(ChoiceMakerCoreMessages.m.formatMessage("cancel"));
		layoutContent();
		addContentListeners();
		setContentPane(content);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		getRootPane().setDefaultButton(ok);
		pack();
		setLocation();
		setEnabledness();
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				res = false;
				end();
			}
		});
	}

	public void setEnabledness() {
		ok.setEnabled(ModelMaker.checkValidity(configuration.getText()));
	}

	public void layoutContent() {
		GridBagLayout layout = new GridBagLayout();
		content.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(2, 2, 5, 10);

		// 2009-07-06 rphall
		// Removed license validation for open-source release
		// //Row 0 ........................................
		// c.gridy = 0;
		// c.gridx = 0;
		// c.anchor = GridBagConstraints.WEST;
		// content.add(new
		// JLabel(ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.dialog.launchpad.licensed.to")),
		// c);
		// c.gridx = 1;
		// c.gridwidth = 2;
		// content.add(
		// new JLabel(
		// ChoiceMakerCoreMessages.m.formatMessage(
		// "train.gui.modelmaker.dialog.launchpad.licensee",
		// LicenseManager.getString("name"),
		// LicenseManager.getString("company"))),
		// c);
		//
		// c.gridy = 1;
		// c.gridx = 0;
		// c.gridwidth = 1;
		// c.anchor = GridBagConstraints.WEST;
		// content.add(new
		// JLabel(ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.dialog.launchpad.expires")),
		// c);
		// c.gridx = 1;
		// c.gridwidth = 2;
		// content.add(
		// new JLabel(
		// ChoiceMakerCoreMessages.m.formatMessage(
		// "train.gui.modelmaker.dialog.launchpad.expiration",
		// java.sql.Date.valueOf(LicenseManager.getString("expiration")))),
		// c);
		// c.gridwidth = 1;

		// Row 0 ........................................
		c.gridy = 2;
		c.gridx = 0;
		c.anchor = GridBagConstraints.WEST;
		content.add(configurationLabel, c);
		c.gridx = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		content.add(configuration, c);
		c.gridx = 2;
		c.weightx = 0;
		content.add(configurationBrowse, c);

		// Row 1 ........................................
		c.gridy = 3;
		c.gridx = 0;
		c.anchor = GridBagConstraints.WEST;
		content.add(passwordLabel, c);
		c.gridx = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		content.add(password, c);

		// Row 2 ........................................
		c.gridy = 4;
		c.gridx = 1;
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
		content.add(ok, c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		content.add(cancel, c);
	}

	public boolean isRes() {
		return res;
	}

	public String getConfigurationFileName() {
		return configuration.getText();
	}

	public char[] getPassword() {
		return password.getPassword();
	}

	public void clearPassword() {
		password.setText(null);
	}

	private void end() {
		synchronized (ModelMaker.class) {
			ModelMaker.class.notifyAll();
		}
		dispose();
	}

	private void addContentListeners() {
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				res = true;
				end();
			}
		});

		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				res = false;
				end();
			}
		});

		configurationBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				File file = FileChooserFactory.selectConfFile(null, new File(
						configuration.getText()));
				if (file != null) {
					configuration.setText(file.getAbsolutePath());
				} else {
					return;
				}
			}
		});

		EnablednessGuard dl = new EnablednessGuard(this);
		configuration.getDocument().addDocumentListener(dl);

		JavaHelpUtils.enableHelpKey(this, "train.gui.dialog.start");

	}

	private void setLocation() {
		setSize(700, 170);
		Dimension d1 = getSize();
		Dimension d2 = getToolkit().getScreenSize();
		int x = Math.max((d2.width - d1.width) / 2, 0);
		int y = Math.max((d2.height - d1.height) / 2, 0);
		super.setBounds(x, y, d1.width, d1.height);
	}
}
