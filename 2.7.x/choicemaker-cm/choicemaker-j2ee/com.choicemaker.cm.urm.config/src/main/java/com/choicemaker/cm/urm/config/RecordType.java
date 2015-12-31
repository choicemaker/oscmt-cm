/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.config;


/**
 * A type of the record object
 * <ul>
 * <li>NONE no data</li>
 * <li>HOLDER record holder object</li>
 * <li>REF record reference object</li>
 * <li>GLOBAL_REF record global reference object</li>
 * </ul>
 * <p>
 *
 * @author emoussikaev
 */
public enum RecordType {
	NONE, HOLDER, REF, GLOBAL_REF
}
