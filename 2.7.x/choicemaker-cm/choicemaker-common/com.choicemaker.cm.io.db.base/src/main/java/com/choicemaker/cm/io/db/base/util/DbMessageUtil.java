/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.db.base.util;

import java.util.ResourceBundle;

import com.choicemaker.util.MessageUtil;

/**
 *
 * @author    Martin Buechi
 */
public class DbMessageUtil {
	public static MessageUtil m = 
		new MessageUtil(ResourceBundle.getBundle("com.choicemaker.cm.io.db.base.util.res.Db"));
}
