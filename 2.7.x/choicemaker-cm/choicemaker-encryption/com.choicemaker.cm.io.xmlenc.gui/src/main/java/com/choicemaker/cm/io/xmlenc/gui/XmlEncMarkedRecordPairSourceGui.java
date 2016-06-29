/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.xmlenc.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.choicemaker.cm.core.Constants;
import com.choicemaker.cm.core.MarkedRecordPairSource;
import com.choicemaker.cm.core.Source;
import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.core.base.MarkedRecordPairBinder;
import com.choicemaker.cm.core.util.LoggingObject;
import com.choicemaker.cm.core.xmlconf.MarkedRecordPairSourceXmlConf;
import com.choicemaker.cm.gui.utils.JavaHelpUtils;
import com.choicemaker.cm.gui.utils.dialogs.FileChooserFactory;
import com.choicemaker.cm.io.xmlenc.base.XmlEncMarkedRecordPairSink;
import com.choicemaker.cm.io.xmlenc.base.XmlEncMarkedRecordPairSinkFactory;
import com.choicemaker.cm.io.xmlenc.base.XmlEncMarkedRecordPairSource;
import com.choicemaker.cm.io.xmlenc.base.xmlconf.InMemoryXmlEncManager;
import com.choicemaker.cm.io.xmlenc.base.xmlconf.XmlEncryptionManager;
import com.choicemaker.cm.io.xmlenc.res.XmlEncMessageUtil;
import com.choicemaker.cm.modelmaker.gui.ModelMaker;
import com.choicemaker.cm.modelmaker.gui.dialogs.MarkedRecordPairSourceGui;
import com.choicemaker.cm.modelmaker.gui.utils.Enable;
import com.choicemaker.cm.modelmaker.gui.utils.EnablednessGuard;
import com.choicemaker.util.FileUtilities;
import com.choicemaker.util.MessageUtil;
import com.choicemaker.xmlencryption.CredentialSet;
import com.choicemaker.xmlencryption.EncryptionScheme;

/**
 * The MRPSGui associated the XmlEncMarkedRecordPairSource. An objects of this
 * class would be created by the XmlEncMarkedRecordPairSourceGuiFactory. It is
 * used by the AbstractApplication so that users can easily configure and build
 * XmlEncMarkedRecordPairSources.
 *
 * @author rphall
 * @see com.choicemaker.cm.io.xml.gui.XmlMarkedRecordPairSourceGui
 */
