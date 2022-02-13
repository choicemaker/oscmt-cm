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
package com.choicemaker.cmit.trans;

import static com.choicemaker.cm.args.BatchProcessingConstants.EVT_DONE;
import static com.choicemaker.cm.args.BatchProcessingConstants.PCT_DONE;

import java.util.logging.Logger;

import javax.jms.Queue;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cmit.testconfigs.SimplePersonSqlServerTestConfiguration;
import com.choicemaker.cmit.utils.j2ee.BatchProcessingPhase;

/**
 * This class is reused in other modules to perform a test of transitivity
 * analysis. It differs from {@link TransSerializerMdbIT} only in that it lacks
 * an Arquillian shrink-wrap method and an Arquillian <code>RunWith</code>
 * directive.
 * 
 * @author rphall
 */
public class TransSerializerMdbProcessing extends
		AbstractTransitivityMdbTest<SimplePersonSqlServerTestConfiguration> {

	public static final String LOG_SOURCE = TransSerializerMdbProcessing.class
			.getSimpleName();

	private static final Logger logger = Logger.getLogger(TransSerializerMdbProcessing.class
			.getName());

	public TransSerializerMdbProcessing() {
		super(LOG_SOURCE, logger, EVT_DONE, PCT_DONE,
				SimplePersonSqlServerTestConfiguration.class,
				BatchProcessingPhase.FINAL);
	}

	@Override
	public final Queue getResultQueue() {
		return null;
	}

	/** Stubbed implementation that does not check the working directory */
	@Override
	public boolean isWorkingDirectoryCorrectAfterProcessing(BatchJob batchJob) {
		return true;
	}

}
