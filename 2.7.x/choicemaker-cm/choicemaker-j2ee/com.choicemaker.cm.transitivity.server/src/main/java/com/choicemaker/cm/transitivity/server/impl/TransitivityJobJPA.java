/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.server.impl;

import com.choicemaker.cm.oaba.server.impl.OabaJobJPA;

/**
 * Java Persistence API (JPA) for TransitivityJob beans.<br/>
 * Prefixes:
 * <ul>
 * <li>JPQL -- Java Persistence Query Language</li>
 * <li>CN -- Column Name</li>
 * <li>QN -- Query Name</li>
 * <li>PN -- Parameter Name</li>
 * </ul>
 * 
 * @author rphall
 *
 */
public interface TransitivityJobJPA extends OabaJobJPA {

	/** Hides {@link OabaJobJPA#DISCRIMINATOR_VALUE} */
	String DISCRIMINATOR_VALUE = "TRANSITIVITY";

	/**
	 * Name of the query that finds all persistent transitivity job instances
	 */
	String QN_TRANSITIVITY_FIND_ALL = "transitivityFindAll";

	/** JPQL used to implement {@link #QN_TRANSITIVITY_FIND_ALL} */
	String JPQL_TRANSITIVITY_FIND_ALL =
		"Select job from TransitivityJobEntity job";

	/**
	 * Name of the query that finds all persistent transitivity jobs that have a
	 * given (persistent) batch job as their parent
	 */
	String QN_TRANSITIVITY_FIND_ALL_BY_PARENT_ID =
		"transitivityFindAllByParentId";

	/** JPQL used to implement {@link #QN_TRANSITIVITY_FIND_ALL_BY_PARENT_ID} */
	String JPQL_TRANSITIVITY_FIND_ALL_BY_PARENT_ID =
		"Select job from TransitivityJobEntity job where job.bparentId = :bparentId";

	/**
	 * Name of the parameter used to specify the parent-id parameter of
	 * {@link #QN_TRANSITIVITY_FIND_ALL_BY_PARENT_ID}
	 */
	String PN_TRANSITIVITY_FIND_ALL_BY_PARENT_ID_BPARENTID = "bparentId";

}
