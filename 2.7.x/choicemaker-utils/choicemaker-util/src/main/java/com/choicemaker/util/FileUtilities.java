/*
 * Copyright (c) 2001, 2018 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

/**
 * Description
 *
 * @author Martin Buechi
 */
@SuppressWarnings({
		"rawtypes", "unchecked" })
public class FileUtilities {

	public static final String MD5_HASH_ALGORITHM = "MD5";

	public static final String SHA1_HASH_ALGORITHM = "SHA1";

	private static Logger logger = Logger.getLogger(FileUtilities.class
			.getName());

	public static File resolveFile(File relativeTo, String fileName) {
		if (relativeTo == null) {
			throw new IllegalArgumentException("null reference file");
		}
		if (!relativeTo.isDirectory()) {
			relativeTo = relativeTo.getParentFile();
		}
		assert relativeTo.isDirectory();
		Path referencePath = Paths.get(relativeTo.toURI());
		Path path = referencePath.resolve(fileName);
		Path normalized = path.normalize();
		File retVal = normalized.toFile();
		return retVal;
	}

	/**
	 * Returns a file in canonical form for the path from
	 * <code>relativeToDir</code> to <code>file</code>
	 * 
	 * @param relativeToDir
	 *            the anchor of the path to be formed
	 * @param file
	 *            the relative path from the anchor
	 * @return a file in canonical form
	 */
	public static File getRelativeFile(File relativeToDir, String file) {

		File relDir = relativeToDir.getAbsoluteFile();
		ArrayList relPieces = new ArrayList();
		while (relDir != null) {
			relPieces.add(0, relDir);
			relDir = relDir.getParentFile();
		}

		File f = new File(file);
		ArrayList fPieces = new ArrayList();
		while (f != null) {
			fPieces.add(0, f);
			f = f.getParentFile();
		}

		// figure out how long they're the same...
		int sameIndex = -1;
		while (sameIndex + 1 < relPieces.size()
				&& sameIndex + 1 < fPieces.size()) {
			if (relPieces.get(sameIndex + 1).equals(fPieces.get(sameIndex + 1))) {
				sameIndex++;
			} else {
				break;
			}
		}

		if (sameIndex < 0) {
			throw new IllegalArgumentException("Files on different disks!");
		}

		File rel = null;

		for (int i = sameIndex + 1; i < relPieces.size(); i++) {
			rel = new File(rel, "..");
		}

		for (int i = sameIndex + 1; i < fPieces.size(); i++) {
			File piece = (File) fPieces.get(i);
			rel = new File(rel, piece.getName());
		}

		return rel;
	}

	public static File getAbsoluteFile(File relativeTo, String file) {
		File f = new File(file);
		if (f.isAbsolute()) {
			return f;
		} else {
			return new File(relativeTo, file).getAbsoluteFile();
		}
	}

	public static File getAbsoluteFile(File relativeTo, File file) {
		if (file.isAbsolute()) {
			return file;
		} else {
			return new File(relativeTo, file.getPath()).getAbsoluteFile();
		}
	}

	public static boolean isFileAbsolute(String file) {
		return new File(file).isAbsolute();
	}

	public static URL[] cpToUrls(File wdir, String cp)
			throws MalformedURLException, IOException {
		StringTokenizer st = new StringTokenizer(cp, ";:" + File.pathSeparator);
		List l = new ArrayList();
		while (st.hasMoreElements()) {
			String t = st.nextToken();
			l.add(toURL(FileUtilities.resolveFile(wdir, t)));
		}
		return (URL[]) l.toArray(new URL[l.size()]);
	}

	public static URL toURL(File f) throws MalformedURLException, IOException {
		String path = f.getCanonicalPath();
		if (File.separatorChar != '/') {
			path = path.replace(File.separatorChar, '/');
		}
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		String ext =
			path.substring(Math.max(0, path.length() - 4), path.length())
					.toUpperCase().intern();
		if (!path.endsWith("/")
				&& (f.isDirectory() || !(ext == ".JAR" || ext == ".ZIP"))) {
			path = path + "/";
		}
		return new URL("file", "", path);
	}

	public static File findFileOnClasspath(String name) {
		File file = null;
		StringTokenizer st =
			new StringTokenizer(System.getProperty("java.class.path"), ";:"
					+ File.pathSeparator);
		while (file == null && st.hasMoreElements()) {
			String t = st.nextToken();
			File f = new File(t + File.separatorChar + name);
			if (f.exists()) {
				file = f;
			}
		}
		return file;
	}

