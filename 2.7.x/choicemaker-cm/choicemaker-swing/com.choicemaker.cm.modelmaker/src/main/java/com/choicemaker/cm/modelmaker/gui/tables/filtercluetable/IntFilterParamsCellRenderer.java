/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.modelmaker.gui.tables.filtercluetable;

import javax.swing.table.DefaultTableCellRenderer;

import com.choicemaker.cm.analyzer.filter.IntFilterCondition;

/**
 * .
 *
 * @author   Arturo Falck
 */
public class IntFilterParamsCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	/**
	 * @see javax.swing.table.DefaultTableCellRenderer#setValue(java.lang.Object)
	 */
	@Override
	protected void setValue(Object value) {
		if (value instanceof IntFilterCondition) {
			IntFilterCondition filterCondition = (IntFilterCondition) value;

			switch (filterCondition.getCondition()) {
				case IntFilterCondition.NULL_CONDITION:
					setText("");
					break;

				case IntFilterCondition.BETWEEN :
					if (filterCondition.getA() != IntFilterCondition.NULL_PARAM
					&&  filterCondition.getB() != IntFilterCondition.NULL_PARAM){
						setText("[" + filterCondition.getA() + " ... " + filterCondition.getB() + "]");
					}
					break;

				case IntFilterCondition.OUTSIDE :
					if (filterCondition.getA() != IntFilterCondition.NULL_PARAM
					&&  filterCondition.getB() != IntFilterCondition.NULL_PARAM){
						setText(")" + filterCondition.getA() + " ... " + filterCondition.getB() + "(");
					}
					break;

				case IntFilterCondition.LESS_THAN :
					//FALLS THROUGH
				case IntFilterCondition.LESS_THAN_EQUAL :
					if (filterCondition.getA() != IntFilterCondition.NULL_PARAM){
						setText("" + filterCondition.getB());
					}
					break;

				default :
					if (filterCondition.getA() != IntFilterCondition.NULL_PARAM){
						setText("" + filterCondition.getA());
					}
					break;
			}
		} else {
			setText("");
		}
	}

}
