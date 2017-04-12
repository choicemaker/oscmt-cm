/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.db.postgres.xmlconf;

import org.jasypt.encryption.StringEncryptor;
import org.jdom2.Element;

import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.core.xmlconf.XmlModuleInitializer;

/**
 * Description
 *
 * @author Martin Buechi
 */
public class PostgresXmlInitializer implements XmlModuleInitializer {
	public static PostgresXmlInitializer instance = new PostgresXmlInitializer();

	private PostgresXmlInitializer() {
	}

	@Override
	public void init(Element e) throws XmlConfException {
		init(e, null);
	}

	@Override
	public void init(Element e, StringEncryptor encryptor) throws XmlConfException {
		PostgresConnectionCacheXmlConf.init(encryptor);
	}
}
