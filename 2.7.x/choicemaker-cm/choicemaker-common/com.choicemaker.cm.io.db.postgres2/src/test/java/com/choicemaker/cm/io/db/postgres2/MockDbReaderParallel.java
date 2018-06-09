package com.choicemaker.cm.io.db.postgres2;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.choicemaker.cm.core.DerivedSource;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.io.db.base.DbField;
import com.choicemaker.cm.io.db.base.DbReaderParallel;
import com.choicemaker.cm.io.db.base.DbView;
import com.choicemaker.cm.io.db.base.Index;

public final class MockDbReaderParallel implements DbReaderParallel {

	private static Logger logger =
		Logger.getLogger(MockDbReaderParallel.class.getName());

	private ResultSet[] rs;
	private static DerivedSource src = DerivedSource.valueOf("db");

	public String getName() {
		return "MciRecords:r:patient";
	}

	// private PatientImpl o__PatientImpl;
	// private NamesImpl o__NamesImpl;
	// private LinkedList l__NamesImpl = new LinkedList();
	// private int NamesImpl__mci_id;
	// private EthnicityImpl o__EthnicityImpl;
	// private LinkedList l__EthnicityImpl = new LinkedList();
	// private int EthnicityImpl__mci_id;
	// private RaceImpl o__RaceImpl;
	// private LinkedList l__RaceImpl = new LinkedList();
	// private int RaceImpl__mci_id;
	// private IdsImpl o__IdsImpl;
	// private LinkedList l__IdsImpl = new LinkedList();
	// private int IdsImpl__mci_id;
	// private AddressImpl o__AddressImpl;
	// private LinkedList l__AddressImpl = new LinkedList();
	// private int AddressImpl__mci_id;
	// private RelImpl o__RelImpl;
	// private LinkedList l__RelImpl = new LinkedList();
	// private int RelImpl__mci_id;
	// private long RelImpl__addr_id;
	// private ContactsImpl o__ContactsImpl;
	// private LinkedList l__ContactsImpl = new LinkedList();
	// private int ContactsImpl__mci_id;
	// private MothersImpl o__MothersImpl;
	// private LinkedList l__MothersImpl = new LinkedList();
	// private int MothersImpl__mci_id;
	// private EventsImpl o__EventsImpl;
	// private LinkedList l__EventsImpl = new LinkedList();
	// private int EventsImpl__mci_id;
	// private ProvidersImpl o__ProvidersImpl;
	// private LinkedList l__ProvidersImpl = new LinkedList();
	// private int ProvidersImpl__mci_id;
	// private FrozenImpl o__FrozenImpl;
	// private LinkedList l__FrozenImpl = new LinkedList();
	// private int FrozenImpl__mci_id;
	// private IndexAndOutstandingImpl o__IndexAndOutstandingImpl;
	// private LinkedList l__IndexAndOutstandingImpl = new LinkedList();
//	private int IndexAndOutstandingImpl__mci_id;

	public void open(ResultSet[] rs) throws java.sql.SQLException {
		this.rs = rs;
		// getRecordNamesImpl();
		// getRecordEthnicityImpl();
		// getRecordRaceImpl();
		// getRecordIdsImpl();
		// getRecordRelImpl();
		// getRecordAddressImpl();
		// getRecordContactsImpl();
		// getRecordMothersImpl();
		// getRecordEventsImpl();
		// getRecordProvidersImpl();
		// getRecordFrozenImpl();
		// getRecordIndexAndOutstandingImpl();
		// getRecordPatientImpl();
	}

	public Record getNext() throws java.sql.SQLException {
		// Record __res = o__PatientImpl;
		// __res.computeValidityAndDerived(src);
		// getRecordPatientImpl();
		// return __res;
		return null;
	}

	public boolean hasNext() {
		// return o__PatientImpl != null;
		return false;
	}

	public int getNoCursors() {
		return NO_CURSORS;
	}

	static final int NO_CURSORS = 13;

