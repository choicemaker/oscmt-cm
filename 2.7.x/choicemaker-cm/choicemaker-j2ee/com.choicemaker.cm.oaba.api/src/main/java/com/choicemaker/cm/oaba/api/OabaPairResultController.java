/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.api;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.oaba.core.IMatchRecord2Sink;
import com.choicemaker.cm.oaba.core.IMatchRecord2Source;
import com.choicemaker.cm.oaba.core.RECORD_ID_TYPE;

public interface OabaPairResultController {

	void saveResults(BatchJob job, IMatchRecord2Source<?> results)
			throws BlockingException;

	int getResultCount(BatchJob job) throws BlockingException;

	RECORD_ID_TYPE getResultType(BatchJob job) throws BlockingException;

	void getResults(BatchJob job, IMatchRecord2Sink<?> results)
			throws BlockingException;

}
