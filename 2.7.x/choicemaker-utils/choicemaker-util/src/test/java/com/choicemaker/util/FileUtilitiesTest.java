/*******************************************************************************
 * Copyright (c) 2003, 2018 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker LLC - initial API and implementation
 *******************************************************************************/
package com.choicemaker.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

public class FileUtilitiesTest extends TestCase {

	public static class ByteArrayPair {
		final private byte[] a1;
		final private byte[] a2;

		public ByteArrayPair(byte[] b1, byte[] b2) {
			a1 = b1;
			a2 = b2;
		}

		protected byte[] getArray(byte[] a) {
			byte[] retVal = null;
			if (a != null) {
				retVal = new byte[a.length];
				System.arraycopy(a, 0, retVal, 0, a.length);
			}
			return retVal;
		}

		public byte[] getArray1() {
			return getArray(a1);
		}

		public byte[] getArray2() {
			return getArray(a2);
		}
	}

	public static final byte LF = (byte) '\n';
	public static final byte CR = (byte) '\r';
	public static final byte A = 'A';
	public static final String MD5 = FileUtilities.MD5_HASH_ALGORITHM;
	public static final String SHA1 = FileUtilities.SHA1_HASH_ALGORITHM;
	public static final String FILE_PREFIX = "FileUtilitiesTest";
	public static final String FILE_SUFFIX = ".txt";

	protected static final ByteArrayPair[] EQUAL = new ByteArrayPair[] {
		new ByteArrayPair(new byte[0], new byte[0]),
		new ByteArrayPair(new byte[] { A }, new byte[] { A } ),
		new ByteArrayPair(new byte[] { LF }, new byte [] { LF } ),
		new ByteArrayPair(new byte[] { CR }, new byte [] { CR } ),
		new ByteArrayPair(new byte[] { A, LF }, new byte [] { A, LF } ),
		new ByteArrayPair(new byte[] { LF, A }, new byte [] { LF, A } ),
		new ByteArrayPair(new byte[] { A, CR }, new byte [] { A, CR } ),
		new ByteArrayPair(new byte[] { CR, A }, new byte [] { CR, A } ),
		new ByteArrayPair(new byte[] { LF, CR }, new byte [] { LF, CR } ),
		new ByteArrayPair(new byte[] { CR, LF }, new byte [] { CR, LF } ),
		new ByteArrayPair(new byte[] { A, LF, CR }, new byte [] { A, LF, CR } ),
		new ByteArrayPair(new byte[] { LF, A, CR }, new byte [] { LF, A, CR } ),
		new ByteArrayPair(new byte[] { LF, CR, A }, new byte [] { LF, CR, A } ),
		new ByteArrayPair(new byte[] { A, CR, LF }, new byte [] { A, CR, LF } ),
		new ByteArrayPair(new byte[] { CR, A, LF }, new byte [] { CR, A, LF } ),
		new ByteArrayPair(new byte[] { CR, LF, A }, new byte [] { CR, LF, A } )
	};

	protected static final ByteArrayPair[] EOL_EQUAL = new ByteArrayPair[] {
		new ByteArrayPair(new byte[0], new byte[] { LF }),
		new ByteArrayPair(new byte[0], new byte[] { CR }),
		new ByteArrayPair(new byte[0], new byte[] { LF, CR }),
		new ByteArrayPair(new byte[0], new byte[] { CR, LF }),
		new ByteArrayPair(new byte[] { A }, new byte[] { A, LF } ),
		new ByteArrayPair(new byte[] { A }, new byte[] { LF, A } ),
		new ByteArrayPair(new byte[] { A }, new byte[] { A, CR } ),
		new ByteArrayPair(new byte[] { A }, new byte[] { CR, A } ),
		new ByteArrayPair(new byte[] { A }, new byte[] { A, LF, CR } ),
		new ByteArrayPair(new byte[] { A }, new byte[] { A, CR, LF } ),
		new ByteArrayPair(new byte[] { A }, new byte[] { LF, CR, A } ),
		new ByteArrayPair(new byte[] { A }, new byte[] { CR, LF, A } ),
		new ByteArrayPair(new byte[] { LF }, new byte [] { CR } ),
		new ByteArrayPair(new byte[] { CR }, new byte [] { CR, LF, CR } ),
		new ByteArrayPair(new byte[] { A, LF }, new byte [] { LF, A, LF } ),
		new ByteArrayPair(new byte[] { LF, A }, new byte [] { LF, A , CR, LF} ),
		new ByteArrayPair(new byte[] { A, CR }, new byte [] { CR, A, CR } ),
		new ByteArrayPair(new byte[] { CR, A }, new byte [] { CR, LF, A , CR} ),
		new ByteArrayPair(new byte[] { LF, CR }, new byte [] { LF, CR, CR } ),
	};

