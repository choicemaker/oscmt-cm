/*******************************************************************************
 * Copyright (c) 2003, 2017 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.geo;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import com.choicemaker.util.EnumUtils;

/**
 * A map that defines co-ordinates for geo-entities of a certain type. Each map
 * can be either TreeMap or HashMap. In addition to map itselt the object stores
 * information about key type, key length (-1 if undefined), and key fields. Key
 * fields can be used to build an adequate GUI. If a key is a simple scalar
 * number or string key fields are undefined (null).
 * 
 * @author emoussikaev
 *
 * @since 2.7
 * 
 */
public class GeoMap {
	
	private static enum MAP_TYPE {
		tree, hash;
		Map<Integer,GeoPoint> newInstance() {
			Map<Integer,GeoPoint> retVal;
			switch(this) {
			case tree:
				retVal = new TreeMap<>();
				break;
			case hash:
			default:
				retVal = new HashMap<>();
			}
			return retVal;
		}
	}

	/**
	 * Supply information about name and length of a key filed.
	 * 
	 */
	public class KeyField {
		public final String name;
		public final int length;

		public KeyField(String name, int length) {
			this.name = name;
			this.length = length;
		}
	}

	private final Map<Integer, GeoPoint> map;
	public final String keyType;
	private final int keyLen;
	private Vector<GeoMap.KeyField> fields;

	public GeoMap(String mapType, String keyType, int keyLen) {
		MAP_TYPE _mapType = EnumUtils.valueOfIgnoreCase(MAP_TYPE.class, mapType);
		this.map = _mapType.newInstance();
		this.keyType = keyType;
		this.keyLen = keyLen;
	}

	public void setFields(Vector<GeoMap.KeyField> fields) {
		this.fields = fields;
	}

	public Vector<GeoMap.KeyField> getFields() {
		return this.fields;
	}

	public Map<Integer, GeoPoint> getMap() {
		return map;
	}

	public String getKeyType() {
		return this.keyType;
	}

	public int getKeyLength() {
		return this.keyLen;
	}
}