	public static String toAbsoluteClasspath(File wdir, String cp)
			throws IOException {
		StringBuffer res = new StringBuffer();
		StringTokenizer st = new StringTokenizer(cp, ";" + File.pathSeparator);
		while (st.hasMoreElements()) {
			res.append(File.pathSeparator);
			res.append(resolveFile(wdir, st.nextToken()));
		}
		return res.toString();
	}

	public static String toAbsoluteUrlClasspath(File wdir, String cp)
			throws IOException {
		StringBuffer res = new StringBuffer();
		StringTokenizer st = new StringTokenizer(cp, ";" + File.pathSeparator);
		while (st.hasMoreElements()) {
			res.append(" ");
			res.append(resolveFile(wdir, st.nextToken()).toURL().toString());
		}
		return res.toString();
	}

	/**
	 * Deletes the files and subdirectories contained in the specified directory
	 * <code>d</code>. If the specified file <code>d</code> is not a directory,
	 * this method has no effect.
	 * 
	 * @param d
	 *            a non-null file or directory. If a file, this method has no
	 *            effect. If a directory has empty, this method has no effect.
	 */
	public static void removeChildren(File d) {
		if (d == null) {
			throw new IllegalArgumentException("null file or directory");
		}
		String[] list = d.list();
		if (d.isDirectory() && list != null) {
			for (int i = 0; i < list.length; i++) {
				String s = list[i];
				File f = new File(d, s);
				if (f.isDirectory()) {
					removeDir(f);
				} else {
					if (!f.delete()) {
						logger.severe("Unable to delete file "
								+ f.getAbsolutePath());
					}
				}
			}
		}
	}

	/**
	 * Deletes the specified file or directory <code>f</code>. If <code>f</code>
	 * is a directory, removes all the children in the directory. If a file or
	 * directory or child can not be removed, this method fails without an
	 * exception being thrown, although the error is logged.
	 *
	 * @param f
	 *            file or directory
	 */
	public static void removeDir(File f) {
		if (f == null) {
			throw new IllegalArgumentException("null file or directory");
		}
		if (f.isDirectory()) {
			removeChildren(f);
		}
		if (!f.delete()) {
			logger.severe("Unable to delete directory " + f.getAbsolutePath());
		}
	}

	public static String getExtension(String fileName) {
		int n = fileName.lastIndexOf('.');
		if (n > 0) {
			return fileName.substring(n + 1);
		} else {
			return "";
		}
	}

	public static String getBase(String fileName) {
		int n = fileName.lastIndexOf('.');
		if (n > 0) {
			return fileName.substring(0, n);
		} else {
			return fileName;
		}
	}

	public static boolean hasExtension(File f, String extension) {
		return f.getName().toLowerCase()
				.endsWith("." + extension.toLowerCase());
	}

	public static File addExtensionIfNecessary(File f, String extension) {
		if (hasExtension(f, extension)) {
			return f;
		} else {
			return new File(f.getAbsolutePath() + "." + extension);
		}
	}

	/**
	 * Computes the MD5 or SHA1
	 *
	 * @param algo
	 *            see {@link #MD5_HASH_ALGORITHM} and
	 *            {@link #SHA1_HASH_ALGORITHM}
	 * @param f
	 *            a non-null, readable file
	 * @return A String representing the hash value in hexadecimal
	 * @throws IllegalArgumentException
	 *             if <code>algo</code> is neither MD5 nor SHA1, or if
	 *             <code>f</code> is null, doesn't exist or can not be read
	 * @throws IOException
	 *             if a IO exception occurs while the file is being read
	 * @throws Error
	 *             if a NoSuchAlgorithmException occurs (should never happen)
	 */
	public static String computeHash(String algo, File f)
			throws IllegalArgumentException, IOException {
		if (!MD5_HASH_ALGORITHM.equals(algo)
				&& !SHA1_HASH_ALGORITHM.equals(algo)) {
			throw new IllegalArgumentException("invalid algorithm: " + algo);
		}
		if (f == null || !f.exists() || !f.canRead()) {
			String fName = f == null ? "null" : f.getName();
			String msg =
				"file '" + fName + "' is null, doesn't exist or cannot be read";
			throw new IllegalArgumentException(msg);
		}

		byte[] buffer = new byte[8192];
		BufferedInputStream bis = null;
		try {
			MessageDigest digest = null;
			digest = MessageDigest.getInstance(algo);
			InputStream fis = new FileInputStream(f);
			bis = new BufferedInputStream(fis);
			int n = bis.read(buffer);
			while (n != -1) {
				if (n > 0) {
					digest.update(buffer, 0, n);
				}
				n = bis.read(buffer);
			}
			buffer = digest.digest();
		} catch (NoSuchAlgorithmException e) {
			String msg = "UNEXPECTED FAILURE: algorithm '" + algo + "'";
			throw new Error(msg, e);
		} finally {
			if (bis != null) {
				bis.close();
			}
		}
		String retVal = bytesToHex(buffer);

		return retVal;
	}

