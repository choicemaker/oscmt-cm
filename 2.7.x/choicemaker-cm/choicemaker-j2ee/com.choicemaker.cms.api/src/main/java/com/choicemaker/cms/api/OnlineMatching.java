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

import com.choicemaker.cm.core.Record;

/**
 */
public interface OnlineMatching<T extends Comparable<T>> {

	MatchCandidateGroup<T> getMatchCandidateGroup(Record<T> query,
			OnlineContext configuration);

	MatchCandidateGroup<T> getMatchCandidateGroup(Record<T> query,
			OnlineContext configuration, OnlineContext overrides);

	MatchCandidateGroup<T> getMatchCandidateGroup(Record<T> query,
			String configurationName);

	MatchCandidateGroup<T> getMatchCandidateGroup(Record<T> query,
			String configurationName, OnlineContext overrides);

	TransitiveCandidateGroup<T> getTransitiveCandidateGroup(Record<T> query,
			OnlineContext configuration);

	TransitiveCandidateGroup<T> getTransitiveCandidateGroup(Record<T> query,
			OnlineContext configuration, OnlineContext overrides);

	TransitiveCandidateGroup<T> getTransitiveCandidateGroup(Record<T> query,
			String configurationName);

	TransitiveCandidateGroup<T> getTransitiveCandidateGroup(Record<T> query,
			String configurationName, OnlineContext overrides);

}
