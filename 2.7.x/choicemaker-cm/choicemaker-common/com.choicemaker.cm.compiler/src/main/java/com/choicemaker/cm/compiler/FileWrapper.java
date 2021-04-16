/*
 * Copyright (c) 2001, 2009 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.compiler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;
import java.util.jar.JarEntry;

/**
 * Wrapper around ClueMaker source file.
 *
 * @author   Matthias Zenger
 */
public interface FileWrapper {

	/** get name of the file
	 */
	public String getName();

	/** get path of the file
	 */
	public String getPath();

	/** does the file exist?
	 */
	public boolean exists();

	/** is the file a directory?
	 */
	public boolean isDirectory();

	/** return an input stream for the file
	 */
	public InputStream getInputStream() throws IOException;

	/** list contents of a directory
	 */
	public String[] list() throws IOException;

	/** open a new file
	 */
	public FileWrapper access(String name);

	public static class NativeFile implements FileWrapper {
		private File file;

		public NativeFile(File file) {
			this.file = file;
		}

		@Override
		public String getName() {
			return file.getName();
		}

		@Override
		public String getPath() {
			return file.getPath();
		}

		@Override
		public boolean exists() {
			return file.exists();
		}

		@Override
		public boolean isDirectory() {
			return file.isDirectory();
		}

		@Override
		public InputStream getInputStream() throws IOException {
			return new FileInputStream(file.getAbsoluteFile());
		}

		@Override
		public String[] list() throws IOException {
			return file.list();
		}

		@Override
		public FileWrapper access(String name) {
			return new NativeFile(new File(file, name).getAbsoluteFile());
		}
	}

	public static class JarFile implements FileWrapper {
		private JarArchive archive;
		private JarEntry entry;

		JarFile(JarArchive archive, String name) {
			if ((this.archive = archive).jarfile != null) {
				name = name.replace(File.separatorChar, '/');
				entry = archive.jarfile.getJarEntry(name);
			}
		}

		@Override
		public String getName() {
			return entry.getName();
		}

		@Override
		public String getPath() {
			return archive.getPath() + "/" + entry.getName();
		}

		@Override
		public boolean exists() {
			return entry != null;
		}

		@Override
		public boolean isDirectory() {
			return entry.isDirectory();
		}

		@Override
		public InputStream getInputStream() throws IOException {
			return archive.jarfile.getInputStream(entry);
		}

		@Override
		public String[] list() throws IOException {
			if (!isDirectory())
				throw new IOException();
			else
				return archive.filter(entry.getName());
		}

		@Override
		public FileWrapper access(String filename) {
			return archive.access(entry.getName() + filename);
		}
	}

	public static class JarDir implements FileWrapper {
		private JarArchive archive;
		private String name;

		JarDir(JarArchive archive, String name) {
			this.archive = archive;
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public String getPath() {
			return archive.getPath() + "/" + name;
		}

		@Override
		public boolean exists() {
			return true;
		}

		@Override
		public boolean isDirectory() {
			return true;
		}

		@Override
		public InputStream getInputStream() throws IOException {
			throw new IOException("cannot open input stream for directory");
		}

		@Override
		public String[] list() throws IOException {
			return archive.filter(name);
		}

		@Override
		public FileWrapper access(String filename) {
			return archive.access(name + filename);
		}
	}

	public static class JarArchive implements FileWrapper {
		private String name;
		private String path;
		private java.util.jar.JarFile jarfile;
		private HashMap implicit;

		public JarArchive(File f) {
			name = f.getName();
			path = f.getPath();
			implicit = new HashMap();
			try {
				// open Jar file
				jarfile = new java.util.jar.JarFile(f);
				// extract all implicit directories
				Enumeration e = jarfile.entries();
				while (e.hasMoreElements()) {
					String fname = ((JarEntry) e.nextElement()).getName();
					for (int i = fname.indexOf('/'); i >= 0;) {
						String dname = fname.substring(0, ++i);
						if (implicit.get(dname) == null)
							implicit.put(dname, new JarDir(this, dname));
						i = fname.indexOf('/', i);
					}
				}
			} catch (Exception e) {
			}
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public String getPath() {
			return path;
		}

		@Override
		public boolean exists() {
			return jarfile != null;
		}

		@Override
		public boolean isDirectory() {
			return jarfile != null;
		}

		@Override
		public InputStream getInputStream() throws IOException {
			throw new IOException("cannot open input stream for jar file");
		}

		@Override
		public String[] list() throws IOException {
			return filter("");
		}

		public String[] filter(String prefix) {
			int len = prefix.length();
			Vector files = new Vector();
			Enumeration e = jarfile.entries();
			while (e.hasMoreElements()) {
				String name = ((JarEntry) e.nextElement()).getName();
				if (name.startsWith(prefix)) {
					int l = name.length();
					if (l != len) {
						int i = name.indexOf('/', len);
						if ((i < 0) || (i == (l - 1)))
							files.add((i < 0) ? name.substring(len) : name.substring(len, i));
					}
				}
			}
			return (String[]) files.toArray(new String[files.size()]);
		}

		@Override
		public FileWrapper access(String name) {
			name = name.replace(File.separatorChar, '/');
			FileWrapper entry = (FileWrapper) implicit.get(name);
			return (entry == null) ? new JarFile(this, name) : entry;
		}
	}
}
