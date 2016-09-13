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
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

/**
 *
 * @author rphall
 * @version $Revision: 1.4.2.2 $ $Date: 2010/04/08 16:14:18 $
 */
public class BlockingCallArguments {

	private static final Logger logger =
		Logger.getLogger(BlockingCallArguments.class.getName());

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

	private Map<String, String> map = new HashMap<>();
	private final AtomicReference<String> sqlIdRef =
		new AtomicReference<>(null);

	private BlockingCallArguments() {
	}

	public String getBlockConfig() {
		return this.map.get(BLOCK_CONFIG);
	}

	public String getCondition1() {
		return this.map.get(CONDITION_1);
	}

	public String getCondition2() {
		return this.map.get(CONDITION_2);
	}

	public String getQuery() {
		return this.map.get(QUERY);
	}

	public String getQueryId() {
		String retVal = sqlIdRef.get();
		if (retVal == null) {
			String sql = getQuery();
			String sqlId = AppUtils.computeMd5Hash(sql);
			boolean alreadySet = sqlIdRef.compareAndSet(null, sqlId);
			retVal = sqlIdRef.get();
			if (alreadySet) {
				logger.warning("SqlId already set: " + retVal);
			}
		}
		assert retVal != null;
		assert retVal == AppUtils.computeMd5Hash(getQuery());
		return retVal;
	}

	public String getReadConfig() {
		return this.map.get(READ_CONFIG);
	}

	void logArguments(String tag) {
		LogUtil.logExtendedInfo(logger,
				tag + "Blocking configuration: '" + getBlockConfig() + "'");
		LogUtil.logExtendedInfo(logger, tag + "Query: '" + getQuery() + "'");
		LogUtil.logExtendedInfo(logger,
				tag + "Condition 1: '" + getCondition1() + "'");
		LogUtil.logExtendedInfo(logger,
				tag + "Condition 2: '" + getCondition2() + "'");
		LogUtil.logExtendedInfo(logger,
				tag + "Read configuration: '" + getReadConfig() + "'");
	}

}
