/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.api;

import com.choicemaker.cm.batch.api.BatchProcessingEvent;
import com.choicemaker.cm.transitivity.core.TransitivityProcessingConstants;

public interface TransitivityProcessingEvent
		extends BatchProcessingEvent, TransitivityProcessingConstants {

	String DEFAULT_EJB_REF_NAME = "ejb/TransitivityBatchProcessingEvent";
	String DEFAULT_JNDI_COMP_NAME = "java:comp/env/" + DEFAULT_EJB_REF_NAME;

}
