/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.xml.base;

import com.choicemaker.cm.core.ImmutableProbabilityModel;

/**
 * @author ajwinkel
 *
 */
public class SimpleXmlMarkedRecordPairSource extends XmlMarkedRecordPairSource {

	public SimpleXmlMarkedRecordPairSource() { }

	public SimpleXmlMarkedRecordPairSource(String xmlFileName, ImmutableProbabilityModel model) {
		super(xmlFileName, xmlFileName, model);
	}

	public void setFileName(String fn) {
		super.setFileName(fn);
		super.setRawXmlFileName(fn);	
	}

	public void setRawXmlFileName(String fn) {
		setFileName(fn);	
	}

}
