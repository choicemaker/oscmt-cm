/*******************************************************************************
 * Copyright (c) 2003, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.cfg.earley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.choicemaker.cm.matching.cfg.ContextFreeGrammar;
import com.choicemaker.cm.matching.cfg.ParseTreeNode;
import com.choicemaker.cm.matching.cfg.Rule;
import com.choicemaker.cm.matching.cfg.Token;
import com.choicemaker.cm.matching.cfg.Variable;
import com.choicemaker.util.Precondition;

/**
 * (Hopefully) useful common functionality to be used by any CFG
 * or PCFG parsing algorithm.
 *
 * Currently only subclassed by EarleyParserChart.
 *
 * @author   Adam Winkel
 */
public class ParserChart {

	protected List<Token> tokens;
	protected ContextFreeGrammar grammar;

	/** List of buckets */
	private ParserChartBucket[] buckets;
	protected int size;
			
	protected Rule fakeRule;
	private ParserState tempState;
							
	/**
	 * Create a new ParserChart based on the specified token set.
	 * The size of the parser chart will be <code>tokens.size() + 1</code>
	 */
	public ParserChart(List<Token> tokens, ContextFreeGrammar grammar) {
		Precondition.assertNonNullArgument("null token list", tokens);
		Precondition.assertNonNullArgument("null grammar", grammar);
		this.tokens = tokens;
		this.grammar = grammar;
		
		this.size = tokens.size() + 1;
		buckets = new ParserChartBucket[size];
		for (int i = 0; i < size; i++) {
			buckets[i] = new ParserChartBucket();
		}
		
		fakeRule = new Rule(new Variable("Gamma"), grammar.getStartVariable(), 1.0);		
		tempState = new ParserState(fakeRule, 0, 0, 0);
	}
	
	public ParserState enqueue(Rule rule, int dotPos, int start, int end) {
		return buckets[end].conditionalAddState(rule, dotPos, start, end);
	}
	
	public ParserState getState(Rule rule, int dotPos, int start, int end) {
		return buckets[end].getState(rule, dotPos, start, end);	
	}
	
	public boolean containsState(Rule rule, int dotPos, int start, int end) {
		return buckets[end].containsState(rule, dotPos, start, end);
	}
	
	public List<ParserState> getStates(int bucket) {
		return buckets[bucket].getStates();
	}
	
	public List<ParserState> getIncompleteStates(int bucket) {
		return buckets[bucket].getIncompleteStates();	
	}
	
	/**
	 * The Earley algorithm considers each state exactly once...
	 */
	public boolean hasUnexploredStates(int bucket) {
		return buckets[bucket].hasUnexploredStates();
	}
	
	public ParserState nextUnexploredState(int bucket) {
		return buckets[bucket].nextUnexploredState();
	}
		
	/**
	 * Returns the number of buckets (not states) in this ParserChart.
	 */
	public int getSize() {
		return size;	
	}
		
	public boolean isParsed() {
		return getEndState() != null;
	}
		
	public int getNumParseTrees() {
		ParserState end = getEndState();
		if (end == null) {
			return 0;
		} else {
			return end.getNumParseTrees();
		}
	}

	public double getBestProbability() {
		return getEndState().getBestProbability();
	}
		
	public double getProbability(int index) {
		return getEndState().getProbability(index);
	}
			
	public ParseTreeNode getBestParseTree() {
		return getEndState().getBestParseTree().getChild(0);
	}

	public ParseTreeNode getParseTree(int i) {
		if (i >= getNumParseTrees()) {
			throw new IllegalArgumentException(i + "");	
		}

		ParseTreeNode root = getEndState().getParseTree(i);
		return root.getChild(0); // this is the S --> XXX rule.
	}
	
	protected ParserState getEndState() {
		return getState(fakeRule, 1, 0, size-1);
	}
			
	@Override
	public String toString() {
		String s = "";
		
		for (int i = 0; i < size; i++) {
			s += "Chart[" + i + "]\n";
			s += "---------\n";
			
			// use a TreeSet to sort the states...
			Iterator<ParserState> states = new TreeSet<>(getStates(i)).iterator();

			while (states.hasNext()) {
				ParserState state = states.next();
				s += state.toString() + "\n";
			}
								
			s += "\n";
		}
		
		return s;	
	}
		
	private class ParserChartBucket {
		
		private List<ParserState> states = new ArrayList<>();
		private int numStates = 0;
		private int nextUnexplored = 0;
		
		private List<ParserState> incomplete = new ArrayList<>();
		
		private Map<ParserState, ParserState> all = new HashMap<>();
		
		protected void addState(ParserState state) {
			all.put(state, state);
			
			states.add(state);
			numStates++;
						
			if (!state.isComplete())
				incomplete.add(state);
		}

		public ParserState conditionalAddState(Rule rule, int dotPos, int start, int end) {
			tempState.rule = rule;
			tempState.dotPosition = dotPos;
			tempState.start = start;
			tempState.end = end;
			
			ParserState state = all.get(tempState);
			if (state == null) {
				state = new ParserState(rule, dotPos, start, end);	
				addState(state);
			}
			
			return state;
		}

		public boolean containsState(Rule rule, int dotPos, int start, int end) {
			tempState.rule = rule;
			tempState.dotPosition = dotPos;
			tempState.start = start;
			tempState.end = end;
			
			return all.containsKey(tempState);
		}

		public ParserState getState(Rule rule, int dotPos, int start, int end) {
			tempState.rule = rule;
			tempState.dotPosition = dotPos;
			tempState.start = start;
			tempState.end = end;
			
			return all.get(tempState);	
		}
		
		public boolean hasUnexploredStates() {
			return nextUnexplored < numStates;
		}
		
		public ParserState nextUnexploredState() {
			return states.get(nextUnexplored++);
		}
		
		public List<ParserState> getIncompleteStates() {
			return incomplete;
		}
		
		public List<ParserState> getStates() {
			return states;
		}
		
	}
	
}
