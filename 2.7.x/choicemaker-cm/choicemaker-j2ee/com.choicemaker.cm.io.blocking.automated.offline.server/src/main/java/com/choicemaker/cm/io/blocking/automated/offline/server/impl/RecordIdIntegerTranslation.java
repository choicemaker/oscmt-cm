package com.choicemaker.cm.io.blocking.automated.offline.server.impl;

import static com.choicemaker.cm.io.blocking.automated.offline.server.impl.RecordIdTranslationJPA.*;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import com.choicemaker.cm.batch.BatchJob;
import com.choicemaker.cm.io.blocking.automated.offline.core.RECORD_ID_TYPE;
import com.choicemaker.cm.io.blocking.automated.offline.core.RECORD_SOURCE;

@NamedQueries({
		@NamedQuery(name = QN_TRANSLATEDINTEGERID_FIND_ALL,
				query = JPQL_TRANSLATEDINTEGERID_FIND_ALL),
		@NamedQuery(name = QN_TRANSLATEDINTEGERID_FIND_BY_JOBID,
				query = PN_TRANSLATEDINTEGERID_FIND_BY_JOBID_JOBID) })
@Entity
@DiscriminatorValue(DV_INTEGER)
public class RecordIdIntegerTranslation extends
		AbstractRecordIdTranslationEntity<Integer> {

	private static final long serialVersionUID = 271L;

	public static Integer idFromString(String s) {
		Integer retVal;
		if (s == null) {
			retVal = null;
		} else {
			retVal = Integer.valueOf(s);
		}
		return retVal;
	}

	public static String idToString(Integer id) {
		String retVal;
		if (id == null) {
			retVal = null;
		} else {
			retVal = id.toString();
		}
		return retVal;
	}

	public RecordIdIntegerTranslation(BatchJob job, int recordId,
			RECORD_SOURCE source, long translatedId) {
		super(job.getId(), idToString(recordId), RECORD_ID_TYPE.TYPE_LONG
				.getCharSymbol(), source.getCharSymbol(), translatedId);
	}

	@Override
	public Integer getRecordId() {
		return idFromString(recordId);
	}

}
