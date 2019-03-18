/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.base;

import com.choicemaker.cm.urm.exceptions.RecordCollectionException;



/**
 * Represents a record collection in the ChoiceMaker ".rc" format. This format provides matching engine
 * with all necessary information to be able to retrieve records.
 * 
 * @author emoussikaev
 * @see
 */
public class SelfDescrRecordCollection extends RefRecordCollection {//implements ISelfDescrRecordCollection {

	/** As of 2010-11-12 */
	static final long serialVersionUID = -5232976242571513436L;

	/**
	 * @param locator URL that specify the location of .RC file
	 */
	public SelfDescrRecordCollection(String url) {
		super(url);
		
	}
	@Override
	public void accept(IRecordCollectionVisitor ext) throws RecordCollectionException{
		ext.visit(this);
	}

}
