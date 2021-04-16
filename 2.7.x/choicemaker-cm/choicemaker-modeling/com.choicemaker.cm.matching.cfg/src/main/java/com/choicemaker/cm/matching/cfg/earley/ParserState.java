/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.cfg.earley;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.choicemaker.cm.matching.cfg.ParseTreeNode;
import com.choicemaker.cm.matching.cfg.Rule;
import com.choicemaker.cm.matching.cfg.Symbol;
import com.choicemaker.cm.matching.cfg.Token;

/**
 * A State in a ParserChart.
 *
 * @author   Adam Winkel
 * @see ParserChart
 * @see EarleyParserChart
 */
public class ParserState implements Comparable<Object> {

	protected Rule rule;
	protected int dotPosition;
	protected int start;
	protected int end;

	protected boolean isComplete;
	protected boolean isLeaf;

	protected int numParseTrees;

	protected List<BackPointer> backPointers = new ArrayList<>();
	protected int numBackPointers = 0;
	protected BackPointer bestBackPointer;

	public ParserState(Rule rule, int dotPosition, int start, int end) {
		this.rule = rule;
		this.dotPosition = dotPosition;
		this.start = start;
		this.end = end;

		this.isLeaf = (rule.getRhsSize() == 1 && rule.getRhsSymbol(0) instanceof Token);
		this.isComplete = (dotPosition == rule.getRhsSize());

		this.numParseTrees = 1;

		if (dotPosition > rule.getRhsSize()) {
			throw new IllegalArgumentException();
		}
	}

	public Rule getRule() {
		return rule;
	}

	public int getDotPosition() {
		return dotPosition;
	}

	public int getStartIndex() {
		return start;
	}

	public int getEndIndex() {
		return end;
	}

	/**
	 * Returns true iff all the symbols in the RHS have been accounted for, i.e.
	 * the dot is after the last symbol.
	 */
	public boolean isComplete() {
		return isComplete;
	}

	public boolean isLeafState() {
		return isLeaf;
	}

	/**
	 * Returns the first Symbol after the dot position.
	 */
	public Symbol getNextSymbol() {
		return rule.getRhsSymbol(dotPosition);
	}

	//
	// Everything below here is bookkeeping for building parse trees
	// from the completed chart.
	//

	public void addBackPointer(ParserState oldState, ParserState nextSubtree) {
		BackPointer bp = new BackPointer(oldState, nextSubtree);
		backPointers.add(bp);

		if (++numBackPointers == 1) {
			numParseTrees = 0;
		}

		numParseTrees += bp.getNumParseTrees();

		if (bestBackPointer == null ||
			bp.getBestProbability() > bestBackPointer.getBestProbability()) {
			bestBackPointer = bp;
		}
	}


	/**
	 * @return the number of different parse trees starting from
	 * this node.
	 */
	public int getNumParseTrees() {
		return numParseTrees;
	}

	/**
	 * @return the probability of the highest-probability parse tree for
	 * this node.
	 */
	public double getBestProbability() {
		if (bestBackPointer == null)
			return rule.getProbability();  // base case...

		return bestBackPointer.getBestProbability() * rule.getProbability();
	}

	protected double getBestSubtreeProbability() {
		if (bestBackPointer == null) {
			return 1;
		}

		return bestBackPointer.getBestProbability();
	}

	public ParseTreeNode getBestParseTree() {
		if (isLeaf) {
			return new ParseTreeNode(rule);
		} else {
			LinkedList<ParseTreeNode> list = new LinkedList<>();
			getBestSubtrees(list);
			return new ParseTreeNode(rule, list);
		}
	}

	protected void getBestSubtrees(LinkedList<ParseTreeNode> list) {
		if (bestBackPointer != null) {
			bestBackPointer.getBestSubtrees(list);
		}
	}

	public double getProbability(int index) {
		if (index >= numParseTrees) {
			throw new IllegalArgumentException(index + " > " + numParseTrees);
		}

		if (isLeaf) {
			return rule.getProbability();
		}

		for (int i = 0; i < numBackPointers; i++) {
			BackPointer bp = backPointers.get(i);
			int numForBackPointer = bp.getNumParseTrees();

			if (numForBackPointer > index)
				return bp.getProbability(index);

			index -= numForBackPointer;
		}

		throw new IllegalStateException("Internal Code Error.");
	}

