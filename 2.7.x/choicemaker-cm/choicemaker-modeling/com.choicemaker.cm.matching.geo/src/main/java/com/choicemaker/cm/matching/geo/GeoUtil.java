package com.choicemaker.cm.matching.geo;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.choicemaker.util.Precondition;

public class GeoUtil {

	private static final Logger logger =
		Logger.getLogger(GeoUtil.class.getName());

	private static final String msg0 =
		"latitude1: %f, latitude2: %f, longitude1: %f, longitude2: %f, miles: %f";

	/** Earth's mean radius in miles (6371 km) */
	public static double EARTH_RADIOUS = 3963.1;

	/** 1 mile = 1.609344 kilometers */
	public static double KM_PER_MILE = 1.609344;

	@Deprecated
	public static double RPG_INT = Math.PI / 1800000.00;

	/**
	 * Converts miles to kilometers
	 * 
	 * @param mld
	 *            distance in miles
	 * @return distance in kilometers
	 */
	public static double mileToKm(double mld) {
		return mld * KM_PER_MILE;
	}

	/**
	 * Converts kilometers to miles
	 * 
	 * @param kmd
	 *            distance in kilometers
	 * @return distance in miles
	 */
	public static double kmToMile(double kmd) {
		return kmd / KM_PER_MILE;
	}

	/**
	 * Calculates distance in miles between two geo-points using Haversine
	 * formula.
	 * 
	 * @param latitude1,
	 *            in degrees, between -180f and +180f
	 * @param latitude2,
	 *            in degrees, between -180f and +180f
	 * @param longitude1,
	 *            in degrees, between -90f and +90f
	 * @param lon2,
	 *            in degrees, between -90f and +90f
	 * @return distance in miles
	 */
	public static double distance(double latitude1, double latitude2,
			double longitude1, double longitude2) {
		Precondition.assertBoolean(
				"latitude1 must be between -180.0 and +180.0, inclusive",
				latitude1 >= -180.0 && latitude1 <= 180.0);
		Precondition.assertBoolean(
				"latitude2 must be between -180.0 and +180.0, inclusive",
				latitude2 >= -180.0 && latitude2 <= 180.0);
		Precondition.assertBoolean(
				"longitude1 must be between -90.0 and +90.0, inclusive",
				longitude1 >= -90.0 && longitude1 <= 90.0);
		Precondition.assertBoolean(
				"longitude2 must be between -90.0 and +90.0, inclusive",
				longitude2 >= -90.0 && longitude2 <= 90.0);

		longitude1 = Math.toRadians(longitude1);
		longitude2 = Math.toRadians(longitude2);
		latitude1 = Math.toRadians(latitude1);
		latitude2 = Math.toRadians(latitude2);

		// Haversine formula
		double dlon = longitude2 - longitude1;
		double dlat = latitude2 - latitude1;
		double a = Math.pow(Math.sin(dlat / 2), 2) + Math.cos(latitude1)
				* Math.cos(latitude2) * Math.pow(Math.sin(dlon / 2), 2);

		double c = 2 * Math.asin(Math.sqrt(a));

		double retVal = (c * EARTH_RADIOUS);

		if (logger.isLoggable(Level.FINE)) {
			String msg = String.format(msg0, latitude1, latitude2, longitude1,
					longitude2, retVal);
			logger.fine(msg);
		}

		return retVal;
	}

	/**
	 * Calculates distance in miles between two geo-points using Haversine
	 * formula.
	 * 
	 * @param gp1
	 *            First geo-point.
	 * @param gp2
	 *            Second geo-point.
	 * @return distance in miles, or -1.0 if either argument is null
	 */
	public static double dist(GeoPoint gp1, GeoPoint gp2) {

		double retVal;
		if (gp1 == null || gp2 == null) {
			retVal = -1d;
		} else {
			retVal = distance(gp1.latitude, gp2.latitude, gp1.longitude,
					gp2.longitude);
		}

		return retVal;
	}

}
