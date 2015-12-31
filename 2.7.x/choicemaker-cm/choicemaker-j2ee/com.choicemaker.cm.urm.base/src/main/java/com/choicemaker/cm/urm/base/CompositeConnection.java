/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.base;

/**
 * A record connection between two records at least one of which is a composite record.
 * <p>
 *
 * @author emoussikaev
 * @see
 */
public class CompositeConnection extends RecordConnection {

	/** As of 2010-11-12 */
	private static final long serialVersionUID = 1468508143179500532L;

	protected RecordConnection[]	inwardConnections;

	public CompositeConnection() {
		super();
	}

	public CompositeConnection(MatchScore score, int i1, int i2, RecordConnection[]	inwardLinks) {
		super(score, i1, i2);
		this.inwardConnections = inwardLinks;
	}

	public RecordConnection[] getInwardConnections() {
		return inwardConnections;
	}

	public void setInwardConnections(RecordConnection[] links) {
		inwardConnections = links;
	}
}
