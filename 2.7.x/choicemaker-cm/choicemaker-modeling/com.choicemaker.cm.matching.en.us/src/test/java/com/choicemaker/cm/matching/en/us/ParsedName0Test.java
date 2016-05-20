package com.choicemaker.cm.matching.en.us;

import junit.framework.TestCase;

public class ParsedName0Test extends TestCase {

	public static final String SPACE = " ";

	public static final String FN = "Pete".intern();
	public static final String MN = "Petey Petersen".intern();
	public static final String LN = "Peters".intern();
	public static final String TITLES = "Professor PhD".intern();
	public static final String MOMFN = "Patricia".intern();
	public static final String MAIDEN = "Peterson".intern();

	public static void assertEmptyField(String accessorName, String accessorValue) {
		assert accessorName != null && !accessorName.isEmpty() ;
		String msg;

		msg = "'" + accessorName + "' is null";
		assertTrue(msg, accessorValue != null);

		msg = "'" + accessorName + "' is not empty";
		assertTrue(msg, accessorValue.isEmpty());
	}

	public static String pad(String s) {
		String retVal;
		if (s != null) {
			retVal = SPACE + s  + SPACE;
		} else {
			retVal = SPACE + SPACE;
		}
		return retVal;
	}

	public void testParsedName0() {
		ParsedName0 pn = new ParsedName0();
		assertEmptyField("getFirstName()", pn.getFirstName());
		assertEmptyField("getFirstName()", pn.getMiddleNames());
		assertEmptyField("getFirstName()", pn.getLastName());
		assertEmptyField("getFirstName()", pn.getTitles());
		assertEmptyField("getFirstName()", pn.getMothersFirstName());
		assertEmptyField("getFirstName()", pn.getPotentialMaidenName());
	}

	public void testParsedName0StringStringString() {
		ParsedName0 pn = new ParsedName0(FN, MN, LN);
		assertTrue(pn.getFirstName() == FN);
		assertTrue(pn.getMiddleNames() == MN);
		assertTrue(pn.getLastName() == LN);
		assertEmptyField("getFirstName()", pn.getTitles());
		assertEmptyField("getFirstName()", pn.getMothersFirstName());
		assertEmptyField("getFirstName()", pn.getPotentialMaidenName());
	}

	public void testSetFirstName() {
		ParsedName0 pn = new ParsedName0();
		pn.setFirstName(FN);
		assertTrue(FN == pn.getFirstName());
		pn.setFirstName(pad(FN));
		assertTrue(pad(FN).equals(pn.getFirstName()));
		pn.setFirstName(null);
		assertTrue(ParsedName0.EMPTY == pn.getFirstName());
	}

	public void testSetMiddleNames() {
		ParsedName0 pn = new ParsedName0();
		pn.setMiddleNames(MN);
		assertTrue(MN == pn.getMiddleNames());
		pn.setMiddleNames(pad(MN));
		assertTrue(pad(MN).equals(pn.getMiddleNames()));
		pn.setMiddleNames(null);
		assertTrue(ParsedName0.EMPTY == pn.getMiddleNames());
	}

	public void testSetLastName() {
		ParsedName0 pn = new ParsedName0();
		pn.setLastName(LN);
		assertTrue(LN == pn.getLastName());
		pn.setLastName(pad(LN));
		assertTrue(pad(LN).equals(pn.getLastName()));
		pn.setLastName(null);
		assertTrue(ParsedName0.EMPTY == pn.getLastName());
	}

	public void testSetTitles() {
		ParsedName0 pn = new ParsedName0();
		pn.setTitles(TITLES);
		assertTrue(TITLES == pn.getTitles());
		pn.setTitles(pad(TITLES));
		assertTrue(pad(TITLES).equals(pn.getTitles()));
		pn.setTitles(null);
		assertTrue(ParsedName0.EMPTY == pn.getTitles());
	}

	public void testSetPotentialMaidenName() {
		ParsedName0 pn = new ParsedName0();
		pn.setPotentialMaidenName(MAIDEN);
		assertTrue(MAIDEN == pn.getPotentialMaidenName());
		pn.setPotentialMaidenName(pad(MAIDEN));
		assertTrue(pad(MAIDEN).equals(pn.getPotentialMaidenName()));
		pn.setPotentialMaidenName(null);
		assertTrue(ParsedName0.EMPTY == pn.getPotentialMaidenName());
	}

	public void testSetMothersFirstName() {
		ParsedName0 pn = new ParsedName0();
		pn.setMothersFirstName(MOMFN);
		assertTrue(MOMFN == pn.getMothersFirstName());
		pn.setMothersFirstName(pad(MOMFN));
		assertTrue(pad(MOMFN).equals(pn.getMothersFirstName()));
		pn.setMothersFirstName(null);
		assertTrue(ParsedName0.EMPTY == pn.getMothersFirstName());
	}

}
