/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.en.us;

/**
 * A poorly named class. This class is not a parser at all. Instead, it is a
 * class that represents a <strong> <em>parsed</em> </strong> name. Retained for
 * backwards compatibility with some ChoiceMaker 2.5 models.
 *
 * @deprecated use the generic CFG Parser interface
 * @see com.choicemaker.cm.matching.cfg.Parsers
 * @see com.choicemaker.cm.matching.en.us.NameParser0
 * @see com.choicemaker.cm.matching.en.us.ParsedName0
 *
 * @author S. Yoakum-Stover
 * @author rphall (refactored for CM 2.7)
 */
public class NameParser {

	private ParsedName0 delegate;

	public NameParser(String f, String m, String l) {
		delegate = NameParser0.getDefaultInstance().parse(f, m, l);
	}

	/**
	 * @return the first name.
	 */
	public String getFirstName() {
		return delegate.getFirstName();
	}

	/**
	 * @return the middle name.
	 */
	public String getMiddleNames() {
		return delegate.getMiddleNames();
	}

	/**
	 * @return the last name.
	 */
	public String getLastName() {
		return delegate.getLastName();
	}

	/**
	 * @return the TITLES.
	 */
	public String getTitles() {
		return delegate.getTitles();
	}

	/**
	 * @return the MAIDEN name.
	 */
	public String getPotentialMaidenName() {
		return delegate.getPotentialMaidenName();
	}

	/**
	 * @return the mother's first name. This is specific to children's names.
	 */
	public String getMothersFirstName() {
		return delegate.getMothersFirstName();
	}

	/**
	 * Retained for backwards compatibility with some ChoiceMaker 2.5 models.
	 * 
	 * @deprecated
	 */
	public int getSwapSimilarity(NameParser o) {
		return NameParser0.getSwapSimilarity(this, o);
	}

	/**
	 * Retained for backwards compatibility with some ChoiceMaker 2.5 models.
	 * 
	 * @deprecated
	 */
	public static boolean matchingInitialOrName(String s1, String s2) {
		return NameParser0.matchingInitialOrName(s1, s2);
	}

	/**
	 * Retained for backwards compatibility with some ChoiceMaker 2.5 models.
	 * 
	 * @deprecated
	 */
	public static String fixMc(String s) {
		return NameParser0.fixMc(s);
	}

}
