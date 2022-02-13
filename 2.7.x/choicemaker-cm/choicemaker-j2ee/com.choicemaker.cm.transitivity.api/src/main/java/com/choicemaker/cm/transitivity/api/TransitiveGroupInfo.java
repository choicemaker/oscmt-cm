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
package com.choicemaker.cm.transitivity.api;

import java.util.List;

import com.choicemaker.cm.batch.api.BATCH_RESULTS_PERSISTENCE_SCHEME;
import com.choicemaker.cm.oaba.core.RECORD_ID_TYPE;

public interface TransitiveGroupInfo {

	List<String> getGroupFileURIs();
	
	int getHoldGroupCount();
	
	int getMergeGroupCount();
	
	BATCH_RESULTS_PERSISTENCE_SCHEME getPersistenceScheme();

	RECORD_ID_TYPE getRecordIdType();

}
