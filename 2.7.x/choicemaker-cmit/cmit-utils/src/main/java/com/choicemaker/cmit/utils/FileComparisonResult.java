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



final class FileComparisonResult {
	
	private final Path p1;
	private final Path p2;
	private final FileContentComparison0 result;

	FileComparisonResult(Path p1, Path p2, FileContentComparison0 result) {
		if (result == null) {
			throw new IllegalArgumentException("null result");
		}
		this.p1 = p1;
		this.p2 = p2;
		this.result = result;
	}
	
	public Path getPath1() {
		return p1;
	}

	public Path getPath2() {
		return p2;
	}

	public FileContentComparison0 getResult() {
		return result;
	}

}