	// private void getRecordPatientImpl() throws java.sql.SQLException {
	// String __tmpStr;
	// if(rs[0].next()) {
	// o__PatientImpl = new PatientImpl();
	// o__PatientImpl.mci_id = rs[0].getInt(1);
	// o__PatientImpl.facility_id = rs[0].getString(2);
	// o__PatientImpl.language_cd = rs[0].getString(3);
	// o__PatientImpl.birth_country_cd = rs[0].getString(4);
	// o__PatientImpl.multi_birth_ind =
	// com.choicemaker.util.StringUtils.getChar(rs[0].getString(5));
	// o__PatientImpl.src_system_id = rs[0].getString(6);
	// while(o__NamesImpl != null && o__PatientImpl.mci_id == NamesImpl__mci_id)
	// {
	// l__NamesImpl.add(o__NamesImpl);
	// o__NamesImpl.outer = o__PatientImpl;
	// getRecordNamesImpl();
	// }
	// if(l__NamesImpl.size() == 0) {
	// o__PatientImpl.names = NamesImpl.__zeroArray;
	// } else {
	// l__NamesImpl.toArray(o__PatientImpl.names = new
	// NamesImpl[l__NamesImpl.size()]);
	// l__NamesImpl.clear();
	// }
	// while(o__EthnicityImpl != null && o__PatientImpl.mci_id ==
	// EthnicityImpl__mci_id) {
	// l__EthnicityImpl.add(o__EthnicityImpl);
	// o__EthnicityImpl.outer = o__PatientImpl;
	// getRecordEthnicityImpl();
	// }
	// if(l__EthnicityImpl.size() == 0) {
	// o__PatientImpl.ethnicity = EthnicityImpl.__zeroArray;
	// } else {
	// l__EthnicityImpl.toArray(o__PatientImpl.ethnicity = new
	// EthnicityImpl[l__EthnicityImpl.size()]);
	// l__EthnicityImpl.clear();
	// }
	// while(o__RaceImpl != null && o__PatientImpl.mci_id == RaceImpl__mci_id) {
	// l__RaceImpl.add(o__RaceImpl);
	// o__RaceImpl.outer = o__PatientImpl;
	// getRecordRaceImpl();
	// }
	// if(l__RaceImpl.size() == 0) {
	// o__PatientImpl.race = RaceImpl.__zeroArray;
	// } else {
	// l__RaceImpl.toArray(o__PatientImpl.race = new
	// RaceImpl[l__RaceImpl.size()]);
	// l__RaceImpl.clear();
	// }
	// while(o__IdsImpl != null && o__PatientImpl.mci_id == IdsImpl__mci_id) {
	// l__IdsImpl.add(o__IdsImpl);
	// o__IdsImpl.outer = o__PatientImpl;
	// getRecordIdsImpl();
	// }
	// if(l__IdsImpl.size() == 0) {
	// o__PatientImpl.ids = IdsImpl.__zeroArray;
	// } else {
	// l__IdsImpl.toArray(o__PatientImpl.ids = new IdsImpl[l__IdsImpl.size()]);
	// l__IdsImpl.clear();
	// }
	// while(o__AddressImpl != null && o__PatientImpl.mci_id ==
	// AddressImpl__mci_id) {
	// l__AddressImpl.add(o__AddressImpl);
	// o__AddressImpl.outer = o__PatientImpl;
	// getRecordAddressImpl();
	// }
	// if(l__AddressImpl.size() == 0) {
	// o__PatientImpl.address = AddressImpl.__zeroArray;
	// } else {
	// l__AddressImpl.toArray(o__PatientImpl.address = new
	// AddressImpl[l__AddressImpl.size()]);
	// l__AddressImpl.clear();
	// }
	// while(o__ContactsImpl != null && o__PatientImpl.mci_id ==
	// ContactsImpl__mci_id) {
	// l__ContactsImpl.add(o__ContactsImpl);
	// o__ContactsImpl.outer = o__PatientImpl;
	// getRecordContactsImpl();
	// }
	// if(l__ContactsImpl.size() == 0) {
	// o__PatientImpl.contacts = ContactsImpl.__zeroArray;
	// } else {
	// l__ContactsImpl.toArray(o__PatientImpl.contacts = new
	// ContactsImpl[l__ContactsImpl.size()]);
	// l__ContactsImpl.clear();
	// }
	// while(o__MothersImpl != null && o__PatientImpl.mci_id ==
	// MothersImpl__mci_id) {
	// l__MothersImpl.add(o__MothersImpl);
	// o__MothersImpl.outer = o__PatientImpl;
	// getRecordMothersImpl();
	// }
	// if(l__MothersImpl.size() == 0) {
	// o__PatientImpl.mothers = MothersImpl.__zeroArray;
	// } else {
	// l__MothersImpl.toArray(o__PatientImpl.mothers = new
	// MothersImpl[l__MothersImpl.size()]);
	// l__MothersImpl.clear();
	// }
	// while(o__EventsImpl != null && o__PatientImpl.mci_id ==
	// EventsImpl__mci_id) {
	// l__EventsImpl.add(o__EventsImpl);
	// o__EventsImpl.outer = o__PatientImpl;
	// getRecordEventsImpl();
	// }
	// if(l__EventsImpl.size() == 0) {
	// o__PatientImpl.events = EventsImpl.__zeroArray;
	// } else {
	// l__EventsImpl.toArray(o__PatientImpl.events = new
	// EventsImpl[l__EventsImpl.size()]);
	// l__EventsImpl.clear();
	// }
	// while(o__ProvidersImpl != null && o__PatientImpl.mci_id ==
	// ProvidersImpl__mci_id) {
	// l__ProvidersImpl.add(o__ProvidersImpl);
	// o__ProvidersImpl.outer = o__PatientImpl;
	// getRecordProvidersImpl();
	// }
	// if(l__ProvidersImpl.size() == 0) {
	// o__PatientImpl.providers = ProvidersImpl.__zeroArray;
	// } else {
	// l__ProvidersImpl.toArray(o__PatientImpl.providers = new
	// ProvidersImpl[l__ProvidersImpl.size()]);
	// l__ProvidersImpl.clear();
	// }
	// while(o__FrozenImpl != null && o__PatientImpl.mci_id ==
	// FrozenImpl__mci_id) {
	// l__FrozenImpl.add(o__FrozenImpl);
	// o__FrozenImpl.outer = o__PatientImpl;
	// getRecordFrozenImpl();
	// }
	// if(l__FrozenImpl.size() == 0) {
	// o__PatientImpl.frozen = FrozenImpl.__zeroArray;
	// } else {
	// l__FrozenImpl.toArray(o__PatientImpl.frozen = new
	// FrozenImpl[l__FrozenImpl.size()]);
	// l__FrozenImpl.clear();
	// }
	// while(o__IndexAndOutstandingImpl != null && o__PatientImpl.mci_id ==
	// IndexAndOutstandingImpl__mci_id) {
	// l__IndexAndOutstandingImpl.add(o__IndexAndOutstandingImpl);
	// o__IndexAndOutstandingImpl.outer = o__PatientImpl;
	// getRecordIndexAndOutstandingImpl();
	// }
	// if(l__IndexAndOutstandingImpl.size() == 0) {
	// o__PatientImpl.indexAndOutstanding = IndexAndOutstandingImpl.__zeroArray;
	// } else {
	// l__IndexAndOutstandingImpl.toArray(o__PatientImpl.indexAndOutstanding =
	// new IndexAndOutstandingImpl[l__IndexAndOutstandingImpl.size()]);
	// l__IndexAndOutstandingImpl.clear();
	// }
	// } else {
	// o__PatientImpl = null;
	// }
	// }
	// private void getRecordNamesImpl() throws java.sql.SQLException {
	// String __tmpStr;
	// if(rs[1].next()) {
	// o__NamesImpl = new NamesImpl();
	// NamesImpl__mci_id = rs[1].getInt(1);
	// o__NamesImpl.first_name = rs[1].getString(2);
	// o__NamesImpl.middle_name = rs[1].getString(3);
	// o__NamesImpl.last_name = rs[1].getString(4);
	// o__NamesImpl.dob = rs[1].getDate(5);
	// o__NamesImpl.sex_cd =
	// com.choicemaker.util.StringUtils.getChar(rs[1].getString(6));
	// o__NamesImpl.facility_id = rs[1].getString(7);
	// } else {
	// o__NamesImpl = null;
	// }
	// }
	// private void getRecordEthnicityImpl() throws java.sql.SQLException {
	// String __tmpStr;
	// if(rs[2].next()) {
	// o__EthnicityImpl = new EthnicityImpl();
	// EthnicityImpl__mci_id = rs[2].getInt(1);
	// o__EthnicityImpl.ethnicity_cd = rs[2].getString(2);
	// } else {
	// o__EthnicityImpl = null;
	// }
	// }
	// private void getRecordRaceImpl() throws java.sql.SQLException {
	// String __tmpStr;
	// if(rs[3].next()) {
	// o__RaceImpl = new RaceImpl();
	// RaceImpl__mci_id = rs[3].getInt(1);
	// o__RaceImpl.race_cd = rs[3].getString(2);
	// } else {
	// o__RaceImpl = null;
	// }
	// }
	// private void getRecordIdsImpl() throws java.sql.SQLException {
	// String __tmpStr;
	// if(rs[4].next()) {
	// o__IdsImpl = new IdsImpl();
	// IdsImpl__mci_id = rs[4].getInt(1);
	// o__IdsImpl.patient_id = rs[4].getString(2);
	// o__IdsImpl.identifier_type = rs[4].getString(3);
	// o__IdsImpl.facility_id = rs[4].getString(4);
	// } else {
	// o__IdsImpl = null;
	// }
	// }
	// private void getRecordAddressImpl() throws java.sql.SQLException {
	// String __tmpStr;
	// if(rs[5].next()) {
	// o__AddressImpl = new AddressImpl();
	// AddressImpl__mci_id = rs[5].getInt(1);
	// o__AddressImpl.addr_id = rs[5].getLong(2);
	// o__AddressImpl.boro_cd =
	// com.choicemaker.util.StringUtils.getChar(rs[5].getString(3));
	// o__AddressImpl.bin = rs[5].getLong(4);
	// o__AddressImpl.house_no = rs[5].getString(5);
	// o__AddressImpl.street_cd = rs[5].getString(6);
	// o__AddressImpl.street_name = rs[5].getString(7);
	// o__AddressImpl.city_name = rs[5].getString(8);
	// o__AddressImpl.state_cd = rs[5].getString(9);
	// o__AddressImpl.zipcode = rs[5].getString(10);
	// while(o__RelImpl != null && AddressImpl__mci_id == RelImpl__mci_id &&
	// o__AddressImpl.addr_id == RelImpl__addr_id) {
	// l__RelImpl.add(o__RelImpl);
	// o__RelImpl.outer = o__AddressImpl;
	// getRecordRelImpl();
	// }
	// if(l__RelImpl.size() == 0) {
	// o__AddressImpl.rel = RelImpl.__zeroArray;
	// } else {
	// l__RelImpl.toArray(o__AddressImpl.rel = new RelImpl[l__RelImpl.size()]);
	// l__RelImpl.clear();
	// }
	// } else {
	// o__AddressImpl = null;
	// }
	// }
	// private void getRecordRelImpl() throws java.sql.SQLException {
	// String __tmpStr;
	// if(rs[6].next()) {
	// o__RelImpl = new RelImpl();
	// RelImpl__mci_id = rs[6].getInt(1);
	// RelImpl__addr_id = rs[6].getLong(2);
	// o__RelImpl.last_date_rptd = rs[6].getDate(3);
	// o__RelImpl.apt_no = rs[6].getString(4);
	// o__RelImpl.addr_type_cd = rs[6].getString(5);
	// o__RelImpl.raw_street_name = rs[6].getString(6);
	// o__RelImpl.phone_no = rs[6].getString(7);
	// } else {
	// o__RelImpl = null;
	// }
	// }
	// private void getRecordContactsImpl() throws java.sql.SQLException {
	// String __tmpStr;
	// if(rs[7].next()) {
	// o__ContactsImpl = new ContactsImpl();
	// ContactsImpl__mci_id = rs[7].getInt(1);
	// o__ContactsImpl.relationship_cd = rs[7].getString(2);
	// o__ContactsImpl.last_name = rs[7].getString(3);
	// o__ContactsImpl.first_name = rs[7].getString(4);
	// o__ContactsImpl.sex_cd =
	// com.choicemaker.util.StringUtils.getChar(rs[7].getString(5));
	// o__ContactsImpl.phone_no_h = rs[7].getString(6);
	// o__ContactsImpl.phone_no_w = rs[7].getString(7);
	// } else {
	// o__ContactsImpl = null;
	// }
	// }
	// private void getRecordMothersImpl() throws java.sql.SQLException {
	// String __tmpStr;
	// if(rs[8].next()) {
	// o__MothersImpl = new MothersImpl();
	// MothersImpl__mci_id = rs[8].getInt(1);
	// o__MothersImpl.mothers_maiden_name = rs[8].getString(2);
	// o__MothersImpl.mothers_dob = rs[8].getDate(3);
	// } else {
	// o__MothersImpl = null;
	// }
	// }
	// private void getRecordEventsImpl() throws java.sql.SQLException {
	// String __tmpStr;
	// if(rs[9].next()) {
	// o__EventsImpl = new EventsImpl();
	// EventsImpl__mci_id = rs[9].getInt(1);
	// o__EventsImpl.event_date = rs[9].getDate(2);
	// o__EventsImpl.event_type_cd = rs[9].getString(3);
	// o__EventsImpl.facility_id = rs[9].getString(4);
	// o__EventsImpl.event_id = rs[9].getString(5);
	// o__EventsImpl.lab_date = rs[9].getDate(6);
	// } else {
	// o__EventsImpl = null;
	// }
	// }
	// private void getRecordProvidersImpl() throws java.sql.SQLException {
	// String __tmpStr;
	// if(rs[10].next()) {
	// o__ProvidersImpl = new ProvidersImpl();
	// ProvidersImpl__mci_id = rs[10].getInt(1);
	// o__ProvidersImpl.provider_id = rs[10].getString(2);
	// o__ProvidersImpl.facility_id = rs[10].getString(3);
	// } else {
	// o__ProvidersImpl = null;
	// }
	// }
	// private void getRecordFrozenImpl() throws java.sql.SQLException {
	// String __tmpStr;
	// if(rs[11].next()) {
	// o__FrozenImpl = new FrozenImpl();
	// FrozenImpl__mci_id = rs[11].getInt(1);
	// o__FrozenImpl.src_system_id = rs[11].getString(2);
	// } else {
	// o__FrozenImpl = null;
	// }
	// }
	// private void getRecordIndexAndOutstandingImpl() throws
	// java.sql.SQLException {
	// String __tmpStr;
	// if(rs[12].next()) {
	// o__IndexAndOutstandingImpl = new IndexAndOutstandingImpl();
	// IndexAndOutstandingImpl__mci_id = rs[12].getInt(1);
	// o__IndexAndOutstandingImpl.src_system_id = rs[12].getString(2);
	// o__IndexAndOutstandingImpl.patient_id = rs[12].getInt(3);
	// } else {
	// o__IndexAndOutstandingImpl = null;
	// }
	// }
	public String getMasterId() {
		return masterId;
	}

