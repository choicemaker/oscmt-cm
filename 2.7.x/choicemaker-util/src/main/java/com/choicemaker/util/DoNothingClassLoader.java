package com.choicemaker.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

public class DoNothingClassLoader extends ClassLoader {

	private static final Logger logger =
		Logger.getLogger(DoNothingClassLoader.class.getName());

	private static final String SHORT_NAME = DoNothingClassLoader.class.getSimpleName();

	private static final String NOT_IMPLEMENTED = "not implemented";
	
	private static final String NOT_IMPLEMENTED(String method) {
		String retVal = SHORT_NAME + "." + method + "is not implemented -- check before invoking";
		return retVal;
	}

	public static final DoNothingClassLoader instance =
		new DoNothingClassLoader();

	private DoNothingClassLoader() {
	}

	public int hashCode() {
		return 0;
	}

	public boolean equals(Object obj) {
		return instance == obj;
	}

	public String toString() {
		return SHORT_NAME;
	}

	public Class<?> loadClass(String name) throws ClassNotFoundException {
		logger.warning(NOT_IMPLEMENTED("loadClass(String)"));
		throw new ClassNotFoundException(NOT_IMPLEMENTED);
	}

	public URL getResource(String name) {
		logger.warning(NOT_IMPLEMENTED("getResourceAsStream(String)"));
		return null;
	}

	public Enumeration<URL> getResources(String name) throws IOException {
		return new Enumeration<URL>() {

			@Override
			public boolean hasMoreElements() {
				return false;
			}

			@Override
			public URL nextElement() {
				logger.warning(NOT_IMPLEMENTED("getResources().nextElement(String)"));
				throw new NoSuchElementException(NOT_IMPLEMENTED);
			}

		};
	}

	public InputStream getResourceAsStream(String name) {
		logger.warning(NOT_IMPLEMENTED("getResourceAsStream(String)"));
		return null;
	}

	public void setDefaultAssertionStatus(boolean enabled) {
		logger.warning(NOT_IMPLEMENTED("setDefaultAssertionStatus(boolean)"));
	}

	public void setPackageAssertionStatus(String packageName, boolean enabled) {
		logger.warning(NOT_IMPLEMENTED("setPackageAssertionStatus(String, boolean)"));
	}

	public void setClassAssertionStatus(String className, boolean enabled) {
		logger.warning(NOT_IMPLEMENTED("setClassAssertionStatus(String, boolean)"));
	}

	public void clearAssertionStatus() {
		logger.warning(NOT_IMPLEMENTED("clearAssertionStatus()"));
	}
}