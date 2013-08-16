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
package com.choicemaker.cm.modelmaker.filter;

import com.choicemaker.cm.core.ActiveClues;

/**
 * Description
 * 
 * @author  Martin Buechi
 * @version $Revision: 1.1.1.1 $ $Date: 2009/05/03 16:03:08 $
 */
public class RuleFilterCondition implements FilterCondition {
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
	public int getClueNum() {
		return clueNum;
	}
	
	/**
	 * @see com.choicemaker.cm.train.filter.FilterCondition#satisfy(com.choicemaker.cm.core.ActiveClues)
	 */
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

	/**
	 * @see com.choicemaker.cm.train.filter.FilterCondition#getConditionString()
	 */
	/**
	 * @see com.choicemaker.cm.train.filter.FilterCondition#getConditionString()
	 */
	public String getConditionString() {
		if (value == null){
			return NULL_STRING;
		} else {
			return isActive() ? ACTIVE_STRING : INACTIVE_STRING;
		}
	}
	/**
	 * @see com.choicemaker.cm.train.filter.FilterCondition#createFilterCondition(int)
	 */
	public FilterCondition createFilterCondition(int clueNum) {
		return new RuleFilterCondition(clueNum, value);
	}

	public String toString(){
		String returnValue = getConditionString();
		
		//TODO: consider making this more sophisticated: i.e. (if it is a prototype return the conditionString, otherwise return a nicely formatted compilation)
		
		return returnValue;
	}

	/**
	 * @return true if we represent a NULL_CONDITION and are being compared against a null, or a NULL_FILTER_CONDITION;
	 * true if we are compared agains another IntFilterCondition that represents the same condition as us;
	 * false otherwise.
	 */
	public boolean equals(Object other){
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