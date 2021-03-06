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
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import com.choicemaker.cm.core.Constants;
import com.choicemaker.cm.core.IProbabilityModel;
import com.choicemaker.cm.core.OperationFailedException;
import com.choicemaker.cm.core.base.DoNothingMachineLearning;
import com.choicemaker.cm.core.base.MutableProbabilityModel;
import com.choicemaker.cm.core.util.ChoiceMakerCoreMessages;
import com.choicemaker.cm.core.util.LoggingObject;
import com.choicemaker.cm.gui.utils.JavaHelpUtils;
import com.choicemaker.cm.gui.utils.dialogs.FileChooserFactory;
import com.choicemaker.cm.modelmaker.gui.ModelMaker;
import com.choicemaker.cm.modelmaker.gui.utils.Enable;
import com.choicemaker.cm.modelmaker.gui.utils.EnablednessGuard;
import com.choicemaker.util.FileUtilities;
/**
 * Description
 * 
 * @author S. Yoakum-Stover
 */
public class ModelBuilderDialog extends JDialog implements Enable {
	private static final long serialVersionUID = 1L;
	private static final String ABSOLUTE = 
		ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.dialog.model.builder.cluefile.absolute");
	private static final String RELATIVE = 
		ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.dialog.model.builder.cluefile.relative");
	
	private static Logger logger = Logger.getLogger(ModelBuilderDialog.class.getName());
	private ModelMaker parent;
	private JPanel content;
	private JLabel modelFileName;
	private JTextField modelFileNameField;
	private JButton modelFileNameBrowseButton;
	private JLabel cluesFileName;
	private JTextField cluesFileNameField;
	private JButton cluesFileNameBrowseButton;
	private JLabel cluesRelativeLabel;
	private JComboBox cluesRelativeBox;
	private JButton buildButton;
	private JButton cancelButton;
	private boolean isNewModel;
	private String oldName;

