/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.en.us;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import com.choicemaker.cm.matching.en.Soundex;
import com.choicemaker.util.StringUtils;

/**
 * A simple name parser that deals with compound names.
 * <ul>
 * <li>The name parser breaks multiple names reported in a single field</li>
 * <li>Filters out titles and suffixes, such as MR and JR</li>
 * <li>Removes invalid values such as N/A</li>
 * <ul>
 *
 * @see com.choicemaker.cm.matching.cfg.Parsers
 *
 * @author S. Yoakum-Stover
 * @author rphall (refactored for CM 2.7)
 */
public class NameParser0 {

	private static final Logger logger = Logger.getLogger(NameParser0.class
			.getName());

	public static final String DEFAULT_NAMEPARSER0 = "en.us.defaultNameParser0";

	private static final AtomicReference instance = new AtomicReference();

	public static NameParser0 getDefaultInstance() {
		if (instance.get() == null) {
			NameParser0 defaultNP0 = lookupDefaultNameParser0();
			instance.compareAndSet(null, defaultNP0);
		}
		NameParser0 retVal = (NameParser0) instance.get();
		assert retVal != null;
		return retVal;
	}

	public static NameParser0 lookupDefaultNameParser0() {
		NameParser0 retVal = NameParsers0.get(DEFAULT_NAMEPARSER0);
		if (retVal == null) {
			retVal = new NameParser0();
			retVal.setName(DEFAULT_NAMEPARSER0);
			NameParsers0.put(DEFAULT_NAMEPARSER0, retVal);
			String msg =
				"No-data parser registered under '" + DEFAULT_NAMEPARSER0
						+ "'; using do-nothing parser: " + retVal.toString();
			logger.warning(msg);
		}
		return retVal;
	}

	static final int NUM_NAMES = 4;
	static final int NUM_NAMES2 = 2 * NUM_NAMES;

	/**
	 * @return a measure for how many name components are swapped. Each exact
	 *         swap increments the score by 2. Each name approximate swap
	 *         (Soundex) increases the score by 1.
	 *
	 *         A higher score indicates a higher similarity. E.g., the
	 *         similarity score for "JIM R. SMITH" and "SMIT JIM" is 3.
	 */
	public static int getSwapSimilarity(NameParser np1, NameParser np2) {
		int score = 0;
		if (np1 != null && np2 != null) {
			String[] n = new String[NUM_NAMES2 + NUM_NAMES2];
			boolean[] f = new boolean[NUM_NAMES2];
			n[0] = np1.getFirstName();
			n[1] = np1.getMiddleNames();
			n[2] = np1.getLastName();
			n[3] = np1.getPotentialMaidenName();
			n[4] = np2.getFirstName();
			n[5] = np2.getMiddleNames();
			n[6] = np2.getLastName();
			n[7] = np2.getPotentialMaidenName();
			for (int i = 0; i < NUM_NAMES2; ++i) {
				f[i] = n[i] != null;
				if (f[i] && n[i].length() > 0) {
					n[i + NUM_NAMES2] = Soundex.soundex(n[i]);
				}
			}
			for (int i = 0; i < NUM_NAMES; ++i) {
				boolean sndx = false;
				for (int j = NUM_NAMES; j < NUM_NAMES2; ++j) {
					if (i + NUM_NAMES != j && f[i] && f[j]) {
						if (n[i] == n[j]) {
							if (sndx) {
								score += 1;
							} else {
								score += 2;
							}
							break;
						} else if (!sndx
								&& n[i + NUM_NAMES2].equals(n[j + NUM_NAMES2])) {
							score += 1;
							sndx = true;
						}
					}
				}
			}
		}
		return score;
	}

	private static String dedupTokens(String tokens) {
		String retVal = "";
		if (StringUtils.nonEmptyString(tokens)) {
			StringTokenizer st = new StringTokenizer(tokens);
			int count = st.countTokens();
			// Do nothing if count == 0
			if (count == 1) {
				retVal = tokens.trim();
			} else {
				Set seen = new HashSet();
				while (st.hasMoreTokens()) {
					String token = st.nextToken();
					boolean isNew = seen.add(token);
					if (isNew) {
						retVal = concatWithSeparator(retVal, token, " ");
					}
				}
			}
		}
		return retVal;
	}

