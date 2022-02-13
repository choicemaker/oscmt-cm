/*******************************************************************************
 * Copyright (c) 2003, 2018 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc  - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
package com.choicemaker.cmit.oaba;

import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.EVT_DONE_DEDUP_OVERSIZED;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.PCT_DONE_DEDUP_OVERSIZED;

import java.util.logging.Logger;

import javax.jms.Queue;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.junit.runner.RunWith;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.oaba.ejb.Chunk2MDB;
import com.choicemaker.cmit.oaba.util.OabaDeploymentUtils;
import com.choicemaker.cmit.testconfigs.SimplePersonSqlServerTestConfiguration;
import com.choicemaker.cmit.utils.j2ee.BatchProcessingPhase;

@RunWith(Arquillian.class)
public class DedupMdbIT extends
		AbstractOabaMdbTest<SimplePersonSqlServerTestConfiguration> {

	private static final Logger logger = Logger.getLogger(DedupMdbIT.class
			.getName());

	private static final boolean TESTS_AS_EJB_MODULE = false;

	private final static String LOG_SOURCE = DedupMdbIT.class.getSimpleName();

	/**
	 * Creates an EAR deployment in which the OABA server JAR is missing the
	 * ChunkMDB* message beans. This allows other classes to
	 * attach to the chunkQueue and update queues for testing.
	 */
	@Deployment
	public static EnterpriseArchive createEarArchive() {
		Class<?>[] removedClasses = { Chunk2MDB.class };
		return OabaDeploymentUtils.createEarArchive(removedClasses,
				TESTS_AS_EJB_MODULE);
	}

	public DedupMdbIT() {
		super(LOG_SOURCE, logger, EVT_DONE_DEDUP_OVERSIZED,
				PCT_DONE_DEDUP_OVERSIZED,
				SimplePersonSqlServerTestConfiguration.class,
				BatchProcessingPhase.INTERMEDIATE);
	}

	@Override
	public final Queue getResultQueue() {
		return getChunkQueue();
	}

	/** Stubbed implementation that does not check the working directory */
	@Override
	public boolean isWorkingDirectoryCorrectAfterProcessing(BatchJob batchJob) {
		return true;
	}

}
