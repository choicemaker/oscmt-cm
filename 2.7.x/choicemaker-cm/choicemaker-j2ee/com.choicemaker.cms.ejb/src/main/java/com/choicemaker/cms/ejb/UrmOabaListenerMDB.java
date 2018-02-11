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

import com.choicemaker.cms.api.WorkFlowManager;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup",
				propertyValue = "java:/choicemaker/urm/jms/statusTopic"),
		@ActivationConfigProperty(propertyName = "destinationType",
				propertyValue = "javax.jms.Topic") })
public class UrmOabaListenerMDB extends AbstractStatusListener {

	private static final long serialVersionUID = 271L;

	@EJB(beanName="BatchMatchingBean")
	protected WorkFlowManager bmaBean;

	@Override
	protected WorkFlowManager getWorkFlowManager() {
		return bmaBean;
	}

}
