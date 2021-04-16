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
package com.choicemaker.cms.ejb;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;

import com.choicemaker.cm.batch.api.WorkflowListener;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup",
				propertyValue = "java:/choicemaker/urm/jms/transStatusTopic"),
		@ActivationConfigProperty(propertyName = "destinationType",
				propertyValue = "javax.jms.Topic") })
public class UrmTransitivityListenerMDB extends AbstractStatusListener {

	private static final long serialVersionUID = 271L;

	@EJB(beanName = "BatchMatchingBean")
	protected WorkflowListener bmaBean;

	@Override
	protected WorkflowListener getWorkFlowManager() {
		return bmaBean;
	}

}
