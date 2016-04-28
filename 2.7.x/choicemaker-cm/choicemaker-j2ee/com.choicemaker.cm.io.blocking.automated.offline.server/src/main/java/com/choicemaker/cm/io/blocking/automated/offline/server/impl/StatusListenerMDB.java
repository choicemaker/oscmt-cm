/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.blocking.automated.offline.server.impl;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import com.choicemaker.cm.batch.BatchProcessingNotification;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup",
				propertyValue = "java:/choicemaker/urm/jms/statusTopic"),
		@ActivationConfigProperty(propertyName = "destinationType",
				propertyValue = "javax.jms.Topic") })
public class StatusListenerMDB implements MessageListener, Serializable {

	private static final long serialVersionUID = 271L;
	private static final Logger log = Logger.getLogger(StatusListenerMDB.class
			.getName());
	private static final Logger jmsTrace = Logger.getLogger("jmstrace."
			+ StatusListenerMDB.class.getName());

	@Override
	public void onMessage(Message inMessage) {
		jmsTrace.info("Entering onMessage for " + this.getClass().getName());
		ObjectMessage msg = null;

		log.fine("starting onMessage...");
		try {
			if (inMessage instanceof ObjectMessage) {
				msg = (ObjectMessage) inMessage;
				Object o = msg.getObject();
				if (o instanceof BatchProcessingNotification) {
					BatchProcessingNotification bpn =
						(BatchProcessingNotification) o;
					log.info("received batch processing notification; " + bpn);
				} else {
					log.warning("received unexpected object message: " + o);
				}
			} else
				log.warning("received unexpected message: " + inMessage);
		} catch (Exception e) {
			String msg0 = throwableToString(e);
			log.severe(msg0);
		}

		log.fine("... finished onMessage");
		jmsTrace.info("Exiting onMessage for " + this.getClass().getName());
	}

	protected String throwableToString(Throwable throwable) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.println(throwable.toString());
		throwable.printStackTrace(pw);
		pw.close();
		return sw.toString();
	}

} // StatusListenerMDB

