/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.ejb;

import static com.choicemaker.cm.oaba.ejb.OabaJobJPA.DISCRIMINATOR_VALUE;

import com.choicemaker.cm.batch.api.BatchJob;

public class OabaEjbUtils {

	private OabaEjbUtils() {
	}

	public static boolean isOabaJob(BatchJob batchJob) {
		boolean retVal = false;
		if (batchJob != null) {
			retVal = DISCRIMINATOR_VALUE.equals(batchJob.getBatchJobType());
		}
		return retVal;
	}

}
