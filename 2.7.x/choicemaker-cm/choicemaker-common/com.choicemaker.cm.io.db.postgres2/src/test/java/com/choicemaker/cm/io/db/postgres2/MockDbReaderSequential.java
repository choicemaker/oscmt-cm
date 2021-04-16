package com.choicemaker.cm.io.db.postgres2;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.choicemaker.cm.core.DerivedSource;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.io.db.base.DbReaderSequential;
import com.choicemaker.cm.io.db.base.DbView;

public final class MockDbReaderSequential implements DbReaderSequential<String> {

	private ResultSet rs;
	private List<Record<String>> res = new ArrayList<>();
	private int resSize;
	private Iterator<Record<String>> iRes;
	private ArrayList<String> l = new ArrayList<>();
	private static DerivedSource src = DerivedSource.valueOf("db");

	@Override
	public String getName() {
		return "MciRecords:r:patient";
	}

	@Override
	public void open(ResultSet rs, Statement stmt)
			throws java.sql.SQLException {
		this.rs = rs;
		getRecordPatientImpl();
		resSize = res.size();
		if (resSize != 0) {
			stmt.getMoreResults();
			this.rs = stmt.getResultSet();
		}
		rs.close();
		iRes = res.iterator();
		l = null;
	}

	@Override
	public Record<String> getNext() {
		Record<String> r = iRes.next();
		r.computeValidityAndDerived(src);
		return r;
	}

	@Override
	public boolean hasNext() {
		return iRes.hasNext();
	}

	@Override
	public int getNoCursors() {
		return MockDbReaderParallel.NO_CURSORS;
	}

	private void getRecordPatientImpl() throws java.sql.SQLException {
	}

	@Override
	public String getMasterId() {
		return MockDbReaderParallel.masterId;
	}

	@Override
	public String getMasterIdType() {
		return MockDbReaderParallel.masterIdType;
	}

	@Override
	public DbView[] getViews() {
		return MockDbReaderParallel.views;
	}
}
