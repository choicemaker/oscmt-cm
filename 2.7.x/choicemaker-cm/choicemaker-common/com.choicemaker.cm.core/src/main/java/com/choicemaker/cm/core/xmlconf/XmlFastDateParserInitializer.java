/*
 * Copyright (c) 2001, 2016 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.core.xmlconf;

import org.jasypt.encryption.StringEncryptor;
import org.jdom2.Element;

import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.core.configure.ConfigurationUtils;
import com.choicemaker.cm.core.util.DateHelper;
import com.choicemaker.cm.core.util.FastDateParser;

/**
 * XML initializer for collections (sets).
 *
 * @author Martin Buechi
 */
public class XmlFastDateParserInitializer implements XmlModuleInitializer {
	public final static XmlFastDateParserInitializer instance = new XmlFastDateParserInitializer();

	private XmlFastDateParserInitializer() {
	}

	@Override
	public void init(Element e, StringEncryptor encryptor)
			throws XmlConfException {
		int centuryTurn = 20;
		Element c = e.getChild("centuryTurn");
		if (c != null) {
			String ct = ConfigurationUtils.getTextValue(c, encryptor);
			if (ct != null) {
				centuryTurn = Integer.parseInt(ct);
			}
			Element c2 = e.getChild("dmy");
			if (c2 != null) {
				String ct2 = ConfigurationUtils.getTextValue(c2, encryptor);
				boolean dmy = "true".equals(ct2);
				DateHelper.setDateParser(new FastDateParser(centuryTurn, dmy));
			}
		}
	}

	@Override
	public void init(Element e) throws XmlConfException {
		init(e, null);
	}
}
