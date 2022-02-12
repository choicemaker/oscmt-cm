/*******************************************************************************
 * Copyright (c) 2003, 2016 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
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
		AdhocName pn = new AdhocName();
		assertEmptyField("getFirstName()", pn.getFirstName());
		assertEmptyField("getFirstName()", pn.getMiddleNames());
		assertEmptyField("getFirstName()", pn.getLastName());
		assertEmptyField("getFirstName()", pn.getTitles());
		assertEmptyField("getFirstName()", pn.getMothersFirstName());
		assertEmptyField("getFirstName()", pn.getPotentialMaidenName());
	}

	public void testParsedName0StringStringString() {
		AdhocName pn = new AdhocName(FN, MN, LN);
		assertTrue(pn.getFirstName() == FN);
		assertTrue(pn.getMiddleNames() == MN);
		assertTrue(pn.getLastName() == LN);
		assertEmptyField("getFirstName()", pn.getTitles());
		assertEmptyField("getFirstName()", pn.getMothersFirstName());
		assertEmptyField("getFirstName()", pn.getPotentialMaidenName());
	}

	public void testSetFirstName() {
		AdhocName pn = new AdhocName();
		pn.setFirstName(FN);
		assertTrue(FN == pn.getFirstName());
		pn.setFirstName(pad(FN));
		assertTrue(pad(FN).equals(pn.getFirstName()));
		pn.setFirstName(null);
		assertTrue(AdhocName.EMPTY == pn.getFirstName());
	}

	public void testSetMiddleNames() {
		AdhocName pn = new AdhocName();
		pn.setMiddleNames(MN);
		assertTrue(MN == pn.getMiddleNames());
		pn.setMiddleNames(pad(MN));
		assertTrue(pad(MN).equals(pn.getMiddleNames()));
		pn.setMiddleNames(null);
		assertTrue(AdhocName.EMPTY == pn.getMiddleNames());
	}

	public void testSetLastName() {
		AdhocName pn = new AdhocName();
		pn.setLastName(LN);
		assertTrue(LN == pn.getLastName());
		pn.setLastName(pad(LN));
		assertTrue(pad(LN).equals(pn.getLastName()));
		pn.setLastName(null);
		assertTrue(AdhocName.EMPTY == pn.getLastName());
	}

	public void testSetTitles() {
		AdhocName pn = new AdhocName();
		pn.setTitles(TITLES);
		assertTrue(TITLES == pn.getTitles());
		pn.setTitles(pad(TITLES));
		assertTrue(pad(TITLES).equals(pn.getTitles()));
		pn.setTitles(null);
		assertTrue(AdhocName.EMPTY == pn.getTitles());
	}

	public void testSetPotentialMaidenName() {
		AdhocName pn = new AdhocName();
		pn.setPotentialMaidenName(MAIDEN);
		assertTrue(MAIDEN == pn.getPotentialMaidenName());
		pn.setPotentialMaidenName(pad(MAIDEN));
		assertTrue(pad(MAIDEN).equals(pn.getPotentialMaidenName()));
		pn.setPotentialMaidenName(null);
		assertTrue(AdhocName.EMPTY == pn.getPotentialMaidenName());
	}

	public void testSetMothersFirstName() {
		AdhocName pn = new AdhocName();
		pn.setMothersFirstName(MOMFN);
		assertTrue(MOMFN == pn.getMothersFirstName());
		pn.setMothersFirstName(pad(MOMFN));
		assertTrue(pad(MOMFN).equals(pn.getMothersFirstName()));
		pn.setMothersFirstName(null);
		assertTrue(AdhocName.EMPTY == pn.getMothersFirstName());
	}

}
