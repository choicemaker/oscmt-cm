/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.ejb;

import com.choicemaker.cm.oaba.ejb.AbstractParametersJPA;
import com.choicemaker.cm.transitivity.pojo.TransitivityParametersBean;

public interface TransitivityParametersJPA extends AbstractParametersJPA {

	String DV_TRANS = TransitivityParametersBean.DV_TRANS;

	/**
	 * Name of the query that finds all persistent Transitivity instances
	 */
	String QN_TRANSPARAMETERS_FIND_ALL = "transParametersFindAll";

	/** JPQL used to implement {@link #QN_TRANSPARAMETERS_FIND_ALL} */
	String JPQL_TRANSPARAMETERS_FIND_ALL =
		"Select p from TransitivityParametersEntity p";

}
