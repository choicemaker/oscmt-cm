/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.core;

import com.choicemaker.cm.core.Decision;
import com.choicemaker.cm.io.blocking.automated.offline.data.MatchRecord2;

/**
 * This class checks to see if the edge is a match edge.
 * 
 * @author pcheung
 *
 * ChoiceMaker Technologies, Inc.
 */
@SuppressWarnings({"rawtypes" })
public class MatchEdgeProperty implements EdgeProperty {
	
	private static MatchEdgeProperty property;
	
	private MatchEdgeProperty () {
	}
	
	public static MatchEdgeProperty getInstance () {
		if (property == null) property = new MatchEdgeProperty ();
		return property;
	}
	

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.transitivity.core.EdgeProperty#hasProperty(com.choicemaker.cm.io.blocking.automated.offline.data.MatchRecord2)
	 */
	public boolean hasProperty(MatchRecord2 mr) {
		if (mr.getMatchType() == Decision.MATCH) return true;
		else return false;
	}

}
