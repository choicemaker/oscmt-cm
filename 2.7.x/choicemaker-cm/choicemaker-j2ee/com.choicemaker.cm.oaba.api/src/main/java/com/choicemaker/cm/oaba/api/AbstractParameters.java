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
package com.choicemaker.cm.oaba.api;

import com.choicemaker.cm.args.OabaLinkageType;
import com.choicemaker.cm.args.PersistentObject;

public interface AbstractParameters extends PersistentObject {

	float getLowThreshold();

	float getHighThreshold();

	OabaLinkageType getOabaLinkageType();

	String getType();

	String getModelConfigName();

	String getBlockingConfiguration();

	long getQueryRsId();

	String getQueryRsType();

	boolean isQueryRsIsDeduplicated();

	String getQueryRsDatabaseConfiguration();

	Long getReferenceRsId();

	String getReferenceRsType();

	String getReferenceRsDatabaseConfiguration();

	String getTask();

	String getFormat();

	String getGraph();

}