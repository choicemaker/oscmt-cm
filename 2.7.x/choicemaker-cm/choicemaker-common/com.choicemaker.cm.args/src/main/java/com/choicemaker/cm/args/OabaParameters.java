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
package com.choicemaker.cm.args;

import java.io.Serializable;

/**
 * @author rphall
 */
public interface OabaParameters extends PersistentObject, Serializable {

	String DEFAULT_EJB_REF_NAME = "ejb/OabaParameters";
	String DEFAULT_JNDI_COMP_NAME = "java:comp/env/" + DEFAULT_EJB_REF_NAME;

	long NONPERSISTENT_ID = 0;

	boolean DEFAULT_QUERY_RS_IS_DEDUPLICATED = false;

	@Override
	long getId();

	String getModelConfigurationName();

	OabaLinkageType getOabaLinkageType();

	float getLowThreshold();

	float getHighThreshold();

	String getBlockingConfiguration();

	/** @return The query record source id, never null */
	long getQueryRsId();

	/** @return The query record source type, never null */
	String getQueryRsType();

	/**
	 * @return A flag indicating whether records from the query record source have
	 * already been duplicated.
	 */
	boolean isQueryRsDeduplicated();

	String getQueryRsDatabaseConfiguration();

	String getQueryToQueryBlockingConfiguration();

	/** @return The reference record source, may be null */
	Long getReferenceRsId();

	/** @return The reference record source, may be null */
	String getReferenceRsType();

	String getReferenceRsDatabaseConfiguration();

	String getQueryToReferenceBlockingConfiguration();

}
