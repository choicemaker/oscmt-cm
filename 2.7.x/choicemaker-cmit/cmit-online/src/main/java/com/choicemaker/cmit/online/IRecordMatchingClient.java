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
/*
 * Created on Sep 9, 2009
 */
package com.choicemaker.cmit.online;

import com.choicemaker.cm.core.Match;
import com.choicemaker.cm.core.Record;

/**
 * @author rphall
 * @version $Revision$ $Date$
 */
public interface IRecordMatchingClient {

	/**
	 * Finds records that are candidate matches to a input record.
	 * 
	 * @param matchingParams an object specifying parameters used in
	 * matching that don't typically changed much over the course of a
	 * matching session. These include command-line arguments or
	 * properties necessary to initialize a CORBA ORB, or properties
	 * necessary to specify a URM collection of database records.
	 * 
	 * @param queryParams a collection of query-specific (but
	 * implementation-independent) parameters that affect what records
	 * match an input query record. These include parameters such
	 * as the differ and match threshold or the maximum number
	 * of matches to return.
	 * 
	 * @param queryRecord the input record to be matched.
	 * 
	 * @return a non-null, but possibly empty, array of candidate matches
	 * to the input record.
	 * 
	 */
	public abstract Match[] getMatches(
		Object matchingParams,
		QueryParams queryParams,
		Record queryRecord)
		throws Exception;

}
