/*
 * Copyright (c) 2001, 2009 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.io.blocking.automated.offline.core;

import com.choicemaker.cm.core.BlockingException;

/** This is a source from which to get SuffixTreeNode objects.
 * 
 * @author pcheung
 *
 */
public interface ISuffixTreeSource<T extends Comparable<? super T>> extends ISource {

	/** This returns the next SuffixTree in the source.  Make sure you call hasNext before calling
	 * this method.
	 * 
	 * @return SuffixTreeNode
	 * @throws OABABlockingException
	 */
	public SuffixTreeNode<T> getNext () throws BlockingException;

	/** Returns the number of Suffix Tree read so far.
	 * 
	 * @return int
	 */
	public int getCount ();

}
