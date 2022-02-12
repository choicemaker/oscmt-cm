/*******************************************************************************
 * Copyright (c) 2003, 2016 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.cfg.xmlconf;

import org.jdom2.Element;

import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.matching.cfg.TokenType;

/**
 * @author ajwinkel
 *
 */
@SuppressWarnings({
	"rawtypes" })
public class TokenTypeXmlConf {

	public static TokenType readFromElement(Element e, ClassLoader cl) throws XmlConfException {
		String name = e.getAttributeValue("name");
		if (name == null) {
			throw new XmlConfException("Element " + e.getName() + " does not define a 'name' attribute");
		}
		
		Class cls = ParserXmlConf.getClass(e, cl, null);
		assert cls != null ;
		
		TokenType tt = (TokenType) ParserXmlConf.instantiate(cls, new Class[] {String.class}, new Object[] {name});
		
		ParserXmlConf.invoke(tt, e.getChildren(), cl);
		
		return tt;
	}

}
