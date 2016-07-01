/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.modelmaker.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;

import com.choicemaker.cm.core.util.ChoiceMakerCoreMessages;
import com.choicemaker.cm.modelmaker.gui.ModelMaker;
import com.choicemaker.cm.modelmaker.gui.utils.HistoCategoryDataset;
import com.choicemaker.cm.modelmaker.gui.utils.HistoChartPanel;

/**
 * Panel that contains the histogram showing the ChoiceMaker system accuracy.  This
 * histogram listens for mouse clicks on its bars.
 *
 * @author S. Yoakum-Stover
 */
public class StatisticsHistogramPanel extends JPanel {

	private static final long serialVersionUID = -9207566748507473389L;

	private TestingControlPanel parent;
	private HistoChartPanel histoPanel;
	private JFreeChart histogram;
	private HistoCategoryDataset data;
	private JLabel binWidthLabel;
	private JTextField binWidthField;
	private float binWidth = 5.0f;
	private boolean dirty;
	private static final String[] SERIES = { "human differ", "human hold",
			"human match" };

	public StatisticsHistogramPanel(TestingControlPanel g) {
		super();
		parent = g;
		setBorder(BorderFactory.createEmptyBorder(15, 5, 5, 5));
		buildPanel();
		addListeners();
		layoutPanel();
	}

	private int getNumBins() {
		return (int) Math.ceil(100 / binWidth);
	}

	private void buildPanel() {
		final PlotOrientation orientation = PlotOrientation.VERTICAL;
		data = new HistoCategoryDataset(SERIES, getNumBins());
		histogram =
			ChartFactory.createBarChart(
				ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.panel.histogram.cm.accuracy"),
				ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.panel.histogram.cm.matchprob"),
				ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.panel.histogram.cm.numpairs"),
				data,
				orientation,
				true,
				true,
				true);
		histogram.setBackgroundPaint(getBackground());
		CategoryPlot plot = (CategoryPlot) histogram.getPlot();
		plot.setForegroundAlpha(0.9f);
		CategoryAxis axis = plot.getDomainAxis();
		axis.setLowerMargin(0.02);
		axis.setUpperMargin(0.02);
		axis.setCategoryMargin(0.2);
		histoPanel = new HistoChartPanel(histogram, false, false, false, true, true, parent.getModelMaker());

		binWidthLabel = new JLabel(ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.panel.histogram.binwidth"));
		binWidthField = new JTextField(Float.toString(binWidth), 4);
		binWidthField.setMinimumSize(new Dimension(50, 20));
	}

	private void addListeners() {
		//        histo.addEditListener(this);

		//binWidthField
		binWidthField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				setBinWidth(Float.parseFloat(binWidthField.getText()));
			}
		});

	}

	private void setBinWidth(float bw) {
		if (bw > 0.1 && bw <= 100) {
			binWidth = bw;
			data.setNumCategories(getNumBins());
			display();
		}
	}

	private void layoutPanel() {
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(2, 10, 5, 10);

		//Row 0..........................................................
		//histo
		c.gridy = 0;
		c.gridx = 0;
		c.gridwidth = 5;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		add(histoPanel, c);

		//Row 1..........................................................
		//binWidthLabel
		c.gridy = 1;
		c.gridx = 1;
		c.gridwidth = 1;
		c.weightx = 0;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.EAST;
		layout.setConstraints(binWidthLabel, c);
		add(binWidthLabel);
		//binWidthField
		c.gridx = 2;
		c.ipadx = 10;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(binWidthField, c);
		add(binWidthField);
	}

	public void reset() {
		data.setData(null);
	}

	public void setVisible(boolean b) {
		super.setVisible(b);
		if (b && dirty) {
			display();
		}
	}

	public void plot() {
		setDirty();
	}

	private void setDirty() {
		if (isVisible()) {
			display();
		} else {
			dirty = true;
		}
	}

	private void display() {
		dirty = false;
		if (parent.isEvaluated()) {
			ModelMaker mm = parent.getModelMaker();
			int[][] h = mm.getStatistics().getHistogram(getNumBins());
			final int len = h[0].length;
			Integer[][] v = new Integer[h.length][len];
			for (int i = 0; i < h.length; ++i) {
				int[] hi = h[i];
				Integer[] vi = v[i];
				for (int j = 0; j < len; ++j) {
					vi[j] = new Integer(hi[j]);
				}
			}
			data.setData(v);
		}
	}
}