	public ModelBuilderDialog(ModelMaker g) {
		super(g, ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.dialog.model.builder.label"), true);
		parent = g;
		isNewModel = true;
		buildContent();
		layoutContent();
		addContentListeners();
		setContentPane(content);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		pack();
		setLocationRelativeTo(parent);
	}

	public void modifyModel(IProbabilityModel pm) {
		isNewModel = false;
		oldName = pm.getModelFilePath();
		modelFileNameField.setText(oldName);
		cluesFileNameField.setText(pm.getClueFilePath());
		if (FileUtilities.isFileAbsolute(pm.getClueFilePath())) {
			cluesRelativeBox.setSelectedItem(ABSOLUTE);	
		} else {
			cluesRelativeBox.setSelectedItem(RELATIVE);	
		}
		setEnabledness();
	}

	public void newModel() {
		isNewModel = true;
		setEnabledness();
	}

	@Override
	public void setEnabledness() {
		buildButton.setEnabled(modelFileNameField.getText().length() > 0 && cluesFileNameField.getText().length() > 0);
	}

	private boolean buildModel() {
		String modelFileName = modelFileNameField.getText().trim();
		if (!modelFileName.endsWith("." + Constants.MODEL_EXTENSION)) {
			modelFileName += "." + Constants.MODEL_EXTENSION;
		}
		if ((isNewModel || !modelFileName.equals(oldName)) && new File(modelFileName).exists()) {
			if (JOptionPane
				.showConfirmDialog(
					this,
					ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.dialog.model.builder.replace", modelFileName),
					ChoiceMakerCoreMessages.m.formatMessage("confirm"),
					JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE)
				== JOptionPane.YES_OPTION) {
			} else {
				return false;
			}
		}
		
		String cluesFileName = cluesFileNameField.getText().trim();
		if (!cluesFileName.endsWith("." + Constants.CLUES_EXTENSION)) {
			cluesFileName += "." + Constants.CLUES_EXTENSION;	
		}

		// get an absolute file
		File cf = new File(cluesFileName);
		if (!cf.isAbsolute()) {
			File rel = new File(modelFileName).getAbsoluteFile().getParentFile();
			cluesFileName = FileUtilities.getAbsoluteFile(rel, cluesFileName).toString();
		}
		
		// adjust for the user's save preference.
		if (cluesRelativeBox.getSelectedItem().equals(RELATIVE)) {
			File rel = new File(modelFileName).getAbsoluteFile().getParentFile();
			cluesFileName = FileUtilities.getRelativeFile(rel, cluesFileName).toString();	
		}
		
		IProbabilityModel pm = new MutableProbabilityModel(modelFileName, cluesFileName);
		pm.setMachineLearner(new DoNothingMachineLearning());
		boolean success = parent.buildProbabilityModel(pm);
		if (success) {
			try {
				parent.saveProbabilityModel(pm);
				parent.setProbabilityModel(pm);
				return true;
			} catch (OperationFailedException ex) {
				logger.severe(new LoggingObject("CM-100501", pm.getModelName()).getFormattedMessage() + ": " + ex);
				return false;
			}
		} else {
			return false;
		}
	}

	private void addContentListeners() {
		//buildButton
		buildButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (buildModel()) {
					dispose();
				}
			}
		});

		//modelsFileNameBrowseButton
		modelFileNameBrowseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				File file = FileChooserFactory.selectModelFile(parent);
				if (file != null) {
					modelFileNameField.setText(file.getAbsolutePath());
				}
			}
		});

		//cluesFileNameBrowseButton
		cluesFileNameBrowseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				File file = FileChooserFactory.selectCluesFile(parent);
				if (file != null) {
					cluesFileNameField.setText(file.getAbsolutePath());
				}
			}
		});

		//cancelButton
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		EnablednessGuard dl = new EnablednessGuard(this);
		modelFileNameField.getDocument().addDocumentListener(dl);
		cluesFileNameField.getDocument().addDocumentListener(dl);

		JavaHelpUtils.enableHelpKey(this, "train.gui.dialog.modelbuilder");
	}

	private void buildContent() {
		content = new JPanel();
		modelFileName = new JLabel(ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.dialog.model.builder.name"));
		modelFileNameField = new JTextField(35);
		modelFileNameBrowseButton = new JButton(ChoiceMakerCoreMessages.m.formatMessage("browse.elipsis"));
		cluesFileName = new JLabel(ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.dialog.model.builder.cluefile"));
		cluesFileNameField = new JTextField(35);
		cluesFileNameBrowseButton = new JButton(ChoiceMakerCoreMessages.m.formatMessage("browse.elipsis"));
		cluesRelativeLabel = new JLabel(ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.dialog.model.builder.save.cluefile.as"));
		cluesRelativeBox = new JComboBox();
		cluesRelativeBox.addItem(RELATIVE);
		cluesRelativeBox.addItem(ABSOLUTE);
		buildButton = new JButton(ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.dialog.model.builder.build"));
		cancelButton = new JButton(ChoiceMakerCoreMessages.m.formatMessage("cancel"));
	}

	private void layoutContent() {
		GridBagLayout layout = new GridBagLayout();
		layout.columnWeights = new double[] {0, 1, 0, 0};
		content.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(2, 2, 5, 10);
		c.fill = GridBagConstraints.NONE;

		//Row 0 ........................................
		//modelName
		c.gridy = 0;
		c.gridx = 0;
		c.anchor = GridBagConstraints.EAST;
		content.add(modelFileName, c);
		//modelNameField
		c.gridx = 1;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		content.add(modelFileNameField, c);
		c.gridwidth = 1;

		//modelFileNameBrowseButton
		c.gridx = 3;
		c.weightx = 0;
		layout.setConstraints(modelFileNameBrowseButton, c);
		content.add(modelFileNameBrowseButton);

		//Row 1 ........................................
		//cluesFileName
		c.gridy = 1;
		c.gridx = 0;
		c.fill = GridBagConstraints.NONE;
		content.add(cluesFileName, c);
		//cluesFileNameField
		c.gridx = 1;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(cluesFileNameField, c);
		content.add(cluesFileNameField);
		c.gridwidth = 1;

		//cluesFileNameBrowseButton
		c.gridx = 3;
		layout.setConstraints(cluesFileNameBrowseButton, c);
		content.add(cluesFileNameBrowseButton);

		//Row 2 ........................................
		c.gridy = 2;
		c.gridx = 1;
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
		content.add(cluesRelativeLabel, c);
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;
		
		c.gridx = 2;
		c.fill = GridBagConstraints.NONE;
		content.add(cluesRelativeBox, c);

//		//Row 3 ........................................
//		c.gridy = 3;
//		c.gridx = 0;
//		c.fill = GridBagConstraints.NONE;
//		c.anchor = GridBagConstraints.EAST;
//		content.add(useAnt, c);
//
//		c.gridx = 1;
//		c.anchor = GridBagConstraints.WEST;
//		content.add(useAntCheckBox, c);
//
//		//Row 4 ........................................
//		c.gridy = 4;
//		c.gridx = 0;
//		c.fill = GridBagConstraints.NONE;
//		content.add(antCommand, c);
//
//		c.gridx = 1;
//		c.gridwidth = 2;
//		c.fill = GridBagConstraints.HORIZONTAL;
//		content.add(antCommandField, c);
//		c.gridwidth = 1;

		//Row 3 ........................................
		c.gridy = 3;
		c.gridx = 2;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		layout.setConstraints(buildButton, c);
		content.add(buildButton);

		c.gridx = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(cancelButton, c);
		content.add(cancelButton);
	}
}
