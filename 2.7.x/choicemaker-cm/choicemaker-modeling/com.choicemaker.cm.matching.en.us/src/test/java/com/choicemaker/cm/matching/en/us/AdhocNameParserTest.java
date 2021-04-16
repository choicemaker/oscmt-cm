package com.choicemaker.cm.matching.en.us;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import com.choicemaker.cm.core.PropertyNames;

import junit.framework.TestCase;

/**
 * This test is assumed to run in an E2 context in which no installable
 * ChoiceMaker configurator has been been specified, so that the default
 * configurator is a do-nothing implementation.
 * 
 * @author rphall
 *
 */
public class AdhocNameParserTest extends TestCase {

	private static final Logger logger = Logger.getLogger(AdhocNameParserTest.class
			.getName());

	public void assertDefaultConfigurator() {
		String fqcn =
			System.getProperty(PropertyNames.INSTALLABLE_CHOICEMAKER_CONFIGURATOR);
		assertTrue(fqcn == null);
	}

	public void assertEmptyCollection(String accessorName, Collection<?> c) {
		assert accessorName != null && !accessorName.isEmpty();
		String msg;

		msg = "'" + accessorName + "' is null";
		assertTrue(msg, c != null);

		msg = "'" + accessorName + "' is not empty";
		assertTrue(msg, c.isEmpty());
	}

	public void testGetDefaultInstance() {
		assertDefaultConfigurator();
		final AdhocNameParser np =
			AdhocNameParser
					.lookupAdhocNameParser(AdhocNameParser.DEFAULT_ADHOC_NAME_PARSER);
		assertEmptyCollection("getChildOfIndicators()",
				np.getChildOfIndicators());
		assertEmptyCollection("getGenericFirstNames()",
				np.getGenericFirstNames());
		assertEmptyCollection("getInvalidLastNames()", np.getInvalidLastNames());
		assertEmptyCollection("getLastNamePrefixes()", np.getLastNamePrefixes());
		assertEmptyCollection("getNameTitles()", np.getNameTitles());
	}

	public void testParse() throws Exception {
		final InputAndExpected[] testData =
			InputAndExpectedTestData.getTestData();
		final AdhocNameParser np =
				AdhocNameParser
						.lookupAdhocNameParser(AdhocNameParser.DEFAULT_ADHOC_NAME_PARSER);
		final List<String> exceptions = new ArrayList<>();
		final List<String> errors = new ArrayList<>();
		for (int i = 0; i < testData.length; i++) {
			InputAndExpected iae = null;
			String f = null;
			String m = null;
			String l = null;
			AdhocName expected = null;
			AdhocName computed = null;
			try {
				iae = testData[i];
				f = iae.first;
				m = iae.middle;
				l = iae.last;
				expected = iae.expected;
				computed = np.parse(f, m, l);
				assertTrue(computed != null);
				assertTrue(computed.equals(iae.expected));
			} catch (Exception x) {
				String msg =
					"testParse threw an exception on testData[" + i + "]: "
							+ x.toString();
				logger.severe(msg);
				exceptions.add(msg);
			} catch (AssertionError e) {
				String msg =
					"testParse failed on testData[" + i + "]: f = '" + f
							+ "', m = '" + m + "', l = '" + l
							+ "', expected = " + expected.toString()
							+ ", computed = " + computed.toString();
				logger.severe(msg);
				errors.add(msg);
			}
		}
		if (exceptions.size() > 0) {
			String msg =
				exceptions.size() + " exceptions. First occurrence: "
						+ exceptions.get(0)
						+ ". See log for any additional exceptions.";
			throw new Exception(msg);
		}
		if (errors.size() > 0) {
			String msg =
				errors.size() + " assertion failures. First failure: "
						+ errors.get(0)
						+ ". See log for any additional failures.";
			throw new AssertionError(msg);
		}
		logger.info("AdhocNameParserTest.testParse() completed successfully");
	}

	// public void testGetSwapSimilarity() {
	// fail("Not yet implemented");
	// }
	//
	// public void testFixMc() {
	// fail("Not yet implemented");
	// }
	//
	// public void testFlipToks() {
	// fail("Not yet implemented");
	// }
	//
	// public void testMatchingInitialOrName() {
	// fail("Not yet implemented");
	// }
	//
	// public void testConcatWithSeparator() {
	// fail("Not yet implemented");
	// }
	//
	// public void testSetName() {
	// fail("Not yet implemented");
	// }
	//
	// public void testSetGenericFirstNames() {
	// fail("Not yet implemented");
	// }
	//
	// public void testSetChildOfIndicators() {
	// fail("Not yet implemented");
	// }
	//
	// public void testSetInvalidLastNames() {
	// fail("Not yet implemented");
	// }
	//
	// public void testSetNameTitles() {
	// fail("Not yet implemented");
	// }
	//
	// public void testSetLastNamePrefixes() {
	// fail("Not yet implemented");
	// }

}
