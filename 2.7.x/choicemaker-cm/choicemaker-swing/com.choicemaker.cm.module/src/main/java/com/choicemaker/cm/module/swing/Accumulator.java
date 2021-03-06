/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.module.swing;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Writer;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import com.choicemaker.cm.module.IUserMessages;

class Accumulator implements IUserMessages, Runnable {
		
	private final Document document;
//	private final EventListenerList listeners = new EventListenerList();
	private StringBuffer buf = new StringBuffer();
	private boolean queued;
	private final Writer w = new Writer() {
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
	private final OutputStream os = new OutputStream() {
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
		
		public void addDocumentListener(DocumentListener listener) {
			document.addDocumentListener(listener);
		}
		
		public void removeDocumentListener(DocumentListener listener) {
			document.removeDocumentListener(listener);
		}
		
		public Accumulator(Document d) {
			this.document = d;
			// Fail fast
			if (this.document == null) {
				throw new IllegalArgumentException("null document");
			}
		}
		
		@Override
		public void run() {
			synchronized(this) {
				try {
					String str = buf.toString();
					document.insertString(document.getLength(), str, null);
				} catch (BadLocationException e) {
					throw new Error("Should never happen");
				}
				buf.delete(0, buf.length());
				queued = false;
			}
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


	@Override
	public Writer getWriter() {
		return w;
	}

	@Override
	public OutputStream getOutputStream() {
		return os;
	}

	@Override
	public PrintStream getPrintStream() {
		return new PrintStream(os, true);
	}
	
	@Override
	public void postInfo(final String s) {
		postMessage(s);
	}

	@Override
	public void postMessage(final String s) {
		this.append(s);
	}
	
	private void setText(String t) {
		try {
			if (document instanceof AbstractDocument) {
				((AbstractDocument)document).replace(0, document.getLength(), t,null);
			}
			else {
				document.remove(0, document.getLength());
				document.insertString(0, t, null);
			}
		} catch (BadLocationException e) {
			// A bit tricky to handle, since this object may have commandeered
			// the log, System.out and System.err
		UIManager.getLookAndFeel().provideErrorFeedback(null);
		}
	}

	@Override
	public void clearMessages() {
		setText("");
	}

}

