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
package com.choicemaker.cms.urm_tmp.impl;

import java.io.Serializable;
import java.util.logging.Logger;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import com.choicemaker.cm.batch.api.BatchProcessingNotification;
import com.choicemaker.cms.api.WorkFlowManager;

/**
 * Base class for UrmOabaListenerMDB and UrmTransitivityListenerMDB
 */
public abstract class AbstractStatusListener implements MessageListener,
		Serializable {

	private static final long serialVersionUID = 271L;
	private static final Logger log = Logger
			.getLogger(AbstractStatusListener.class.getName());
	private static final Logger jmsTrace = Logger.getLogger("jmstrace."
			+ AbstractStatusListener.class.getName());

	protected abstract WorkFlowManager getWorkFlowManager();

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
					getWorkFlowManager().jobUpdated(bpn);

				} else {
					log.warning("received unexpected object message: " + o);
				}
			} else
				log.warning("received unexpected message: " + inMessage);
		} catch (Exception e) {
			log.severe(e.toString());
		}

		log.fine("... finished onMessage");
		jmsTrace.info("Exiting onMessage for " + this.getClass().getName());
		return;
	}

}
