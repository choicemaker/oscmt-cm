/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.xml.base.gen;

import java.io.File;

import com.choicemaker.cm.core.Constants;
import com.choicemaker.cm.core.gen.GenException;
import com.choicemaker.cm.core.gen.GeneratorPlugin;
import com.choicemaker.cm.core.gen.IGenerator;

/**
 * Main generator plugin for XML IO.
 *
 * @author    Martin Buechi
 */
public class XmlGenerator implements GeneratorPlugin {
	public void generate(IGenerator g) throws GenException {
		g.addAccessorImport("import com.choicemaker.cm.io.xml.base.*;" + Constants.LINE_SEPARATOR);
		g.addAccessorImport("import " + g.getPackage() + ".xml.*;" + Constants.LINE_SEPARATOR);
		g.addAccessorImplements(", com.choicemaker.cm.io.xml.base.XmlAccessor");
		g.addAccessorImplements(", com.choicemaker.cm.io.xml.base.XmlReporterAccessor");
		String directoryName = g.getSourceCodePackageRoot() + File.separator + "xml";
		new File(directoryName).mkdir();
		XmlReaderGenerator.instance.generate(g);
		XmlRecordOutputterGenerator.instance.generate(g);
		XmlRecordOutputterGenerator.reportinstance.generate(g);		
	}
}
