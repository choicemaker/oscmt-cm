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
package com.choicemaker.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @author Adam Winkel
 */
public class IntValuedHashMap<K> extends HashMap<K, Integer> {
	private static final long serialVersionUID = 1L;

	public IntValuedHashMap() {
	}

	public IntValuedHashMap(IntValuedHashMap<K> map) {
		super(map);
	}

	@Override
	public Integer put(K key, Integer value) {
		if (!(value instanceof Integer)) {
			throw new IllegalArgumentException();
		}

		return super.put(key, value);
	}

	public void putInt(K key, int value) {
		super.put(key, new Integer(value));
	}

	public int getInt(K key) {
		Integer value = get(key);
		if (value != null)
			return value.intValue();
		else
			return 0;
	}

	public void increment(K key) {
		putInt(key, getInt(key) + 1);
	}

	public List<K> sortedKeys() {
		List<K> keys = new ArrayList<>(keySet());
		Collections.sort(keys, new MapKeyComparator<K,Integer>(this));
		return keys;
	}

}
