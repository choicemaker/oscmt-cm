/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.aba.gen;

import java.io.File;

import com.choicemaker.cm.core.Constants;
import com.choicemaker.cm.core.gen.GenException;
import com.choicemaker.cm.core.gen.GeneratorPlugin;
import com.choicemaker.cm.core.gen.IGenerator;

/**
 * Main generator plugin for Blocking IO.
 *
 * @author    Martin Buechi
 */
public class BlockingGenerator implements GeneratorPlugin {
	static boolean filesAdded;

	public void generate(IGenerator g) throws GenException {
		filesAdded = false;
		g.addAccessorImport("import com.choicemaker.cm.aba.base.*;" + Constants.LINE_SEPARATOR);
		g.addAccessorImplements(", com.choicemaker.cm.aba.BlockingAccessor");
		String directoryName = g.getSourceCodePackageRoot() + File.separator + "blocking";
		new File(directoryName).mkdir();
		BlockingConfigurationsGenerator.instance.generate(g);
		if (filesAdded) {
			g.addAccessorImport("import " + g.getPackage() + ".blocking.*;" + Constants.LINE_SEPARATOR);
		}
	}
}
