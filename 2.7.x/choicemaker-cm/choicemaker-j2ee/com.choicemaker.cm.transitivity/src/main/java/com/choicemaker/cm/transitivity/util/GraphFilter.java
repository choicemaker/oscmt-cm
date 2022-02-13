/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.util;

import java.util.List;

import com.choicemaker.cm.core.base.MatchRecord2;
import com.choicemaker.cm.transitivity.core.CompositeEntity;
import com.choicemaker.cm.transitivity.core.EdgeProperty;
import com.choicemaker.cm.transitivity.core.Link;
import com.choicemaker.util.UniqueSequence;

/**
 * This object takes in a graph (CompositeEntity) and an EdgeProperty and
 * returns a graph satisfying that EdgeProperty.
 * 
 * @author pcheung
 *
 *         ChoiceMaker Technologies, Inc.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class GraphFilter {

	private static GraphFilter filter = new GraphFilter();

	private GraphFilter() {
	}

	public static GraphFilter getInstance() {
		return filter;
	}

	/**
	 * This method returns a graph where the edges satisfy the given
	 * EdgeProperty.
	 * 
	 * @param ce
	 *            - input graph
	 * @param ep
	 *            - property which the edges need to satisfy
	 * @return CompositeEnity - new graph.
	 */
	public CompositeEntity filter(
			CompositeEntity ce, EdgeProperty ep) {
		UniqueSequence seq = UniqueSequence.getInstance();
		CompositeEntity ret = new CompositeEntity(seq.getNextInteger());

		// get all the links
		List links = ce.getAllLinks();
		for (int i = 0; i < links.size(); i++) {
			Link link = (Link) links.get(i);
			List mrs = link.getLinkDefinition();
			for (int j = 0; j < mrs.size(); j++) {
				MatchRecord2 mr = (MatchRecord2) mrs.get(j);
				if (ep.hasProperty(mr)) {
					ret.addMatchRecord(mr);
				}
			}
		}

		return ret;
	}

}
