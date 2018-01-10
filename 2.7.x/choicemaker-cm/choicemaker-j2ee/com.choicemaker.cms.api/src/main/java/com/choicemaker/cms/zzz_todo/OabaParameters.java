/*
 * Copyright (c) 2001, 2009 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cms.zzz_todo;

import java.io.Serializable;

import com.choicemaker.cm.args.OabaLinkageType;
import com.choicemaker.cm.args.PersistentObject;
import com.choicemaker.cms.api.AbaParameters;

/**
 * @author rphall
 */
public interface OabaParameters extends AbaParameters, PersistentObject, Serializable {

//	String DEFAULT_EJB_REF_NAME = "ejb/OabaParameters";
//	String DEFAULT_JNDI_COMP_NAME = "java:comp/env/" + DEFAULT_EJB_REF_NAME;

	long NONPERSISTENT_ID = 0;

	boolean DEFAULT_QUERY_RS_IS_DEDUPLICATED = false;

	long getId();

	OabaLinkageType getOabaLinkageType();

	/** The query record source (and its id) is never null */
	long getQueryRsId();

	/** The query record source (and its type) is never null */
	String getQueryRsType();
	
	/**
	 * A flag indicating whether records from the query record source have
	 * already been duplicated.
	 */
	boolean isQueryRsDeduplicated();
	
	String getQueryRsDatabaseConfiguration();
	
	String getQueryToQueryBlockingConfiguration();

	Long getReferenceRsId();

	String getReferenceRsType();

}
