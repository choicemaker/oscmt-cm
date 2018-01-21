package com.choicemaker.cms.ejb;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.choicemaker.client.api.DataAccessObject;
import com.choicemaker.client.api.IGraphProperty;
import com.choicemaker.cm.args.TransitivityException;
import com.choicemaker.cm.core.Match;
import com.choicemaker.cm.transitivity.core.CompositeEntity;
import com.choicemaker.cm.transitivity.core.INode;
import com.choicemaker.cm.transitivity.core.Link;
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
		ExpectedResult<Integer> testdata = TestData.createResult01();
		final DataAccessObject<Integer> query = testdata.getInputQueryRecord();
		final List<Match> matchList = testdata.getInputMatchList();
		final AbaParameters parameters = testdata.getInputAbaParameters();
		IGraphProperty mergeConnectivity = testdata.getInputMergeConnectivity();

		OnlineDelegate<Integer> delegate = new OnlineDelegate<Integer>();
		try {
			final CompositeEntity computed =
				delegate.computeCompositeEntity(query, matchList, parameters,
						mergeConnectivity);
			assertTrue(computed != null);
			System.out.println(computed);
			final CompositeEntity expected =
				testdata.getExpectedCompositeEntity();
			assertTrue(expected != null);
			assertTrue(equals(expected, computed));
			assertTrue(computed.equalsIgnoreId(expected));
		} catch (TransitivityException e) {
			e.printStackTrace();
			fail(e.toString());
		}

		fail("Not yet implemented");
	}

	public static boolean equals(
			CompositeEntity ce1, CompositeEntity ce2) {
		boolean retVal =
			(ce1 == null && ce2 == null) || (ce1 != null && ce2 != null);
		done: if (ce1 != null && ce2 != null) {
			INode<?> firstNode1 = ce1.getFirstNode();
			INode<?> firstNode2 = ce2.getFirstNode();
			retVal = (firstNode1 == null && firstNode2 == null)
					|| (firstNode1 != null && firstNode1.equals(firstNode2));
			if (!retVal)
				break done;

			List<Link<?>> links1 = ce1.getAllLinks();
			assert links1 != null;
			List<Link<?>> links2 = ce2.getAllLinks();
			assert links2 != null;
			retVal = links1.equals(links2);
			if (!retVal)
				break done;
			
			Set<INode<?>> nodes1 = new HashSet<>();
			CompositeEntity.getAllAccessibleNodes(ce1, nodes1, firstNode1);
			Set<INode<?>> nodes2 = new HashSet<>();
			CompositeEntity.getAllAccessibleNodes(ce2, nodes2, firstNode2);
			retVal = nodes1.equals(nodes2);
//			if (!retVal)
//				break done;
		}
		return retVal;
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
