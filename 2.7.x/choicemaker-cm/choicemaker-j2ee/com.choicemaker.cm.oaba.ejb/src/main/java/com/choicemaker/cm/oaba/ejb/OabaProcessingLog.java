/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.ejb;

import static javax.ejb.TransactionAttributeType.REQUIRED;
import static javax.ejb.TransactionAttributeType.SUPPORTS;

import java.util.Date;
import java.util.logging.Logger;

import javax.ejb.TransactionAttribute;
import javax.persistence.EntityManager;

import com.choicemaker.cm.args.ProcessingEvent;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchProcessingEvent;
import com.choicemaker.cm.batch.api.ProcessingEventLog;

/**
 * OabaProcessingLog restricts a logging context to a specific OABA job by
 * attaching an OABA job identifier to each OabaEvent instance that it records
 * or retrieves.
 *
 * @author rphall
 */
@TransactionAttribute(REQUIRED)
public class OabaProcessingLog implements ProcessingEventLog {

	private static final Logger logger =
		Logger.getLogger(OabaProcessingLog.class.getName());

	private static final String LOG_SOURCE =
		OabaProcessingLog.class.getSimpleName();

	private final EntityManager em;
	private final BatchJob batchJob;

	public OabaProcessingLog(EntityManager em, BatchJob job) {
		if (em == null) {
			throw new IllegalArgumentException("null EntityManager");
		}
		if (job == null || !job.isPersistent()) {
			throw new IllegalArgumentException("invalid OABA job: " + job);
		}
		this.em = em;
		this.batchJob = job;
	}

	protected BatchProcessingEvent getCurrentOabaProcessingEvent() {
		return OabaEventManager.getCurrentBatchProcessingEvent(em, batchJob);
	}

	@TransactionAttribute(SUPPORTS)
	@Override
	public ProcessingEvent getCurrentProcessingEvent() {
		BatchProcessingEvent ope = getCurrentOabaProcessingEvent();
		ProcessingEvent retVal = ope.getProcessingEvent();
		return retVal;
	}

	@TransactionAttribute(SUPPORTS)
	@Override
	public int getCurrentProcessingEventId() {
		return getCurrentProcessingEvent().getEventId();
	}

	@TransactionAttribute(SUPPORTS)
	@Override
	public String getCurrentProcessingEventInfo() {
		BatchProcessingEvent ope = getCurrentOabaProcessingEvent();
		String retVal = ope.getEventInfo();
		return retVal;
	}

	@Override
	public void setCurrentProcessingEvent(ProcessingEvent event) {
		setCurrentProcessingEvent(event, null);
	}

	@Override
	public void setCurrentProcessingEvent(ProcessingEvent event, String info) {
		logger.fine(LOG_SOURCE + ".setCurrentProcessingEvent: " + event
				+ " (job " + this.batchJob.getId() + ")");
		OabaEventManager.updateStatus(em, batchJob, event, new Date(), info);
	}

	@Override
	public String toString() {
		return "OabaProcessingLog [jobId=" + batchJob.getId() + "]";
	}

}
