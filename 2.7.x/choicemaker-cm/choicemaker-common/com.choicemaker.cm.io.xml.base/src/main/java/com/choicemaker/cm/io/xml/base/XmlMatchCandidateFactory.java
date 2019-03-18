/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.xml.base;

import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Match;
import com.choicemaker.cm.core.base.MatchCandidate;
import com.choicemaker.cm.core.base.MatchCandidateFactory;

/**
 * Comment
 *
 * @author   Martin Buechi
 */
public class XmlMatchCandidateFactory extends MatchCandidateFactory {

	@Override
	public MatchCandidate createMatchCandidate(Match match, ImmutableProbabilityModel model) {
		return new XmlMatchCandidate(match, model);
	}
}
