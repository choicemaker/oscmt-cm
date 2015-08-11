/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.blocking.automated.offline.core;

/**
 * This is a source from which to get SuffixTreeNode objects.
 * 
 * @author pcheung
 *
 */
public interface ISuffixTreeSource extends ISource<SuffixTreeNode> {

	/**
	 * Returns the number of Suffix Tree read so far.
	 * 
	 * @return int
	 */
	public int getCount();

}
