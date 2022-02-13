/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.base;

/**
 * Xml format.
 * 
 * @author emoussikaev
 * @see
 */
public class XmlTextFormat implements ITextFormat {
	
	/** As of 2010-11-12 */
	static final long serialVersionUID = -8655981214222719560L;

	public XmlTextFormat() {
		super();
	}

	@Override
	public void accept(ITextFormatVisitor ext) {
		ext.visit(this);
	}
}
