/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.e2.mbd;

import java.io.File;

import com.choicemaker.e2.CMPath;
import com.choicemaker.e2.mbd.runtime.IPath;

public class PathAdapter {

	public static IPath convert(CMPath o) {
		IPath retVal = null;
		if (o != null) {
			retVal = new CMtoStd(o);
		}
		return retVal;
	}

	public static IPath[] convert(CMPath[] o) {
		IPath[] retVal = null;
		if (o != null) {
			retVal = new IPath[o.length];
			for (int i=0; i<o.length; i++) {
				retVal[i] = convert(o[i]);
			}
		}
		return retVal;
	}
	
	public static CMPath convert(IPath o) {
		CMPath retVal = null;
		if (o != null) {
			retVal = new StdToCM(o);
		}
		return retVal;
	}

	public static CMPath[] convert(IPath[] o) {
		CMPath[] retVal = null;
		if (o != null) {
			retVal = new CMPath[o.length];
			for (int i=0; i<o.length; i++) {
					retVal[i] = convert(o[i]);
			}
		}
		return retVal;
	}
	
	protected static class StdToCM implements CMPath {
		
		private final IPath delegate;

		public StdToCM(IPath o) {
			if (o == null) {
				throw new IllegalArgumentException("null delegate");
			}
			this.delegate = o;
		}

		@Override
		public CMPath addFileExtension(String extension) {
			return PathAdapter.convert(delegate.addFileExtension(extension));
		}

		@Override
		public CMPath addTrailingSeparator() {
			return PathAdapter.convert(delegate.addTrailingSeparator());
		}

		@Override
		public CMPath append(String path) {
			return PathAdapter.convert(delegate.append(path));
		}

		@Override
		public CMPath append(CMPath path) {
			return PathAdapter.convert(delegate.append(PathAdapter.convert(path)));
		}

		@Override
		public Object clone() {
			return delegate.clone();
		}

		@Override
		public boolean equals(Object obj) {
			return delegate.equals(obj);
		}

		@Override
		public String getDevice() {
			return delegate.getDevice();
		}

		@Override
		public String getFileExtension() {
			return delegate.getFileExtension();
		}

		@Override
		public boolean hasTrailingSeparator() {
			return delegate.hasTrailingSeparator();
		}

		@Override
		public boolean isAbsolute() {
			return delegate.isAbsolute();
		}

		@Override
		public boolean isEmpty() {
			return delegate.isEmpty();
		}

		@Override
		public boolean isPrefixOf(CMPath anotherPath) {
			return delegate.isPrefixOf(PathAdapter.convert(anotherPath));
		}

		@Override
		public boolean isRoot() {
			return delegate.isRoot();
		}

		@Override
		public boolean isUNC() {
			return delegate.isUNC();
		}

		@Override
		public boolean isValidPath(String path) {
			return delegate.isValidPath(path);
		}

		@Override
		public boolean isValidSegment(String segment) {
			return delegate.isValidSegment(segment);
		}

		@Override
		public String lastSegment() {
			return delegate.lastSegment();
		}

		@Override
		public CMPath makeAbsolute() {
			return PathAdapter.convert(delegate.makeAbsolute());
		}

		@Override
		public CMPath makeRelative() {
			return PathAdapter.convert(delegate.makeRelative());
		}

		@Override
		public CMPath makeUNC(boolean toUNC) {
			return PathAdapter.convert(delegate.makeUNC(toUNC));
		}

		@Override
		public int matchingFirstSegments(CMPath anotherPath) {
			return delegate.matchingFirstSegments(PathAdapter.convert(anotherPath));
		}

		@Override
		public CMPath removeFileExtension() {
			return PathAdapter.convert(delegate.removeFileExtension());
		}

		@Override
		public CMPath removeFirstSegments(int count) {
			return PathAdapter.convert(delegate.removeFirstSegments(count));
		}

		@Override
		public CMPath removeLastSegments(int count) {
			return PathAdapter.convert(delegate.removeLastSegments(count));
		}

		@Override
		public CMPath removeTrailingSeparator() {
			return PathAdapter.convert(delegate.removeTrailingSeparator());
		}

		@Override
		public String segment(int index) {
			return delegate.segment(index);
		}

		@Override
		public int segmentCount() {
			return delegate.segmentCount();
		}