	static final String masterId = "mci_id";
	static final String masterIdType = "number(9)";

	public DbView[] getViews() {
		return views;
	}

	static DbView[] views = {
			new DbView(0, new DbField[] {
					new DbField("TB_PATIENT", "mci_id", "mci_id"),
					new DbField("TB_PATIENT", "facility_id", "facility_id"),
					new DbField("TB_PATIENT", "language_cd", "language_cd"),
					new DbField("TB_PATIENT", "birth_country_cd",
							"birth_country_cd"),
					new DbField("TB_PATIENT", "multi_birth_ind",
							"multi_birth_ind"),
					new DbField("TB_PATIENT", "src_system_id",
							"src_system_id") },
					"TB_PATIENT", null, new DbField[] {
							new DbField("TB_PATIENT", "mci_id", "mci_id") }),
			new DbView(1, new DbField[] {
					new DbField("ST_PATIENT_NAMES", "mci_id", "mci_id"),
					new DbField("ST_PATIENT_NAMES", "first_name", "first_name"),
					new DbField("ST_PATIENT_NAMES", "middle_name",
							"middle_name"),
					new DbField("ST_PATIENT_NAMES", "last_name", "last_name"),
					new DbField("ST_PATIENT_NAMES", "dob", "dob"),
					new DbField("ST_PATIENT_NAMES", "sex_cd", "sex_cd"),
					new DbField("ST_PATIENT_NAMES", "facility_id",
							"facility_id") },
					"ST_PATIENT_NAMES", null, new DbField[] {
							new DbField("ST_PATIENT_NAMES", "mci_id",
									"mci_id") }),
			new DbView(2, new DbField[] {
					new DbField("ST_PATIENT_ETHNICITY", "mci_id", "mci_id"),
					new DbField("ST_PATIENT_ETHNICITY", "ethnicity_cd",
							"ethnicity_cd") },
					"ST_PATIENT_ETHNICITY", null, new DbField[] {
							new DbField("ST_PATIENT_ETHNICITY", "mci_id",
									"mci_id") }),
			new DbView(3, new DbField[] {
					new DbField("ST_PATIENT_RACE", "mci_id", "mci_id"),
					new DbField("ST_PATIENT_RACE", "race_cd", "race_cd") },
					"ST_PATIENT_RACE", null, new DbField[] {
							new DbField("ST_PATIENT_RACE", "mci_id",
									"mci_id") }),
			new DbView(4, new DbField[] {
					new DbField("ST_PATIENT_IDS", "mci_id", "mci_id"),
					new DbField("ST_PATIENT_IDS", "patient_id", "patient_id"),
					new DbField("ST_PATIENT_IDS", "identifier_type",
							"identifier_type"),
					new DbField("ST_PATIENT_IDS", "facility_id",
							"facility_id") },
					"ST_PATIENT_IDS", null, new DbField[] {
							new DbField("ST_PATIENT_IDS", "mci_id",
									"mci_id") }),
			new DbView(5, new DbField[] {
					new DbField("ST_ADDRESS_RELATIONSHIP", "mci_id", "mci_id"),
					new DbField("TB_ADDRESS", "addr_id", "addr_id"),
					new DbField("TB_ADDRESS", "boro_cd", "boro_cd"),
					new DbField("TB_ADDRESS", "bin", "bin"),
					new DbField("TB_ADDRESS", "house_no", "house_no"),
					new DbField("TB_ADDRESS", "street_cd", "street_cd"),
					new DbField("TB_ADDRESS", "street_name", "street_name"),
					new DbField("TB_ADDRESS", "city_name", "city_name"),
					new DbField("TB_ADDRESS", "state_cd", "state_cd"),
					new DbField("TB_ADDRESS", "zipcode", "zipcode") },
					"TB_ADDRESS,ST_ADDRESS_RELATIONSHIP",
					"TB_ADDRESS.ADDR_ID = ST_ADDRESS_RELATIONSHIP.ADDR_ID",
					new DbField[] {
							new DbField("ST_ADDRESS_RELATIONSHIP", "mci_id",
									"mci_id"),
							new DbField("TB_ADDRESS", "addr_id", "addr_id") }),
			new DbView(6, new DbField[] {
					new DbField("ST_ADDRESS_RELATIONSHIP", "mci_id", "mci_id"),
					new DbField("ST_ADDRESS_RELATIONSHIP", "addr_id",
							"addr_id"),
					new DbField("ST_ADDRESS_RELATIONSHIP", "last_date_rptd",
							"last_date_rptd"),
					new DbField("ST_ADDRESS_RELATIONSHIP", "apt_no", "apt_no"),
					new DbField("ST_ADDRESS_RELATIONSHIP", "addr_type_cd",
							"addr_type_cd"),
					new DbField("ST_ADDRESS_RELATIONSHIP", "raw_street_name",
							"raw_street_name"),
					new DbField("ST_ADDRESS_RELATIONSHIP", "phone_no",
							"phone_no") },
					"ST_ADDRESS_RELATIONSHIP", null, new DbField[] {
							new DbField("ST_ADDRESS_RELATIONSHIP", "mci_id",
									"mci_id"),
							new DbField("ST_ADDRESS_RELATIONSHIP", "addr_id",
									"addr_id") }),
			new DbView(7, new DbField[] {
					new DbField("ST_CONTACTS", "mci_id", "mci_id"),
					new DbField("ST_CONTACTS", "relationship_cd",
							"relationship_cd"),
					new DbField("ST_CONTACTS", "last_name", "last_name"),
					new DbField("ST_CONTACTS", "first_name", "first_name"),
					new DbField("ST_CONTACTS", "sex_cd", "sex_cd"),
					new DbField("ST_CONTACTS", "phone_no_h", "phone_no_h"),
					new DbField("ST_CONTACTS", "phone_no_w", "phone_no_w") },
					"ST_CONTACTS", null, new DbField[] {
							new DbField("ST_CONTACTS", "mci_id", "mci_id") }),
			new DbView(8, new DbField[] {
					new DbField("ST_MOTHERS_MAIDEN_NAME", "mci_id", "mci_id"),
					new DbField("ST_MOTHERS_MAIDEN_NAME", "mothers_maiden_name",
							"mothers_maiden_name"),
					new DbField("ST_MOTHERS_MAIDEN_NAME", "mothers_dob",
							"mothers_dob") },
					"ST_MOTHERS_MAIDEN_NAME", null, new DbField[] {
							new DbField("ST_MOTHERS_MAIDEN_NAME", "mci_id",
									"mci_id") }),
			new DbView(9, new DbField[] {
					new DbField("ST_HEALTH_EVENTS", "mci_id", "mci_id"),
					new DbField("ST_HEALTH_EVENTS", "event_date", "event_date"),
					new DbField("ST_HEALTH_EVENTS", "event_type_cd",
							"event_type_cd"),
					new DbField("ST_HEALTH_EVENTS", "facility_id",
							"facility_id"),
					new DbField("ST_HEALTH_EVENTS", "event_id", "event_id"),
					new DbField("ST_HEALTH_EVENTS", "lab_date", "lab_date") },
					"ST_HEALTH_EVENTS", null, new DbField[] {
							new DbField("ST_HEALTH_EVENTS", "mci_id",
									"mci_id") }),
			new DbView(10, new DbField[] {
					new DbField("ST_HEALTH_PROVIDERS", "mci_id", "mci_id"),
					new DbField("ST_HEALTH_PROVIDERS", "provider_id",
							"provider_id"),
					new DbField("ST_HEALTH_PROVIDERS", "facility_id",
							"facility_id") },
					"ST_HEALTH_PROVIDERS", null, new DbField[] {
							new DbField("ST_HEALTH_PROVIDERS", "mci_id",
									"mci_id") }),
			new DbView(11, new DbField[] {
					new DbField("ST_FROZEN_IDS", "mci_id", "mci_id"),
					new DbField("ST_FROZEN_IDS", "src_system_id",
							"src_system_id") },
					"ST_FROZEN_IDS", null, new DbField[] {
							new DbField("ST_FROZEN_IDS", "mci_id", "mci_id") }),
			new DbView(12, new DbField[] {
					new DbField(null, "mci_id", "mci_id"),
					new DbField(null, "src_system_id", "src_system_id"),
					new DbField(null, "patient_id", "patient_id") },
					"TB_PATIENT_INDEX", null, new DbField[] {
							new DbField(null, "mci_id", "mci_id") }),
			new DbView(12, new DbField[] {
					new DbField(null, "mci_id", "mci_id"),
					new DbField(null, "src_system_id", "src_system_id"),
					new DbField(null, "patient_id", "patient_id") },
					"VW_CMT_OUTSTANDING_CLIENT_IDS", null, new DbField[] {
							new DbField(null, "mci_id", "mci_id") }) };

