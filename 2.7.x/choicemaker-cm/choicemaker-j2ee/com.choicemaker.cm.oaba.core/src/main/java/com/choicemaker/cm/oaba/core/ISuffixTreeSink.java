/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.core;

import com.choicemaker.cm.core.BlockingException;

/**
 * This interface handles the writing of SUffixTrees.
 * 
 * @author pcheung
 *
 */
public interface ISuffixTreeSink extends ISink {

	/**
	 * This method writes the input node all its descendants to the sink.
	 * 
	 * @param root
	 * @throws BlockingException
	 */
	public void writeSuffixTree(SuffixTreeNode root) throws BlockingException;

}
