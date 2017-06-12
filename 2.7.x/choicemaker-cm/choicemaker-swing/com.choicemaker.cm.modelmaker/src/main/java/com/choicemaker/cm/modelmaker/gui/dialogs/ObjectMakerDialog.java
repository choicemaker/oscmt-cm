/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.modelmaker.gui.dialogs;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.choicemaker.cm.compiler.util.ModelArtifactBuilder;
import com.choicemaker.cm.core.ChoiceMakerExtensionPoint;
import com.choicemaker.cm.core.Constants;
import com.choicemaker.cm.core.util.ObjectMaker;
import com.choicemaker.cm.gui.utils.dialogs.FileChooserFactory;
import com.choicemaker.cm.modelmaker.gui.ModelMaker;
import com.choicemaker.cm.modelmaker.gui.utils.Enable;
import com.choicemaker.cm.modelmaker.gui.utils.EnablednessGuard;
import com.choicemaker.cm.modelmaker.gui.utils.ThreadWatcher;
import com.choicemaker.e2.CMConfigurationElement;
import com.choicemaker.e2.CMExtension;
import com.choicemaker.e2.CMExtensionPoint;
import com.choicemaker.e2.platform.CMPlatformUtils;

/**
 *
 * @author Adam Winkel
 */
public class ObjectMakerDialog extends JDialog implements Enable {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger
			.getLogger(ObjectMakerDialog.class.getName());

	private ModelMaker modelMaker;

	private JTextField dirField;
	private JButton dirBrowse;

	private ObjectMaker[] objectMakers;
	private String[] descriptions;
	private Boolean[] defaults;

	private JCheckBox[] boxes;
	private JButton ok, cancel;

	public ObjectMakerDialog(ModelMaker modelMaker) {
		super(modelMaker, "Holder Classes Jar and DB Objects Dialog", true);
		this.modelMaker = modelMaker;

		getPlugins();

		createContent();
		createListeners();

		setEnabledness();

		pack();
		setLocationRelativeTo(modelMaker);
	}

	public File getOutDir() {
		if (dirField.getText().trim().length() == 0) {
			return null;
		} else {
			return new File(dirField.getText().trim()).getAbsoluteFile();
		}
	}

	public void setEnabledness() {
		File outDir = getOutDir();
		if (outDir == null || outDir.isFile()) {
			ok.setEnabled(false);
			return;
		}

		for (int i = 0; i < boxes.length; i++) {
			if (boxes[i].isSelected()) {
				ok.setEnabled(true);
				return;
			}
		}

		ok.setEnabled(false);
	}

	private void generateObjects() {
		final List<String> errorMsgs = new ArrayList<>();
		final File outDir = getOutDir();
		if (!outDir.isDirectory()) {
			outDir.mkdirs();
		}

		final Thread t = new Thread() {
			public void run() {
				try {
					ModelArtifactBuilder
							.refreshProductionProbabilityModels();
				} catch (Exception ex) {
					errorMsgs.add(ex.toString());
					return;
				}

				for (int i = 0; i < boxes.length; i++) {
					if (currentThread().isInterrupted()) {
						return;
					}
					if (boxes[i].isSelected()) {
						try {
							objectMakers[i].generateObjects(outDir);
						} catch (Exception ex) {
							errorMsgs.add(ex.toString());
						}
					}
				}
			}
		};

		boolean interrupted =
			ThreadWatcher.watchThread(t, modelMaker, "Please Wait",
					"Generating Objects");

		if (interrupted) {
			setVisible(true);

		} else {
			setVisible(false);
			String status;
			String results;
			Level level;
			if (errorMsgs.size() > 0) {
				status = "Problems during object generation:";
				results = "Incomplete results in ' " + outDir.getAbsolutePath() + "'";
				level = Level.SEVERE;
			} else {
				status = "Object generation complete.";
				results = "Results in ' " + outDir.getAbsolutePath() + "'";
				level = Level.INFO;
			}
			logStatus(status, results, errorMsgs, level);
			reportStatus(status, results, errorMsgs);
			displayStatus(status, results, errorMsgs);
			dispose();
		}
	}

	private static final String INDENT = "   ";

