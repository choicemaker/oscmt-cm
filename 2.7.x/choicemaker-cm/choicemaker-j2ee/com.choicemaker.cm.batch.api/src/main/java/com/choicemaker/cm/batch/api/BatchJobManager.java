/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.batch.api;

import java.util.List;

/*
 * Saves, deletes and finds batch jobs. (Job creation is defined by derived
 * interfaces.)
 */
public interface BatchJobManager {

	void delete(BatchJob batchJob);

	void detach(BatchJob oabaJob);

	BatchJob findBatchJob(long id);

	List<BatchJob> findAll();

	BatchJob save(BatchJob batchJob);

}
