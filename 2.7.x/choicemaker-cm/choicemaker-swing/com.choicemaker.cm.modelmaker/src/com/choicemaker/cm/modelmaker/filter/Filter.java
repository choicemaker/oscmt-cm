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
package com.choicemaker.cm.modelmaker.filter;

import com.choicemaker.cm.core.MutableMarkedRecordPair;

/**
 * Description
 * 
 * @author  Martin Buechi
 * @version $Revision: 1.2 $ $Date: 2010/03/29 12:22:17 $
 */
public interface Filter {
	/**
	 * Method satisfy.
	 * @param matchProbability
	 * @param decision
	 * @return boolean
	 */
	boolean satisfy(MutableMarkedRecordPair pair);
	
	void resetLimiters();
}