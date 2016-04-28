/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.modelmaker.filter;

import java.beans.PropertyChangeListener;

import com.choicemaker.cm.analyzer.filter.Filter;
import com.choicemaker.cm.analyzer.filter.IMarkedRecordPairFilter;

public interface ListeningMarkedRecordPairFilter extends Filter,
		IMarkedRecordPairFilter, PropertyChangeListener {

}
