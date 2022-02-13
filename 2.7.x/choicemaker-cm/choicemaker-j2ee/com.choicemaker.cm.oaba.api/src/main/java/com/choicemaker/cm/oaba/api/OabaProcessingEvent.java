/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.api;

import com.choicemaker.cm.batch.api.BatchProcessingEvent;
import com.choicemaker.cm.oaba.core.OabaProcessingConstants;

public interface OabaProcessingEvent
		extends BatchProcessingEvent, OabaProcessingConstants {

	String DEFAULT_EJB_REF_NAME = "ejb/OabaBatchProcessingEvent";
	String DEFAULT_JNDI_COMP_NAME = "java:comp/env/" + DEFAULT_EJB_REF_NAME;

}
