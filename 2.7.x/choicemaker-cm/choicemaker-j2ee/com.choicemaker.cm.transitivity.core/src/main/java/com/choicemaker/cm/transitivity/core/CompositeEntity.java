/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
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
import java.util.TreeMap;

import com.choicemaker.cm.core.base.MatchRecord2;

/**
 * This object represents a subgraph of records that are related to each other,
 * typically through match or hold relationships that meet some connectivity
 * criteria such as simply connected, bi-connected or fully connected.
 * 
 * @author pcheung
 */
// @SuppressWarnings({
// "rawtypes", "unchecked" })
public class CompositeEntity<T extends Comparable<T>> implements INode<T> {

	private Integer marking;

	private T id;

	private TreeMap<INode<T>, INode<T>> nodes = new TreeMap<>();

	// a list of edges in this graph
	private List<Link<T>> links = new LinkedList<>();

	// a mapping of node to adjacency list of INode
	private Map<INode<T>, List<INode<T>>> adjacencyMap = new HashMap<>();

	/**
	 * This constructor takes in an id.
	 */
	public CompositeEntity(T id) {
		this.id = id;
	}

	/**
	 * This method adds a MatchRecord2 to this graph.
	 */
	public void addMatchRecord(MatchRecord2<T> mr) {
		// first, add the ids to the nodes set
		final T c1 = mr.getRecordID1();
		Entity<T> ent = new Entity<T>(c1, INode.STAGE_TYPE);
		Entity<T> ent1 = (Entity<T>) nodes.get(ent);
		if (ent1 == null) {
			nodes.put(ent, ent);
			ent1 = ent;
		}

		final T c2 = mr.getRecordID2();
		ent = new Entity<T>(c2, mr.getRecord2Role().getCharSymbol());
		Entity<T> ent2 = (Entity<T>) nodes.get(ent);
		if (ent2 == null) {
			nodes.put(ent, ent);
			ent2 = ent;
		}

		// second, add them to each other's adjacency lists
		List<INode<T>> l = adjacencyMap.get(ent1);
		if (l == null) {
			l = new ArrayList<INode<T>>(2);
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
	public void addNode(INode<T> node) {
		if (!nodes.containsKey(node))
			nodes.put(node, node);
	}

	/**
	 * This returns the first node (smallest node) in the tree.
	 */
	public INode<T> getFirstNode() {
		return nodes.firstKey();
	}

	/**
	 * This method adds a Link to this graph.
	 */
	public void addLink(Link<T> link) {
		INode<T> node1 = link.getNode1();
		INode<T> node2 = link.getNode2();

		if (!nodes.containsKey(node1)) {
			nodes.put(node1, node1);
		}

		if (!nodes.containsKey(node2)) {
			nodes.put(node2, node2);
		}

		// second, add them to each other's adjacency lists
		List<INode<T>> l = adjacencyMap.get(node1);
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

	public T getNodeId() {
		return id;
	}

	public boolean hasChildren() {
		return true;
	}

	public List<INode<T>> getChildren() {
		return new ArrayList<>(nodes.values());
	}

	/**
	 * This method returns all the links.
	 * 
	 * @return List of Link
	 */
	public List<Link<T>> getAllLinks() {
		return links;
	}

	/**
	 * This method returns the adjacency list of the give node.
	 * 
	 * @param node
	 * @return List of INode.
	 */
	public List<INode<T>> getAdjacency(INode<T> node) {
		return adjacencyMap.get(node);
	}

	public int compareTo(INode<T> o) {
		if (o instanceof CompositeEntity) {
			CompositeEntity<T> ce = (CompositeEntity<T>) o;
			return this.id.compareTo(ce.id);
		} else {
			return -1;
		}
	}

	// public int hashCode() {
	// return id.hashCode();
	// }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((adjacencyMap == null) ? 0 : adjacencyMap.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((links == null) ? 0 : links.hashCode());
		result = prime * result + ((marking == null) ? 0 : marking.hashCode());
		result = prime * result + ((nodes == null) ? 0 : nodes.hashCode());
		return result;
	}

	// public boolean equals(Object o) {
	// if (o instanceof CompositeEntity) {
	// CompositeEntity<T> ce = (CompositeEntity<T>) o;
	// return this.id.equals(ce.id);
	// } else {
	// return false;
	// }
	// }
	//

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("unchecked")
		CompositeEntity<T> other = (CompositeEntity<T>) obj;
		if (adjacencyMap == null) {
			if (other.adjacencyMap != null)
				return false;
		} else if (!adjacencyMap.equals(other.adjacencyMap))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
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

	public void mark(Integer I) {
		marking = I;
	}

	public Integer getMarking() {
		return marking;
	}

	public char getType() {
		return INode.COMPOSIT_TYPE;
	}

}
