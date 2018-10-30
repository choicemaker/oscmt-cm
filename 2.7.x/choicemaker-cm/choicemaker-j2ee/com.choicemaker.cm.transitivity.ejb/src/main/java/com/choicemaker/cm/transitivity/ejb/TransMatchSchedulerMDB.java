/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.ejb;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * This MDB delegates message handling to a singleton EJB, which tracks certain
 * transitivity-related data between invocations of <code>onMessage</code>.
 * 
 * @author rphall
 */
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "maxSession",
				propertyValue = "1"), // Singleton (JBoss only)
		@ActivationConfigProperty(propertyName = "destinationLookup",
				propertyValue = "java:/choicemaker/urm/jms/transMatchSchedulerQueue"),
		@ActivationConfigProperty(propertyName = "destinationType",
				propertyValue = "javax.jms.Queue") })
//@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class TransMatchSchedulerMDB implements MessageListener {

	@EJB
	private TransMatchSchedulerSingleton singleton;

	@Override
	public void onMessage(Message message) {
		singleton.onMessage(message);
	}

}
