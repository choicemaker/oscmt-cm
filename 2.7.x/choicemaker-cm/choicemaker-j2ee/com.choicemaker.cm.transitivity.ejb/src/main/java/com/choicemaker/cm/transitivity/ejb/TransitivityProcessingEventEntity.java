/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.ejb;

import static com.choicemaker.cm.transitivity.ejb.TransitivityProcessingEventJPA.DISCRIMINATOR_VALUE;
import static com.choicemaker.cm.transitivity.ejb.TransitivityProcessingEventJPA.JPQL_TRANSPROCESSING_DELETE_BY_JOBID;
import static com.choicemaker.cm.transitivity.ejb.TransitivityProcessingEventJPA.JPQL_TRANSPROCESSING_FIND_ALL;
import static com.choicemaker.cm.transitivity.ejb.TransitivityProcessingEventJPA.JPQL_TRANSPROCESSING_FIND_BY_JOBID;
import static com.choicemaker.cm.transitivity.ejb.TransitivityProcessingEventJPA.QN_TRANSPROCESSING_DELETE_BY_JOBID;
import static com.choicemaker.cm.transitivity.ejb.TransitivityProcessingEventJPA.QN_TRANSPROCESSING_FIND_ALL;
import static com.choicemaker.cm.transitivity.ejb.TransitivityProcessingEventJPA.QN_TRANSPROCESSING_FIND_BY_JOBID;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import com.choicemaker.cm.args.ProcessingEvent;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.ejb.BatchProcessingEventEntity;
import com.choicemaker.cm.transitivity.api.TransitivityProcessingEvent;
import com.choicemaker.cm.transitivity.core.TransitivityEventBean;

/**
 * This is the EJB implementation of the Transitivity BatchProcessingEventEntity
 * interface.
 */
@NamedQueries({
		@NamedQuery(name = QN_TRANSPROCESSING_FIND_ALL,
				query = JPQL_TRANSPROCESSING_FIND_ALL),
		@NamedQuery(name = QN_TRANSPROCESSING_FIND_BY_JOBID,
				query = JPQL_TRANSPROCESSING_FIND_BY_JOBID),
		@NamedQuery(name = QN_TRANSPROCESSING_DELETE_BY_JOBID,
				query = JPQL_TRANSPROCESSING_DELETE_BY_JOBID) })
@Entity
@DiscriminatorValue(DISCRIMINATOR_VALUE)
public class TransitivityProcessingEventEntity extends
		BatchProcessingEventEntity implements TransitivityProcessingEvent {

	private static final long serialVersionUID = 271L;

	// -- Construction

	/** Required by JPA; do not invoke directly */
	protected TransitivityProcessingEventEntity() {
		super();
	}

	public TransitivityProcessingEventEntity(BatchJob job,
			ProcessingEvent status) {
		this(job, status, null);
	}

	public TransitivityProcessingEventEntity(BatchJob job,
			ProcessingEvent event, String info) {
		super(job.getId(), DISCRIMINATOR_VALUE, event.getEventName(),
				event.getEventId(), event.getFractionComplete(), info);
	}

	@Override
	public ProcessingEvent getProcessingEvent() {
		ProcessingEvent retVal = new TransitivityEventBean(getEventName(),
				getEventSequenceNumber(), getFractionComplete());
		return retVal;
	}

}
