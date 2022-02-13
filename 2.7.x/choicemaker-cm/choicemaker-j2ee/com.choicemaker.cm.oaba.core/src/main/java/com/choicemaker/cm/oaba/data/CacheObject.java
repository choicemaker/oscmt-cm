/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.data;

/**
 * @author pcheung
 *
 * @deprecated
 */
@Deprecated
public class CacheObject {
	int count;
	Object obj;

	public CacheObject(Object o) {
		count = 1;
		this.obj = o;
	}

	public Object getObject() {
		return obj;
	}

	public int getCount() {
		return count;
	}

	public void addCount() {
		count++;
	}

	public void subtractCount() {
		count--;
	}

}
