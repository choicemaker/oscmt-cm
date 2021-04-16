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
 * A record collection represented by the text in a certain format. 
 * <p>  
 *
 * @author emoussikaev
 * @see
 */
public class TextRefRecordCollection extends RefRecordCollection { //implements ITextRefRecordCollection {

	/** As of 2010-11-12 */
	static final long serialVersionUID = -2150279459283519005L;

	ITextFormat format;
	
	/**
	 * Constructs a <code>RefRecordCollection</code> with the specified Uniform Resource Locator and format.
	 * 
	 * @param   locator  The URL that defines the location of the "resource" that provides the set of the records
	 * @param   f		 The format.
	 *  
	 */
	public TextRefRecordCollection(String url, ITextFormat f) {
		super(url);
		this.format = f; 
	}

	/**
	 * @return
	 */
	//public TextFormatType ugetFormat() {
	//	return format;
	//}


	public ITextFormat getFormat() {
		return format;
	}
	/**
	 * @param format
	 */
	public void setFormat(ITextFormat format) {
		this.format = format;
	}
	
	@Override
	public void accept(IRecordCollectionVisitor ext)throws RecordCollectionException{
		ext.visit(this);
	}
	
	@Override
	public String toString() {
		return super.toString()+"|"+this.format.toString();
	}
}
