/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.base;

import java.io.Serializable;

import com.choicemaker.client.api.IGraphProperty;
import com.choicemaker.util.Precondition;


/**
 * Criteria for identifying a set of records as linked together and denoting the same physical entity.
 * Criteria includes graph property type and the flag of the query record containment. 
 * <p>  
 *
 * @author emoussikaev
 * @see
 */
public class LinkCriteria implements Serializable {

	/* As of 2010-03-10 */
	static final long serialVersionUID = 4915885639786038386L;

	protected IGraphProperty graphPropType;
	protected boolean mustIncludeQuery;
		
	public LinkCriteria(IGraphProperty t, boolean mic) {
		super();
		Precondition.assertNonNullArgument("null graph property", t);
		this.graphPropType = t;
		this.mustIncludeQuery = mic;
	}

	public boolean isMustIncludeQuery() {
		return mustIncludeQuery;
	}


	public void setMustIncludeQuery(boolean b) {
		mustIncludeQuery = b;
	}

	public IGraphProperty getGraphPropType() {
		return graphPropType;
	}

	public void setGraphPropType(IGraphProperty type) {
		graphPropType = type;
	}

}