	public Map getIndices() {
		return indices;
	}

	private static Map<String, Map<String, Index[]>> indices = new HashMap<>();
	static {
		Map<String, Index[]> tableIndices = new HashMap<>();
		indices.put("ST_PATIENT_NAMES", tableIndices);
		Index idxST_PATIENT_NAMESi3 =
			new Index("i3", "ST_PATIENT_NAMES", new String[] {
					"first_name", "last_name", });
		Index idxST_PATIENT_NAMESi4 =
			new Index("i4", "ST_PATIENT_NAMES", new String[] {
					"last_name", "dob_y_m", });
		Index idxST_PATIENT_NAMESi5 =
			new Index("i5", "ST_PATIENT_NAMES", new String[] {
					"sound_first_name", "last_name", });
		Index idxST_PATIENT_NAMESi6 =
			new Index("i6", "ST_PATIENT_NAMES", new String[] {
					"sound_last_name", "sound_first_name", });
		Index idxST_PATIENT_NAMESi7 =
			new Index("i7", "ST_PATIENT_NAMES", new String[] {
					"dob", "sound_first_name", });
		Index idxST_PATIENT_NAMESi8 =
			new Index("i8", "ST_PATIENT_NAMES", new String[] {
					"dob_y_m", "sound_last_name", });
		tableIndices.put("first_name|", new Index[] {
				idxST_PATIENT_NAMESi3, });
		tableIndices.put("last_name|", new Index[] {
				idxST_PATIENT_NAMESi4, });
		tableIndices.put("first_name|last_name|", new Index[] {
				idxST_PATIENT_NAMESi3, });
		tableIndices.put("dob|", new Index[] {
				idxST_PATIENT_NAMESi7, });
		tableIndices.put("dob|first_name|", new Index[] {
				idxST_PATIENT_NAMESi3, idxST_PATIENT_NAMESi7, });
		tableIndices.put("dob|last_name|", new Index[] {
				idxST_PATIENT_NAMESi4, idxST_PATIENT_NAMESi7, });
		tableIndices.put("dob|first_name|last_name|", new Index[] {
				idxST_PATIENT_NAMESi3, idxST_PATIENT_NAMESi7, });
		tableIndices.put("sound_first_name|", new Index[] {
				idxST_PATIENT_NAMESi5, });
		tableIndices.put("first_name|sound_first_name|", new Index[] {
				idxST_PATIENT_NAMESi3, idxST_PATIENT_NAMESi5, });
		tableIndices.put("last_name|sound_first_name|", new Index[] {
				idxST_PATIENT_NAMESi5, });
		tableIndices.put("first_name|last_name|sound_first_name|", new Index[] {
				idxST_PATIENT_NAMESi3, idxST_PATIENT_NAMESi5, });
		tableIndices.put("dob|sound_first_name|", new Index[] {
				idxST_PATIENT_NAMESi7, });
		tableIndices.put("dob|first_name|sound_first_name|", new Index[] {
				idxST_PATIENT_NAMESi3, idxST_PATIENT_NAMESi7, });
		tableIndices.put("dob|last_name|sound_first_name|", new Index[] {
				idxST_PATIENT_NAMESi5, idxST_PATIENT_NAMESi7, });
		tableIndices.put("dob|first_name|last_name|sound_first_name|",
				new Index[] {
						idxST_PATIENT_NAMESi3, idxST_PATIENT_NAMESi7, });
		tableIndices.put("dob_y_m|", new Index[] {
				idxST_PATIENT_NAMESi8, });
		tableIndices.put("dob_y_m|first_name|", new Index[] {
				idxST_PATIENT_NAMESi3, idxST_PATIENT_NAMESi8, });
		tableIndices.put("dob_y_m|last_name|", new Index[] {
				idxST_PATIENT_NAMESi4, });
		tableIndices.put("dob_y_m|first_name|last_name|", new Index[] {
				idxST_PATIENT_NAMESi3, idxST_PATIENT_NAMESi8, });
		tableIndices.put("dob|dob_y_m|", new Index[] {
				idxST_PATIENT_NAMESi7, idxST_PATIENT_NAMESi8, });
		tableIndices.put("dob|dob_y_m|first_name|", new Index[] {
				idxST_PATIENT_NAMESi3, idxST_PATIENT_NAMESi7,
				idxST_PATIENT_NAMESi8, });
		tableIndices.put("dob|dob_y_m|last_name|", new Index[] {
				idxST_PATIENT_NAMESi4, idxST_PATIENT_NAMESi7, });
		tableIndices.put("dob|dob_y_m|first_name|last_name|", new Index[] {
				idxST_PATIENT_NAMESi3, idxST_PATIENT_NAMESi7,
				idxST_PATIENT_NAMESi8, });
		tableIndices.put("dob_y_m|sound_first_name|", new Index[] {
				idxST_PATIENT_NAMESi5, idxST_PATIENT_NAMESi8, });
		tableIndices.put("dob_y_m|first_name|sound_first_name|", new Index[] {
				idxST_PATIENT_NAMESi3, idxST_PATIENT_NAMESi5,
				idxST_PATIENT_NAMESi8, });
		tableIndices.put("dob_y_m|last_name|sound_first_name|", new Index[] {
				idxST_PATIENT_NAMESi5, idxST_PATIENT_NAMESi8, });
		tableIndices.put("dob_y_m|first_name|last_name|sound_first_name|",
				new Index[] {
						idxST_PATIENT_NAMESi3, idxST_PATIENT_NAMESi5,
						idxST_PATIENT_NAMESi8, });
		tableIndices.put("dob|dob_y_m|sound_first_name|", new Index[] {
				idxST_PATIENT_NAMESi7, idxST_PATIENT_NAMESi8, });
		tableIndices.put("dob|dob_y_m|first_name|sound_first_name|",
				new Index[] {
						idxST_PATIENT_NAMESi3, idxST_PATIENT_NAMESi7,
						idxST_PATIENT_NAMESi8, });
		tableIndices.put("dob|dob_y_m|last_name|sound_first_name|",
				new Index[] {
						idxST_PATIENT_NAMESi4, idxST_PATIENT_NAMESi7, });
		tableIndices.put("dob|dob_y_m|first_name|last_name|sound_first_name|",
				new Index[] {
						idxST_PATIENT_NAMESi3, idxST_PATIENT_NAMESi7,
						idxST_PATIENT_NAMESi8, });
		tableIndices.put("sound_last_name|", new Index[] {
				idxST_PATIENT_NAMESi6, });
		tableIndices.put("first_name|sound_last_name|", new Index[] {
				idxST_PATIENT_NAMESi3, idxST_PATIENT_NAMESi6, });
		tableIndices.put("last_name|sound_last_name|", new Index[] {
				idxST_PATIENT_NAMESi4, idxST_PATIENT_NAMESi6, });
		tableIndices.put("first_name|last_name|sound_last_name|", new Index[] {
				idxST_PATIENT_NAMESi3, idxST_PATIENT_NAMESi6, });
		tableIndices.put("dob|sound_last_name|", new Index[] {
				idxST_PATIENT_NAMESi6, idxST_PATIENT_NAMESi7, });
		tableIndices.put("dob|first_name|sound_last_name|", new Index[] {
				idxST_PATIENT_NAMESi3, idxST_PATIENT_NAMESi6,
				idxST_PATIENT_NAMESi7, });
		tableIndices.put("dob|last_name|sound_last_name|", new Index[] {
				idxST_PATIENT_NAMESi4, idxST_PATIENT_NAMESi6,
				idxST_PATIENT_NAMESi7, });
		tableIndices.put("dob|first_name|last_name|sound_last_name|",
				new Index[] {
						idxST_PATIENT_NAMESi3, idxST_PATIENT_NAMESi6,
						idxST_PATIENT_NAMESi7, });
		tableIndices.put("sound_first_name|sound_last_name|", new Index[] {
				idxST_PATIENT_NAMESi6, });
		tableIndices.put("first_name|sound_first_name|sound_last_name|",
				new Index[] {
						idxST_PATIENT_NAMESi3, idxST_PATIENT_NAMESi6, });
		tableIndices.put("last_name|sound_first_name|sound_last_name|",
				new Index[] {
						idxST_PATIENT_NAMESi5, idxST_PATIENT_NAMESi6, });
		tableIndices.put(
				"first_name|last_name|sound_first_name|sound_last_name|",
				new Index[] {
						idxST_PATIENT_NAMESi3, idxST_PATIENT_NAMESi6, });
		tableIndices.put("dob|sound_first_name|sound_last_name|", new Index[] {
				idxST_PATIENT_NAMESi6, idxST_PATIENT_NAMESi7, });
		tableIndices.put("dob|first_name|sound_first_name|sound_last_name|",
				new Index[] {
						idxST_PATIENT_NAMESi3, idxST_PATIENT_NAMESi6,
						idxST_PATIENT_NAMESi7, });
		tableIndices.put("dob|last_name|sound_first_name|sound_last_name|",
				new Index[] {
						idxST_PATIENT_NAMESi5, idxST_PATIENT_NAMESi6,
						idxST_PATIENT_NAMESi7, });
		tableIndices.put(
				"dob|first_name|last_name|sound_first_name|sound_last_name|",
				new Index[] {
						idxST_PATIENT_NAMESi3, idxST_PATIENT_NAMESi6,
						idxST_PATIENT_NAMESi7, });
		tableIndices.put("dob_y_m|sound_last_name|", new Index[] {
				idxST_PATIENT_NAMESi8, });
		tableIndices.put("dob_y_m|first_name|sound_last_name|", new Index[] {
				idxST_PATIENT_NAMESi3, idxST_PATIENT_NAMESi8, });
		tableIndices.put("dob_y_m|last_name|sound_last_name|", new Index[] {
				idxST_PATIENT_NAMESi4, idxST_PATIENT_NAMESi8, });
		tableIndices.put("dob_y_m|first_name|last_name|sound_last_name|",
				new Index[] {
						idxST_PATIENT_NAMESi3, idxST_PATIENT_NAMESi8, });
		tableIndices.put("dob|dob_y_m|sound_last_name|", new Index[] {
				idxST_PATIENT_NAMESi7, idxST_PATIENT_NAMESi8, });
		tableIndices.put("dob|dob_y_m|first_name|sound_last_name|",
				new Index[] {
						idxST_PATIENT_NAMESi3, idxST_PATIENT_NAMESi7,
						idxST_PATIENT_NAMESi8, });
		tableIndices.put("dob|dob_y_m|last_name|sound_last_name|", new Index[] {
				idxST_PATIENT_NAMESi4, idxST_PATIENT_NAMESi7,
				idxST_PATIENT_NAMESi8, });
		tableIndices.put("dob|dob_y_m|first_name|last_name|sound_last_name|",
				new Index[] {
						idxST_PATIENT_NAMESi3, idxST_PATIENT_NAMESi7,
						idxST_PATIENT_NAMESi8, });
		tableIndices.put("dob_y_m|sound_first_name|sound_last_name|",
				new Index[] {
						idxST_PATIENT_NAMESi6, idxST_PATIENT_NAMESi8, });
		tableIndices.put("dob_y_m|first_name|sound_first_name|sound_last_name|",
				new Index[] {
						idxST_PATIENT_NAMESi3, idxST_PATIENT_NAMESi6,
						idxST_PATIENT_NAMESi8, });
		tableIndices.put("dob_y_m|last_name|sound_first_name|sound_last_name|",
				new Index[] {
						idxST_PATIENT_NAMESi5, idxST_PATIENT_NAMESi8, });
		tableIndices.put(
				"dob_y_m|first_name|last_name|sound_first_name|sound_last_name|",
				new Index[] {
						idxST_PATIENT_NAMESi3, idxST_PATIENT_NAMESi6,
						idxST_PATIENT_NAMESi8, });
		tableIndices.put("dob|dob_y_m|sound_first_name|sound_last_name|",
				new Index[] {
						idxST_PATIENT_NAMESi7, idxST_PATIENT_NAMESi8, });
		tableIndices.put(
				"dob|dob_y_m|first_name|sound_first_name|sound_last_name|",
				new Index[] {
						idxST_PATIENT_NAMESi3, idxST_PATIENT_NAMESi7,
						idxST_PATIENT_NAMESi8, });
		tableIndices.put(
				"dob|dob_y_m|last_name|sound_first_name|sound_last_name|",
				new Index[] {
						idxST_PATIENT_NAMESi5, idxST_PATIENT_NAMESi7,
						idxST_PATIENT_NAMESi8, });
		tableIndices.put(
				"dob|dob_y_m|first_name|last_name|sound_first_name|sound_last_name|",
				new Index[] {
						idxST_PATIENT_NAMESi3, idxST_PATIENT_NAMESi7,
						idxST_PATIENT_NAMESi8, });
	}
}
