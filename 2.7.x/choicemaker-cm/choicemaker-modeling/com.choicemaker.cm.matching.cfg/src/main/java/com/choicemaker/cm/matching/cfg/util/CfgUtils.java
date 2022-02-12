/*******************************************************************************
 * Copyright (c) 2003, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.cfg.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.choicemaker.cm.matching.cfg.ParseTreeNode;
import com.choicemaker.cm.matching.cfg.ParsedData;

/**
 * .
 *
 * @author   Adam Winkel
 */
public final class CfgUtils {

	/**
	 * Sorts parse trees (the root nodes of parse trees, to be precise)
	 * in decreasing order by probability.
	 * 
	 * @param parseTrees a list of ParseTreeNodes
	 * @throws ClassCastException if an element of the list is not an
	 * instance of ParseTreeNode
	 */
	public static void sortParseTrees(List<ParseTreeNode> parseTrees) {
		Collections.sort(parseTrees, REVERSE_PARSE_TREE_COMPARATOR);
	}
	
	public static void sortParseTrees(ParseTreeNode[] parseTrees) {
		Arrays.sort(parseTrees, REVERSE_PARSE_TREE_COMPARATOR);	
	}
	
	/**
	 * Sorts ParsedAddresses in decreasing order by probability.
	 * 
	 * @param parsedDataHolders the ParsedDataHolders to sort
	 * @throws ClassCastException if an element of the list is not an
	 * instance of ParsedDataHolder
	 */
	public static void sortParsedDataHolders(List<ParsedData> parsedDataHolders) {
		Collections.sort(parsedDataHolders, REVERSE_PARSED_DATA_HOLDER_COMPARATOR);	
	}

	public static void sortParsedDataHolders(ParsedData[] parsedData) {
		Arrays.sort(parsedData, REVERSE_PARSED_DATA_HOLDER_COMPARATOR);
	}

	/**
	 * Comparator to sort ParseTreeNodes in decreasing order by probability.
	 */
	private static class ReverseParseTreeNodeComparator
			implements Comparator<ParseTreeNode> {
		@Override
		public int compare(ParseTreeNode obj1, ParseTreeNode obj2) {
			double p1 = obj1.getProbability();
			double p2 = obj2.getProbability();
			return p1 < p2 ? 1 : p1 > p2 ? -1 : 0;
		}
	}

	/**
	 * Comparator to sort ParsedAddresses in decreasing order by probability.
	 */
	private static class ReverseParsedDataHolderComparator
			implements Comparator<ParsedData> {
		@Override
		public int compare(ParsedData obj1, ParsedData obj2) {
			double p1 = obj1.getProbability();
			double p2 = obj2.getProbability();
			return p1 < p2 ? 1 : p1 > p2 ? -1 : 0;
		}
	}

	private static final Comparator<ParseTreeNode> REVERSE_PARSE_TREE_COMPARATOR =
		new ReverseParseTreeNodeComparator();

	private static final Comparator<ParsedData> REVERSE_PARSED_DATA_HOLDER_COMPARATOR =
		new ReverseParsedDataHolderComparator();

}
