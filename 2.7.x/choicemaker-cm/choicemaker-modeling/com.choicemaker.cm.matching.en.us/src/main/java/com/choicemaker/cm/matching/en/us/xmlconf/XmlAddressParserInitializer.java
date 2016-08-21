/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.en.us.xmlconf;

import java.util.logging.Logger;

import org.jasypt.encryption.StringEncryptor;
import org.jdom2.Element;

import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.core.xmlconf.XmlModuleInitializer;

/**
 * Deprecated XML initializer for CFG address parsers. CFG address parsers are
 * no longer configured as CM Analyzer " modules. Instead, they are specified as
 * plugins. See the com.choicemaker.cm.matching.en.us plugin descriptor for the
 * relevant extension point.
 * <p/>
 * This class is retained for backwards compatibility with ChoiceMaker 2.5
 * Analyzer configuration files.
 * 
 * @author Martin Buechi
 * @deprecated
 */
public class XmlAddressParserInitializer implements XmlModuleInitializer {

	private static Logger logger =
		Logger.getLogger(XmlAddressParserInitializer.class.getName());

	public final static XmlAddressParserInitializer instance =
		new XmlAddressParserInitializer();

	private XmlAddressParserInitializer() {
	}

	@Override
	public void init(Element unused) throws XmlConfException {
		init(unused, null);
	}

	@Override
	public void init(Element unused1, StringEncryptor unused2)
			throws XmlConfException {
		String msg =
			"CFG address parsers are no longer configured as CM Analyzer "
					+ "modules. They should be configured as plugins. "
					+ "See the com.choicemaker.cm.matching.en.us module for details.";
		logger.warning(msg);
	}

}
