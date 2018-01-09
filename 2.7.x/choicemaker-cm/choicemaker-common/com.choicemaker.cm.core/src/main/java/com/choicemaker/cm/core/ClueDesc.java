/*******************************************************************************
 * Copyright (c) 2015, 2016 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.core;

import com.choicemaker.client.api.Decision;

/**
 * Describes a single clue.
 *
 * A <code>ClueSet</code> creates an instance of this class for each
 * of its clues and rules. Instances of this class are immutable.
 *
 * @author    Martin Buechi
 */
public class ClueDesc {
	public static final int NONE = 0;
	public static final int NOTE = 1;
	public static final int REPORT = 2;

	public final int number;
	public final String name;
	public final Decision decision;
	public final boolean rule;
	public final int modifier;
	public final int startLineNumber;
	public final int endLineNumber;

	/**
	 * Creates a <code>ClueDesc</code>.
	 *
	 * This constructor performs no validity checks on its arguments as all calls
	 * are in generated code.
	 *
	 * @param   number  The clue or rule number. The clues/rules of each <code>ClueSet</code>
	 *            are numbered consecutively in the order they appear in the ClueMaker
	 *            source, starting from 0.
	 * @param   name  The name given to the clue in the ClueMaker source.
	 * @param   decision  The <code>Decision</code> (future) that this clue predicts.
	 * @param   startLineNumber  The first line number of this clue in the ClueMaker
	 *            source code. Includes immediately preceeding JavaDoc-style comments.
	 * @param   endLineNumber  The last line number of this clue in the ClueMaker
	 *            source code.
	 */
	public ClueDesc(
		int number,
		String name,
		Decision decision,
		boolean rule,
		int modifier,
		int startLineNumber,
		int endLineNumber) {
		this.number = number;
		this.name = name;
		this.decision = decision;
		this.rule = rule;
		this.modifier = modifier;
		this.startLineNumber = startLineNumber;
		this.endLineNumber = endLineNumber;
	}

	/**
	 * Returns the clue number number.
	 *
	 * @return  The clue number.
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * Returns the clue name.
	 *
	 * @return  The clue name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the <code>Decision</code>.
	 *
	 * @return  The <code>Decision</code>.
	 */
	public Decision getDecision() {
		return decision;
	}

	/**
	 * Returns the first line number of this clue in the ClueMaker source.
	 *
	 * @return  The first line number of this clue in the ClueMaker source.
	 */
	public int getStartLineNumber() {
		return startLineNumber;
	}

	/**
	 * Returns the last line number of this clue in the ClueMaker source.
	 *
	 * @return  The last line number of this clue in the ClueMaker source.
	 */
	public int getEndLineNumber() {
		return endLineNumber;
	}
}
