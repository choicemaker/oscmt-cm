import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.choicemaker.cm.batch.api.BatchJob;

public class UrmUtilsTest {

	@Test
	public void testIsUrmJob() {
		BatchJob job = UrmJobFactoryForTest.createUrmJobForTest();
		boolean computed = UrmUtils.isUrmJob(job);
		assertTrue(computed);
	}

}

