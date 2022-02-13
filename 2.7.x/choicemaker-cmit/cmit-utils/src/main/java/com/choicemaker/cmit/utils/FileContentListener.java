/*******************************************************************************
 * Copyright (c) 2003, 2015 ChoiceMaker LLC and others.
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
package com.choicemaker.cmit.utils;

import java.nio.file.Path;

public interface FileContentListener {

//	enum FILE_CONTENT_COMPARISON {
//		ONLY_IN_PATH1, ONLY_IN_PATH12, DIFFERENT_CONTENT, SAME_CONTENT,
//		UNREACHABLE_PATH1, UNREACHABLE_PATH2
//	}

	void fileComparison(Path p1, Path p2, FileContentComparison0 result);

}
