/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.ejb.util;

import static com.choicemaker.cm.transitivity.ejb.TransitivityJobJPA.DISCRIMINATOR_VALUE;

import com.choicemaker.cm.batch.api.BatchJob;

public class TransitivityUtils {

	private TransitivityUtils() {
	}

	public static boolean isTransitivityJob(BatchJob batchJob) {
		boolean retVal = false;
		if (batchJob != null) {
			retVal = DISCRIMINATOR_VALUE.equals(batchJob.getBatchJobType());
		}
		return retVal;
	}

}
