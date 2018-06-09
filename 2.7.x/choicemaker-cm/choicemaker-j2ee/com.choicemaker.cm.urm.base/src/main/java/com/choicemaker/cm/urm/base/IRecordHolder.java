/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.base;

import java.io.Serializable;

/**
 * A record object that contains the data at runtime.
 * <p>
 *
 * @author emoussikaev
 * @see
 */
public interface IRecordHolder<T extends Comparable<T> & Serializable>
		extends ISingleRecord<T> {

}
