/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.choicemaker.cm.core.base.MatchRecord2;
import com.choicemaker.util.Precondition;
import com.choicemaker.util.UniqueSequence;

/**
 * This object represents a subgraph of records that are related to each other,
 * typically through match or hold relationships that meet some connectivity
 * criteria such as simply connected, bi-connected or fully connected.
 *
 * @author pcheung
 */
public class CompositeEntity implements INode<Integer> {

	/**
	 * This method recursively collects all nodes reachable from the given node.
	 * The method checks that preconditions are satisfied.
	 */
	public static <T extends Comparable<?>> void getAllAccessibleNodes(
			CompositeEntity ce, Set<INode<?>> seenNodes, INode<?> currentNode) {
		Precondition.assertNonNullArgument(ce);
		Precondition.assertNonNullArgument(seenNodes);
		Precondition.assertNonNullArgument(currentNode);
		getAllAccessibleNodesInternal(ce, seenNodes, currentNode);
	}

	/** No checks that preconditions are satisfied */
	protected static <T extends Comparable<?>> void getAllAccessibleNodesInternal(
			CompositeEntity ce, Set<INode<?>> seenNodes, INode<?> currentNode) {
		if (!seenNodes.contains(currentNode)) {
			seenNodes.add(currentNode);
			List<INode<?>> al = ce.getAdjacency(currentNode);
			for (int i = 0; i < al.size(); i++) {
				INode<?> node = al.get(i);
				getAllAccessibleNodesInternal(ce, seenNodes, node);
			}
		}
	}

	private Integer marking;
	private final Integer id;
	private TreeMap<INode<?>, INode<?>> nodes = new TreeMap<>();

	// a list of edges in this graph
	private List<Link<?>> links = new LinkedList<>();

	// a mapping of node to adjacency list of INode
	private Map<INode<?>, List<INode<?>>> adjacencyMap = new HashMap<>();

	/**
	 * This constructor creates a default id.
	 */
	public CompositeEntity() {
		this(UniqueSequence.getInstance().getNextInteger());
	}

	/**
	 * This constructor specifies an id.
	 */
	public CompositeEntity(Integer id) {
		this.id = id;
	}

	/**
	 * This method adds a MatchRecord2 to this graph.
	 */
	public <T extends Comparable<T>> void addMatchRecord(
			final MatchRecord2<T> mr) {
		// first, add the ids to the nodes set
		final T c1 = mr.getRecordID1();
		Entity<T> ent = new Entity<T>(c1, INode.STAGE_TYPE);
		@SuppressWarnings("unchecked")
		Entity<T> ent1 = (Entity<T>) nodes.get(ent);
		if (ent1 == null) {
			nodes.put(ent, ent);
			ent1 = ent;
		}

		final T c2 = mr.getRecordID2();
		ent = new Entity<T>(c2, mr.getRecord2Role().getCharSymbol());
		@SuppressWarnings("unchecked")
		Entity<T> ent2 = (Entity<T>) nodes.get(ent);
		if (ent2 == null) {
			nodes.put(ent, ent);
			ent2 = ent;
		}

		// second, add them to each other's adjacency lists
		List<INode<?>> l = adjacencyMap.get(ent1);
		if (l == null) {
			l = new ArrayList<INode<?>>(2);
			l.add(ent2);
			adjacencyMap.put(ent1, l);
		} else {
			l.add(ent2);
		}

		l = adjacencyMap.get(ent2);
		if (l == null) {
			l = new ArrayList<>(2);
			l.add(ent1);
			adjacencyMap.put(ent2, l);
		} else {
			l.add(ent1);
		}

		// third, add a link
		List<MatchRecord2<T>> ml = new ArrayList<>(1);
		ml.add(mr);
		Link<T> link = new Link<T>(ent1, ent2, ml);
		links.add(link);

	}

	/**
	 * This method adds a node to the list of children. Adjacency list and Link
	 * are not specified.
	 *
	 * @param node
	 *            - a new node on this graph
	 */
	public void addNode(INode<?> node) {
		if (!nodes.containsKey(node))
			nodes.put(node, node);
	}

