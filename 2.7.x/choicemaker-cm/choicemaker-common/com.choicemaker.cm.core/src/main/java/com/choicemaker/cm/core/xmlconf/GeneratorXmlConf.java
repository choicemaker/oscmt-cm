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

import java.io.File;

import org.jdom2.Element;

/**
 * Description
 *
 * @author    Martin Buechi
 * @deprecated
 */
@Deprecated
public class GeneratorXmlConf {

	public static String getCodeRoot() {
		String codeRoot = new File("etc/models/gen").getAbsolutePath();
		Element e = XmlConfigurator.getInstance().getCore();
		if (e != null) {
			e = e.getChild("generator");
			if (e != null) {
				String t = e.getAttributeValue("codeRoot");
				if (t != null) {
					codeRoot = new File(t).getAbsolutePath();
				}
			}
		}

		return codeRoot;
	}

}
