/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.en.us;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
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

	private static HashMap parsers = new HashMap();

	public static final String COMMENT_FLAG = "#";

	static {
		initRegisteredParsers();
	}

	public static boolean has(String name) {
		return parsers.containsKey(name);
	}

	public static NameParser0 get(String name) {
		Precondition.assertNonNullArgument(name);
		NameParser0 retVal = (NameParser0) parsers.get(name);
		if (retVal == null) {
			String msg = "No parser named '" + name + "'";
			logger.warning(msg);
		}
		return retVal;
	}

	public static Set getParserKeys() {
		return parsers.keySet();
	}

	public static void put(NameParser0 parser) {
		Precondition.assertNonNullArgument(parser);
		String name = parser.getName();
		Precondition.assertNonNullArgument("A parser must have a name!", name);
		put(name, parser);
	}

	public static void put(String name, NameParser0 parser) {
		Precondition.assertNonNullArgument(
				"Cannot add a parser without a name!", name);
		parsers.put(name, parser);
	}

	static void initRegisteredParsers() {
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

				Set setGFN = initializeSetAndContents(gfn);
				Set setCOI = initializeSetAndContents(coi);
				Set setILN = initializeSetAndContents(iln);
				Set setNT = initializeSetAndContents(nt);
				Set setLNP = initializeSetAndContents(lnp);

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

	private static Set initializeSetAndContents(String _setName) {

		// Preconditions
		if (!StringUtils.nonEmptyString(_setName)) {
			throw new IllegalArgumentException("null or blank set name");
		}
		Collection c = Sets.getCollection(_setName);
		if (c == null) {
			String msg = "No set named '" + _setName + "'";
			logger.severe(msg);
			throw new IllegalArgumentException(msg);
		}

		Set retVal = new HashSet();
		for (Iterator i = c.iterator(); i.hasNext();) {
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
