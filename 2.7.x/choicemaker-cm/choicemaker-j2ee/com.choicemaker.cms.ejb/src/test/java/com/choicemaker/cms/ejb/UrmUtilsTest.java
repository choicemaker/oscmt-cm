<<<<<<< HEAD
import com.choicemaker.cm.batch.api.BatchJob;

public class UrmUtilsTest {

	@Test
	public void testIsUrmJob() {
		BatchJob job = UrmJobFactoryForTest.createUrmJobForTest();
		boolean computed = UrmUtils.isUrmJob(job);
		assertTrue(computed);
	}

}