	private String createMessage(String status, String results,
			List<String> errorMsgs) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.println(status);
		if (errorMsgs != null && errorMsgs.size() > 0) {
			for (int i=0; i<errorMsgs.size(); i++) {
				String s = (String) errorMsgs.get(i);
				pw.println(INDENT + s);
			}
		}
		pw.println(results);
		final String retVal = sw.toString();
		return retVal;
	}

	private void logStatus(String status, String results,
			List<String> errorMsgs, Level level) {
		final String msg = createMessage(status, results,errorMsgs);
		logger.log(level, msg);
	}

	private void reportStatus(String status, String results,
			List<String> errorMsgs) {
		final String msg = createMessage(status, results,errorMsgs);
		this.modelMaker.getMessagePanel().postMessage(msg);
	}

	private void displayStatus(String status, String results,
			List<String> errorMsgs) {
		StringBuilder sb = new StringBuilder("<html>");
		sb.append("<body style='width: 200px; padding: 5px;'>");
		sb.append(status);
		if (errorMsgs != null && errorMsgs.size() > 0) {
			sb.append("<blockQuote>");
			for (int i=0; i<errorMsgs.size(); i++) {
				String e = (String) errorMsgs.get(i);
				sb.append("<br/>").append(e);
			}
			sb.append("</blockQuote>");
		} else {
			sb.append("<br/>");
		}
		sb.append(results);
		sb.append("</body>");
		sb.append("</html>");
		final String msg = sb.toString();

		final JDialog d = new JDialog(modelMaker, "Status", true);
		d.getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(10, 10, 10, 10);
		c.gridx = 0;
		c.gridy = 0;
		d.getContentPane().add(new JLabel(msg), c);
		JButton dOk = new JButton("OK");
		c.gridy = 1;
		d.getContentPane().add(dOk, c);
		dOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				d.dispose();
			}
		});
		d.pack();
		d.setLocationRelativeTo(modelMaker);
		d.setVisible(true);
		dispose();
	}

	private void getPlugins() {
		ArrayList<ObjectMaker> makers = new ArrayList<>();
		ArrayList<String> descs = new ArrayList<>();
		ArrayList<Boolean> defs = new ArrayList<>();

		String extPtName = ChoiceMakerExtensionPoint.CM_CORE_OBJECTGENERATOR;
		CMExtensionPoint pt = CMPlatformUtils.getExtensionPoint(extPtName);
		CMExtension[] extensions = pt.getExtensions();
		for (int i = 0; i < extensions.length; i++) {
			CMExtension extension = extensions[i];
			CMConfigurationElement[] els = extension.getConfigurationElements();
			for (int j = 0; j < els.length; j++) {
				CMConfigurationElement element = els[j];
				try {
					Object o = element.createExecutableExtension("class");
					if (o == null) {
						String msg = "Null instance created for extension '"
								+ extension.getUniqueIdentifier() + "' ('"
								+ extPtName + "')";
						logger.severe(msg);
						msg = "PLUGIN ERROR: " + msg;
						this.modelMaker.getMessagePanel().postMessage(msg);
						continue;
					} else if (!(o instanceof ObjectMaker)) {
						String msg = "Invalid class specified for extension '"
								+ extension.getUniqueIdentifier() + "' ('"
								+ extPtName + "'): " + o.getClass().getName();
						logger.severe(msg);
						msg = "PLUGIN ERROR: " + msg;
						this.modelMaker.getMessagePanel().postMessage(msg);
						continue;
					}
					assert o != null && (o instanceof ObjectMaker);
					ObjectMaker maker = (ObjectMaker) o;
					makers.add(maker);
					descs.add(element.getAttribute("description"));
					defs.add(new Boolean(
							"true".equals(element.getAttribute("default"))));
				} catch (Exception ex) {
					logger.severe(ex.toString());
				}
			}
		}

		objectMakers = makers.toArray(new ObjectMaker[makers.size()]);
		descriptions = descs.toArray(new String[descs.size()]);
		defaults = defs.toArray(new Boolean[defs.size()]);
	}

	private void createContent() {
		GridBagLayout layout = new GridBagLayout();
		layout.columnWeights = new double[] {
				0, 1, 0, 0 };
		getContentPane().setLayout(layout);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(3, 3, 3, 3);
		c.weighty = 0;

		//

		c.gridy = 0;

		c.gridx = 0;
		getContentPane().add(new JLabel("Output Directory: "), c);

		c.gridx = 1;
		c.gridwidth = 2;
		dirField = new JTextField(35);
		File f =
			new File(Constants.MODELS_DIRECTORY, Constants.GEN_OUT_DIRECTORY);
		dirField.setText(f.getAbsolutePath());
		getContentPane().add(dirField, c);
		c.gridwidth = 1;

		c.gridx = 3;
		dirBrowse = new JButton("Browse");
		getContentPane().add(dirBrowse, c);

		//

		c.gridx = 1;
		c.gridwidth = 3;
		c.anchor = GridBagConstraints.WEST;

		boxes = new JCheckBox[descriptions.length];
		for (int i = 0; i < boxes.length; i++) {
			c.gridy++;

			boxes[i] = new JCheckBox(descriptions[i]);
			if (defaults[i].booleanValue()) {
				boxes[i].setSelected(true);
			}

			getContentPane().add(boxes[i], c);
		}

		//

		c.gridy++;

		c.gridx = 2;
		c.gridwidth = 1;
		ok = new JButton("OK");
		ok.setEnabled(false);
		getContentPane().add(ok, c);

		c.gridx = 3;
		cancel = new JButton("Cancel");
		getContentPane().add(cancel, c);

	}

	private void createListeners() {
		EnablednessGuard dl = new EnablednessGuard(this);
		dirField.getDocument().addDocumentListener(dl);

		dirBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File f = new File(dirField.getText());
				Component c = ObjectMakerDialog.this;
				File dir = FileChooserFactory.selectDirectory(c, f);
				if (dir != null) {
					dirField.setText(dir.getAbsolutePath());
				}
			}
		});

		ChangeListener boxListener = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				setEnabledness();
			}
		};
		for (int i = 0; i < boxes.length; i++) {
			boxes[i].addChangeListener(boxListener);
		}

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				generateObjects();
			}
		});

		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}

}
