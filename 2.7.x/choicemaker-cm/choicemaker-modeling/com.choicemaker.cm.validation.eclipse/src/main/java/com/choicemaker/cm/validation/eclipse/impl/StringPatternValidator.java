/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.validation.eclipse.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.choicemaker.cm.matching.gen.Sets;
import com.choicemaker.cm.validation.AbstractSetBasedValidator;
import com.choicemaker.cm.validation.IPatternMatcher;
import com.choicemaker.util.Precondition;
import com.choicemaker.util.StringUtils;

/**
 * Validates a non-null name by checking whether it matches
 * any pattern in some set of patterns. Null names are considered invalid.
 * <p>see StringPatternInvalidator</p>
 *
 * @author rphall
 */
public class StringPatternValidator
	extends AbstractSetBasedValidator<String>
	implements IPatternMatcher {

	/** The hash character (#) */
	public static final String COMMENT_FLAG = "#";

	protected static Logger logger =
		Logger.getLogger(StringPatternValidator.class.getName());
	
	protected static final String INMEMORY_SET_NAME_PREFIX = "InMemorySet_";

	protected static <T> String generateSetName(Collection<T> set) {
		String retVal = null;
		if (set != null) {
			retVal = INMEMORY_SET_NAME_PREFIX + set.hashCode();
		}
		return retVal;
	}

	protected Pattern[] patterns;

	protected String setName;

	/**
	 * Partially constructs a validator. The
	 * {@link #setNamedSet(String)} method must
	 * be called to finish construction.
	 */
	public StringPatternValidator() {
	}

	/**
	 * Constructs a validator from an in-memory set of patterns.
	 * A generated name is assigned to this validator.
	 * @param A non-null collection of regex expressions.
	 */
	public StringPatternValidator(Collection<String> regexCollection) {
		String generatedName = generateSetName(regexCollection);
		initializeSetNameAndPatterns(generatedName, regexCollection);
	}

	/**
	 * Constructs a validator from a named set of patterns.
	 * @param setName a named set in the collection
	 * com.choicemaker.cm.matching.gen.Sets;
	 */
	public StringPatternValidator(String setName) {
		initializeSetNameAndPatterns(setName);
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.validation.eclipse.ISetBasedValidator#getNamedSet()
	 */
	@Override
	public String getNamedSet() {
		if (this.setName == null) {
			throw new IllegalStateException("set name not initialized");
		}
		return this.setName;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.validation.eclipse.ISetBasedValidator#setNamedSet(String)
	 */
	@Override
	public void setNamedSet(String setName) {
		initializeSetNameAndPatterns(setName);
	}

	private void initializeSetNameAndPatterns(String setName, Collection<String> regexCollection) {
		// Preconditions
		Precondition.assertNonEmptyString("Null or blank set name", setName);
		Precondition.assertNonNullArgument(
				"Null regex collection for set name: " + setName, regexCollection);
		Precondition.assertBoolean(
				"empty regex collection for set name : " + setName,
				regexCollection.size() > 0);

		this.setName = setName;
		List<Pattern> patternList = new ArrayList<>();
		for (String s : regexCollection) {
			boolean nonEmptyString = StringUtils.nonEmptyString(s);
			if (nonEmptyString && !s.startsWith(COMMENT_FLAG)) {
				logger.fine(setName + ": adding '" + s + "'");
				Pattern p = null;
				try {
					p = Pattern.compile(s);
				} catch (PatternSyntaxException x) {
					String msg = "invalid pattern '" + s + "'";
					logger.severe(msg + ": " + x);
					throw x;
				}
				patternList.add(p);
			} else if (nonEmptyString) {
				logger.fine(setName + ": skipping '" + s + "'");
			} else {
				logger.fine(setName + ": skipping blank line");
			}
		}
		this.patterns = patternList.toArray(new Pattern[0]);
	}

	private void initializeSetNameAndPatterns(String setName) {
		Precondition.assertNonEmptyString("Null or blank set name", setName);
		Collection<String> c = Sets.getCollection(setName);
		initializeSetNameAndPatterns(setName,c);
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.validation.IPatternMatcher#getPatterns()
	 */
	@Override
	public Pattern[] getPatterns() {
		if (this.patterns == null) {
			throw new IllegalStateException("patterns not initialized");
		}

		Pattern[] retVal = new Pattern[this.patterns.length];
		for (int i = 0; i < this.patterns.length; i++) {
			String pattern = this.patterns[i].pattern();
			retVal[i] = Pattern.compile(pattern);
		}
		return retVal;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.validation.eclipse.ISetBasedValidator#getSetContents()
	 */
	@Override
	public Set<String> getSetContents() {
		if (this.patterns == null) {
			throw new IllegalStateException("patterns not initialized");
		}

		Set<String> retVal = new HashSet<>();
		for (int i = 0; i < this.patterns.length; i++) {
			retVal.add(this.patterns[i].pattern());
		}
		return retVal;
	}

	public Class<?>[] getValidationTypes() {
		return new Class[] { String.class };
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.validation.eclipse.IValidator#isValid(java.lang.String)
	 */
	@Override
	public boolean isValid(String object) {
		boolean retVal = false;
		if (object != null && object instanceof String) {
			retVal = isMatch(object);
		}
		return retVal;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.validation.IPatternMatcher#isMatch(java.lang.String)
	 */
	@Override
	public boolean isMatch(String s) {
		boolean retVal = false;
		Matcher[] matches = getAllMatches(s);
		if (matches.length > 0) {
			retVal = true;
		}
		return retVal;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.validation.IPatternMatcher#getFirstMatch(java.lang.String)
	 */
	@Override
	public Matcher getFirstMatch(String s) {
		Matcher retVal = null;
		Matcher[] matches = getAllMatches(s,true);
		if (matches.length > 0) {
			retVal = matches[0];
		}
		return retVal;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.validation.IPatternMatcher#getAllMatches(java.lang.String)
	 */
	@Override
	public Matcher[] getAllMatches(String s) {
		return getAllMatches(s,false);
	}

	protected Matcher[] getAllMatches(String s, boolean firstMatchOnly) {
		// Preconditions
		if (this.patterns == null) {
			throw new IllegalStateException("patterns not initialized");
		}

		List<Matcher> matches = new ArrayList<>();
		for (int i = 0; i < this.patterns.length; i++) {
			Matcher matcher = this.patterns[i].matcher(s);
			if (matcher.matches()) {
				matches.add(matcher);
				if (firstMatchOnly) {
					break;
				} 
			}
		}
		Matcher[] retVal = matches.toArray(new Matcher[matches.size()]);
		return retVal;
	}

}
