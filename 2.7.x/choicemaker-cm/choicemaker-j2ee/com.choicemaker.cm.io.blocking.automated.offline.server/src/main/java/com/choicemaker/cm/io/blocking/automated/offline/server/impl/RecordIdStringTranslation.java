/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.blocking.automated.offline.server.impl;

import static com.choicemaker.cm.io.blocking.automated.offline.server.impl.RecordIdTranslationJPA.DV_STRING;
import static com.choicemaker.cm.io.blocking.automated.offline.server.impl.RecordIdTranslationJPA.JPQL_TRANSLATEDSTRINGID_FIND_ALL;
import static com.choicemaker.cm.io.blocking.automated.offline.server.impl.RecordIdTranslationJPA.JPQL_TRANSLATEDSTRINGID_FIND_BY_JOBID;
import static com.choicemaker.cm.io.blocking.automated.offline.server.impl.RecordIdTranslationJPA.QN_TRANSLATEDSTRINGID_FIND_ALL;
import static com.choicemaker.cm.io.blocking.automated.offline.server.impl.RecordIdTranslationJPA.QN_TRANSLATEDSTRINGID_FIND_BY_JOBID;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.core.base.RECORD_SOURCE_ROLE;
import com.choicemaker.cm.io.blocking.automated.offline.core.RECORD_ID_TYPE;

@NamedQueries({
		@NamedQuery(name = QN_TRANSLATEDSTRINGID_FIND_ALL,
				query = JPQL_TRANSLATEDSTRINGID_FIND_ALL),
		@NamedQuery(name = QN_TRANSLATEDSTRINGID_FIND_BY_JOBID,
				query = JPQL_TRANSLATEDSTRINGID_FIND_BY_JOBID) })
@Entity
@DiscriminatorValue(DV_STRING)
public class RecordIdStringTranslation extends
		AbstractRecordIdTranslationEntity<String> {

	private static final long serialVersionUID = 271L;

	static final String RECORD_ID_PLACEHOLDER = ""
			+ RecordIdIntegerTranslation.RECORD_ID_PLACEHOLDER;

	protected RecordIdStringTranslation() {
		super();
	}

	public RecordIdStringTranslation(BatchJob job, String recordId,
			RECORD_SOURCE_ROLE source, int translatedId) {
		super(job.getId(), recordId,
				RECORD_ID_TYPE.TYPE_STRING.getCharSymbol(), source
						.getCharSymbol(), translatedId);
	}

	@Override
	public String getRecordId() {
		return recordId;
	}

}
