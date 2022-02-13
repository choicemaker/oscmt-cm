/*******************************************************************************
 * Copyright (c) 2003, 2019 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc  - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
package com.choicemaker.cm.io.xml.base;

import java.io.IOException;
import java.io.Writer;

import com.choicemaker.cm.core.Constants;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.util.Precondition;

/**
 * Writes marked record pairs to a specified Writer, not necessarily a
 * FileWriter.
 * 
 * @author rphall
 *
 */
public class XmlMarkedRecordPairSink2 extends XmlMarkedRecordPairSink {

	public XmlMarkedRecordPairSink2(Writer w, ImmutableProbabilityModel model) {
		super(model);
		Precondition.assertNonNullArgument("writer must be non-null", w);
		this.setWriter(w);
	}

	@Override
	public void close() throws IOException, XmlDiagnosticException {
		finishRootEntity();
		getWriter().flush();
	}

	@Override
	public void open() throws IOException {
		setRecordOutputter(((XmlAccessor) getModel().getAccessor())
				.getXmlRecordOutputter());
		getWriter().write("<?xml version=\"1.0\" encoding=\"" + getEncoding()
				+ "\"?>" + Constants.LINE_SEPARATOR);
		startRootEntity();
		getWriter().flush();
	}

	@Override
	public void setRawXmlFileName(String fn) {
	}

}