	public static String fixMc(String s) {
		int len = s.length();
		int i = 0;
		if (len >= 4) {
			if (s.charAt(0) == 'M') {
				char c = s.charAt(1);
				if (c == 'C' && s.charAt(2) == ' ') {
					i = 3;
					while (i < len && s.charAt(i) == ' ') {
						++i;
					}
					s = "MC" + s.substring(i, len);
					len = s.length();
				} else if (c == 'A' && s.charAt(2) == 'C' && s.charAt(3) == ' ') {
					i = 4;
					while (i < len && s.charAt(i) == ' ') {
						++i;
					}
					s = "MAC" + s.substring(i, len);
					len = s.length();
				}
			}
			while (i < len) {
				char c = s.charAt(i);
				if (c == '-' || c == ' ' || c == '/') {
					return s.substring(0, i + 1)
							+ fixMc(s.substring(i + 1, len));
				}
				++i;
			}
		}
		return s;
	}

	/**
	 * Flips the order of the tokens in the string.
	 *
	 * @param s
	 */
	public static String flipToks(String s) {
		StringTokenizer sTok = new StringTokenizer(s);
		int numToks = sTok.countTokens();
		if (numToks <= 1) {
			return s;
		}
		String[] tokArray = new String[numToks];
		for (int i = 0; i < numToks; i++) {
			tokArray[numToks - 1 - i] = sTok.nextToken();
		}
		String result = "";
		for (int j = 0; j < numToks; j++) {
			result = concatWithSeparator(result, tokArray[j], " ");
		}
		return result;
	}

	/**
	 * Declares a match if one String is of length 1 and the other String begins
	 * with it, or if the two Strings are equals. For example
	 * MatchingInitialOrName("A", "Annie") -> true MatchingInitialOrName("A",
	 * "A") -> true MatchingInitialOrName("Ann", "Ann") -> true
	 * MatchingInitialOrName("Anette", "Annie") -> false
	 * MatchingInitialOrName("A", "B") -> false
	 */
	public static boolean matchingInitialOrName(String s1, String s2) {
		boolean isMatch = false;
		if (!StringUtils.nonEmptyString(s1) || !StringUtils.nonEmptyString(s2)) {
			return false;
		}
		int len1 = s1.length();
		int len2 = s2.length();

		// Check for matching initial
		if (len1 == 1) {
			isMatch = s2.startsWith(s1);
		} else if (len2 == 1) {
			isMatch = s1.startsWith(s2);
		} else { // Otherwise check for complete match
			isMatch = s2.equals(s1);
		}
		return isMatch;
	}

	public static String concatWithSeparator(String s1, String s2, String sep) {
		boolean b1 = StringUtils.nonEmptyString(s1);
		boolean b2 = StringUtils.nonEmptyString(s2);
		if (b1) {
			return b2 ? (s1 + sep + s2) : s1;
		} else {
			return b2 ? s2 : null;
		}
	}

	private String name;
	private Collection genericFirstNames = new HashSet();
	private Collection childOfIndicators = new HashSet();
	private Collection invalidLastNames = new HashSet();
	private Collection nameTitles = new HashSet();
	private Collection lastNamePrefixes = new HashSet();