	/**
	 * Computes the MD5 or SHA1 of a text file, ignoring EOL characters. Files
	 * created under Linux have 0x0D as the EOL marker, whereas files created
	 * under Windows have \x0A\x0D as the EOL marker, whereas files created
	 * under Mac OS may have either \0x0D or \0x0A, but not both, as the EOL
	 * marker. This method strips out all \0x0A or \0x0D characters before
	 * computing a hash value.
	 *
	 * @param algo
	 *            see {@link #MD5_HASH_ALGORITHM} and
	 *            {@link #SHA1_HASH_ALGORITHM}
	 * @param f
	 *            a non-null, readable file
	 * @return A String representing the hash value in hexadecimal
	 * @throws IllegalArgumentException
	 *             if <code>algo</code> is neither MD5 nor SHA1, or if
	 *             <code>f</code> is null, doesn't exist or can not be read
	 * @throws IOException
	 *             if a IO exception occurs while the file is being read
	 * @throws Error
	 *             if a NoSuchAlgorithmException occurs (should never happen)
	 */
	public static String computeHashIgnoreEOL(String algo, File f)
			throws IllegalArgumentException, IOException {

		final int LF = '\n';
		final int CR = '\r';

		if (!MD5_HASH_ALGORITHM.equals(algo)
				&& !SHA1_HASH_ALGORITHM.equals(algo)) {
			throw new IllegalArgumentException("invalid algorithm: " + algo);
		}
		if (f == null || !f.exists() || !f.canRead()) {
			String fName = f == null ? "null" : f.getName();
			String msg =
				"file '" + fName + "' is null, doesn't exist or cannot be read";
			throw new IllegalArgumentException(msg);
		}

		byte[] buffer = new byte[8192];
		BufferedInputStream bis = null;
		try {
			MessageDigest digest = null;
			digest = MessageDigest.getInstance(algo);
			InputStream fis = new FileInputStream(f);
			bis = new BufferedInputStream(fis);

			// read bytes from the file
			int n = bis.read(buffer);
			while (n != -1) {
				int skipped = 0;
				int digested = 0;

				// i is the first potential non-EOL index
				int i = 0;

				// j is the index that will be checked
				int j = 0;

				while (j < n) {
					assert i <= j;
					int b = buffer[j];
					if (b == LF || b == CR) {
						// increment the skip count
						++skipped;
						// N is count from i of non-EOL
						int N = j - i;
						// update the digest if there are non-EOL bytes
						if (N > 0) {
							digest.update(buffer, i, N);
							// increment the digest count
							digested += N;
						}
						// increment i to the next check
						i = j + 1;
					}
					// increment j to the next check
					++j;
				}

				// Handle non-EOL stuff at the end of the buffer
				if (i < j) {
					assert j == n;
					int N = j - i;
					if (N > 0) {
						digest.update(buffer, i, N);
						digested += N;
					}
					i = j;
					assert i == n;
				}

				// Invariants after the buffer is processed
				assert skipped + digested == n;
				assert j == n;
				assert i == n;

				n = bis.read(buffer);
			}
			buffer = digest.digest();
		} catch (NoSuchAlgorithmException e) {
			String msg = "UNEXPECTED FAILURE: algorithm '" + algo + "'";
			throw new Error(msg, e);
		} finally {
			if (bis != null) {
				bis.close();
			}
		}
		String retVal = bytesToHex(buffer);

		return retVal;
	}

	final protected static char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = HEX_ARRAY[v >>> 4];
			hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
		}
		return new String(hexChars);
	}

}
