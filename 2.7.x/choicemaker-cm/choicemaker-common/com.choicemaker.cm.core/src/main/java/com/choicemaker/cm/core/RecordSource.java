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

import java.io.IOException;

/**
 * Source of records.
 *
 * @author    Martin Buechi
 */

public interface RecordSource extends Source {
	/**
	 * Returns the next record.
	 *
	 * @return  The next record.
	 * @throws  IOException  if there is a problem retrieving the data.
	 */
	Record getNext() throws IOException;
}
