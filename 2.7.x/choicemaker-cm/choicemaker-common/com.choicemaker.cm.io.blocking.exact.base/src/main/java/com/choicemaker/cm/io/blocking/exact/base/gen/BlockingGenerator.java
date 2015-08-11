/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.blocking.exact.base.gen;

import java.io.File;

import com.choicemaker.cm.core.Constants;
import com.choicemaker.cm.core.gen.GenException;
import com.choicemaker.cm.core.gen.GeneratorPlugin;
import com.choicemaker.cm.core.gen.IGenerator;

/**
 * Description
 * 
 * @author  Martin Buechi
 * @version $Revision: 1.1.1.1 $ $Date: 2009/05/03 16:02:54 $
 */
public class BlockingGenerator implements GeneratorPlugin {
	static boolean filesAdded;

	public void generate(IGenerator g) throws GenException {
		filesAdded = false;
		g.addAccessorImport("import com.choicemaker.cm.core.blocking.*;" + Constants.LINE_SEPARATOR);
		g.addAccessorImport("import com.choicemaker.cm.io.blocking.exact.base.*;" + Constants.LINE_SEPARATOR);
		g.addAccessorImplements(", com.choicemaker.cm.io.blocking.exact.gui.matcher.ExactInMemoryBlockerAccessor");
		String directoryName = g.getSourceCodePackageRoot() + File.separator + "blocking";
		new File(directoryName).mkdir();
		BlockingConfigurationsGenerator.instance.generate(g);
		if (filesAdded) {
			g.addAccessorImport("import " + g.getPackage() + ".blocking.*;" + Constants.LINE_SEPARATOR);
		}
	}
}
