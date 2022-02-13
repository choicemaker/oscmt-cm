/*******************************************************************************
 * Copyright (c) 2015, 2020 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.ejb;

import com.choicemaker.cm.batch.ejb.BatchJobJPA;

/**
 * Java Persistence API (JPA) for BatchJob beans.<br/>
 * Prefixes:
 * <ul>
 * <li>JPQL -- Java Persistence Query Language</li>
 * <li>QN -- Query Name</li>
 * <li>CN -- Column Name</li>
 * </ul>
 * 
 * @author rphall
 */
public interface OabaJobJPA extends BatchJobJPA {

	/**
	 * Value of the discriminator column used to mark BatchJob types (and not
	 * sub-types)
	 */
	String DISCRIMINATOR_VALUE = "OABA";

	/** Name of the query that finds all persistent batch job instances */
	String QN_OABAJOB_FIND_ALL = "oabaJobFindAll";

	/** JPQL used to implement {@link #QN_OABAJOB_FIND_ALL} */
	String JPQL_OABAJOB_FIND_ALL = "Select job from OabaJobEntity job order by job.id";

}
