/*******************************************************************************
 * Copyright (c) 2003, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.geo;

import java.util.Collection;

import com.choicemaker.cm.matching.geo.eclipse.GeoMaps;

/**
 * @deprecated see com.choicemaker.cm.matching.geo.eclipse.GeoMaps
 */
@Deprecated
public class GeoHelper {

	public GeoHelper() {
	}

	/**
	 * @deprecated see {@link GeoUtil#mileToKm(double)}
	 */
	@Deprecated
	public static double mileToKm(double mld) {
		return GeoUtil.mileToKm(mld);
	}

	/**
	 * @deprecated see {@link GeoUtil#kmToMile(double)}
	 */
	@Deprecated
	public static double kmToMile(double kmd) {
		return GeoUtil.kmToMile(kmd);
	}

	/**
	 * @deprecated see {@link GeoUtil#dist(GeoPoint, GeoPoint)}
	 */
	@Deprecated
	public static double dist(GeoPoint gp1, GeoPoint gp2) {
		return GeoUtil.dist(gp1, gp2);
	}

	/**
	 * @deprecated see {@link GeoMaps#addMap(String, GeoMap) }
	 */
	@Deprecated
	public static void addMap(String name, GeoMap map) {
		GeoMaps.addMap(name, map);
	}

	/**
	 * @deprecated see {@link GeoMaps#getMap(String) }
	 */
	@Deprecated
	public static GeoMap getMap(String geoEntityType) {
		return GeoMaps.getMap(geoEntityType);
	}

	/**
	 * @deprecated see {@link GeoMaps#getGeoTypes() }
	 */
	@Deprecated
	public static Collection<String> getGeoTypes() {
		return GeoMaps.getGeoTypes();
	}

	/**
	 * @deprecated see {@link GeoMaps#geoPoint(String, int) }
	 */
	@Deprecated
	public static GeoPoint geoPoint(String geoEntityType,
			int geoEntityDescription) {
		return GeoMaps.geoPoint(geoEntityType, geoEntityDescription);
	}

	/**
	 * @deprecated see {@link GeoMaps#geoPoint(String, String) }
	 */
	@Deprecated
	public static GeoPoint geoPoint(String geoEntityType,
			String geoEntityDescription) {
		return GeoMaps.geoPoint(geoEntityType, geoEntityDescription);
	}

}
