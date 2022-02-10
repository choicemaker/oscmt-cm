/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.geo;

/**
 * Geo-point class represents a point on the earth surface.
 * 
 * @author emoussikaev
 */
public class GeoPoint {

	/** latitude: float value, between -90f and +90f */
	public final float longitude;

	/** longitude: float value, between -180f and +180f */
	public final float latitude;

	/**
	 * integer approximation to latitude, retaining 4 significant digits after
	 * the decimal place, computed as (int) (10000 * latitude)
	 */
	@Deprecated
	public final int lon;

	/**
	 * integer approximation to longitude, retaining 4 significant digits after
	 * the decimal place, computed as (int) (10000 * latitude)
	 */
	@Deprecated
	public final int lat;

	/**
	 * @param latitude
	 *            in degrees, between -180f and +180f
	 * @param longitude
	 *            in degrees, between -90f and +90f
	 */
	public GeoPoint(double latitude, double longitude) {
		this.latitude = (float) latitude;
		this.lat = (int) (latitude * 10000);
		this.longitude = (float) longitude;
		this.lon = (int) (longitude * 10000);
	}

	/**
	 * @param lat
	 *            value, retaining 4 significant digits after the decimal place,
	 *            computed as (int) (10000 * latitude).</li>
	 * @param lon
	 *            int value, retaining 4 significant digits after the decimal
	 *            place, computed as (int) (10000 * longitude).</li>
	 *            </ul>
	 */
	@Deprecated
	public GeoPoint(int lat, int lon) {
		this.lat = lat;
		this.latitude = (lat) / 10000f;
		this.lon = lon;
		this.longitude = (lon) / 10000f;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(latitude);
		result = prime * result + Float.floatToIntBits(longitude);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GeoPoint other = (GeoPoint) obj;
		if (Float.floatToIntBits(latitude) != Float
				.floatToIntBits(other.latitude))
			return false;
		if (Float.floatToIntBits(longitude) != Float
				.floatToIntBits(other.longitude))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "GeoPoint [longitude=" + longitude + ", latitude=" + latitude
				+ "]";
	}

}
