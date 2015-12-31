/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.modelmaker.gui.panels;

import java.awt.BorderLayout;
//import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.choicemaker.cm.core.util.ChoiceMakerCoreMessages;

/**
 * Hold percentage versus accuracy using a independent match/differ thresholds.
 * 
 * @author S. Yoakum-Stover
 */
public class AsymmetricThresholdVsAccuracyPlotPanel extends JPanel {

	private static final long serialVersionUID = -1283403271414404208L;

	private static final String PRECISION = ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.panel.asymm.precision");
	private static final String RECALL = ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.panel.asymm.recall");
	private TestingControlPanel parent;
	private JFreeChart dPlot;
	private JFreeChart mPlot;
	private XYSeries differPrecision;
	private XYSeries differRecall;
	private XYSeries matchPrecision;
	private XYSeries matchRecall;
//	private DecimalFormat df = new DecimalFormat("##0.00");
	private boolean dirty;

	public AsymmetricThresholdVsAccuracyPlotPanel(TestingControlPanel g) {
		super();
		parent = g;
		setBorder(BorderFactory.createEmptyBorder(15, 5, 5, 5));
		buildPlots();
		layoutPanel();
		addListeners();
	}

	public void buildPlots() {
		differPrecision = new XYSeries(PRECISION);
		differRecall = new XYSeries(RECALL);
		dPlot =
			buildPlot(
				differPrecision,
				differRecall,
				ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.panel.asymm.hold.vs.accuracy.d.title"),
				ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.panel.asymm.threshold.differ"));
		matchPrecision = new XYSeries(PRECISION);
		matchRecall = new XYSeries(RECALL);
		mPlot =
			buildPlot(
				matchPrecision,
				matchRecall,
				ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.panel.asymm.hold.vs.accuracy.m.title"),
				ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.panel.asymm.threshold.match"));
	}

	private JFreeChart buildPlot(XYSeries d, XYSeries m, String title, String xAxis) {
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(d);
		dataset.addSeries(m);
		final String yAxis = ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.panel.asymm.cm.accuracy");
		final PlotOrientation orientation = PlotOrientation.VERTICAL;
		final boolean hasLegend = true;
		final boolean hasToolTips = true;
		final boolean hasURLs = true;
		JFreeChart chart =
//			ChartFactory.createLineXYChart(
//				title,
//				xAxis,
//				ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.panel.asymm.cm.accuracy"),
//				dataset,
//				true, true, true);
		ChartFactory.createXYLineChart(title, xAxis, yAxis, dataset, orientation, hasLegend, hasToolTips, hasURLs);
		chart.setBackgroundPaint(getBackground());
		return chart;
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
			reset();
			float[][] ta = parent.getModelMaker().getStatistics().getThresholdVsAccuracy();
			int len = ta.length;
			float lastThr = Float.NaN;
			for (int i = 0; i < len; ++i) {
				float[] p = ta[i];
				float thr = p[0] * 100;
				if (thr != lastThr) {
					lastThr = thr;
					if (thr < 50f) {
						differPrecision.add(thr, 100 - p[1] * 100);
						differRecall.add(thr, p[2] * 100);
					} else {
						matchPrecision.add(thr, 100 - p[3] * 100);
						matchRecall.add(thr, p[4] * 100);
					}
				}
			}
		}
	}

	private void addListeners() {
	}

	public void reset() {
		differPrecision.clear();
		differRecall.clear();
		matchPrecision.clear();
		matchRecall.clear();
	}

	private void layoutPanel() {
		setLayout(new BorderLayout());
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setDividerSize(2);
		splitPane.setContinuousLayout(true);
		splitPane.setDividerLocation(0.5d);
		splitPane.setResizeWeight(0.5f);
		splitPane.setOneTouchExpandable(true);
		ChartPanel dP = new ChartPanel(dPlot, false, false, false, true, true);
//		dP.setHorizontalZoom(true);
//		dP.setVerticalZoom(true);
		splitPane.setTopComponent(dP);
		ChartPanel mP = new ChartPanel(mPlot, false, false, false, true, true);
//		mP.setHorizontalZoom(true);
//		mP.setVerticalZoom(true);
		splitPane.setBottomComponent(mP);
		add(splitPane, BorderLayout.CENTER);
	}
}
