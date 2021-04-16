/*
 * Copyright (c) 2001, 2009 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.core.configure.eclipse;

import java.io.IOException;
import java.io.Reader;

import com.choicemaker.cm.core.configure.jdom.JdomBuilder;
import com.choicemaker.cm.core.configure.xml.IBuilder;
import com.choicemaker.cm.core.configure.xml.IDocument;
import com.choicemaker.cm.core.configure.xml.XmlSpecificationException;

/**
 * Parses a fragment of plugin.xml files for extensions based on XmlConfigurable.
 * @author rphall
 */
public class EclipseBuilder implements IBuilder {

	public EclipseBuilder() {}

	/**
	 * Plug-ins may define multiple extensions based on XmlConfigurable, so
	 * before this method is invoked, the relevant portion of the plugin.xml
	 * file should be isolated so that the character stream starts reading at
	 * the start of a <code>configurable</code> element.
	 */
	@Override
	public IDocument build(Reader characterStream)
		throws XmlSpecificationException, IOException {
		JdomBuilder builder = new JdomBuilder();
		return builder.build(characterStream);
	}

}
