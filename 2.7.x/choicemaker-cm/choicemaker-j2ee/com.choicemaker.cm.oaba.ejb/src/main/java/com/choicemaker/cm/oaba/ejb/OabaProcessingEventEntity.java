/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.ejb;

import static com.choicemaker.cm.oaba.ejb.OabaProcessingEventJPA.DISCRIMINATOR_VALUE;
import static com.choicemaker.cm.oaba.ejb.OabaProcessingEventJPA.JPQL_OABAPROCESSING_DELETE_BY_JOBID;
import static com.choicemaker.cm.oaba.ejb.OabaProcessingEventJPA.JPQL_OABAPROCESSING_FIND_ALL;
import static com.choicemaker.cm.oaba.ejb.OabaProcessingEventJPA.JPQL_OABAPROCESSING_FIND_BY_JOBID;
import static com.choicemaker.cm.oaba.ejb.OabaProcessingEventJPA.QN_OABAPROCESSING_DELETE_BY_JOBID;
import static com.choicemaker.cm.oaba.ejb.OabaProcessingEventJPA.QN_OABAPROCESSING_FIND_ALL;
import static com.choicemaker.cm.oaba.ejb.OabaProcessingEventJPA.QN_OABAPROCESSING_FIND_BY_JOBID;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import com.choicemaker.cm.args.ProcessingEvent;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.ejb.BatchProcessingEventEntity;
import com.choicemaker.cm.oaba.api.OabaProcessingEvent;
import com.choicemaker.cm.oaba.core.OabaEventBean;

/**
 * This is the EJB implementation of the OABA BatchProcessingEventEntity
 * interface.
 */
@NamedQueries({
		@NamedQuery(name = QN_OABAPROCESSING_FIND_ALL,
				query = JPQL_OABAPROCESSING_FIND_ALL),
		@NamedQuery(name = QN_OABAPROCESSING_FIND_BY_JOBID,
				query = JPQL_OABAPROCESSING_FIND_BY_JOBID),
		@NamedQuery(name = QN_OABAPROCESSING_DELETE_BY_JOBID,
				query = JPQL_OABAPROCESSING_DELETE_BY_JOBID) })
@Entity
@DiscriminatorValue(DISCRIMINATOR_VALUE)
public class OabaProcessingEventEntity extends BatchProcessingEventEntity
		implements OabaProcessingEvent {

	private static final long serialVersionUID = 271L;

	// -- Construction

	/** Required by JPA; do not invoke directly */
	protected OabaProcessingEventEntity() {
		super();
	}

	public OabaProcessingEventEntity(BatchJob job, ProcessingEvent status) {
		this(job, status, null);
	}

	public OabaProcessingEventEntity(BatchJob job, ProcessingEvent event,
			String info) {
		super(job.getId(), DISCRIMINATOR_VALUE, event.getEventName(),
				event.getEventId(), event.getFractionComplete(), info);
	}

	@Override
	public ProcessingEvent getProcessingEvent() {
		ProcessingEvent retVal = new OabaEventBean(getEventName(),
				getEventSequenceNumber(), getFractionComplete());
		return retVal;
	}

}
