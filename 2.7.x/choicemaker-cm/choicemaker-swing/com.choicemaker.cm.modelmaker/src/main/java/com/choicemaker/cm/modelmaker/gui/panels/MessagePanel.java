/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.modelmaker.gui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Writer;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;

import com.choicemaker.cm.core.util.ChoiceMakerCoreMessages;
import com.choicemaker.cm.modelmaker.gui.ModelMaker;

/**
 * The panel which displays status messages to the user.
 * 
 * @author S. Yoakum-Stover
 */
public class MessagePanel extends JPanel {
	private static final long serialVersionUID = 1L;
//	private ModelMaker parent;
	private JScrollPane statusTextAreaScrollPane;
	private JTextArea statusTextArea;
	private Writer w;
	private OutputStream os;
	private Accumulator accumulator;

	public MessagePanel(ModelMaker g) {
		super();
//		parent = g;
		accumulator = new Accumulator();
		buildPanel();
		w = new Writer() {
			@Override
			public void close() {
			}
			@Override
			public void flush() {
			}
			@Override
			public void write(char[] cbuf, int off, int len) {
				postMessage(new String(cbuf, off, len));
			}
			@Override
			public void write(int c) {
				postMessage(String.valueOf((char) c));
			}
			@Override
			public void write(String str) {
				postMessage(str);
			}
		};
		os = new OutputStream() {
			@Override
			public void close() {
			}
			@Override
			public void flush() {
			}
			@Override
			public void write(byte[] b) {
				postMessage(new String(b));
			}
			@Override
			public void write(byte[] b, int off, int len) {
				postMessage(new String(b, off, len));
			}
			@Override
			public void write(int b) {
				postMessage(String.valueOf((char) b));
			}
		};
	}
	
	public Document getDocument() {
		return this.statusTextArea.getDocument();
	}

	public Writer getWriter() {
		return w;
	}

	public OutputStream getOutputStream() {
		return os;
	}

	public PrintStream getPrintStream() {
		return new PrintStream(os, true);
	}

	private void buildPanel() {
		statusTextArea = new JTextArea("");
		statusTextArea.setLineWrap(true);
		statusTextArea.setWrapStyleWord(true);
		statusTextArea.setMargin(new Insets(1, 3, 1, 3));

		//build the JScrollPane that the status text will live inside
		statusTextAreaScrollPane = new JScrollPane(statusTextArea);
		statusTextAreaScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		statusTextAreaScrollPane.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder(
						ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.panel.message.title")),
					BorderFactory.createEmptyBorder(5, 5, 5, 5)),
				statusTextAreaScrollPane.getBorder()));

		setPreferredSize(new Dimension(400, 100));
		setLayout(new BorderLayout());
		add(statusTextAreaScrollPane, BorderLayout.CENTER);
	}

	public void postMessage(final String s) {
		accumulator.append(s);
	}

	private class Accumulator implements Runnable {
		private StringBuffer buf = new StringBuffer();
		private boolean queued;
		
		@Override
		public void run() {
			synchronized(this) {
				statusTextArea.append(buf.toString());
				buf.delete(0, buf.length());
				queued = false;
			}
			statusTextArea.setCaretPosition(statusTextArea.getDocument().getLength());
//			statusTextArea.paintImmediately(statusTextArea.getBounds());			
		}
		
		synchronized void append(String s) {
			buf.append(s);
			if(SwingUtilities.isEventDispatchThread() && !queued) {
				run();
			} else if(!queued) {
				queued = true;
				SwingUtilities.invokeLater(this);
			}	
		}
	}

	public void clearMessages() {
		statusTextArea.setText("");
	}
}