	public ParsedName0 parse(String f, String m, String l) {
		String first =
			StringUtils.nonEmptyString(f) ? StringUtils.removePunctuation(f)
					: "";
		String middle =
			StringUtils.nonEmptyString(m) ? StringUtils.removePunctuation(m)
					: "";
		String last =
			StringUtils.nonEmptyString(l) ? StringUtils.removePunctuation(l)
					: "";
		// BUGFIX rphall 2008-07-13
		// StringUtils.removePunctuation sometimes does not remove trailing
		// space
		first = first.trim();
		middle = middle.trim();
		last = last.trim();
		// END BUGFIX
		ParsedName0 retVal = new ParsedName0();
		ParsedName0 test = chunkUpNamesStrings(first, middle, last);
		if (test.getFirstName() != null)
			retVal.setFirstName(test.getFirstName().intern());
		if (test.getMiddleNames() != null)
			retVal.setMiddleNames(test.getMiddleNames().intern());
		if (test.getLastName() != null)
			retVal.setLastName(test.getLastName().intern());
		if (test.getTitles() != null)
			retVal.setTitles(test.getTitles().intern());
		if (test.getPotentialMaidenName() != null)
			retVal.setPotentialMaidenName(test.getPotentialMaidenName()
					.intern());
		if (test.getMothersFirstName() != null)
			retVal.setMothersFirstName(test.getMothersFirstName().intern());
		return retVal;
	}

	protected ParsedName0 chunkUpNamesStrings(String first, String middle,
			String last) {

		ParsedName0 retVal = new ParsedName0();

		// chunk up the first name
		ParsedName0 firstNames = chunkUpNameString(first, true);

		// place the first name chunks
		retVal.setFirstName(firstNames.getFirstName());
		retVal.setMiddleNames(firstNames.getMiddleNames());
		retVal.setMothersFirstName(firstNames.getMothersFirstName());
		String tmpTitles = firstNames.getTitles();

		// place the middle names
		if (StringUtils.nonEmptyString(middle)) {
			ParsedName0 tmpMiddles = chunkUpNameString(middle, false);
			if (StringUtils.nonEmptyString(tmpMiddles.getFirstName())) {
				String s =
					concatWithSeparator(retVal.getMiddleNames(),
							tmpMiddles.getFirstName(), " ");
				retVal.setMiddleNames(s);
			}
			if (StringUtils.nonEmptyString(tmpMiddles.getMiddleNames())) {
				String s =
					concatWithSeparator(retVal.getMiddleNames(),
							tmpMiddles.getMiddleNames(), " ");
				retVal.setMiddleNames(s);
			}
			tmpTitles =
				concatWithSeparator(tmpTitles, tmpMiddles.getTitles(), " ");
		}

		// chunk up the last name
		ParsedName0 lastNames = chunkUpLastNameString(last);

		// place the last name chunks
		// 2008-10-20 rphall
		// Parsing of compound last names is improved if the
		// lastNames tokens are NOT combined with middle names
		// e.g. WONG DE JESUS, VAN DER ZEE
		retVal.setLastName(lastNames.getLastName());
		retVal.setPotentialMaidenName(lastNames.getPotentialMaidenName());
		tmpTitles = concatWithSeparator(tmpTitles, lastNames.getTitles(), " ");
		retVal.setTitles(dedupTokens(tmpTitles));

		return retVal;
	}

	protected ParsedName0 chunkUpLastNameString(String s) {
		ParsedName0 retVal = new ParsedName0();
		assert retVal.getLastName().equals("");
		assert retVal.getTitles().equals("");
		assert retVal.getMothersFirstName().equals("");

		if (!StringUtils.nonEmptyString(s)) {
			return retVal;
		}

		s = fixMc(s);
		String flipped = flipToks(s);
		ParsedName0 chunks = chunkUpNameString(flipped, false);
		retVal.setTitles(chunks.getTitles());

		// 2008-10-20 rphall
		// Compound names like VAN DER ZEE or WONG DE JESUS
		// need special handling. Iterate through the middle tokens,
		// placing last-name prefixes like DE, DER or VAN back with
		// the last name. Any thing left over in a compound name
		// is a maiden name. Note that compound names have
		// precedence over hyphenated names; e.g.
		//
		// WONG DE SMITH-JONES => MAIDEN: WONG, LN: DE SMITH-JONES
		// SMITH-JONES LEE-TAYLOR => MAIDEN: SMITH-JONES, LN: LEE-TAYLOR
		// DE JESUS VAN DER ZEE => MAIDEN: DE JESUS, LN: VAN DER ZEE
		//

		// If a name is compound, calculate maiden without regard for
		// hyphenation
		String tmpLast = chunks.getLastName();
		String tmpMaiden = "";
		StringTokenizer st = new StringTokenizer(chunks.getMiddleNames());
		boolean isCompound = st.hasMoreTokens();

		boolean isLastNameComponent = true; // last vs maiden
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			boolean isLastNamePrefix = getLastNamePrefixes().contains(token);
			if (isLastNameComponent && isLastNamePrefix) {
				tmpLast = concatWithSeparator(tmpLast, token, " ");
			} else {
				isLastNameComponent = false;
				tmpMaiden = concatWithSeparator(tmpMaiden, token, " ");
			}
		}

