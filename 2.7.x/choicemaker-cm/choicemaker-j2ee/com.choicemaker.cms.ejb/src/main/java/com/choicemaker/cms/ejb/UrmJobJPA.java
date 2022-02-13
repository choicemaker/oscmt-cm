/*******************************************************************************
 * Copyright (c) 2003, 2018 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc  - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
package com.choicemaker.cms.ejb;

import com.choicemaker.cm.transitivity.ejb.TransitivityJobJPA;

/**
 * Java Persistence API (JPA) for UrmJob beans.<br/>
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
public interface UrmJobJPA extends TransitivityJobJPA {

	/** Hides {@link TransitivityJobJPA#DISCRIMINATOR_VALUE} */
	String DISCRIMINATOR_VALUE = "URM";

	/**
	 * Name of the query that finds all persistent URM job instances
	 */
	String QN_URM_FIND_ALL = "urmFindAll";

	/** JPQL used to implement {@link #QN_URM_FIND_ALL} */
	String JPQL_URM_FIND_ALL =
		"Select job from UrmJobEntity job order by job.id";

	/**
	 * Name of the query that finds all persistent batch jobs that are linked
	 * with a given URM job via their urmId field.
	 */
	String QN_URM_FIND_ALL_BY_URM_ID = "urmFindAllByUrmId";

	/** JPQL used to implement {@link #QN_URM_FIND_ALL_BY_URM_ID} */
	String JPQL_URM_FIND_ALL_BY_URM_ID =
		"Select job from BatchJobEntity job where job.urmId = :urmId";

	/**
	 * Name of the parameter used to specify the parent-id parameter of
	 * {@link #QN_URM_FIND_ALL_BY_URM_ID}
	 */
	String PN_URM_FIND_ALL_BY_URM_ID_URMID = "urmId";

}
