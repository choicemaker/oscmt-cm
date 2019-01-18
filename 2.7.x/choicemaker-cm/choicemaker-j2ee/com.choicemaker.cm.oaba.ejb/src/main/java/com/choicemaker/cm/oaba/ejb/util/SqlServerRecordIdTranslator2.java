package com.choicemaker.cm.oaba.ejb.util;

import java.util.List;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.oaba.api.ImmutableRecordIdTranslatorLocal;
import com.choicemaker.cm.oaba.api.MutableRecordIdTranslatorLocal;
import com.choicemaker.cm.oaba.core.IRecordIdSink;
import com.choicemaker.cm.oaba.core.IRecordIdSinkSourceFactory;
import com.choicemaker.cm.oaba.core.ImmutableRecordIdTranslator;
import com.choicemaker.cm.oaba.core.RECORD_ID_TYPE;
import com.choicemaker.util.Precondition;

public class SqlServerRecordIdTranslator2<T extends Comparable<T>> implements
		MutableRecordIdTranslatorLocal<T>, ImmutableRecordIdTranslatorLocal<T> {

	final BatchJob batchJob;
	final boolean keepFiles;
	ImmutableRecordIdTranslator<T> immrit;

	public SqlServerRecordIdTranslator2(BatchJob job,
			IRecordIdSinkSourceFactory factory, IRecordIdSink s1,
			IRecordIdSink s2, boolean doKeepFiles) {
		Precondition.assertNonNullArgument(job);

		this.batchJob = job;
		this.keepFiles = doKeepFiles;
	}

	@Override
	public void cleanUp() throws BlockingException {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() throws BlockingException {
		// NOP: effectively closed at the end of the open() method
	}

	@Override
	public BatchJob getBatchJob() {
		return batchJob;
	}

	@Override
	public List<T> getList1() {
		return immrit.getList1();
	}

	@Override
	public List<T> getList2() {
		return immrit.getList2();
	}

	@Override
	public RECORD_ID_TYPE getRecordIdType() {
		return immrit.getRecordIdType();
	}

	@Override
	public int getSplitIndex() {
		return immrit.getSplitIndex();
	}

	public boolean isClosed() {
		return immrit != null;
	}

	@Override
	public boolean isSplit() {
		return immrit.isSplit();
	}

	@Override
	public int lookupMasterIndex(T recordID) {
		return immrit.lookupMasterIndex(recordID);
	}

	@Override
	public int lookupStagingIndex(T recordID) {
		return immrit.lookupStagingIndex(recordID);
	}

	/**
	 * This method downloads translations from a database to this instance. If
	 * translations do not already exist in the database, this method first
	 * computes them using a stored procedure. Once translations have been
	 * loaded from the database, this method closes this instance to prevent new
	 * translations from being added.
	 */
	@Override
	public void open() throws BlockingException {
		// TODO Auto-generated method stub

		assert isClosed();
	}

	@Override
	public Comparable<?> reverseLookup(int internalID) {
		return immrit.reverseLookup(internalID);
	}

	@Override
	public void split() throws BlockingException {
		// TODO Auto-generated method stub

	}

	@Override
	public int translate(T o) throws BlockingException {
		// TODO Auto-generated method stub
		return 0;
	}

}
