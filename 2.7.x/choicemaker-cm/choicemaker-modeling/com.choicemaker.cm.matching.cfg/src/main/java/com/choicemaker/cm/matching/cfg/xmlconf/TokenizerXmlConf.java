/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.cfg.xmlconf;

import org.jdom2.Element;

import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.matching.cfg.SimpleTokenizer;
import com.choicemaker.cm.matching.cfg.Tokenizer;

/**
 * Comment
 *
 * @author   Adam Winkel
 */
public class TokenizerXmlConf {

	public static Tokenizer readFromElement(Element e) throws XmlConfException {
		Class cls = ParserXmlConf.getClass(e, SimpleTokenizer.class);
		Tokenizer tokenizer = (Tokenizer) ParserXmlConf.instantiate(cls);
		
		ParserXmlConf.invoke(tokenizer, e.getChildren());

		return tokenizer;
	}

}
