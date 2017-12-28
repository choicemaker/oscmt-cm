/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.base;



/**
 * A set of records connected by the match or hold relationship. Field <code>connections</code> provides
 * evaluation of the matching between those records.
 * <p>
 *
 * @author emoussikaev
 * @see
 */
public class ConnectedRecordSet<T extends Comparable<T>> extends CompositeRecord<T> {

	private static final long serialVersionUID = 1604807022706941623L;

	private IRecordConnection[] connections;

	public ConnectedRecordSet(T id, IRecord<T>[] r, IRecordConnection[] connections) {
		super(id,r);
		this.connections = connections;
	}

	public IRecordConnection[] getConnections() {
		return connections;
	}

	public void setConnections(RecordConnection[] links) {
		this.connections = links;
	}

	public void accept(IRecordVisitor ext){
		ext.visit(this);
	}
}
