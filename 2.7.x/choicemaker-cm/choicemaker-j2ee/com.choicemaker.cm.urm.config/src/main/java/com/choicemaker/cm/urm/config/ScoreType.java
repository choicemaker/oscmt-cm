/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.config;


/**
 * A type of the match score
 * <ul>
 * <li>NO_NOTE - match score note member will not assigned.</li>
 * <li>RULE_LIST_NOTE match score note member will contain names of the fired
 * clues or notes. Only clues and notes marked in the model clue file by the
 * <code>note</code> modifier will be included into the note.</li>
 * </ul>
 * <p>
 *
 * @author emoussikaev
 */
public enum ScoreType {
	NO_NOTE, RULE_LIST_NOTE;
}
