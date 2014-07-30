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
package com.choicemaker.cm.core;


/**
 * Base interface for the main record.
 * This interface is implemented by the main record of each schema.
 *
 * @author    Martin Buechi
 * @version   $Revision: 1.1 $ $Date: 2010/01/20 15:05:04 $
 */
public interface Record extends BaseRecord {
	/**
	 * Returns the ID.
	 *
	 * @return   The ID.
	 */
	Comparable getId();

	void computeValidityAndDerived();

	DerivedSource getDerivedSource();
}
