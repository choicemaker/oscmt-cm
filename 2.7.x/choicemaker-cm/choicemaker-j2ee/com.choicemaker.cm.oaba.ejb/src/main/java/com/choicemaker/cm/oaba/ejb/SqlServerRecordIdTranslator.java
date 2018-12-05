package com.choicemaker.cm.oaba.ejb;

import com.choicemaker.cm.oaba.api.DbRecordIdTranslator;
import com.choicemaker.cm.oaba.core.RECORD_ID_TYPE;

public class SqlServerRecordIdTranslator implements DbRecordIdTranslator {

	@Override
	public RECORD_ID_TYPE determineRecordIdType(long jobId,
			String sqlIdSelection) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int computeRecordIdTranslations(long jobId,
			RECORD_ID_TYPE recordIdType, String queryIdSelection,
			String referenceIdSelection) {
		// TODO Auto-generated method stub
		return 0;
	}

}
