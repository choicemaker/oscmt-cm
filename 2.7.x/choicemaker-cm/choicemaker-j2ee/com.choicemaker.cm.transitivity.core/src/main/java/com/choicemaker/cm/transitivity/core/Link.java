/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.core;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.choicemaker.cm.core.base.MatchRecord2;

/**
 * This represents a link between two nodes on a graph.
 * 
 * @author pcheung
 *
 * ChoiceMaker Technologies Inc.
 */
public class Link<T extends Comparable<T>> {

	private INode<T> node1;
	private INode<T> node2;
	
	/**
	 * This list contains all the MatchRecord2 objects that make up this Link. 
	 */
	private List<MatchRecord2<T>> matchRecords;


	/** This constructor takes in node1, node2, and a list of MatchRecord2.
	 * 
	 * @param node1
	 * @param node2
	 * @param mrs
	 */
	public Link (INode<T> node1, INode<T> node2, List<MatchRecord2<T>> mrs) {
		this.node1 = node1;
		this.node2 = node2;
		this.matchRecords = new LinkedList<>();
		this.matchRecords.addAll(mrs);
	}
	

	/** This returns the first node of this link.
	 * 
	 * @return INode
	 */
	public INode<T> getNode1 () {
		return node1;
	}
	
	
	/** This returns the second node of this link.
	 * 
	 * @return INode
	 */
	public INode<T> getNode2 () {
		return node2;
	}
	
	
	/** This returns a list of MatchRecord2 that defines this link.
	 * 
	 * @return ArrayList of MatchRecord2
	 */
	public List<MatchRecord2<T>> getLinkDefinition () {
		return Collections.unmodifiableList(matchRecords);
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((matchRecords == null) ? 0 : matchRecords.hashCode());
		result = prime * result + ((node1 == null) ? 0 : node1.hashCode());
		result = prime * result + ((node2 == null) ? 0 : node2.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		Link other = (Link) obj;
		if (matchRecords == null) {
			if (other.matchRecords != null)
				return false;
		} else if (!matchRecords.equals(other.matchRecords))
			return false;
		if (node1 == null) {
			if (other.node1 != null)
				return false;
		} else if (!node1.equals(other.node1))
			return false;
		if (node2 == null) {
			if (other.node2 != null)
				return false;
		} else if (!node2.equals(other.node2))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "Link [node1=" + node1 + ", node2=" + node2 + "]";
	}

}
