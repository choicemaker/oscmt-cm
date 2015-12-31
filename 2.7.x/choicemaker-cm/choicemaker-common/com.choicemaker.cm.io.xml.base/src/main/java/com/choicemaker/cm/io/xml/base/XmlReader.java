/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.xml.base;

import org.xml.sax.helpers.DefaultHandler;

/**
 * Description
 *
 * @author    Martin Buechi
 */
public abstract class XmlReader extends DefaultHandler {
	public abstract void open(RecordHandler rh);
}