	public ParseTreeNode getParseTree(int index) {
		if (index >= getNumParseTrees())
			throw new IllegalArgumentException(index + " > " + numParseTrees);

		if (isLeaf) {
			return new ParseTreeNode(rule);
		}

		for (int i = 0; i < numBackPointers; i++) {
			BackPointer bp = backPointers.get(i);
			int numForBackPointer = bp.getNumParseTrees();

			if (numForBackPointer > index) {
				LinkedList<ParseTreeNode> list = new LinkedList<>();
				bp.getSubtrees(index, list);
				return new ParseTreeNode(rule, list);
			}

			index -= numForBackPointer;
		}

		throw new IllegalStateException("Internal Code Error.");
	}

	protected void getSubtrees(int index, LinkedList<ParseTreeNode> list) {
		if (index >= getNumParseTrees())
			throw new IllegalArgumentException(index + " > " + numParseTrees);

		if (isLeaf) {
			list.add(new ParseTreeNode(rule));
			return;
		}

		for (int i = 0; i < numBackPointers; i++) {
			BackPointer bp = backPointers.get(i);
			int numForBackPointer = bp.getNumParseTrees();

			if (numForBackPointer > index) {
				bp.getSubtrees(index, list);
				return;
			}

			index -= numForBackPointer;
		}
	}

	/**
	 * Implementation of the Comparator interface.
	 */
	@Override
	public int compareTo(Object obj) {
		return toString().compareTo(obj.toString());
	}

	@Override
	public String toString() {
		StringBuffer sBuff = new StringBuffer();

		sBuff.append(rule.getLhs().toString());
		sBuff.append(" -->");

		for (int i = 0; i < dotPosition; i++) {
			sBuff.append(' ');
			sBuff.append(rule.getRhsSymbol(i).toString());
		}

		sBuff.append(" *");

		for (int i = dotPosition; i < rule.getRhsSize(); i++) {
			sBuff.append(' ');
			sBuff.append(rule.getRhsSymbol(i).toString());
		}

		sBuff.append("  [");
		sBuff.append(start);
		sBuff.append(',');
		sBuff.append(end);
		sBuff.append(']');

		return sBuff.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ParserState)) {
			return false;
		}

		ParserState s = (ParserState) obj;

		return end == s.end && start == s.start &&
			   dotPosition == s.dotPosition && rule.equals(s.rule);
	}

	@Override
	public int hashCode() {
		return rule.hashCode() + dotPosition * 100 +  start * 10 + end;
	}

	static class BackPointer {

		ParserState oldState;
		ParserState nextSubtree;

		int numForOldState;
		int numForNextSubtree;
		int numParseTrees;
		double bestProbability;

		public BackPointer(ParserState oldState, ParserState nextSubtree) {
			this.oldState = oldState;
			this.nextSubtree = nextSubtree;

			this.numForOldState = oldState.getNumParseTrees();
			this.numForNextSubtree = nextSubtree.getNumParseTrees();
			this.numParseTrees = numForOldState * numForNextSubtree;

			this.bestProbability =
				oldState.getBestSubtreeProbability() * nextSubtree.getBestProbability();
		}

		public int getNumParseTrees() {
			return numParseTrees;
		}

		public double getBestProbability() {
			return bestProbability;
		}

		public void getBestSubtrees(LinkedList<ParseTreeNode> list) {
			list.addFirst(nextSubtree.getBestParseTree());
			oldState.getBestSubtrees(list);
		}

		public double getProbability(int index) {
			int indexForNextSubtree = index % numForNextSubtree;
			return nextSubtree.getProbability(indexForNextSubtree) * oldState.getProbability(indexForNextSubtree);
		}

		public void getSubtrees(int index, LinkedList<ParseTreeNode> list) {
			int indexForNextSubtree = index % numForNextSubtree;
			int indexForOldState = (index - indexForNextSubtree) / numForNextSubtree;

			list.addFirst(nextSubtree.getParseTree(indexForNextSubtree));
			oldState.getSubtrees(indexForOldState, list);
		}

	}

}