		// The tmp variables hold tokens in reverse order
		retVal.setLastName(flipToks(tmpLast));
		retVal.setPotentialMaidenName(flipToks(tmpMaiden));
		// potential maiden names flipped back into the correct order

		// If not compound, look for a hyphen in the last name.
		// If found separate names
		if (!isCompound) {
			int index =
				Math.max(chunks.getLastName().indexOf('-'), chunks
						.getLastName().indexOf('/'));
			if (index > 0) {
				retVal.setLastName(chunks.getLastName().substring(index + 1));
				retVal.setPotentialMaidenName(chunks.getLastName().substring(0,
						index));
				// the first part becomes a potential maiden name
			}
		}
		// END CHANGES 2008-10-20 rphall

		return retVal;
	}

	/**
	 * Chunk up a first name into three parts - a first name, one or more middle
	 * names, and one or more titles. We parse only on spaces including newlines
	 * and such. We don't do anything special with hyphens, they remain.
	 *
	 * @param str
	 * @param isAFirstName
	 *            true if this is a first name string, false for last name.
	 */
	protected ParsedName0 chunkUpNameString(String str, boolean isAFirstName) {
		ParsedName0 retVal = new ParsedName0();
		assert retVal.getFirstName().equals("");
		assert retVal.getMiddleNames().equals("");
		assert retVal.getTitles().equals("");
		assert retVal.getMothersFirstName().equals("");
		if (!StringUtils.nonEmptyString(str)) {
			return retVal;
		}

		if (isAFirstName) {
			if (getGenericFirstNames().contains(str)) {
				return retVal;
			}
			int lastSpace = str.lastIndexOf(' ');
			if (lastSpace > 0) {
				String s = str.substring(0, lastSpace).trim();
				if (getChildOfIndicators().contains(s)) {
					String tmp = str.substring(lastSpace + 1, str.length());
					StringTokenizer st = new StringTokenizer(tmp);
					while (st.hasMoreTokens()) {
						String token = st.nextToken();
						boolean isGenericFirstName =
							getGenericFirstNames().contains(token);
						if (!isGenericFirstName) {
							String s2 =
								concatWithSeparator(
										retVal.getMothersFirstName(), token,
										" ");
							if (s2 != null) {
								retVal.setMothersFirstName(retVal
										.getMothersFirstName() + s2);
							}
						}
					}
					return retVal;
				}
			}
		}

		StringTokenizer sTok = new StringTokenizer(str);
		int numToks = sTok.countTokens();
		if (numToks == 1) {
			str = str.trim();
			if (getNameTitles().contains(str)) {
				retVal.setTitles(str);
			} else if (isAFirstName || !getInvalidLastNames().contains(str)) {
				retVal.setFirstName(str);
			}
			return retVal;
		}

		for (int iTok = 0; iTok < numToks; iTok++) {
			String nameTok = sTok.nextToken();
			if (getNameTitles().contains(nameTok)) {
				String s =
					concatWithSeparator(retVal.getTitles(), nameTok, " ");
				retVal.setTitles(s);
			}
			// if we don't already have a "first name" this is it.
			else if (!StringUtils.nonEmptyString(retVal.getFirstName())) {
				retVal.setFirstName(nameTok);
			}
			// otherwise add it to the list of middle names.
			else {
				String s =
					concatWithSeparator(retVal.getMiddleNames(), nameTok, " ");
				retVal.setMiddleNames(s);
			}
		}
		return retVal;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection getGenericFirstNames() {
		assert genericFirstNames != null;
		return genericFirstNames;
	}

	public void setGenericFirstNames(Collection firstNames) {
		if (firstNames != null) {
			this.genericFirstNames.clear();
			this.genericFirstNames.addAll(firstNames);
		}
	}

	public Collection getChildOfIndicators() {
		assert childOfIndicators != null;
		return childOfIndicators;
	}

	public void setChildOfIndicators(Collection childIndicators) {
		if (childIndicators != null) {
			this.childOfIndicators.clear();
			this.childOfIndicators.addAll(childIndicators);
		}
	}

	public Collection getInvalidLastNames() {
		assert invalidLastNames != null;
		return invalidLastNames;
	}

	public void setInvalidLastNames(Collection lastNames) {
		if (lastNames != null) {
			this.invalidLastNames.clear();
			this.invalidLastNames.addAll(lastNames);
		}
	}

	public Collection getNameTitles() {
		assert nameTitles != null;
		return nameTitles;
	}

	public void setNameTitles(Collection titles) {
		if (titles != null) {
			this.nameTitles.clear();
			this.nameTitles.addAll(titles);
		}
	}

	public Collection getLastNamePrefixes() {
		assert lastNamePrefixes != null;
		return lastNamePrefixes;
	}

	public void setLastNamePrefixes(Collection prefixes) {
		if (prefixes != null) {
			this.lastNamePrefixes.clear();
			this.lastNamePrefixes.addAll(prefixes);
		}
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result =
			prime
					* result
					+ ((childOfIndicators == null) ? 0 : childOfIndicators
							.hashCode());
		result =
			prime
					* result
					+ ((genericFirstNames == null) ? 0 : genericFirstNames
							.hashCode());
		result =
			prime
					* result
					+ ((invalidLastNames == null) ? 0 : invalidLastNames
							.hashCode());
		result =
			prime
					* result
					+ ((lastNamePrefixes == null) ? 0 : lastNamePrefixes
							.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result =
			prime * result + ((nameTitles == null) ? 0 : nameTitles.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		NameParser0 other = (NameParser0) obj;
		if (childOfIndicators == null) {
			if (other.childOfIndicators != null) {
				return false;
			}
		} else if (!childOfIndicators.equals(other.childOfIndicators)) {
			return false;
		}
		if (genericFirstNames == null) {
			if (other.genericFirstNames != null) {
				return false;
			}
		} else if (!genericFirstNames.equals(other.genericFirstNames)) {
			return false;
		}
		if (invalidLastNames == null) {
			if (other.invalidLastNames != null) {
				return false;
			}
		} else if (!invalidLastNames.equals(other.invalidLastNames)) {
			return false;
		}
		if (lastNamePrefixes == null) {
			if (other.lastNamePrefixes != null) {
				return false;
			}
		} else if (!lastNamePrefixes.equals(other.lastNamePrefixes)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (nameTitles == null) {
			if (other.nameTitles != null) {
				return false;
			}
		} else if (!nameTitles.equals(other.nameTitles)) {
			return false;
		}
		return true;
	}

	public String toShortString(Collection c) {
		String retVal = null;
		if (c != null && c.size() <= 3) {
			retVal = c.toString();
		} else if (c != null) {
			assert c.size() > 3;
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			int count = 0;
			for (Iterator it = c.iterator(); it.hasNext();) {
				count++;
				if (count > 3) {
					break;
				}
				sb.append(it.next()).append(", ");
			}
			sb.append("...]");
			retVal = sb.toString();
		}
		return retVal;
	}

	public String toString() {
		return "NameParser0 [name=" + name + ", genericFirstNames="
				+ toShortString(genericFirstNames) + ", childOfIndicators="
				+ toShortString(childOfIndicators) + ", invalidLastNames="
				+ toShortString(invalidLastNames) + ", nameTitles="
				+ toShortString(nameTitles) + ", lastNamePrefixes="
				+ toShortString(lastNamePrefixes) + "]";
	}

}
