package com.choicemaker.cmtblocking;

import static com.choicemaker.cmtblocking.BlockingCallArguments.SEPARATOR;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.choicemaker.util.SystemPropertyUtils;

public class UnionLiteralTest {

	public static final String blockConfig = "MciRecords:b:batch:patient";

	public static final String compressedSql = "/*+ index_join(v0 i3,i7) */ "
			+ "v0.mci_id FROM ST_PATIENT_NAMES v0`v0.dob='1970-01-01' AND v0.first_name='JOHN'"
			+ "^/*+ index_join(v0 i6,i7) */ "
			+ "v0.mci_id FROM ST_PATIENT_NAMES v0`v0.dob='1970-01-01' AND v0.sound_last_name='P362'"
			+ "^/*+ index_join(v0 i3,i6) */ "
			+ "v0.mci_id FROM ST_PATIENT_NAMES v0`v0.first_name='DOE' AND v0.sound_last_name='P362'"
			+ "^ v0.mci_id FROM ST_MOTHERS_MAIDEN_NAME v0,ST_PATIENT_NAMES v1`v0.mothers_maiden_name='JOHN' AND v1.last_name='DOE'"
			+ "^/*+ index_join(v0 i4,i7) */ "
			+ "v0.mci_id FROM ST_PATIENT_NAMES v0`v0.dob='1970-01-01' AND v0.last_name='DOE'"
			+ "^ v0.mci_id FROM ST_PATIENT_NAMES v0`v0.first_name='JOHN' AND v0.last_name='DOE'"
			+ "^ v0.mci_id FROM ST_MOTHERS_MAIDEN_NAME v0,ST_PATIENT_NAMES v1`v0.mothers_maiden_name='JOHN' AND v1.dob_y_m='197001'"
			+ "^ v0.mci_id FROM ST_PATIENT_NAMES v0`v0.last_name='JOHN' AND v0.dob_y_m='197001'"
			+ "^/*+ index_join(v0 i3,i8) */ v0.mci_id FROM ST_PATIENT_NAMES v0`v0.first_name='DOE' AND v0.dob_y_m='197001'"
			+ "^ v0.mci_id FROM ST_MOTHERS_MAIDEN_NAME v0,ST_PATIENT_NAMES v1`v0.mothers_maiden_name='JOHN' AND v1.sound_first_name='L000'"
			+ "^ v0.mci_id FROM ST_PATIENT_NAMES v0`v0.last_name='JOHN' AND v0.sound_first_name='L000'"
			+ "^ v0.mci_id FROM ST_PATIENT_NAMES v0`v0.dob='1970-01-01' AND v0.sound_first_name='L000'"
			+ "^/*+ index_join(v0 i6,i8) */ v0.mci_id FROM ST_PATIENT_NAMES v0`v0.sound_last_name='P362' AND v0.dob_y_m='197001' AND v0.sound_first_name='L000'";

	public static final String EOL = SystemPropertyUtils.PV_LINE_SEPARATOR;

