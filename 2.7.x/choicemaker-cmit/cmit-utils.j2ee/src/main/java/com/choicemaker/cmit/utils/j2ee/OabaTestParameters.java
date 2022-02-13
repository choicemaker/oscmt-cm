/*******************************************************************************
 * Copyright (c) 2003, 2018 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc  - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
package com.choicemaker.cmit.utils.j2ee;

import java.util.logging.Logger;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import com.choicemaker.cm.args.OabaLinkageType;
import com.choicemaker.cm.batch.api.OperationalPropertyController;
import com.choicemaker.cm.batch.api.EventPersistenceManager;
import com.choicemaker.cm.oaba.api.OabaJobManager;
import com.choicemaker.cm.oaba.api.OabaParametersController;
import com.choicemaker.cm.oaba.api.OabaService;
import com.choicemaker.cm.oaba.api.OabaSettingsController;
import com.choicemaker.cm.oaba.api.RecordIdController;
import com.choicemaker.cm.oaba.api.RecordSourceController;
import com.choicemaker.cm.oaba.api.ServerConfigurationController;
import com.choicemaker.e2.ejb.EjbPlatform;

public interface OabaTestParameters {

	Queue getBlockQueue();

	Queue getChunkQueue();

	Queue getDedupQueue();

	EjbPlatform getE2service();

	EntityManager getEm();

	JMSContext getJmsContext();

	OabaLinkageType getLinkageType();

	Logger getLogger();

	Queue getMatchDedupQueue();

	Queue getMatchSchedulerQueue();

	OabaJobManager getOabaJobManager();

	OabaParametersController getOabaParamsController();

	EventPersistenceManager getOabaProcessingController();

	OabaService getOabaService();

	JMSConsumer getOabaStatusConsumer();

	Topic getOabaStatusTopic();

	OperationalPropertyController getOpPropController();

	BatchProcessingPhase getProcessingPhase();

	RecordIdController getRecordIdController();

	RecordSourceController getRecordSourceController();

	int getResultEventId();

	float getResultPercentComplete();

	Queue getResultQueue();

	ServerConfigurationController getServerController();

	OabaSettingsController getSettingsController();

	Queue getSingleMatchQueue();

	String getSourceName();

	Queue getStartQueue();

	WellKnownTestConfiguration getTestConfiguration();

	TestEntityCounts getTestEntityCounts();

	Queue getTransitivityQueue();

	UserTransaction getUtx();

}