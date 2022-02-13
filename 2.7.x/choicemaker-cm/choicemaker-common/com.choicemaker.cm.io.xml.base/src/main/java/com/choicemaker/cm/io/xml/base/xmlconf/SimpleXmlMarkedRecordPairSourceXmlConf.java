/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.xml.base.xmlconf;

import org.jdom2.Element;

import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.MarkedRecordPairSource;
import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.io.xml.base.SimpleXmlMarkedRecordPairSource;

/**
 * @author ajwinkel
 *
 */
public class SimpleXmlMarkedRecordPairSourceXmlConf extends XmlMarkedRecordPairSourceXmlConf {

	@Override
	public void add(MarkedRecordPairSource src) throws XmlConfException {
		throw new XmlConfException("Can't create a new SimpleXmlMarkedRecordPairSource!");
	}
	
	@Override
	public MarkedRecordPairSource getMarkedRecordPairSource(String fileName, Element e, ImmutableProbabilityModel model) {
		return new SimpleXmlMarkedRecordPairSource(fileName, model);
	}

}
