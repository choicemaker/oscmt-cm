/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.core;

import java.util.List;

import com.choicemaker.cm.core.base.RECORD_SOURCE_ROLE;

/**
 * This represents a node in the transitivity graph.  A node could represent an
 * Entity or a CompositeEntity.
 * 
 * @author pcheung
 *
 * ChoiceMaker Technologies Inc.
 */
public interface INode<T extends Comparable<T>> extends Comparable<INode<T>> {
	
	public static final char STAGE_TYPE = RECORD_SOURCE_ROLE.STAGING.getCharSymbol(); //'S'
	public static final char MASTER_TYPE = RECORD_SOURCE_ROLE.MASTER.getCharSymbol(); //'D'
	public static final char COMPOSIT_TYPE = 'C';

	/** This returns the id of this node */
	public T getNodeId ();
	
	
	/** This returns the type of this node.  It is either a COMPOSITE_TYPE,
	 * STAGE_TYPE or MASTER_TYPE.
	 * 
	 * All CompositeEntity objects have type = COMPOSITE_TYPE.
	 * Entity objects created with staging ID's have type = STAGE_TYPE.
	 * Entity objects created with master ID's have type = MASTER_TYPE.
	 * 
	 * @return char
	 */
	public char getType ();
	
	
	/** This method marks the node.
	 * 
	 * @param I - The Integer marking
	 */
	public void mark (Integer I);
	
	
	/** This method returns the marking of this node. 
	 */
	public Integer getMarking ();
	
	
	/** This returns true if this node is a CompositeEntity.
	 */
	public boolean hasChildren ();
	
	/** This returns a list of children of this node or a list of 0 elements.
	 */
	public List<INode<T>> getChildren ();
	
}