		@Override
		public String[] segments() {
			return delegate.segments();
		}

		@Override
		public CMPath setDevice(String device) {
			return PathAdapter.convert(delegate.setDevice(device));
		}

		@Override
		public File toFile() {
			return delegate.toFile();
		}

		@Override
		public String toOSString() {
			return delegate.toOSString();
		}

		@Override
		public String toString() {
			return delegate.toString();
		}

		@Override
		public CMPath uptoSegment(int count) {
			return PathAdapter.convert(delegate.uptoSegment(count));
		}

	}

	protected static class CMtoStd implements IPath {
		
		private final CMPath delegate;

		public CMtoStd(CMPath o) {
			if (o == null) {
				throw new IllegalArgumentException("null delegate");
			}
			this.delegate = o;
		}

		@Override
		public IPath addFileExtension(String extension) {
			return PathAdapter.convert(delegate.addFileExtension(extension));
		}

		@Override
		public IPath addTrailingSeparator() {
			return PathAdapter.convert(delegate.addTrailingSeparator());
		}

		@Override
		public IPath append(String path) {
			return PathAdapter.convert(delegate.append(path));
		}

		@Override
		public IPath append(IPath path) {
			return PathAdapter.convert(delegate.append(PathAdapter.convert(path)));
		}

		@Override
		public Object clone() {
			return delegate.clone();
		}

		@Override
		public boolean equals(Object obj) {
			return delegate.equals(obj);
		}

		@Override
		public String getDevice() {
			return delegate.getDevice();
		}

		@Override
		public String getFileExtension() {
			return delegate.getFileExtension();
		}

		@Override
		public boolean hasTrailingSeparator() {
			return delegate.hasTrailingSeparator();
		}

		@Override
		public boolean isAbsolute() {
			return delegate.isAbsolute();
		}

		@Override
		public boolean isEmpty() {
			return delegate.isEmpty();
		}

		@Override
		public boolean isPrefixOf(IPath anotherPath) {
			return delegate.isPrefixOf(PathAdapter.convert(anotherPath));
		}

		@Override
		public boolean isRoot() {
			return delegate.isRoot();
		}

		@Override
		public boolean isUNC() {
			return delegate.isUNC();
		}

		@Override
		public boolean isValidPath(String path) {
			return delegate.isValidPath(path);
		}

		@Override
		public boolean isValidSegment(String segment) {
			return delegate.isValidSegment(segment);
		}

		@Override
		public String lastSegment() {
			return delegate.lastSegment();
		}

		@Override
		public IPath makeAbsolute() {
			return PathAdapter.convert(delegate.makeAbsolute());
		}

		@Override
		public IPath makeRelative() {
			return PathAdapter.convert(delegate.makeRelative());
		}

		@Override
		public IPath makeUNC(boolean toUNC) {
			return PathAdapter.convert(delegate.makeUNC(toUNC));
		}

		@Override
		public int matchingFirstSegments(IPath anotherPath) {
			return delegate.matchingFirstSegments(PathAdapter.convert(anotherPath));
		}

		@Override
		public IPath removeFileExtension() {
			return PathAdapter.convert(delegate.removeFileExtension());
		}

		@Override
		public IPath removeFirstSegments(int count) {
			return PathAdapter.convert(delegate.removeFirstSegments(count));
		}

		@Override
		public IPath removeLastSegments(int count) {
			return PathAdapter.convert(delegate.removeLastSegments(count));
		}

		@Override
		public IPath removeTrailingSeparator() {
			return PathAdapter.convert(delegate.removeTrailingSeparator());
		}

		@Override
		public String segment(int index) {
			return delegate.segment(index);
		}

		@Override
		public int segmentCount() {
			return delegate.segmentCount();
		}

		@Override
		public String[] segments() {
			return delegate.segments();
		}

		@Override
		public IPath setDevice(String device) {
			return PathAdapter.convert(delegate.setDevice(device));
		}

		@Override
		public File toFile() {
			return delegate.toFile();
		}

		@Override
		public String toOSString() {
			return delegate.toOSString();
		}

		@Override
		public String toString() {
			return delegate.toString();
		}

		@Override
		public IPath uptoSegment(int count) {
			return PathAdapter.convert(delegate.uptoSegment(count));
		}

	}

}
