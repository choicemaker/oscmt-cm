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

import static junit.framework.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;

import com.choicemaker.util.SystemPropertyUtils;

public class PathComparison {

	public static final int DEFAULT_MAX_REPORTED_DIFFERENCES = 3;

	private static final String EOL = System
			.getProperty(SystemPropertyUtils.PN_LINE_SEPARATOR);

	private static final String MORE_DIFFERENCES_INDICATOR = "...";

	private PathComparison() {
	}

	public static void assertSameContent(Path root1, Path root2,
			boolean ignoreEOL) {
		Set<Path> excluded = Collections.emptySet();
		assertSameContent(root1, root2, DEFAULT_MAX_REPORTED_DIFFERENCES,
				excluded, ignoreEOL);
	}

	public static void assertSameContent(Path root1, Path root2,
			int maxReportedDifferences, Set<Path> excludedComparisons, boolean ignoreEOL) {

		// Check that the first file tree exists
		File f = root1.toFile();
		assertTrue(f.exists() && f.canRead());

		// Check that the second file tree exists
		f = root2.toFile();
		assertTrue(f.exists() && f.canRead());

		// Compare the second tree against the first tree
		final int maxCollected = maxReportedDifferences + 1;
		DefaultFileContentListener listener =
			new DefaultFileContentListener(maxCollected, false);
		FileTreeComparator ftc = new FileTreeComparator(root1, root2, ignoreEOL);
		ftc.addListener(listener);
		ftc.addExcludedPaths(excludedComparisons);
		try {
			ftc.compare();
		} catch (IOException e) {
			fail(e.toString());
		}
		final int differences = listener.getDifferenceCount();
		if (differences != 0) {
			int count = 0;
			StringBuilder sb = new StringBuilder();
			sb.append("Differences found (" + differences + "): ").append(EOL);
			for (String msg : listener.getMessages()) {
				sb.append(msg).append(EOL);
				if (count > maxReportedDifferences) {
					sb.append(MORE_DIFFERENCES_INDICATOR).append(EOL);
					break;
				}
			}
			fail(sb.toString());
		}
	}

}
