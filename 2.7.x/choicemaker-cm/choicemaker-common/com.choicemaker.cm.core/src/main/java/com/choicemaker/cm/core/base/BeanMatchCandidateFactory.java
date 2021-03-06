/*
 * Copyright (c) 2001, 2022 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.core.base;

import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Match;

/**
 * Comment
 *
 * @author   Martin Buechi
 */
public class BeanMatchCandidateFactory extends MatchCandidateFactory {
	@Override
	public MatchCandidate createMatchCandidate(Match match, ImmutableProbabilityModel model) {
		return new BeanMatchCandidate(match, model);
	}

}
