/*******************************************************************************
 * Copyright (c) 2003, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.wfst;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * An ambiguous parser implemented as a weighted finite state transducer.
 * @author christer (parser design and initial coding)
 * @author rphall (adaptation as an Eclipse plugin)
 */
public class WfstParser implements AmbiguousParser, Cloneable {

	private Filter filter, grammar;
	private Interpreter interpreter;

	public WfstParser(Filter filter, Filter grammar) {
		this.filter = filter;
		this.grammar = grammar;
		interpreter = new Interpreter(grammar);
	}
	
	@Override
	public Object clone() {
		Filter f = (Filter) this.filter.clone();
		Filter g = (Filter) this.grammar.clone();
		return new WfstParser(f,g);
	}

	/*
	 * (non-Javadoc)
	 * @see com.choicemaker.cm.matching.wfst.AmbiguousParser#parse(java.lang.String)
	 */
	@Override
	public boolean isWeighted() {
		// stubbed for now
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.choicemaker.cm.matching.wfst.AmbiguousParser#parse(java.lang.String)
	 */
	@Override
	public List<Map<String,String>> parse(String str) {
		if (str !=null ) {
			str = str.trim();
		}
		LinkedList<?> list0 = filter.filter(str);
		LinkedList<?> list = grammar.filter(list0);
		list = interpreter.beststrings(list);
		return interpreter.interpret(list);
	}

	/**
	 * @return a LinkedList of HashMap instances
	 */

	/**
	 * FOR CHOICEMAKER INTERNAL USE ONLY. Reads a *.tab
	 * file and constructs a WfstParser.
	 * FIXME: this method is an incomplete half of a custom serialization-
	 * deserialization method. 
	 */

	public static Filter loadFilter(InputStream stream) {
		Filter filter = null;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(stream));
			filter = Filter.readFilter(reader);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
					reader = null;
				} catch (Exception x) {
					// TODO better error handling
					x.printStackTrace();
				}
			}
		} // finally
		return filter;
	} //  loadFilter(InputStream)

	public static AmbiguousParser readWfstParser(
		InputStream filterStream,
		InputStream grammarStream)
		throws IOException {

		AmbiguousParser retVal = null;
		Filter filter = loadFilter(filterStream);
		Filter grammar = loadFilter(grammarStream);
		retVal = new WfstParser(filter, grammar);
		return retVal;
	} // readWfstParser(InputStream, InputStream)

} // WfstParser

