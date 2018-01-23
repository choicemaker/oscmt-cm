/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.blocking.automated.offline.server.impl;

import static com.choicemaker.cm.io.blocking.automated.offline.server.impl.RecordIdTranslationJPA.DV_LONG;
import static com.choicemaker.cm.io.blocking.automated.offline.server.impl.RecordIdTranslationJPA.JPQL_TRANSLATEDLONGID_FIND_ALL;
import static com.choicemaker.cm.io.blocking.automated.offline.server.impl.RecordIdTranslationJPA.JPQL_TRANSLATEDLONGID_FIND_BY_JOBID;
import static com.choicemaker.cm.io.blocking.automated.offline.server.impl.RecordIdTranslationJPA.QN_TRANSLATEDLONGID_FIND_ALL;
import static com.choicemaker.cm.io.blocking.automated.offline.server.impl.RecordIdTranslationJPA.QN_TRANSLATEDLONGID_FIND_BY_JOBID;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.core.base.RECORD_SOURCE_ROLE;
import com.choicemaker.cm.io.blocking.automated.offline.core.RECORD_ID_TYPE;

@NamedQueries({
		@NamedQuery(name = QN_TRANSLATEDLONGID_FIND_ALL,
				query = JPQL_TRANSLATEDLONGID_FIND_ALL),
		@NamedQuery(name = QN_TRANSLATEDLONGID_FIND_BY_JOBID,
				query = JPQL_TRANSLATEDLONGID_FIND_BY_JOBID) })
@Entity
@DiscriminatorValue(DV_LONG)
public class RecordIdLongTranslation extends
		AbstractRecordIdTranslationEntity<Long> {

	private static final long serialVersionUID = 271L;

	static final long RECORD_ID_PLACEHOLDER =
		RecordIdIntegerTranslation.RECORD_ID_PLACEHOLDER;

	public static Long idFromString(String s) {
		Long retVal;
		if (s == null) {
			retVal = null;
		} else {
			retVal = Long.valueOf(s);
		}
		return retVal;
	}

	public static String idToString(Long id) {
		String retVal;
		if (id == null) {
			retVal = null;
		} else {
			retVal = id.toString();
		}
		return retVal;
	}

	protected RecordIdLongTranslation() {
		super();
	}

	public RecordIdLongTranslation(BatchJob job, long recordId,
			RECORD_SOURCE_ROLE source, int translatedId) {
		super(job.getId(), idToString(recordId), RECORD_ID_TYPE.TYPE_LONG
				.getCharSymbol(), source.getCharSymbol(), translatedId);
	}

	@Override
	public Long getRecordId() {
		return idFromString(recordId);
	}

}
