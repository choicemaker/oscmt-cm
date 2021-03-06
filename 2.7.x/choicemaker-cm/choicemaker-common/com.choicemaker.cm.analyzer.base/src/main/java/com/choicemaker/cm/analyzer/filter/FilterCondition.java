/*
 * Copyright (c) 2001, 2022 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.analyzer.filter;

import java.io.Serializable;

import com.choicemaker.cm.core.ActiveClues;

/**
 * Description
 *
 * @author  Martin Buechi
 */
public interface FilterCondition extends Serializable {
	public static final FilterCondition NULL_FILTER_CONDITION = new FilterCondition() {
		private static final long serialVersionUID = 1L;
		/**
		 * @see com.choicemaker.cm.train.filter.FilterCondition#satisfy(com.choicemaker.cm.core.ActiveClues)
		 */
		@Override
		public boolean satisfy(ActiveClues clues) {
			return true;
		}

		/**
		 * @see com.choicemaker.cm.train.filter.FilterCondition#getConditionString()
		 */
		@Override
		public String getConditionString() {
			return "NULL_FILTER_CONDITION";
		}

		@Override
		public int hashCode() {
			return 0;
		}

		@Override
		public boolean equals(Object other){
			return other == null || other == this || (other instanceof FilterCondition && other.equals(this));
		}

		@Override
		public FilterCondition createFilterCondition(int clueNum) {
			throw new IllegalStateException("cannot set the clue number of the NULL_FILTER_CONDITION");
		}
		@Override
		public int getClueNum() {
			throw new IllegalStateException("cannot get the clue number of the NULL_FILTER_CONDITION");
		}
	};
	/**
	 * Returns the clueNum.
	 * @return int
	 */
	public int getClueNum();

	boolean satisfy(ActiveClues clues);
	String getConditionString();

	/**
	 * creates a Copy of this FilterCondition but with the appropriate Clue Number.
	 * @param clueNum the Clue Number
	 */
	FilterCondition createFilterCondition(int clueNum);

}
