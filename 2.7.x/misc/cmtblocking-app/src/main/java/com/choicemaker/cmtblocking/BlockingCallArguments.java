/*
 * @(#)$RCSfile: BlockingCallArguments.java,v $        $Revision: 1.4.2.2 $ $Date: 2010/04/08 16:14:18 $
 *
 * Copyright (c) 2001 ChoiceMaker Technologies, Inc.
 * 48 Wall Street, 11th Floor, New York, NY 10005
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * ChoiceMaker Technologies Inc. ("Confidential Information").
 */
package com.choicemaker.cmtblocking;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 *
 * @author rphall
 * @version $Revision: 1.4.2.2 $ $Date: 2010/04/08 16:14:18 $
 */
public class BlockingCallArguments {

	private Map<String, String> map = new HashMap<>();

	public static final String SEPARATOR = "|";

	static final String BLOCK_CONFIG = "blockConfig";
	static final String QUERY = "query";
	static final String CONDITION_1 = "condition1";
	static final String CONDITION_2 = "condition2";
	static final String READ_CONFIG = "readConfig";

	private static final String[] _fieldNames = new String[] {
			BLOCK_CONFIG, QUERY, CONDITION_1, CONDITION_2, READ_CONFIG };

	static final String[] fieldNames() {
		return Arrays.copyOf(_fieldNames, _fieldNames.length);
	}

	public String getBlockConfig() {
		return (String) this.map.get(BLOCK_CONFIG);
	}

	public String getQuery() {
		return (String) this.map.get(QUERY);
	}

	public String getCondition1() {
		return (String) this.map.get(CONDITION_1);
	}

	public String getCondition2() {
		return (String) this.map.get(CONDITION_2);
	}

	public String getReadConfig() {
		return (String) this.map.get(READ_CONFIG);
	}

	/**
	 * Expects a line of 5 fields, separated by "|"
	 * 
	 * @param line
	 * @return a new set of blocking call arguments
	 */
	public static BlockingCallArguments parseScriptLine(String line) {
		if (line == null || line.trim().length() == 0) {
			throw new IllegalArgumentException("null or blank line");
		}

		BlockingCallArguments retVal = new BlockingCallArguments();
		int count = 0;
		StringTokenizer st = new StringTokenizer(line, SEPARATOR);
		while (st.hasMoreTokens()) {
			String value = st.nextToken();
			if (value != null && value.trim().length() == 0) {
				value = null;
			}
			if (count < fieldNames().length) {
				retVal.map.put(fieldNames()[count], value);
				++count;
			} else if (count >= fieldNames().length) {
				throw new IllegalArgumentException(
						"too many fields in '" + line + "'");
			}
		}
		if (count < fieldNames().length) {
			throw new IllegalArgumentException(
					"'" + line + "' missing '" + fieldNames()[count] + "'");
		}

		return retVal;
	}

	private BlockingCallArguments() {
	}

	void logInfo() {
		logInfo("Blocking configuration: '" + getBlockConfig() + "'");
		logInfo("Query: '" + getQuery() + "'");
		logInfo("Condition 1: '" + getCondition1() + "'");
		logInfo("Condition 2: '" + getCondition2() + "'");
		logInfo("Read configuration: '" + getReadConfig() + "'");
	}

	private void logInfo(String msg) {
		LogUtil.logExtendedInfo("BlockingCallArguments", msg);
	}

}
