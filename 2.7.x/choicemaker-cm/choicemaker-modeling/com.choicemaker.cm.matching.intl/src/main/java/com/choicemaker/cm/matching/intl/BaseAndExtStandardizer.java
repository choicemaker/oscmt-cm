/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.intl;

import com.choicemaker.cm.matching.cfg.ParseTreeNode;
import com.choicemaker.cm.matching.cfg.ParsedData;
import com.choicemaker.cm.matching.cfg.standardizer.DefaultStandardizer;

/**
 * @author    Adam Winkel
 */
public class BaseAndExtStandardizer extends DefaultStandardizer {

	private static final int DEFAULT_BASE_LENGTH = 10;

	protected String baseKey;
	protected String extKey;
	protected int baseLen;

	public BaseAndExtStandardizer(String baseKey, String extKey) {
		this(baseKey, extKey, DEFAULT_BASE_LENGTH);
	}

	public BaseAndExtStandardizer(String baseKey, String extKey, int baseLen) {
		super("");
		this.baseKey = baseKey;
		this.extKey = extKey;
		this.baseLen = baseLen;
	}

	public void standardize(ParseTreeNode node, ParsedData holder) {
		String s = joinTokens(node);
		
		int len = s.length();
		int bLen = Math.min(len, baseLen);
		String base = s.substring(0, bLen);
		String ext = s.substring(bLen, len);
		
		holder.put(baseKey, base);
		holder.put(extKey, ext);
	}

}
