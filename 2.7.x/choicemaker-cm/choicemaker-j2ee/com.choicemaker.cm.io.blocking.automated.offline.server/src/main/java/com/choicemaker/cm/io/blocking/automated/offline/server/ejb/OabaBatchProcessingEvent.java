/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.blocking.automated.offline.server.ejb;

import com.choicemaker.cm.batch.api.BatchJobProcessingEvent;
import com.choicemaker.cm.io.blocking.automated.offline.core.OabaProcessing;

public interface OabaBatchProcessingEvent extends BatchJobProcessingEvent,
		OabaProcessing {

	String DEFAULT_EJB_REF_NAME = "ejb/OabaBatchProcessingEvent";
	String DEFAULT_JNDI_COMP_NAME = "java:comp/env/" + DEFAULT_EJB_REF_NAME;

}
