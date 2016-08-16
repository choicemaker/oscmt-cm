/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.en.us;


/**
 * A class that represents a name <strong> <em>parsed</em></strong> by the
 * {@link NameParser0} class.
 *
 * <strong>NOTE:</strong> This class is unrelated to the NameParser class, which
 * is based on a context-free grammar (CFG).
 *
 * @see com.choicemaker.cm.matching.cfg.Parsers
 *
 * @author S. Yoakum-Stover
 * @author rphall (refactored for CM 2.7)
 */
public class ParsedName0 {

	public static final String EMPTY = "".intern();

	private String firstName = EMPTY;
	private String middleNames = EMPTY;
	private String lastName = EMPTY;
	private String titles = EMPTY;
	private String potentialMaidenName = EMPTY;
	private String mothersFirstName = EMPTY;

	public ParsedName0() {
		this(EMPTY,EMPTY,EMPTY);
	}

	public ParsedName0(String f, String m, String l) {
		setFirstName(f);
		setMiddleNames(m);
		setLastName(l);
	}

	public String getFirstName() {
		return firstName;
	}

	void setFirstName(String firstName) {
		if (firstName != null) {
			this.firstName = firstName.intern();
		} else {
			this.firstName = EMPTY;
		}
	}

	public String getMiddleNames() {
		return middleNames;
	}

	void setMiddleNames(String middleNames) {
		if (middleNames != null) {
			this.middleNames = middleNames.intern();
		} else {
			this.middleNames = EMPTY;
		}
	}

	public String getLastName() {
		return lastName;
	}

	void setLastName(String lastName) {
		if (lastName != null) {
			this.lastName = lastName.intern();
		} else {
			this.lastName = EMPTY;
		}
	}

	public String getTitles() {
		return titles;
	}

	void setTitles(String titles) {
		if (titles != null) {
			this.titles = titles.intern();
		} else {
			this.titles = EMPTY;
		}
	}

	public String getPotentialMaidenName() {
		return potentialMaidenName;
	}

	void setPotentialMaidenName(String potentialMaidenName) {
		if (potentialMaidenName != null) {
			this.potentialMaidenName = potentialMaidenName.intern();
		} else {
			this.potentialMaidenName = EMPTY;
		}
	}

	public String getMothersFirstName() {
		return mothersFirstName;
	}

	void setMothersFirstName(String mothersFirstName) {
		if (mothersFirstName != null) {
			this.mothersFirstName = mothersFirstName.intern();
		} else {
			this.mothersFirstName = EMPTY;
		}
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result =
			prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result =
			prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		result =
			prime * result
					+ ((middleNames == null) ? 0 : middleNames.hashCode());
		result =
			prime
					* result
					+ ((mothersFirstName == null) ? 0 : mothersFirstName
							.hashCode());
		result =
			prime
					* result
					+ ((potentialMaidenName == null) ? 0 : potentialMaidenName
							.hashCode());
		result = prime * result + ((titles == null) ? 0 : titles.hashCode());
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
		ParsedName0 other = (ParsedName0) obj;
		if (firstName == null) {
			if (other.firstName != null) {
				return false;
			}
		} else if (!firstName.equals(other.firstName)) {
			return false;
		}
		if (lastName == null) {
			if (other.lastName != null) {
				return false;
			}
		} else if (!lastName.equals(other.lastName)) {
			return false;
		}
		if (middleNames == null) {
			if (other.middleNames != null) {
				return false;
			}
		} else if (!middleNames.equals(other.middleNames)) {
			return false;
		}
		if (mothersFirstName == null) {
			if (other.mothersFirstName != null) {
				return false;
			}
		} else if (!mothersFirstName.equals(other.mothersFirstName)) {
			return false;
		}
		if (potentialMaidenName == null) {
			if (other.potentialMaidenName != null) {
				return false;
			}
		} else if (!potentialMaidenName.equals(other.potentialMaidenName)) {
			return false;
		}
		if (titles == null) {
			if (other.titles != null) {
				return false;
			}
		} else if (!titles.equals(other.titles)) {
			return false;
		}
		return true;
	}

	public String toString() {
		return "ParsedName0 [firstName=" + firstName + ", middleNames="
				+ middleNames + ", lastName=" + lastName + ", titles=" + titles
				+ ", potentialMaidenName=" + potentialMaidenName
				+ ", mothersFirstName=" + mothersFirstName + "]";
	}

}
