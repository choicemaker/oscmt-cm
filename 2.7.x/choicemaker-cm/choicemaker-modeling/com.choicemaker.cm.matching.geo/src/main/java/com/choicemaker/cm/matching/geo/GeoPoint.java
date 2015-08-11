/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.geo;

/**
 * Geo-point class represents a point on the earth surface:
 * 	lat - latitude
 *  lon - longitude
 * 
 * @author emoussikaev
  */
public class GeoPoint {

	public int lon;
	public int lat;
	
	public GeoPoint(int lat,int lon){
		this.lon = lon;
		this.lat = lat;
	}
}
