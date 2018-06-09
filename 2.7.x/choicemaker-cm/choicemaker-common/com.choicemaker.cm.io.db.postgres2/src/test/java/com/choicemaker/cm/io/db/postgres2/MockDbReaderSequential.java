package com.choicemaker.cm.io.db.postgres2;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import com.choicemaker.cm.core.DerivedSource;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.io.db.base.DbReaderSequential;
import com.choicemaker.cm.io.db.base.DbView;

public final class MockDbReaderSequential implements DbReaderSequential {

	private static Logger logger =
		Logger.getLogger(MockDbReaderSequential.class.getName());

	private ResultSet rs;
	private List res = new ArrayList();
	private int resSize;
	private Iterator iRes;
	private ArrayList l = new ArrayList();
	private static DerivedSource src = DerivedSource.valueOf("db");

	public String getName() {
		return "MciRecords:r:patient";
	}

	public void open(ResultSet rs, Statement stmt)
			throws java.sql.SQLException {
		this.rs = rs;
		getRecordPatientImpl();
		resSize = res.size();
		if (resSize != 0) {
			stmt.getMoreResults();
			this.rs = stmt.getResultSet();
			// getRecordNamesImpl();
			// stmt.getMoreResults();
			// this.rs = stmt.getResultSet();
			// getRecordEthnicityImpl();
			// stmt.getMoreResults();
			// this.rs = stmt.getResultSet();
			// getRecordRaceImpl();
			// stmt.getMoreResults();
			// this.rs = stmt.getResultSet();
			// getRecordIdsImpl();
			// stmt.getMoreResults();
			// this.rs = stmt.getResultSet();
			// getRecordAddressImpl();
			// stmt.getMoreResults();
			// this.rs = stmt.getResultSet();
			// getRecordRelImpl();
			// stmt.getMoreResults();
			// this.rs = stmt.getResultSet();
			// getRecordContactsImpl();
			// stmt.getMoreResults();
			// this.rs = stmt.getResultSet();
			// getRecordMothersImpl();
			// stmt.getMoreResults();
			// this.rs = stmt.getResultSet();
			// getRecordEventsImpl();
			// stmt.getMoreResults();
			// this.rs = stmt.getResultSet();
			// getRecordProvidersImpl();
			// stmt.getMoreResults();
			// this.rs = stmt.getResultSet();
			// getRecordFrozenImpl();
			// stmt.getMoreResults();
			// this.rs = stmt.getResultSet();
			// getRecordIndexAndOutstandingImpl();
		}
		rs.close();
		iRes = res.iterator();
		l = null;
	}

	public Record getNext() {
		Record r = (Record) iRes.next();
		r.computeValidityAndDerived(src);
		return r;
	}

	public boolean hasNext() {
		return iRes.hasNext();
	}

	public int getNoCursors() {
		return MockDbReaderParallel.NO_CURSORS;
	}

	private void getRecordPatientImpl() throws java.sql.SQLException {
		String __tmpStr;
		while (rs.next()) {
			// PatientImpl r = new PatientImpl();
			// r.mci_id = rs.getInt(1);
			// r.facility_id = rs.getString(2);
			// r.language_cd = rs.getString(3);
			// r.birth_country_cd = rs.getString(4);
			// r.multi_birth_ind =
			// com.choicemaker.util.StringUtils.getChar(rs.getString(5));
			// r.src_system_id = rs.getString(6);
			// r.names = NamesImpl.__zeroArray;
			// r.ethnicity = EthnicityImpl.__zeroArray;
			// r.race = RaceImpl.__zeroArray;
			// r.ids = IdsImpl.__zeroArray;
			// r.address = AddressImpl.__zeroArray;
			// r.contacts = ContactsImpl.__zeroArray;
			// r.mothers = MothersImpl.__zeroArray;
			// r.events = EventsImpl.__zeroArray;
			// r.providers = ProvidersImpl.__zeroArray;
			// r.frozen = FrozenImpl.__zeroArray;
			// r.indexAndOutstanding = IndexAndOutstandingImpl.__zeroArray;
			// res.add(r);
		}
	}

	public String getMasterId() {
		return MockDbReaderParallel.masterId;
	}

	public String getMasterIdType() {
		return MockDbReaderParallel.masterIdType;
	}

	public DbView[] getViews() {
		return MockDbReaderParallel.views;
	}
}
