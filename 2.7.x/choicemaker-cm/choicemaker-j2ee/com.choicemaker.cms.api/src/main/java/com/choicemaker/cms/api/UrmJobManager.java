/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cms.api;

import java.util.List;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchJobManager;

/**
 * Creates, saves, deletes and finds top-level URM jobs.
 */
public interface UrmJobManager extends BatchJobManager {

	BatchJob createPersistentUrmJob(String externalID);

	List<BatchJob> findAllLinkedByUrmId(long urmJobId);

	BatchJob findUrmJob(long id);

	List<BatchJob> findAllUrmJobs();

}
