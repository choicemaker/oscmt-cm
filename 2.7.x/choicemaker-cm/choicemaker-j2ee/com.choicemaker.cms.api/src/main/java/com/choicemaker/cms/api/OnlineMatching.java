/*
 * Copyright (c) 2001, 2009 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cms.api;

import java.io.IOException;

import com.choicemaker.cm.core.Record;
import com.choicemaker.cms.args.AbaParameters;
import com.choicemaker.cms.args.AbaServerConfiguration;
import com.choicemaker.cms.args.AbaSettings;
import com.choicemaker.cms.args.MatchCandidates;

/**
 */
public interface OnlineMatching<T extends Comparable<T>> {

	MatchCandidates<T> getMatchCandidates(Record<T> query,
			AbaParameters parameters, AbaSettings settings,
			AbaServerConfiguration configuration) throws IOException;

	// TransitiveCandidates<T> getTransitiveCandidates(Record<T> query,
	// OnlineContext configuration);
	//
	// TransitiveCandidates<T> getTransitiveCandidates(Record<T> query,
	// OnlineContext configuration, OnlineContext overrides);
	//
	// TransitiveCandidates<T> getTransitiveCandidates(Record<T> query,
	// String configurationName);
	//
	// TransitiveCandidates<T> getTransitiveCandidates(Record<T> query,
	// String configurationName, OnlineContext overrides);

}
