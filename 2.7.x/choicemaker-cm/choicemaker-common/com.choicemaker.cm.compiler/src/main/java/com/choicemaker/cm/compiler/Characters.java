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
package com.choicemaker.cm.compiler;

/**
 * Codes for special white space characters.
 *
 * @author   Matthias Zenger
 */
public interface Characters {
	char FE = 0x1A;
	char LF = 0xA;
	char FF = 0xC;
	char CR = 0xD;
	char BACKSLASH = '\\';
}
