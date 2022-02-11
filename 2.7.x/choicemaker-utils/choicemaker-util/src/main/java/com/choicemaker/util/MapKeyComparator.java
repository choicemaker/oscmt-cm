/*
 * Copyright (c) 2001, 2018 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.util;

import java.util.Comparator;
import java.util.Map;

class MapKeyComparator<K, V extends Comparable<V>> implements Comparator<K> {

	protected Map<K,V> map;

	public MapKeyComparator(Map<K,V> map) {
		this.map = map;
	}

	@Override
	public int compare(K obj1, K obj2) {
		V val1 = map.get(obj1);
		V val2 = map.get(obj2);
		if (val1 == null && val2 == null) {
			return 0;
		} else if (val1 == null) {
			return 1;
		} else if (val2 == null) {
			return -1;
		} else {
			return val2.compareTo(val1);
		}
	}
}
