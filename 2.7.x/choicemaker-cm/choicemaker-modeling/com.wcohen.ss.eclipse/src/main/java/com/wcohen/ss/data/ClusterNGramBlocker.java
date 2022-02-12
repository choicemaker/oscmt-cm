/*******************************************************************************
 * Copyright (c) 2007, 2014 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
package com.wcohen.ss.data;

/**
 * TokenBlocker for clustering based on NGram co-occurence.
 */

public class ClusterNGramBlocker extends NGramBlocker
{
	public ClusterNGramBlocker() {
		super();
		setClusterMode(true);
	}
	public String toString() { return "[ClusterNGramBlocker]"; }
}