public class XmlEncMarkedRecordPairSourceGui extends MarkedRecordPairSourceGui
		implements Enable {

	private static final long serialVersionUID = 271L;
	private static Logger logger = Logger
			.getLogger(XmlEncMarkedRecordPairSourceGui.class.getName());

	private static final MessageUtil m = XmlEncMessageUtil.m;
	private static String FRAME_TITLE = m.formatMessage("io.xmlenc.gui.label");
	private static String GENERATE_MODE_LABEL = "Generate new source file";
	private static String CREATE_MODE_LABEL = "Use existing source file";
	private static String RELATIVE = m
			.formatMessage("io.common.gui.source.file.relative");
	private static String ABSOLUTE = m
			.formatMessage("io.common.gui.source.file.absolute");
	private static Dimension CREATE_DIMENSION = new Dimension(500, 140);
	private static Dimension GENERATE_DIMENSION = new Dimension(500, 400);

	private final XmlEncryptionManager crdsMgr = InMemoryXmlEncManager
			.getInstance();

	private JComponent[] generateComponents;
	private JButton modeButton;
	private JLabel sourceFileNameLabel;
	private JLabel xmlFileNameLabel;
	private JTextField xmlFileName;
	private JButton browseButton;
	private JLabel encSchemesLabel;
	private JComboBox<String> encSchemesBox;
	private JLabel encCredentialsLabel;
	private JComboBox<String> encCredentialsBox;
	private JLabel xmlFileRelativeLabel;
	private JComboBox<String> xmlFileRelativeBox;
	private JLabel sourcesListLabel;
	private JList<String> sourcesList;
	private JScrollPane sourcesListScrollPane;
	private JButton addButton;
	private JButton removeButton;
	private JLabel distributeOverLabel;
	private JTextField distributeOver;
	private JLabel maxPairsPerFileLabel;
	private JTextField maxPairsPerFile;

	private String fileN;
	private String extension;
	private boolean save;

	public XmlEncMarkedRecordPairSourceGui(ModelMaker parent,
			MarkedRecordPairSource s, boolean save) {
		super(parent, FRAME_TITLE);
		this.save = save;
		init(s);
	}

	public void setVisible(boolean b) {
		if (b) {
			setFields();
			setEnabledness();
			super.setVisible(true);
		}
	}

	public void setFields() {
		if (!save) {
			distributeOver.setText("1");
			maxPairsPerFile.setText("0");
		}
		if (getSource() != null) {
			XmlEncMarkedRecordPairSource s = (XmlEncMarkedRecordPairSource) getSource();
			sourceFileName.setText(s.getFileName());
			xmlFileName.setText(s.getXmlFileName());
			if (s.getRawXmlFileName() != null
					&& FileUtilities.isFileAbsolute(s.getRawXmlFileName())) {
				xmlFileRelativeBox.setSelectedItem(ABSOLUTE);
			} else {
				xmlFileRelativeBox.setSelectedItem(RELATIVE);
			}
		}
	}

	private void computeFileNameAndExtension() {
		String fn = getSaveXmlFileName();
		int pos = fn.lastIndexOf('.');
		if (pos >= 0) {
			fileN = fn.substring(0, pos);
			extension = fn.substring(pos, fn.length());
		} else {
			fileN = fn;
			extension = "";
		}
	}

	public void setEnabledness() {
		boolean ok = xmlFileName.getText().length() > 0
				&& sourceFileName.getText().length() > 0;
		if (mode == CREATE) {
			okayButton.setEnabled(ok);
		} else {
			boolean generate = ok && sourcesList.getModel().getSize() > 0
					&& parent.haveProbabilityModel();
			try {
				int d = Integer.parseInt(distributeOver.getText());
				int s = Integer.parseInt(maxPairsPerFile.getText());
				generate &= d > 0 && s >= 0;
			} catch (NumberFormatException ex) {
				generate = false;
			}
			okayButton.setEnabled(generate);
		}
		if (!save) {
			removeButton.setEnabled(!sourcesList.isSelectionEmpty());
		}
	}

	public void buildSource() {
		XmlEncMarkedRecordPairSource xmlSource = (XmlEncMarkedRecordPairSource) getSource();
		xmlSource.setFileName(getSourceFileName());
		xmlSource.setRawXmlFileName(getSaveXmlFileName());
	}

	private String getSaveXmlFileName() {
		if (xmlFileRelativeBox.getSelectedItem().equals(ABSOLUTE)) {
			return getAbsoluteXmlFileName();
		} else {
			File rel = new File(sourceFileName.getText().trim())
					.getAbsoluteFile().getParentFile();
			return FileUtilities.getRelativeFile(rel, getAbsoluteXmlFileName())
					.toString();
		}
	}

	private String getAbsoluteXmlFileName() {
		File rel = new File(sourceFileName.getText().trim()).getAbsoluteFile()
				.getParentFile();
		return FileUtilities.getAbsoluteFile(rel, xmlFileName.getText().trim())
				.toString();
	}

	/**
	 * Executed by the superclass constructor to build the panel.
	 */
	public void buildContent() {
		sourceFileNameLabel = new JLabel(
				m.formatMessage("train.gui.modelmaker.dialog.source.name"));
		sourceFileName = new JTextField(35);
		sourceFileBrowseButton = new JButton(m.formatMessage("browse.elipsis"));

		xmlFileNameLabel = new JLabel(
				m.formatMessage("io.xmlenc.guilsource.file"));
		xmlFileName = new JTextField(10);
		browseButton = new JButton(m.formatMessage("browse.elipsis"));

		encSchemesLabel = new JLabel(
				m.formatMessage("io.xmlenc.gui.encryption.scheme"));
		encSchemesBox = new JComboBox<>();
		List<EncryptionScheme> schemes = crdsMgr.getEncryptionSchemes();
		for (EncryptionScheme scheme : schemes) {
			String id = scheme.getSchemeId();
			encSchemesBox.addItem(id);
		}

		encCredentialsLabel = new JLabel(
				m.formatMessage("io.xmlenc.gui.encryption.credentials"));
		encCredentialsBox = new JComboBox<>();
		List<CredentialSet> credentials = crdsMgr.getEncryptionCredentials();
		for (CredentialSet scheme : credentials) {
			String id = scheme.getCredentialName();
			encCredentialsBox.addItem(id);
		}

		xmlFileRelativeLabel = new JLabel(
				m.formatMessage("io.common.gui.save.source.file.as"));
		xmlFileRelativeBox = new JComboBox<>();
		xmlFileRelativeBox.addItem(RELATIVE);
		xmlFileRelativeBox.addItem(ABSOLUTE);

		okayButton = new JButton(m.formatMessage("ok"));
		cancelButton = new JButton(m.formatMessage("cancel"));

		if (!save) {
			modeButton = new JButton(GENERATE_MODE_LABEL);
			generateComponents = new JComponent[8];
			sourcesListLabel = new JLabel(
					m.formatMessage("io.common.gui.source.sources"));
			generateComponents[0] = sourcesListLabel;
			sourcesList = new JList<>(new DefaultListModel<String>());
			sourcesListScrollPane = new JScrollPane();
			sourcesListScrollPane.getViewport().add(sourcesList);
			sourcesListScrollPane.setPreferredSize(new Dimension(50, 100));
			generateComponents[1] = sourcesListScrollPane;
			addButton = new JButton("Add...");
			generateComponents[2] = addButton;
			removeButton = new JButton("Remove");
			generateComponents[3] = removeButton;
			distributeOverLabel = new JLabel(
					m.formatMessage("io.common.gui.distribute.roundrobin"));
			generateComponents[4] = distributeOverLabel;
			distributeOver = new JTextField(5);
			generateComponents[5] = distributeOver;
			maxPairsPerFileLabel = new JLabel(
					m.formatMessage("io.common.gui.distribute.maxpairsperfile"));
			generateComponents[6] = maxPairsPerFileLabel;
			maxPairsPerFile = new JTextField(5);
			generateComponents[7] = maxPairsPerFile;
		}
		layoutContent();
	}

	public void addContentListeners() {
		super.addContentListeners();
		EnablednessGuard dl = new EnablednessGuard(this);

		// sourceFileBrowseButton
		sourceFileBrowseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				File f = FileChooserFactory.selectMrpsFile(parent);
				if (f != null) {
					sourceFileName.setText(f.getAbsolutePath());
				}
			}
		});

		// browsebutton
		browseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				File f = FileChooserFactory.selectXmlFile(parent);
				if (f != null) {
					xmlFileName.setText(f.getAbsolutePath());
				}
			}
		});

		if (!save) {
			// removeButton
			removeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					DefaultListModel<String> m = (DefaultListModel<String>) sourcesList
							.getModel();
					int[] si = sourcesList.getSelectedIndices();
					for (int i = si.length - 1; i >= 0; --i) {
						m.removeElementAt(si[i]);
					}
					setEnabledness();
				}
			});

			// addButton
			addButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					File[] fs = FileChooserFactory.selectMrpsFiles(parent);
					DefaultListModel<String> m = (DefaultListModel<String>) sourcesList
							.getModel();
					for (int i = 0; i < fs.length; ++i) {
						m.addElement(fs[i].getAbsolutePath());
					}
					setEnabledness();
				}
			});

			modeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					if (mode == CREATE) {
						mode = GENERATE;
						modeButton.setText(CREATE_MODE_LABEL);
						setSize(GENERATE_DIMENSION);
					} else {
						mode = CREATE;
						modeButton.setText(GENERATE_MODE_LABEL);
						setSize(CREATE_DIMENSION);
					}
					setGenerateComponentsVisibility();
					// validate();
					pack();
					setEnabledness();
				}
			});

			sourcesList.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					setEnabledness();
				}
			});
			distributeOver.getDocument().addDocumentListener(dl);
			maxPairsPerFile.getDocument().addDocumentListener(dl);
		}

		sourceFileName.getDocument().addDocumentListener(dl);
		xmlFileName.getDocument().addDocumentListener(dl);

		JavaHelpUtils.enableHelpKey(this, "io.gui.xml.mrps");
	}

	protected void generate() {
		Object[] sources = ((DefaultListModel<?>) sourcesList.getModel())
				.toArray();
		String[] sourceNames = new String[sources.length];
		System.arraycopy(sources, 0, sourceNames, 0, sources.length);
		try {
			int d = Integer.parseInt(distributeOver.getText());
			int s = Integer.parseInt(maxPairsPerFile.getText());
			if (s == 0) {
				s = Integer.MAX_VALUE;
			}
			final EncryptionScheme ep = createEncryptionPolicy();
			final CredentialSet ec = createEncryptionCredential();
			if (d == 1 && s == Integer.MAX_VALUE) {
				XmlEncMarkedRecordPairSink sink = new XmlEncMarkedRecordPairSink(
						getSourceFileName(), getSaveXmlFileName(),
						parent.getProbabilityModel(), ep, ec, crdsMgr);
				MarkedRecordPairBinder.store(sourceNames,
						parent.getProbabilityModel(), sink);
				buildSource();
			} else {
				computeFileNameAndExtension();
				String fileNameBase = getSourceFileName();
				fileNameBase = fileNameBase.substring(0, fileNameBase.length()
						- Constants.MRPS_EXTENSION.length() - 1);
				XmlEncMarkedRecordPairSinkFactory sinkFactory = new XmlEncMarkedRecordPairSinkFactory(
						fileNameBase, fileN, extension,
						parent.getProbabilityModel(), ep, ec, crdsMgr);
				MarkedRecordPairBinder.store(sourceNames,
						parent.getProbabilityModel(), sinkFactory, d, s);
				Source[] srcs = sinkFactory.getSources();
				setSource(srcs[0]);
				for (int i = 1; i < srcs.length; ++i) {
					try {
						MarkedRecordPairSourceXmlConf
								.add((MarkedRecordPairSource) srcs[i]);
					} catch (XmlConfException ex) {
						logger.severe(new LoggingObject("CM-020001", srcs[i])
								.toString() + ": " + ex);
					}
				}
			}
		} catch (XmlConfException ex) {
			logger.severe(new LoggingObject("CM-020001").toString() + ": " + ex);
			return;
		} catch (IOException ex) {
			logger.severe(new LoggingObject("CM-020001").toString() + ": " + ex);
			return;
		}
		dispose();
	}

	private CredentialSet createEncryptionCredential() {
		// TODO Auto-generated method stub
		return null;
	}

	private EncryptionScheme createEncryptionPolicy() {
		// TODO Auto-generated method stub
		return null;
	}

	private void layoutContent() {
		// Layout content
		GridBagLayout layout = new GridBagLayout();
		content.setLayout(layout);
		layout.columnWeights = new double[] { 0, 1, 0 };
		layout.rowWeights = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0 };
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(2, 2, 5, 5);

		// row 0........................................
		c.gridy = 0;
		c.gridx = 0;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.WEST;
		if (!save) {
			sourceFileNameLabel.setPreferredSize(maxPairsPerFileLabel
					.getPreferredSize());
		}
		content.add(sourceFileNameLabel, c);
		c.gridx = 1;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		content.add(sourceFileName, c);
		c.gridwidth = 1;

		c.gridx = 3;
		layout.setConstraints(sourceFileBrowseButton, c);
		content.add(sourceFileBrowseButton);

		// row 1........................................
		c.gridy = 1;
		c.gridx = 0;
		content.add(xmlFileNameLabel, c);
		c.gridx = 1;
		c.gridwidth = 2;
		content.add(xmlFileName, c);
		c.gridwidth = 1;
		c.gridx = 3;
		content.add(browseButton, c);

		// row 2........................................
		c.gridy = 2;
		c.gridx = 1;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		content.add(encSchemesLabel, c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;

		c.gridx = 2;
		c.fill = GridBagConstraints.NONE;
		content.add(encSchemesBox, c);
		c.fill = GridBagConstraints.HORIZONTAL;

		// row 3........................................
		c.gridy = 3;
		c.gridx = 1;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		content.add(encCredentialsLabel, c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;

		c.gridx = 2;
		c.fill = GridBagConstraints.NONE;
		content.add(encCredentialsBox, c);
		c.fill = GridBagConstraints.HORIZONTAL;

		// row 4........................................
		c.gridy = 4;
		c.gridx = 1;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		content.add(xmlFileRelativeLabel, c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;

		c.gridx = 2;
		c.fill = GridBagConstraints.NONE;
		content.add(xmlFileRelativeBox, c);
		c.fill = GridBagConstraints.HORIZONTAL;

		// row 5........................................
		c.gridy = 5;
		c.gridx = 0;
		c.fill = GridBagConstraints.NONE;
		if (!save) {
			content.add(modeButton, c);
		}
		c.gridx = 2;
		c.anchor = GridBagConstraints.EAST;
		content.add(okayButton, c);
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		content.add(cancelButton, c);

		if (!save) {
			c.gridy = 6;
			c.gridx = 0;
			content.add(sourcesListLabel, c);

			// row 7--------------------------
			c.gridy = 7;
			c.fill = GridBagConstraints.BOTH;
			c.gridwidth = 3;
			c.gridheight = 3;
			content.add(sourcesListScrollPane, c);
			c.gridwidth = 1;
			c.gridheight = 1;

			c.gridx = 3;
			c.fill = GridBagConstraints.HORIZONTAL;
			content.add(addButton, c);

			c.gridy = 8;
			content.add(removeButton, c);

			// row 3--------------------------
			c.gridy = 10;
			c.gridx = 0;
			c.gridwidth = 1;
			c.anchor = GridBagConstraints.WEST;
			c.fill = GridBagConstraints.NONE;
			content.add(distributeOverLabel, c);
			c.gridx = 1;
			content.add(distributeOver, c);

			// row 4--------------------------
			c.gridy = 11;
			c.gridx = 0;
			content.add(maxPairsPerFileLabel, c);
			c.gridx = 1;
			content.add(maxPairsPerFile, c);

			setGenerateComponentsVisibility();
		}
	}

	private void setGenerateComponentsVisibility() {
		boolean visible = mode == GENERATE;
		for (int i = 0; i < generateComponents.length; ++i) {
			generateComponents[i].setVisible(visible);
		}
	}
}