	public static final String expectedSql =
		"SELECT /*+ index_join(v0 i3,i7) */ v0.mci_id FROM ST_PATIENT_NAMES v0 WHERE v0.dob='1970-01-01' AND v0.first_name='JOHN'"
				+ EOL
				+ "UNION SELECT /*+ index_join(v0 i6,i7) */ v0.mci_id FROM ST_PATIENT_NAMES v0 WHERE v0.dob='1970-01-01' AND v0.sound_last_name='P362'"
				+ EOL
				+ "UNION SELECT /*+ index_join(v0 i3,i6) */ v0.mci_id FROM ST_PATIENT_NAMES v0 WHERE v0.first_name='DOE' AND v0.sound_last_name='P362'"
				+ EOL
				+ "UNION SELECT  v0.mci_id FROM ST_MOTHERS_MAIDEN_NAME v0,ST_PATIENT_NAMES v1 WHERE v0.mci_id = v1.mci_id AND v0.mothers_maiden_name='JOHN' AND v1.last_name='DOE'"
				+ EOL
				+ "UNION SELECT /*+ index_join(v0 i4,i7) */ v0.mci_id FROM ST_PATIENT_NAMES v0 WHERE v0.dob='1970-01-01' AND v0.last_name='DOE'"
				+ EOL
				+ "UNION SELECT  v0.mci_id FROM ST_PATIENT_NAMES v0 WHERE v0.first_name='JOHN' AND v0.last_name='DOE'"
				+ EOL
				+ "UNION SELECT  v0.mci_id FROM ST_MOTHERS_MAIDEN_NAME v0,ST_PATIENT_NAMES v1 WHERE v0.mci_id = v1.mci_id AND v0.mothers_maiden_name='JOHN' AND v1.dob_y_m='197001'"
				+ EOL
				+ "UNION SELECT  v0.mci_id FROM ST_PATIENT_NAMES v0 WHERE v0.last_name='JOHN' AND v0.dob_y_m='197001'"
				+ EOL
				+ "UNION SELECT /*+ index_join(v0 i3,i8) */ v0.mci_id FROM ST_PATIENT_NAMES v0 WHERE v0.first_name='DOE' AND v0.dob_y_m='197001'"
				+ EOL
				+ "UNION SELECT  v0.mci_id FROM ST_MOTHERS_MAIDEN_NAME v0,ST_PATIENT_NAMES v1 WHERE v0.mci_id = v1.mci_id AND v0.mothers_maiden_name='JOHN' AND v1.sound_first_name='L000'"
				+ EOL
				+ "UNION SELECT  v0.mci_id FROM ST_PATIENT_NAMES v0 WHERE v0.last_name='JOHN' AND v0.sound_first_name='L000'"
				+ EOL
				+ "UNION SELECT  v0.mci_id FROM ST_PATIENT_NAMES v0 WHERE v0.dob='1970-01-01' AND v0.sound_first_name='L000'"
				+ EOL
				+ "UNION SELECT /*+ index_join(v0 i6,i8) */ v0.mci_id FROM ST_PATIENT_NAMES v0 WHERE v0.sound_last_name='P362' AND v0.dob_y_m='197001' AND v0.sound_first_name='L000'"
				+ EOL;

	public static final String condition1 = "fake1";

	public static final String condition2 = "fake2";

	public static final String readConfig = "MciRecords:r:patient";

	public static final String EMPTY = " ";

	public static final String line = blockConfig + SEPARATOR + compressedSql
			+ SEPARATOR + EMPTY + SEPARATOR + EMPTY + SEPARATOR + readConfig;

	public static final String line1 =
		blockConfig + SEPARATOR + compressedSql + SEPARATOR + condition1
				+ SEPARATOR + EMPTY + SEPARATOR + readConfig;

	public static final String line2 =
		blockConfig + SEPARATOR + compressedSql + SEPARATOR + EMPTY + SEPARATOR
				+ condition2 + SEPARATOR + readConfig;

	@Test
	public void testUnionLiteral() {

		UnionLiteral ul;

		ul = new UnionLiteral(line);
		assertTrue(blockConfig.equals(ul.getBlockConfig()));
		assertTrue(compressedSql.equals(ul.getQuery()));
		assertTrue(ul.getCondition1() == null);
		assertTrue(ul.getCondition2() == null);
		assertTrue(readConfig.equals(ul.getReadConfig()));

		ul = new UnionLiteral(line1);
		assertTrue(blockConfig.equals(ul.getBlockConfig()));
		assertTrue(compressedSql.equals(ul.getQuery()));
		assertTrue(condition1.equals(ul.getCondition1()));
		assertTrue(ul.getCondition2() == null);
		assertTrue(readConfig.equals(ul.getReadConfig()));

		ul = new UnionLiteral(line2);
		assertTrue(blockConfig.equals(ul.getBlockConfig()));
		assertTrue(compressedSql.equals(ul.getQuery()));
		assertTrue(ul.getCondition1() == null);
		assertTrue(condition2.equals(ul.getCondition2()));
		assertTrue(readConfig.equals(ul.getReadConfig()));
	}

	@Test
	public void testComputeSqlString() {
		UnionLiteral ul = new UnionLiteral(line);
		String computedSql = ul.computeSql();
		assertTrue(expectedSql.equals(computedSql));
	}

}
