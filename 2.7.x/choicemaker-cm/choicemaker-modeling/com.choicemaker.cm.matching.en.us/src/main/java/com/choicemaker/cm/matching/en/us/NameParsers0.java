/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.en.us;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import com.choicemaker.cm.core.ChoiceMakerExtensionPoint;
import com.choicemaker.cm.matching.gen.Sets;
import com.choicemaker.e2.CMConfigurationElement;
import com.choicemaker.e2.CMExtension;
import com.choicemaker.e2.platform.CMPlatformUtils;
import com.choicemaker.util.Precondition;
import com.choicemaker.util.StringUtils;

/**
 * A collection of NameParser0 instances configured from plugin descriptors
 *
 * @author rphall
 */
public final class NameParsers0 {

	private static final Logger logger = Logger.getLogger(NameParsers0.class
			.getName());

	private static AtomicReference<Map<String, NameParser0>> parsersRef =
		new AtomicReference<>();

	public static final String COMMENT_FLAG = "#";

	private static Map<String, NameParser0> parsersMap() {
		Map<String, NameParser0> parsers = parsersRef.get();
		if (parsers == null) {
			parsers = new HashMap<>();
			initRegisteredParsers(parsers);
			parsersRef.compareAndSet(null, parsers);
		}
		assert parsers == parsersRef.get();
		return Collections.unmodifiableMap(parsers);
	}

	public static boolean has(String name) {
		return parsersMap().containsKey(name);
	}

	public static NameParser0 get(String name) {
		Precondition.assertNonNullArgument(name);
		NameParser0 retVal = (NameParser0) parsersMap().get(name);
		if (retVal == null) {
			String msg = "No parser named '" + name + "'";
			logger.warning(msg);
		}
		return retVal;
	}

	public static Set<String> getParserKeys() {
		return parsersMap().keySet();
	}

	public static void put(NameParser0 parser) {
		Precondition.assertNonNullArgument(parser);
		String name = parser.getName();
		Precondition.assertNonNullArgument("A parser must have a name!", name);
		put(name, parser);
	}

	public static void put(String name, NameParser0 parser) {
		Precondition.assertNonEmptyString(
				"Cannot add a parser without a name!", name);
		Precondition.assertNonNullArgument("Cannot add a null parser: " + name,
				parser);
		final Map<String, NameParser0> parsers = parsersMap();
		final Map<String, NameParser0> newMap = new HashMap<>();
		newMap.putAll(parsers);
		NameParser0 replaced = newMap.put(name, parser);
		if (replaced != null) {
			logger.info("Replaced existing parser named '" + name + "'");
		}
		boolean updated = parsersRef.compareAndSet(parsers, newMap);
		if (!updated) {
			logger.warning("Unable to add parser '" + name + "'");
		}
	}

	public static void remove(NameParser0 parser) {
		Precondition.assertNonNullArgument(parser);
		String name = parser.getName();
		Precondition.assertNonNullArgument("A parser must have a name!", name);
		remove(name);
	}

	public static void remove(String name) {
		Precondition.assertNonEmptyString(
				"Cannot remove a parser without a name!", name);
		final Map<String, NameParser0> parsers = parsersMap();
		final Map<String, NameParser0> newMap = new HashMap<>();
		newMap.putAll(parsers);
		NameParser0 removed = newMap.remove(name);
		if (removed == null) {
			logger.warning("No parser named '" + name + "'");
		} else {
			boolean updated = parsersRef.compareAndSet(parsers, newMap);
			if (!updated) {
				logger.warning("Unable to remove parser '" + name + "'");
			}
		}
	}

	static void initRegisteredParsers(Map<String, NameParser0> parsers) {
		Precondition.assertNonNullArgument("null parser map", parsers);

		CMExtension[] extensions =
			CMPlatformUtils
					.getExtensions(ChoiceMakerExtensionPoint.CM_MATCHING_NAMEPARSER0);
		for (int i = 0; i < extensions.length; i++) {
			CMExtension ext = extensions[i];
			CMConfigurationElement[] els = ext.getConfigurationElements();
			for (int j = 0; j < els.length; j++) {
				CMConfigurationElement el = els[j];

				String name = getRequiredAttribute(el, "name");
				String gfn = getRequiredAttribute(el, "genericFirstNames");
				String coi = getRequiredAttribute(el, "childOfIndicators");
				String iln = getRequiredAttribute(el, "invalidLastNames");
				String nt = getRequiredAttribute(el, "nameTitles");
				String lnp = getRequiredAttribute(el, "lastNamePrefixes");

				Set<String> setGFN = initializeSetAndContents(gfn);
				Set<String> setCOI = initializeSetAndContents(coi);
				Set<String> setILN = initializeSetAndContents(iln);
				Set<String> setNT = initializeSetAndContents(nt);
				Set<String> setLNP = initializeSetAndContents(lnp);

				NameParser0 np = new NameParser0();
				np.setName(name);
				np.setGenericFirstNames(setGFN);
				np.setChildOfIndicators(setCOI);
				np.setInvalidLastNames(setILN);
				np.setNameTitles(setNT);
				np.setLastNamePrefixes(setLNP);

				parsers.put(name, np);
			}
		}
	}

	private static String getRequiredAttribute(CMConfigurationElement el,
			String name) {
		assert el != null;
		assert name != null && StringUtils.nonEmptyString(name);
		assert name.trim().equals(name);
		String retVal = el.getAttribute(name);
		if (retVal == null) {
			String msg = "null value for attribute '" + name + "'";
			throw new IllegalStateException(msg);
		}
		retVal = retVal.trim();
		if (retVal.isEmpty()) {
			String msg = "blank value for attribute '" + name + "'";
			throw new IllegalStateException(msg);
		}
		logger.fine("'" + name + "': '" + retVal + "'");
		return retVal;
	}

	private static Set<String> initializeSetAndContents(String _setName) {

		// Preconditions
		if (!StringUtils.nonEmptyString(_setName)) {
			throw new IllegalArgumentException("null or blank set name");
		}
		Collection<String> c = Sets.getCollection(_setName);
		if (c == null) {
			String msg = "No set named '" + _setName + "'";
			logger.severe(msg);
			throw new IllegalArgumentException(msg);
		}

		Set<String> retVal = new HashSet<>();
		for (Iterator<String> i = c.iterator(); i.hasNext();) {
			String s = (String) i.next();
			boolean nonEmpty = StringUtils.nonEmptyString(s);
			if (nonEmpty && !s.startsWith(COMMENT_FLAG)) {
				logger.finer(_setName + ": adding '" + s + "'");
				retVal.add(s);
			} else if (nonEmpty) {
				logger.finer(_setName + ": skipping '" + s + "'");
			} else {
				logger.finer(_setName + ": skipping blank line");
			}
		}
		if (retVal.isEmpty()) {
			String msg = "Set '" + _setName + "' has no content";
			logger.warning(msg);
		} else {
			int size = retVal.size();
			assert size > 0;
			String msg = "Set '" + _setName + "' size: " + size;
			logger.fine(msg);
		}
		return retVal;
	}

	private NameParsers0() {
	}

}
