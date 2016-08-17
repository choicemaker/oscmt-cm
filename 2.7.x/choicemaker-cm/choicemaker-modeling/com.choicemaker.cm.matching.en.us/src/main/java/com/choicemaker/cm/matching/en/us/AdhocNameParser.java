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
import java.util.logging.Logger;

import com.choicemaker.util.Precondition;
import com.choicemaker.util.StringUtils;

/**
 * A simple name parser that deals with compound names. This class produces
 * {@link AdhocName} instances.
 * <ul>
 * <li>The name parser breaks multiple names reported in a single field</li>
 * <li>Filters out titles and suffixes, such as MR and JR</li>
 * <li>Removes invalid values such as N/A</li>
 * <ul>
 * <strong>NOTE:</strong> This class is unrelated to the NameParser class, which
 * is based on a context-free grammar (CFG).
 *
 * @see com.choicemaker.cm.matching.cfg.Parsers
 *
 * @author S. Yoakum-Stover
 * @author rphall (refactored for CM 2.7)
 */
public class AdhocNameParser {

	private static final Logger logger = Logger.getLogger(AdhocNameParser.class
			.getName());

	public static final String DEFAULT_ADHOC_NAME_PARSER = "en.us.defaultAdhocNameParser";

	public static AdhocNameParser lookupAdhocNameParser(String name) {
		Precondition.assertNonEmptyString("null or blank parser name", name);
		AdhocNameParser retVal = AdhocNameParsers.get(name);
		if (retVal == null) {
			retVal = new AdhocNameParser();
			retVal.setName(name);
			AdhocNameParsers.put(name, retVal);
			String msg = "No-data parser registered under '"
					+ name + "'; using do-nothing parser: "
					+ retVal.toString();
			logger.warning(msg);
		}
		return retVal;
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
				Set<String> seen = new HashSet<>();
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

	/** The name of this parser */
	private String name;

	/** Generic first names */
	private Collection<String> gfn = new HashSet<>();

	/** Child-of indicators */
	private Collection<String> coi = new HashSet<>();

	/** Invalid last names */
	private Collection<String> iln = new HashSet<>();

	/** Name titles */
	private Collection<String> nt = new HashSet<>();

	/** Last name prefixes */
	private Collection<String> lnp = new HashSet<>();

	public AdhocName parse(String f, String m, String l) {
		String first = StringUtils.nonEmptyString(f) ? StringUtils
				.removePunctuation(f) : "";
		String middle = StringUtils.nonEmptyString(m) ? StringUtils
				.removePunctuation(m) : "";
		String last = StringUtils.nonEmptyString(l) ? StringUtils
				.removePunctuation(l) : "";
		// BUGFIX rphall 2008-07-13
		// StringUtils.removePunctuation sometimes does not remove trailing
		// space
		first = first.trim();
		middle = middle.trim();
		last = last.trim();
		// END BUGFIX
		AdhocName retVal = new AdhocName();
		AdhocName test = chunkUpNamesStrings(first, middle, last);
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

	protected AdhocName chunkUpNamesStrings(String first, String middle,
			String last) {

		AdhocName retVal = new AdhocName();

		// chunk up the first name
		AdhocName firstNames = chunkUpNameString(first, true);

		// place the first name chunks
		retVal.setFirstName(firstNames.getFirstName());
		retVal.setMiddleNames(firstNames.getMiddleNames());
		retVal.setMothersFirstName(firstNames.getMothersFirstName());
		String tmpTitles = firstNames.getTitles();

		// place the middle names
		if (StringUtils.nonEmptyString(middle)) {
			AdhocName tmpMiddles = chunkUpNameString(middle, false);
			if (StringUtils.nonEmptyString(tmpMiddles.getFirstName())) {
				String s = concatWithSeparator(retVal.getMiddleNames(),
						tmpMiddles.getFirstName(), " ");
				retVal.setMiddleNames(s);
			}
			if (StringUtils.nonEmptyString(tmpMiddles.getMiddleNames())) {
				String s = concatWithSeparator(retVal.getMiddleNames(),
						tmpMiddles.getMiddleNames(), " ");
				retVal.setMiddleNames(s);
			}
			tmpTitles = concatWithSeparator(tmpTitles, tmpMiddles.getTitles(),
					" ");
		}

		// chunk up the last name
		AdhocName lastNames = chunkUpLastNameString(last);

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

	protected AdhocName chunkUpLastNameString(String s) {
		AdhocName retVal = new AdhocName();
		assert retVal.getLastName().equals("");
		assert retVal.getTitles().equals("");
		assert retVal.getMothersFirstName().equals("");

		if (!StringUtils.nonEmptyString(s)) {
			return retVal;
		}

		s = fixMc(s);
		String flipped = flipToks(s);
		AdhocName chunks = chunkUpNameString(flipped, false);
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
		String tmpLast = chunks.getFirstName();
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
			int index = Math.max(chunks.getFirstName().indexOf('-'), chunks
					.getFirstName().indexOf('/'));
			if (index > 0) {
				retVal.setLastName(chunks.getFirstName().substring(index + 1));
				retVal.setPotentialMaidenName(chunks.getFirstName().substring(
						0, index));
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
	protected AdhocName chunkUpNameString(String str, boolean isAFirstName) {
		AdhocName retVal = new AdhocName();
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
						boolean isGenericFirstName = getGenericFirstNames()
								.contains(token);
						if (!isGenericFirstName) {
							String s2 = concatWithSeparator(
									retVal.getMothersFirstName(), token, " ");
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
				String s = concatWithSeparator(retVal.getTitles(), nameTok, " ");
				retVal.setTitles(s);
			}
			// if we don't already have a "first name" this is it.
			else if (!StringUtils.nonEmptyString(retVal.getFirstName())) {
				retVal.setFirstName(nameTok);
			}
			// otherwise add it to the list of middle names.
			else {
				String s = concatWithSeparator(retVal.getMiddleNames(),
						nameTok, " ");
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

	public Collection<String> getGenericFirstNames() {
		assert gfn != null;
		return gfn;
	}

	public void setGenericFirstNames(Collection<String> firstNames) {
		if (firstNames != null) {
			this.gfn.clear();
			this.gfn.addAll(firstNames);
		}
	}

	public Collection<String> getChildOfIndicators() {
		assert coi != null;
		return coi;
	}

	public void setChildOfIndicators(Collection<String> childIndicators) {
		if (childIndicators != null) {
			this.coi.clear();
			this.coi.addAll(childIndicators);
		}
	}

	public Collection<String> getInvalidLastNames() {
		assert iln != null;
		return iln;
	}

	public void setInvalidLastNames(Collection<String> lastNames) {
		if (lastNames != null) {
			this.iln.clear();
			this.iln.addAll(lastNames);
		}
	}

	public Collection<String> getNameTitles() {
		assert nt != null;
		return nt;
	}

	public void setNameTitles(Collection<String> titles) {
		if (titles != null) {
			this.nt.clear();
			this.nt.addAll(titles);
		}
	}

	public Collection<String> getLastNamePrefixes() {
		assert lnp != null;
		return lnp;
	}

	public void setLastNamePrefixes(Collection<String> prefixes) {
		if (prefixes != null) {
			this.lnp.clear();
			this.lnp.addAll(prefixes);
		}
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((coi == null) ? 0 : coi.hashCode());
		result = prime * result + ((gfn == null) ? 0 : gfn.hashCode());
		result = prime * result + ((iln == null) ? 0 : iln.hashCode());
		result = prime * result + ((lnp == null) ? 0 : lnp.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((nt == null) ? 0 : nt.hashCode());
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
		AdhocNameParser other = (AdhocNameParser) obj;
		if (coi == null) {
			if (other.coi != null) {
				return false;
			}
		} else if (!coi.equals(other.coi)) {
			return false;
		}
		if (gfn == null) {
			if (other.gfn != null) {
				return false;
			}
		} else if (!gfn.equals(other.gfn)) {
			return false;
		}
		if (iln == null) {
			if (other.iln != null) {
				return false;
			}
		} else if (!iln.equals(other.iln)) {
			return false;
		}
		if (lnp == null) {
			if (other.lnp != null) {
				return false;
			}
		} else if (!lnp.equals(other.lnp)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (nt == null) {
			if (other.nt != null) {
				return false;
			}
		} else if (!nt.equals(other.nt)) {
			return false;
		}
		return true;
	}

	public String toShortString(Collection<String> c) {
		String retVal = null;
		if (c != null && c.size() <= 3) {
			retVal = c.toString();
		} else if (c != null) {
			assert c.size() > 3;
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			int count = 0;
			for (Iterator<String> it = c.iterator(); it.hasNext();) {
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
		return "AdhocNameParser [name=" + name + ", genericFirstNames="
				+ toShortString(gfn) + ", childOfIndicators="
				+ toShortString(coi) + ", invalidLastNames="
				+ toShortString(iln) + ", nameTitles=" + toShortString(nt)
				+ ", lastNamePrefixes=" + toShortString(lnp) + "]";
	}

}