	/**
	 * This returns the first node (smallest node) in the tree.
	 */
	public INode<?> getFirstNode() {
		return nodes.firstKey();
	}

	/**
	 * This method adds a Link to this graph.
	 */
	public void addLink(Link<?> link) {
		INode<?> node1 = link.getNode1();
		INode<?> node2 = link.getNode2();

		if (!nodes.containsKey(node1)) {
			nodes.put(node1, node1);
		}

		if (!nodes.containsKey(node2)) {
			nodes.put(node2, node2);
		}

		// second, add them to each other's adjacency lists
		List<INode<?>> l = adjacencyMap.get(node1);
		if (l == null) {
			l = new ArrayList<>(2);
			l.add(node2);
			adjacencyMap.put(node1, l);
		} else {
			l.add(node2);
		}

		l = adjacencyMap.get(node2);
		if (l == null) {
			l = new ArrayList<>(2);
			l.add(node1);
			adjacencyMap.put(node2, l);
		} else {
			l.add(node1);
		}

		// third, add a link
		links.add(link);

	}

	@Override
	public Integer getNodeId() {
		return id;
	}

	@Override
	public boolean hasChildren() {
		return true;
	}

	@Override
	public List<INode<?>> getChildren() {
		return new ArrayList<INode<?>>(nodes.values());
	}

	/**
	 * This method returns all the links.
	 *
	 * @return List of Link
	 */
	public List<Link<?>> getAllLinks() {
		return links;
	}

	/**
	 * This method returns the adjacency list of the give node.
	 *
	 * @param node
	 * @return List of INode.
	 */
	public List<INode<?>> getAdjacency(INode<?> node) {
		return adjacencyMap.get(node);
	}

	@Override
	public int compareTo(INode<Integer> o) {
		if (o instanceof CompositeEntity) {
			CompositeEntity ce = (CompositeEntity) o;
			return this.id.compareTo(ce.id);
		} else {
			return -1;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((adjacencyMap == null) ? 0 : adjacencyMap.hashCode());
		// result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((links == null) ? 0 : links.hashCode());
		result = prime * result + ((marking == null) ? 0 : marking.hashCode());
		result = prime * result + ((nodes == null) ? 0 : nodes.hashCode());
		return result;
	}

	@Override
	public boolean sameId(INode<Integer> other) {
		if (this == other) {
			return true;
		}
		if (other == null) {
			return false;
		}
		if (!(other instanceof Entity)) {
			return false;
		}
		if (getNodeId() == null) {
			if (other.getNodeId() != null) {
				return false;
			}
		} else if (!getNodeId().equals(other.getNodeId())) {
			return false;
		}
		return true;
	}

	public boolean equalsIgnoreId(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CompositeEntity other = (CompositeEntity) obj;
		if (adjacencyMap == null) {
			if (other.adjacencyMap != null)
				return false;
		} else if (!adjacencyMap.equals(other.adjacencyMap))
			return false;
		// if (id == null) {
		// if (other.id != null)
		// return false;
		// } else if (!id.equals(other.id))
		// return false;
		if (links == null) {
			if (other.links != null)
				return false;
		} else if (!links.equals(other.links))
			return false;
		if (marking == null) {
			if (other.marking != null)
				return false;
		} else if (!marking.equals(other.marking))
			return false;
		if (nodes == null) {
			if (other.nodes != null)
				return false;
		} else if (!nodes.equals(other.nodes))
			return false;
		return true;
	}

	public boolean equalsAndSameId(Object obj) {
		boolean retVal = this.equalsIgnoreId(obj);
		if (retVal) {
			assert obj instanceof CompositeEntity;
			CompositeEntity other = (CompositeEntity) obj;
			retVal = sameId(other);
		}
		return retVal;
	}

	/** Same as equalsIgnoreId(Object) */
	@Override
	public boolean equals(Object obj) {
		boolean retVal = equalsIgnoreId(obj);
		return retVal;
	}

	@Override
	public void mark(Integer I) {
		marking = I;
	}

	@Override
	public Integer getMarking() {
		return marking;
	}

	@Override
	public char getType() {
		return INode.COMPOSITE_TYPE;
	}

	@Override
	public String toString() {
		return "CompositeEntity[" + id + "]";
	}

}
