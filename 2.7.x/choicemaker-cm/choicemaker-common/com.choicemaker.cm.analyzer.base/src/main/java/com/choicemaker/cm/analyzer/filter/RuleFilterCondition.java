/*
 * Copyright (c) 2001, 2019 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.analyzer.filter;

import com.choicemaker.cm.core.ActiveClues;

/**
 * Description
 *
 * @author  Martin Buechi
 */
public class RuleFilterCondition implements FilterCondition {
	private static final long serialVersionUID = 1L;

	private static final int NULL_CLUE_NUM = Integer.MIN_VALUE;

	private static final String NULL_STRING = "-";
	private static final String ACTIVE_STRING = "ACTIVE";
	private static final String INACTIVE_STRING = "INACTIVE";
	
	public static final boolean ACTIVE = true;
	public static final boolean INACTIVE = false;
	
	public static final Boolean NULL_CONDITION = null;
	public static final Boolean ACTIVE_CONDITION = new Boolean(ACTIVE);
	public static final Boolean INACTIVE_CONDITION = new Boolean(INACTIVE);

	private int clueNum;
	private Boolean value;

	public RuleFilterCondition(){
		this(null);
	}
	
	public RuleFilterCondition(Boolean value){
		this(NULL_CLUE_NUM, value);
	}
		
	public RuleFilterCondition(int clueNum, boolean value) {
		this(clueNum, new Boolean(value));
	}
	
	public RuleFilterCondition(int clueNum, Boolean value) {
		this.clueNum = clueNum;
		this.value = value;
	}

	/**
	 * Returns the clueNum.
	 * @return int
	 */
	@Override
	public int getClueNum() {
		return clueNum;
	}
	
	@Override
	public boolean satisfy(ActiveClues clues) {
		return clues.containsRule(clueNum) == value.booleanValue();
	}

	/**
	 * Returns the value.
	 * @return boolean
	 */
	public boolean isActive() {
		return value.booleanValue();
	}

	@Override
	public String getConditionString() {
		if (value == null){
			return NULL_STRING;
		} else {
			return isActive() ? ACTIVE_STRING : INACTIVE_STRING;
		}
	}

	@Override
	public FilterCondition createFilterCondition(int clueNum) {
		return new RuleFilterCondition(clueNum, value);
	}

	@Override
	public String toString(){
		String returnValue = getConditionString();
		
		//TODO: consider making this more sophisticated: i.e. (if it is a prototype return the conditionString, otherwise return a nicely formatted compilation)
		
		return returnValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + clueNum;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	/**
	 * @return true if we represent a NULL_CONDITION and are being compared against a null, or a NULL_FILTER_CONDITION;
	 * true if we are compared agains another IntFilterCondition that represents the same condition as us;
	 * false otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ( obj == null || obj == FilterCondition.NULL_FILTER_CONDITION){
			return value == NULL_CONDITION;
		}
		if (getClass() != obj.getClass())
			return false;
		RuleFilterCondition other = (RuleFilterCondition) obj;
		if (clueNum != other.clueNum)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	/**
	 * Old implmemtation of {@link #equals(Object)}. Use this method only for
	 * testing.
	 */
	public boolean equals_00(Object other){
		if ( other == null || other == FilterCondition.NULL_FILTER_CONDITION){
			return value == NULL_CONDITION;
		}
		else{
			if (other instanceof RuleFilterCondition){
				RuleFilterCondition otherfilterCondition = (RuleFilterCondition)other;
				return otherfilterCondition.getClueNum() == getClueNum()
					&& otherfilterCondition.value == value;
			}
			else{
				return false;
			}
		}
	}

}
