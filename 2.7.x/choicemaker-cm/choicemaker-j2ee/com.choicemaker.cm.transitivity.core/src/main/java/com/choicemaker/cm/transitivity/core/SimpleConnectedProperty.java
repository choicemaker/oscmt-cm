/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.core;

import java.util.HashSet;
import java.util.Set;



/**
 * This checks to see if the input graph is a simple connected graph.
 * 
 * @author pcheung
 *
 * ChoiceMaker Technologies, Inc.
 */
public class SimpleConnectedProperty implements SubGraphProperty {

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.transitivity.core.SubGraphProperty#hasProperty(com.choicemaker.cm.transitivity.core.CompositeEntity)
	 */
	@Override
	public boolean hasProperty(CompositeEntity ce) {
		int numChildren = ce.getChildren().size();
		INode<?> fNode = ce.getFirstNode();
		Set<INode<?>> seenNodes = new HashSet<>();

		CompositeEntity.getAllAccessibleNodes (ce, seenNodes, fNode);
		
		//System.out.println (numChildren + " " + seenNodes.size());

		if (numChildren == seenNodes.size()) return true;
		else return false;
	}

}
