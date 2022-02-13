/*******************************************************************************
 * Copyright (c) 2003, 2019 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc  - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
package com.choicemaker.cm.oaba.core;

/**
 * Specifies how a group of query records are to be matched.
 * <ul>
 * <li>Single record matching (SRM): each record from the group is matched one
 * at a time against reference records.</li>
 * <li>Batch record matching (BRM): the entire group is matched collectively
 * against reference records.</li>
 * </ul>
 * Both modes of operation should yield the same results. For small groups of
 * records, typically less than a few thousand, single record matching is more
 * efficient than batch record matching.
 * 
 * @author rphall
 *
 */
public enum RecordMatchingMode {
	/** Single-record matching mode */
	SRM,
	/** Batch-record matching mode */
	BRM
}
