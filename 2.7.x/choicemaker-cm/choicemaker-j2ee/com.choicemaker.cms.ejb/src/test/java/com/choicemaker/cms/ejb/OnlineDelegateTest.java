package com.choicemaker.cms.ejb;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import com.choicemaker.client.api.DataAccessObject;
import com.choicemaker.client.api.IGraphProperty;
import com.choicemaker.cm.args.TransitivityException;
import com.choicemaker.cm.core.Match;
import com.choicemaker.cm.transitivity.core.CompositeEntity;
import com.choicemaker.cms.api.AbaParameters;

/**
 * Tests of the transitivity methods of OnlineDelegate. Non-transitivity methods
 * are tested by integration tests.
 * 
 * @author rphall
 *
 */
public class OnlineDelegateTest {

	@Test
	public void testComputeCompositeEntity() {
		ExpectedResult testdata = null; // new ExpectedResult();
		final DataAccessObject<Integer> query = testdata.getInputQueryRecord();
		final List<Match> matchList = testdata.getInputMatchList();
		final AbaParameters parameters = testdata.getInputAbaParameters();
		IGraphProperty mergeConnectivity = testdata.getInputMergeConnectivity();

		OnlineDelegate<Integer> delegate = new OnlineDelegate<Integer>();
		try {
			final CompositeEntity<Integer> computed =
				delegate.computeCompositeEntity(query, matchList, parameters,
						mergeConnectivity);
			// final CompositeEntity expected =
			// testdata.getExpectedCompositeEntity();
		} catch (TransitivityException e) {
			e.printStackTrace();
			fail(e.toString());
		}

		fail("Not yet implemented");
	}

	@Test
	public void testGetTransitiveCandidatesDataAccessObjectOfTListOfMatchAbaParametersIGraphPropertyBoolean() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetTransitiveCandidatesDataAccessObjectOfTMapOfSafeIndexOfTMatchListOfINodeOfTImmutableProbabilityModelIGraphPropertyBoolean() {
		fail("Not yet implemented");
	}

	// @BeforeClass
	// public static void setUpBeforeClass() throws Exception {
	// }
	//
	// @AfterClass
	// public static void tearDownAfterClass() throws Exception {
	// }
	//
	// @Before
	// public void setUp() throws Exception {
	// }
	//
	// @After
	// public void tearDown() throws Exception {
	// }
	//
	// @Test
	// public void testAddQueryMatchPairToList() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testAddQueryPairToList() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void
	// testAssertValidArgumentsDataAccessObjectOfQAbaParametersAbaSettingsAbaServerConfiguration()
	// {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void
	// testAssertValidArgumentsDataAccessObjectOfTAbaParametersAbaSettingsAbaServerConfigurationAbaStatisticsController()
	// {
	// fail("Not yet implemented");
	// }

	// @Test
	// public void testCreateEvaluatedPairs() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testCreateIncompleteSpecificationMessage() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testCreateMatchMap() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetEvaluatedPair() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetMatchDao() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetMatchList() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetRecordIndicesFromPairs() {
	// fail("Not yet implemented");
	// }

	// @Test
	// public void testListIncompleteSpecifications() {
	// fail("Not yet implemented");
	// }

}
