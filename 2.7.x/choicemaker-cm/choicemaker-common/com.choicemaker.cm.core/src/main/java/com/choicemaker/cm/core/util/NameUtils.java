/*
 * Copyright (c) 2001, 2015 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.core.util;

import java.io.File;

/**
 * Description
 *
 * @author    Martin Buechi
 */

public class NameUtils {
	public static String getNameFromFilePath(String fileName) {
		if (fileName == null) {
			return "";
		} else {
			String name = new File(fileName).getName();
			int pos = name.lastIndexOf(".");
			if (pos > 0) {
				return name.substring(0, pos);
			} else {
				return name;
			}
		}
	}
}
