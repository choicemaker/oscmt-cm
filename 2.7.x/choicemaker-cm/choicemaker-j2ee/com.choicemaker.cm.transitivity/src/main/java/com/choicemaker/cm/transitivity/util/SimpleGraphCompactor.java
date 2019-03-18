/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import com.choicemaker.cm.args.TransitivityException;
import com.choicemaker.cm.core.base.MatchRecord2;
import com.choicemaker.cm.transitivity.core.CompositeEntity;
import com.choicemaker.cm.transitivity.core.GraphCompactor;
import com.choicemaker.cm.transitivity.core.INode;
import com.choicemaker.cm.transitivity.core.Link;

/**
 * This simple graph compactor merges all nodes with the same marking into a
 * single node and related Links into a single Link.
 * 
 * @author pcheung
 *
 *         ChoiceMaker Technologies, Inc.
 */
@SuppressWarnings({
	"rawtypes", "unchecked" })
public class SimpleGraphCompactor implements
		GraphCompactor {

	// this is a map of marking to compacted nodes
	private HashMap<Integer, CompositeEntity> compactedNodes;

	// this is mapping between a pair of INodes and Link
	private HashMap<CompositePair<?>, Link<?>> compactedLinks;

	/**
	 * This compact method does the following:
	 * 
	 * 1. Build a map of nodes to marking. 2. Walk through all the links and use
	 * this logic: A. If neither node has a marking, add this link to return
	 * graph B. If both nodes have the same marking, then this link belongs to a
	 * compacted node. C. If one node is not marked, then replace the marked
	 * node with a compacted node. D. If they have different marking, then this
	 * link is between two compacted nodes.
	 * 
	 */
	@Override
	public CompositeEntity compact(CompositeEntity ce)
			throws TransitivityException {
		CompositeEntity ret = new CompositeEntity(ce.getNodeId());

		// this is a map of marking to compacted nodes
		compactedNodes = new HashMap<>();

		compactedLinks = new HashMap<>();

		TreeSet<Integer> alreadyAdded = new TreeSet<>();

		// walk through all the links.
		List<Link<?>> links = ce.getAllLinks();
		for (int i = 0; i < links.size(); i++) {
			Link<?> link = links.get(i);
			INode node1 = link.getNode1();
			INode node2 = link.getNode2();

			Integer marking1 = node1.getMarking();
			Integer marking2 = node2.getMarking();

			if (marking1 == null && marking2 == null) {
				// case A
				ret.addLink(link);
			} else if (marking1 == null) {
				// case C
				CompositeEntity compacted = getFromCompactedNodes(marking2);

				CompositePair pair = new CompositePair(node1.getNodeId(), marking2);
				Link compLink = compactedLinks.get(pair);

				Link newLink = null;
				if (compLink == null) {
					newLink =
						new Link(node1, compacted, link.getLinkDefinition());
					compactedLinks.put(pair, newLink);
					ret.addLink(newLink);
				} else {
					List<MatchRecord2<?>> mrs = new LinkedList<>();
					mrs.addAll(compLink.getLinkDefinition());
					mrs.addAll(link.getLinkDefinition());
					newLink = new Link(node1, compacted, mrs);
					compactedLinks.put(pair, newLink);
				}

			} else if (marking2 == null) {
				// case C
				CompositeEntity compacted = getFromCompactedNodes(marking1);

				CompositePair pair = new CompositePair(node2.getNodeId(), marking1);
				Link compLink = compactedLinks.get(pair);

				Link newLink = null;
				if (compLink == null) {
					newLink =
						new Link(node2, compacted, link.getLinkDefinition());
					compactedLinks.put(pair, newLink);
					ret.addLink(newLink);
				} else {
					List<MatchRecord2<?>> mrs = new LinkedList<>();
					mrs.addAll(compLink.getLinkDefinition());
					mrs.addAll(link.getLinkDefinition());
					newLink = new Link(node2, compacted, mrs);
					compactedLinks.put(pair, newLink);
				}

			} else if (marking1.equals(marking2)) {
				// case B
				CompositeEntity compacted = getFromCompactedNodes(marking1);

				compacted.addLink(link);

				if (!alreadyAdded.contains(marking1)) {
					ret.addNode(compacted);
					alreadyAdded.add(marking1);
				}

			} else {
				// case D
				CompositeEntity compacted1 = getFromCompactedNodes(marking1);
				CompositeEntity compacted2 = getFromCompactedNodes(marking2);

				CompositePair cp = null;
				if (marking1.compareTo(marking2) < 0)
					cp = new CompositePair(marking1, marking2);
				else
					cp = new CompositePair(marking2, marking1);
				Link compLink = compactedLinks.get(cp);

				Link newLink = null;
				if (compLink == null) {
					newLink =
						new Link(compacted1, compacted2,
								link.getLinkDefinition());
					compactedLinks.put(cp, newLink);
					ret.addLink(newLink);
				} else {
					List<MatchRecord2<?>> mrs = new LinkedList<>();
					mrs.addAll(compLink.getLinkDefinition());
					mrs.addAll(link.getLinkDefinition());
					newLink = new Link(compacted1, compacted2, mrs);
					compactedLinks.put(cp, newLink);
					compLink = null;
				}

			} // end of the different cases

		}

		return ret;
	}

	private CompositeEntity getFromCompactedNodes(Integer I) {
		CompositeEntity compacted = compactedNodes.get(I);

		if (compacted == null) {
			compacted = new CompositeEntity(I);
			// (UniqueSequence.getInstance().getNextInteger());

			compactedNodes.put(I, compacted);
		}
		return compacted;
	}

	/**
	 * Internal object that tracks links between two INodes.
	 * 
	 * @author pcheung
	 *
	 *         ChoiceMaker Technologies, Inc.
	 */
	private class CompositePair<E extends Comparable<E>> implements
			Comparable<CompositePair<E>> {

		private E id1;
		private E id2;

		private CompositePair(E id1, E id2) {
			this.id1 = id1;
			this.id2 = id2;
		}

		@Override
		public int compareTo(CompositePair<E> cp) {
			int i1 = this.id1.compareTo(cp.id1);
			int i2 = this.id2.compareTo(cp.id2);

			if (i1 == 0 && i2 == 0) {
				return 0;
			} else if (i1 != 0) {
				return i1;
			} else {
				return i2;
			}
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((id1 == null) ? 0 : id1.hashCode());
			result = prime * result + ((id2 == null) ? 0 : id2.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			CompositePair other = (CompositePair) obj;
			if (!getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (id1 == null) {
				if (other.id1 != null) {
					return false;
				}
			} else if (!id1.equals(other.id1)) {
				return false;
			}
			if (id2 == null) {
				if (other.id2 != null) {
					return false;
				}
			} else if (!id2.equals(other.id2)) {
				return false;
			}
			return true;
		}

		private SimpleGraphCompactor getOuterType() {
			return SimpleGraphCompactor.this;
		}

		@Override
		public String toString() {
			return "CompositePair [id1=" + id1 + ", id2=" + id2 + "]";
		}

	}

}
