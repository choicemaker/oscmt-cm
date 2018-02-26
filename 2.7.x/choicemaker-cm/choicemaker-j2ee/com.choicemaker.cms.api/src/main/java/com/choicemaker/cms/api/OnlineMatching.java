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
import java.io.Serializable;

import com.choicemaker.client.api.DataAccessObject;
import com.choicemaker.client.api.IGraphProperty;
import com.choicemaker.client.api.MatchGroup;
import com.choicemaker.client.api.TransitiveGroup;
import com.choicemaker.cm.args.AbaSettings;
import com.choicemaker.cm.args.TransitivityException;
import com.choicemaker.cm.core.BlockingException;

/**
 * @author rphall
 */
public interface OnlineMatching<T extends Comparable<T> & Serializable> {

	MatchGroup<T> getMatchCandidates(DataAccessObject<T> query,
			AbaParameters parameters, AbaSettings settings,
			AbaServerConfiguration configuration)
			throws IOException, BlockingException;

	TransitiveGroup<T> getTransitiveCandidates(DataAccessObject<T> query,
			AbaParameters parameters, AbaSettings settings,
			AbaServerConfiguration configuration,
			IGraphProperty mergeConnectivity, boolean mustIncludeQuery)
			throws IOException, BlockingException, TransitivityException;

}
