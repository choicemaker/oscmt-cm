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
package com.choicemaker.cmit.utils.j2ee;

import com.choicemaker.cm.args.AnalysisResultFormat;
import com.choicemaker.cm.args.OabaLinkageType;
import com.choicemaker.cm.args.PersistableRecordSource;
import com.choicemaker.cm.core.ISerializableDbRecordSource;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.ImmutableThresholds;
import com.choicemaker.e2.CMPluginRegistry;

public interface WellKnownTestConfiguration {

	ImmutableProbabilityModel getModel();

	String getModelConfigurationName();

	OabaLinkageType getOabaTask();

	ISerializableDbRecordSource getSerializableMasterRecordSource();

	ISerializableDbRecordSource getSerializableStagingRecordSource();

	int getSingleRecordMatchingThreshold();

	ImmutableThresholds getThresholds();

	String getBlockingConfiguration();

	PersistableRecordSource getQueryRecordSource();
	
	boolean isQueryRsDeduplicated();

	String getQueryDatabaseConfiguration();

	PersistableRecordSource getReferenceRecordSource();

	String getReferenceDatabaseConfiguration();

	boolean getTransitivityAnalysisFlag();

	String getTransitivityGraphProperty();

	AnalysisResultFormat getTransitivityResultFormat();

	/** Post-construction method */
	void initialize(OabaLinkageType type, CMPluginRegistry registry);

}
