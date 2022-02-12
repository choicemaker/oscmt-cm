/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.mmdevtools.xmlconf;

import java.io.File;

import org.jdom2.Element;

import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.MarkedRecordPairSource;
import com.choicemaker.cm.core.RecordSource;
import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.core.xmlconf.MarkedRecordPairSourceXmlConfigurator;
import com.choicemaker.cm.core.xmlconf.RecordSourceXmlConf;
import com.choicemaker.cm.mmdevtools.io.RsToMrpsAdapter;
import com.choicemaker.util.FileUtilities;

/**
 * Comment
 *
 * @author   Adam Winkel
 */
public class RsToMrpsAdapterXmlConf implements MarkedRecordPairSourceXmlConfigurator {

	@Override
	public MarkedRecordPairSource getMarkedRecordPairSource(String fileName, Element e, ImmutableProbabilityModel model) throws XmlConfException {
		String rsFileName = e.getChildText("fileName");
		rsFileName = FileUtilities.getAbsoluteFile(new File(fileName).getParentFile(), rsFileName).getAbsolutePath();
		RecordSource rs = RecordSourceXmlConf.getRecordSource(rsFileName);
		rs.setModel(model);
		return new RsToMrpsAdapter(rs);
	}

	@Override
	public void add(MarkedRecordPairSource desc) throws XmlConfException {
		throw new XmlConfException("Cannot save an RsToMrpsAdapter!");
	}

	@Override
	public Object getHandler() {
		return this;
	}

	@Override
	public Class getHandledType() {
		return RsToMrpsAdapter.class;
	}

}
