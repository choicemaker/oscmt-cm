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
package com.choicemaker.cms.ejb;

import com.choicemaker.cms.api.AbaParameters;
import com.choicemaker.cms.beans.AbaParametersBean;

public class WellKnownInstances {

	public WellKnownInstances() {
		// TODO Auto-generated constructor stub
	}

	public static final TestRecord<Integer> query01 =
		new TestRecord<Integer>(0);

	public static final TestRecord<Integer> dbRecord01 =
		new TestRecord<Integer>(1);
	public static final TestRecord<Integer> dbRecord02 =
		new TestRecord<Integer>(2);
	public static final TestRecord<Integer> dbRecord03 =
		new TestRecord<Integer>(3);
	public static final TestRecord<Integer> dbRecord04 =
		new TestRecord<Integer>(4);
	public static final TestRecord<Integer> dbRecord05 =
		new TestRecord<Integer>(5);
	public static final TestRecord<Integer> dbRecord06 =
		new TestRecord<Integer>(6);
	public static final TestRecord<Integer> dbRecord07 =
		new TestRecord<Integer>(7);
	public static final TestRecord<Integer> dbRecord08 =
		new TestRecord<Integer>(8);
	public static final TestRecord<Integer> dbRecord09 =
		new TestRecord<Integer>(9);

	private static final AbaParametersBean parametersBean01 =
		new AbaParametersBean();
	static {
		parametersBean01.setLowThreshold(0.2f);
		parametersBean01.setHighThreshold(0.8f);
		parametersBean01.setModelConfigurationName(TestModel.MODEL_NAME);
	}
	public static final AbaParameters parameters01 = parametersBean01;

}
