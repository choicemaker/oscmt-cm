package com.choicemaker.cmit.oaba;

import static com.choicemaker.cm.io.blocking.automated.offline.core.OabaProcessing.EVT_DONE_DEDUP_OVERSIZED;
import static com.choicemaker.cm.io.blocking.automated.offline.core.OabaProcessing.PCT_DONE_DEDUP_OVERSIZED;

import java.util.logging.Logger;

import javax.jms.Queue;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.junit.runner.RunWith;

import com.choicemaker.cm.io.blocking.automated.offline.server.impl.ChunkMDB;
import com.choicemaker.cm.io.blocking.automated.offline.server.impl.Chunk2MDB;
import com.choicemaker.cm.io.blocking.automated.offline.server.impl.UpdateStatusMDB;
import com.choicemaker.cmit.AbstractOabaMdbTest;
import com.choicemaker.cmit.oaba.util.OabaDeploymentUtils;
import com.choicemaker.cmit.utils.OabaProcessingPhase;
import com.choicemaker.cmit.utils.SimplePersonSqlServerTestConfiguration;

@RunWith(Arquillian.class)
public class DedupMdbIT extends
		AbstractOabaMdbTest<SimplePersonSqlServerTestConfiguration> {

	private static final Logger logger = Logger.getLogger(DedupMdbIT.class
			.getName());

	private static final boolean TESTS_AS_EJB_MODULE = true;

	private final static String LOG_SOURCE = DedupMdbIT.class
			.getSimpleName();

	/**
	 * Creates an EAR deployment in which the OABA server JAR is missing the
	 * ChunkMDB* and UpdateStatusMDB message beans. This allows other classes to
	 * attach to the chunkQueue and update queues for testing.
	 */
	@Deployment
	public static EnterpriseArchive createEarArchive() {
		Class<?>[] removedClasses = {
				ChunkMDB.class, Chunk2MDB.class, UpdateStatusMDB.class };
		return OabaDeploymentUtils.createEarArchive(removedClasses,
				TESTS_AS_EJB_MODULE);
	}

	public DedupMdbIT() {
		super(LOG_SOURCE, logger, EVT_DONE_DEDUP_OVERSIZED,
				PCT_DONE_DEDUP_OVERSIZED,
				SimplePersonSqlServerTestConfiguration.class,
				OabaProcessingPhase.INTERMEDIATE);
	}

	@Override
	public final Queue getResultQueue() {
		return getChunkQueue();
	}

	/** Stubbed implementation that does not check the working directory */
	@Override
	public boolean isWorkingDirectoryCorrectAfterLinkageProcessing() {
		return true;
	}

	/** Stubbed implementation that does not check the working directory */
	@Override
	public boolean isWorkingDirectoryCorrectAfterDeduplicationProcessing() {
		return true;
	}

}