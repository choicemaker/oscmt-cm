/*
 * Copyright (c) 2001, 2009 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Management of command line arguments.
 *
 * @author Matthias Zenger
 *
 * @see <a
 *      href="https://commons.apache.org/proper/commons-cli/index.html">Apache
 *      Commons CLI</a>
 * @see <a href="http://args4j.kohsuke.org/index.html">Args4j</a>
 * @see <a href="http://jcommander.org/">JCommander</a>
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class Arguments {

	public static final String DEFAULT = "$default$";
	public static final int STOP_PROCESSING = 0;
	public static final int SKIP_UNKNOWN = 1;
	public static final int ERROR = 2;

	private HashMap arguments = new HashMap();
	private List files = new ArrayList();
	private int unknownOptionsHandling;

	public Arguments() {
		this(STOP_PROCESSING);
	}

	public Arguments(int unknownOptionsHandling) {
		this.unknownOptionsHandling = unknownOptionsHandling;
	}

	public Arguments(Arguments a) {
		if (a == null) {
			throw new IllegalArgumentException("null arguments");
		}

		this.arguments = new HashMap();
		this.arguments.putAll(a.arguments);

		this.files = new ArrayList();
		this.files.addAll(a.files);

		this.unknownOptionsHandling = a.unknownOptionsHandling;
	}

	/**
	 * Define a new option; i.e. an argument that is either present or not
	 * 
	 * @param option
	 *            a non-null, non-blank String
	 */
	public void addOption(String option) {
		arguments.put(option, Boolean.FALSE);
	}

	/**
	 * Define a new argument consisting of an argument name and a default value
	 * 
	 * @param option
	 *            a non-null, non-blank String
	 * @param defaultVal
	 *            may be null or blank
	 */
	public void addArgument(String option, String defaultVal) {
		arguments.put(option, defaultVal);
	}

	public void addArgument(String option, int defaultVal) {
		arguments.put(option, Integer.toString(defaultVal));
	}

	public void addArgument(String option) {
		arguments.put(option, DEFAULT);
	}

	/**
	 * Enter arguments from the command line 'args'
   * @param args may be null
   * @return index of an illegal
	 * argument, or -1 for correct command lines
	 */
	public int enter(String[] args) {
		if (args == null)
			return -1;
		int i = 0;
		while (i < args.length) {
			if (args[i].startsWith("-")) {
				Object val = arguments.get(args[i]);
				if (val == null)
					if (unknownOptionsHandling == STOP_PROCESSING) {
						return -1;
					} else if (unknownOptionsHandling == SKIP_UNKNOWN) {
						++i;
						if (i < args.length && !args[i].startsWith("-")) {
							++i;
						}
					} else { // ERROR
						return i;
					}
				else if (val instanceof Boolean)
					arguments.put(args[i++], Boolean.TRUE);
				else if (i == (args.length - 1))
					return i;
				else
					arguments.put(args[i++], args[i++]);
			} else {
				files.add(args[i++]);
			}
		}
		return -1;
	}

	/**
	Check if option was set
@param option a non-null String
@return true if the option was set; false otherwise
*/
	public boolean optionSet(String option) {
		return ((Boolean) arguments.get(option)).booleanValue();
	}

	/** Check the argument value 
@param option a non-null String
@return the value of the argument or null if the argument was not set
*/
	public String argumentVal(String option) {
		Object o = arguments.get(option);
		return (String) ((o == DEFAULT) ? null : o);
	}

	/** @return all files */
	public String[] files() {
		return (String[]) files.toArray(new String[files.size()]);
	}

	@Override
	public String toString() {
		return "Arguments [arguments=" + arguments + ", files=" + files
				+ ", unknownOptionsHandling=" + unknownOptionsHandling + "]";
	}
}
