package com.choicemaker.cm.matching.en.us;

public class InputAndExpectedTestData {

	private static final int IDX_RAW_FN = 0;
	private static final int IDX_RAW_MN = 1;
	private static final int IDX_RAW_LN = 2;
	private static final int IDX_PARSED_FN = 3;
	private static final int IDX_PARSED_MN = 4;
	private static final int IDX_PARSED_LN = 5;
	private static final int IDX_PARSED_TITLES = 6;
	private static final int IDX_PARSED_MAIDEN = 7;
	private static final int IDX_PARSED_MOMSFN = 8;

	/**
	 * Without any collections loaded, the default constructor for AdhocNameParser
	 * should still handle basic name parsing, including guesses at probable
	 * mother's maiden names.
	 */
	private static final String[][] _testData = new String[][] {
			new String[] {
					"", "", "", "", "", "", "", "", "" },
			new String[] {
					"XVE", "", "NEEMEN", "XVE", "", "NEEMEN", "", "", "" },
			new String[] {
					"XEECEL", "", "NEVEX-GEMEX", "XEECEL", "", "GEMEX", "",
					"NEVEX", "" },
			new String[] {
					"XEERE", "TRE'XER", "NELXEN", "XEERE", "TREXER", "NELXEN",
					"", "", "" },
			new String[] {
					"XXRXEH", "X", "NELXEN", "XXRXEH", "X", "NELXEN", "", "",
					"" },
			new String[] {
					"XEXHEE", "KENGXTEN", "NECHELEX-GEFF", "XEXHEE",
					"KENGXTEN", "GEFF", "", "NECHELEX", "" },
			new String[] {
					"XXRXEH", "XEE'", "NELXEN", "XXRXEH", "XEE", "NELXEN", "",
					"", "" },
			new String[] {
					"XELEN", "DEN-ENTHENX", "NELXEN", "XELEN", "DEN-ENTHENX",
					"NELXEN", "", "", "" },
			new String[] {
					"KXLE", "NGEXEN", "E'XELLEVEN", "KXLE", "NGEXEN",
					"EXELLEVEN", "", "", "" }, };

	public static InputAndExpected[] getTestData() {
		final int length = _testData.length;
		InputAndExpected[] retVal = new InputAndExpected[length];
		for (int i = 0; i < length; i++) {
			String RAW_FN = _testData[i][IDX_RAW_FN];
			String RAW_MN = _testData[i][IDX_RAW_MN];
			String RAW_LN = _testData[i][IDX_RAW_LN];
			String PARSED_FN = _testData[i][IDX_PARSED_FN];
			String PARSED_MN = _testData[i][IDX_PARSED_MN];
			String PARSED_LN = _testData[i][IDX_PARSED_LN];
			String PARSED_TITLES = _testData[i][IDX_PARSED_TITLES];
			String PARSED_MAIDEN = _testData[i][IDX_PARSED_MAIDEN];
			String PARSED_MOMSFN = _testData[i][IDX_PARSED_MOMSFN];
			AdhocName p = new AdhocName(PARSED_FN, PARSED_MN, PARSED_LN);
			p.setMothersFirstName(PARSED_MOMSFN);
			p.setPotentialMaidenName(PARSED_MAIDEN);
			p.setTitles(PARSED_TITLES);
			InputAndExpected iae =
				new InputAndExpected(RAW_FN, RAW_MN, RAW_LN, p);
			retVal[i] = iae;
		}
		return retVal;
	}

	private InputAndExpectedTestData() {
	}

}
