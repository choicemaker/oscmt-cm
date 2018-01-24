/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.impl;

import static com.choicemaker.cm.oaba.core.RECORD_ID_TYPE.TYPE_INTEGER;
import static com.choicemaker.cm.oaba.core.RECORD_ID_TYPE.TYPE_LONG;
import static com.choicemaker.cm.oaba.core.RECORD_ID_TYPE.TYPE_STRING;

import java.io.EOFException;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Stack;

import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.oaba.core.ComparisonTreeNode;
import com.choicemaker.cm.oaba.core.Constants;
import com.choicemaker.cm.oaba.core.EXTERNAL_DATA_FORMAT;
import com.choicemaker.cm.oaba.core.IComparisonTreeSource;
import com.choicemaker.cm.oaba.core.RECORD_ID_TYPE;

/**
 * @author pcheung
 *
 */
// @SuppressWarnings({"rawtypes", "unchecked"})
public class ComparisonTreeSource<T extends Comparable<T>> extends
		BaseFileSource<ComparisonTreeNode<T>> implements
		IComparisonTreeSource<T> {

	private ComparisonTreeNode<T> nextTree = null;

	// this indicates if the Record ID is a int, long, or string.
	private RECORD_ID_TYPE dataType;

	/** This constructor creates a string source with the given name */
	public ComparisonTreeSource(String fileName, RECORD_ID_TYPE dataType) {
		super(fileName, EXTERNAL_DATA_FORMAT.STRING);
		this.dataType = dataType;
	}

	protected void resetNext() {
		nextTree = null;
	}

	@Override
	public ComparisonTreeNode<T> next() {
		if (this.nextTree == null) {
			try {
				this.nextTree = readNext();
			} catch (EOFException x) {
				throw new NoSuchElementException("EOFException: "
						+ x.getMessage());
			} catch (IOException x) {
				throw new NoSuchElementException("OABABlockingException: "
						+ x.getMessage());
			}
		}
		ComparisonTreeNode<T> retVal = this.nextTree;
		count++;
		this.nextTree = null;

		return retVal;
	}

	@SuppressWarnings("unchecked")
	private ComparisonTreeNode<T> readNext() throws EOFException, IOException {
		ComparisonTreeNode<T> ret = null;

		String str = br.readLine();
		if (str == null || str.equals(""))
			throw new EOFException();

		int ind = 0;
		int size = str.length();

		ret = ComparisonTreeNode.createRootNode();

		// setting up the stack
		Stack<ComparisonTreeNode<T>> stack = new Stack<>();
		stack.push(ret);

		while (ind < size) {
			if (str.charAt(ind) == Constants.OPEN_NODE) {
				int i = getNextMarker(str, ind, size);
				char stageOrMaster = str.charAt(ind + 1);

				T c = null;

				if (dataType == TYPE_LONG) {
					c = (T) new Long(str.substring(ind + 3, i));
				} else if (dataType == TYPE_INTEGER) {
					c = (T) new Integer(str.substring(ind + 3, i));
				} else if (dataType == TYPE_STRING) {
					c = (T) str.substring(ind + 3, i);
				} else {
					throw new IllegalArgumentException("Unknown DataType: "
							+ dataType);
				}

				ComparisonTreeNode<T> kid = null;

				// use peek here because we don't want to remove from the stack.
				ComparisonTreeNode<T> parent = stack.peek();

				if (str.charAt(i) == Constants.CLOSE_NODE) {
					// leaf
					kid = parent.putChild(c, stageOrMaster, count);
				} else {
					// has at least 1 child
					kid = parent.putChild(c, stageOrMaster);
				}

				stack.push(kid);

				ind = i;
			} else if (str.charAt(ind) == Constants.CLOSE_NODE) {
				stack.pop();
				ind++;
			}
		}

		// at the end, there should only be the root on the stack.
		if (stack.size() != 1)
			throw new IOException("Could not parse this tree: " + str);

		return ret;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.choicemaker.cm.oaba.core.ISource#hasNext()
	 */
	@Override
	public boolean hasNext() throws BlockingException {
		if (this.nextTree == null) {
			try {
				this.nextTree = readNext();
			} catch (EOFException x) {
				this.nextTree = null;
			} catch (IOException x) {
				throw new BlockingException(x.toString());
			}
		}
		return this.nextTree != null;
	}

	/**
	 * This method returns the location of the next OPEN_NODE or CLOSE_NODE
	 * starting from index from+1.
	 *
	 * @param str
	 * @param from
	 */
	private int getNextMarker(String str, int from, int size) {
		boolean found = false;
		int i = from + 1;
		// int size = str.length();
		while (!found && i < size) {
			if (str.charAt(i) == Constants.OPEN_NODE
					|| str.charAt(i) == Constants.CLOSE_NODE)
				found = true;
			else
				i++;
		}
		return i;
	}

}
