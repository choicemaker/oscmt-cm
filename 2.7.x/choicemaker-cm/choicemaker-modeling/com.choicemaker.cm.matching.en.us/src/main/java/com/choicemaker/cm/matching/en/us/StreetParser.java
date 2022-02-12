/*******************************************************************************
 * Copyright (c) 2015, 2016 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.en.us;

import com.choicemaker.util.StringUtils;

/**
 * A very limited parser with just one public method,
 * <code>aptNormaliize(String)</code>, that normalizes apartment numbers.
 * 
 * @author S. Yoakum-Stover (initial version)
 * @author rphall (removed unused methods)
 */
public class StreetParser {

	public static String aptNormalize(String apt) {
		String r = StringUtils.removeNonDigitsLetters(apt).intern();
		if (r == "BAS" || r == "BSMT" || r == "BMT" || r == "BST") {
			return "BSMT";
		}
		if (r == "PVTH" || r == "PH" || r == "PVT") {
			return "PH";
		}
		if (r.startsWith("APT")) {
			r = r.substring(3, r.length());
		} else if (r.startsWith("NO")) {
			r = r.substring(2, r.length());
		} else if (r.endsWith("FL")) {
			r = r.substring(0, r.length() - 2);
		} else if (r.endsWith("FLR")) {
			r = r.substring(0, r.length() - 3);
		} else if (r.endsWith("FLOOR")) {
			r = r.substring(0, r.length() - 5);
		}
		if (r.endsWith("ST") || r.endsWith("ND") || r.endsWith("RD")
				|| r.endsWith("TH")) {
			r = r.substring(0, r.length() - 2);
		}
		final int len = r.length();
		if (len > 1) {
			if (r.charAt(0) == '0') {
				r = numbersBeforeLetters(r);
			} else {
				int i = 0;
				while (i < len && Character.isDigit(r.charAt(i))) {
					++i;
				}
				while (i < len && Character.isLetter(r.charAt(i))) {
					++i;
				}
				if (i != len) {
					r = numbersBeforeLetters(r);
				}
			}
		}
		return r;
	}

	private static String numbersBeforeLetters(String a) {
		final int len = a.length();
		StringBuffer b = new StringBuffer(len);
		boolean leading = true;
		for (int i = 0; i < len; ++i) {
			char c = a.charAt(i);
			if (Character.isDigit(c)) {
				if (!leading || c != '0') {
					b.append(c);
					leading = false;
				}
			}
		}
		for (int i = 0; i < len; ++i) {
			char c = a.charAt(i);
			if (Character.isLetter(c)) {
				b.append(c);
			}
		}
		return b.toString();
	}
}
