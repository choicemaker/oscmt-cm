/*
 * Copyright (c) 2001, 2022 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.core.xmlconf;

import org.jdom2.Element;

import com.choicemaker.cm.core.DynamicDispatchHandler;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.MarkedRecordPairSource;
import com.choicemaker.cm.core.XmlConfException;

/**
 * Base interface for all XML marked record pair configurators.
 *
 * Classes that implement this interface should be singletons. The single instance
 * should be accessible as a public (final) static field <code>instance</code>. This field
 * must be intialized when the class is loaded.
 *
 * @author    Martin Buechi
 */
public interface MarkedRecordPairSourceXmlConfigurator extends DynamicDispatchHandler {
	/**
	 * Returns an instance of the description of the specified <code>MarkedRecordPairSource</code>.
	 *
	 * @param   e  The JDOM element containing the XML configuration information.
	 * @param   model  The description of the probability model to be used.
	 * @return  The descriptor of the specified <code>MarkedRecordPairSource</code>.
	 * @throws  XmlConfException  if any error occurs.
	 */
	MarkedRecordPairSource getMarkedRecordPairSource(String fileName, Element e, ImmutableProbabilityModel model)
		throws XmlConfException;

	void add(MarkedRecordPairSource desc) throws XmlConfException;
}
