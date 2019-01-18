package com.choicemaker.cm.oaba.ejb;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.oaba.api.DbRecordIdTranslator;
import com.choicemaker.cm.oaba.core.IRecordIdSink;
import com.choicemaker.cm.oaba.core.IRecordIdSinkSourceFactory;
import com.choicemaker.cm.oaba.core.RECORD_ID_TYPE;

public class SqlServerRecordIdTranslator1 extends AbstractRecordIdTranslator
		implements DbRecordIdTranslator {

	public SqlServerRecordIdTranslator1(BatchJob job,
			IRecordIdSinkSourceFactory factory, IRecordIdSink s1,
			IRecordIdSink s2, boolean doKeepFiles) throws BlockingException {
		super(job, factory, s1, s2, doKeepFiles);
	}

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

	@Override
	public void open() throws BlockingException {
		// TODO Auto-generated method stub

	}

	@Override
	public void split() throws BlockingException {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() throws BlockingException {
		// TODO Auto-generated method stub

	}

	@Override
	public void cleanUp() throws BlockingException {
		// TODO Auto-generated method stub

	}

	@Override
	public int translate(@SuppressWarnings("rawtypes") Comparable o)
			throws BlockingException {
		// TODO Auto-generated method stub
		return 0;
	}

}