	protected static final ByteArrayPair[] NOT_EQUAL = new ByteArrayPair[] {
		new ByteArrayPair(new byte[0], new byte[] { A } ),
		new ByteArrayPair(new byte[] { A }, new byte[] { A, A } ),
		new ByteArrayPair(new byte[] { A }, new byte[] { A, A, A } ),
	};

	protected static File createFile(byte[] bytes) throws IOException {
		File retVal = File.createTempFile(FILE_PREFIX, FILE_SUFFIX);
		FileOutputStream fos = new FileOutputStream(retVal);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		if (bytes != null) {
			bos.write(bytes, 0, bytes.length);
		}
		bos.flush();
		bos.close();
		return retVal;
	}

	protected static void checkExpectedHashEquality(ByteArrayPair bap,
			boolean isEqualityExpected) throws IOException {
		File f1 = null;
		File f2 = null;
		try {
			f1 = createFile(bap.getArray1());
			String md5_1 = FileUtilities.computeHash(MD5, f1);
			String sha1_1 = FileUtilities.computeHash(SHA1, f1);
			f2 = createFile(bap.getArray2());
			String md5_2 = FileUtilities.computeHash(MD5, f2);
			String sha1_2 = FileUtilities.computeHash(SHA1, f2);

			checkExpectedEquality(md5_1, sha1_1, md5_2, sha1_2, isEqualityExpected);
		} finally {
			if (f1 != null) {
				f1.delete();
			}
			if (f2 != null) {
				f2.delete();
			}
		}
	}

	protected static void checkExpectedHashEqualityIgnoreEOL(ByteArrayPair bap,
			boolean isEqualityExpected) throws IOException {
		File f1 = null;
		File f2 = null;
		try {
			f1 = createFile(bap.getArray1());
			String md5_1 = FileUtilities.computeHashIgnoreEOL(MD5, f1);
			String sha1_1 = FileUtilities.computeHashIgnoreEOL(SHA1, f1);
			f2 = createFile(bap.getArray2());
			String md5_2 = FileUtilities.computeHashIgnoreEOL(MD5, f2);
			String sha1_2 = FileUtilities.computeHashIgnoreEOL(SHA1, f2);

			checkExpectedEquality(md5_1, sha1_1, md5_2, sha1_2,
					isEqualityExpected);
		} finally {
			if (f1 != null) {
				f1.delete();
			}
			if (f2 != null) {
				f2.delete();
			}
		}
	}

	protected static void checkExpectedEquality(String md5_1, String sha1_1,
			String md5_2, String sha1_2, boolean isEqualityExpected)
			throws IOException {
		// Sanity checks on single values
		assertTrue(md5_1 != null);
		assertTrue(md5_1.trim().length() == md5_1.length());
		assertTrue(sha1_1 != null);
		assertTrue(sha1_1.trim().length() == sha1_1.length());
		assertTrue(sha1_1.length() > md5_1.length());

		assertTrue(md5_2 != null);
		assertTrue(md5_2.trim().length() == md5_2.length());
		assertTrue(sha1_2 != null);
		assertTrue(sha1_2.trim().length() == sha1_2.length());
		assertTrue(sha1_2.length() > md5_2.length());

		// Expected inequality of independent values
		if (isEqualityExpected) {
			assertTrue(md5_1.equals(md5_2));
			assertTrue(sha1_1.equals(sha1_2));
		} else {
			assertTrue(!md5_1.equals(md5_2));
			assertTrue(!sha1_1.equals(sha1_2));
		}
	}

	public void testComputeHashes() {
		for (int i = 0; i < EQUAL.length; i++) {
			ByteArrayPair bap = EQUAL[i];
			try {
				checkExpectedHashEquality(bap, true);
				checkExpectedHashEqualityIgnoreEOL(bap, true);
			} catch (Throwable e) {
				String msg = "EQUAL[" + i + "]: " + e.getMessage();
				fail(msg);
			}
		}

		for (int i = 0; i < EOL_EQUAL.length; i++) {
			ByteArrayPair bap = EOL_EQUAL[i];
			try {
				checkExpectedHashEquality(bap, false);
				checkExpectedHashEqualityIgnoreEOL(bap, true);
			} catch (Throwable e) {
				String msg = "EOL_EQUAL[" + i + "]: " + e.getMessage();
				fail(msg);
			}
		}

		for (int i = 0; i < NOT_EQUAL.length; i++) {
			ByteArrayPair bap = NOT_EQUAL[i];
			try {
				checkExpectedHashEquality(bap, false);
				checkExpectedHashEqualityIgnoreEOL(bap, false);
			} catch (Throwable e) {
				String msg = "NOT_EQUAL[" + i + "]: " + e.getMessage();
				fail(msg);
			}
		}
	}

}
