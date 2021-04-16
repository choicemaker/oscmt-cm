package com.choicemaker.cm.io.db.postgres2;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.io.db.base.DbField;
import com.choicemaker.cm.io.db.base.DbReaderParallel;
import com.choicemaker.cm.io.db.base.DbView;
import com.choicemaker.cm.io.db.base.Index;

public final class MockDbReaderParallel implements DbReaderParallel {

	@Override
	public String getName() {
		return "MciRecords:r:patient";
	}


	@Override
	public void open(ResultSet[] rs) throws java.sql.SQLException {
	}

	@Override
	public Record<?> getNext() throws java.sql.SQLException {
		return null;
	}

	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public int getNoCursors() {
		return NO_CURSORS;
	}

	static final int NO_CURSORS = 13;

	@Override
	public String getMasterId() {
		return masterId;
	}

	static final String masterId = "mci_id";
	static final String masterIdType = "number(9)";

	@Override
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

	@Override
	public Map<String, Map<String, Index[]>> getIndices() {
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
