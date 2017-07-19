package com.choicemaker.cm.matching.geo;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GeoUtil {

	private static final Logger logger =
		Logger.getLogger(GeoUtil.class.getName());

	/** Earth's mean radius in miles (6371 km) */
	public static double EARTH_RADIOUS = 3963.1;

	/** 1 mile = 1.609344 kilometers */
	public static double KM_PER_MILE = 1.609344;

	private static double RPG = Math.PI / 180.00;

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
	 * Calculates distance between two geo-points using Haversine formula.
	 * 
	 * @param gp1
	 *            First geo-point.
	 * @param gp2
	 *            Second geo-point.
	 * @return Distance.
	 */
	public static double dist(GeoPoint gp1, GeoPoint gp2) {
		if (gp1 == null || gp2 == null)
			return -1;

		final double dLat = (gp2.latitude - gp1.latitude) * RPG;
		final double dLon = (gp2.longitude - gp1.longitude) * RPG;
		final double lat1 = gp1.latitude * RPG;
		final double lat2 = gp2.latitude * RPG;
		final double a =
			Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(lat1)
					* Math.cos(lat2) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
		final double dist =
			EARTH_RADIOUS * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		// Trick to set flag if assertion is enabled
		boolean assertOn = false;
		assert assertOn = true;

		// If assertion or logging is enabled, compare to deprecated method
		if (logger.isLoggable(Level.WARNING) || assertOn) {
			double PRECISION = 0.002; // 0.002 miles == 10 feet
			double _dLat = (gp2.lat - gp1.lat) * RPG_INT;
			double _dLon = (gp2.lon - gp1.lon) * RPG_INT;
			double _lat1 = gp1.lat * RPG_INT;
			double _lat2 = gp2.lat * RPG_INT;
			double _a = Math.sin(_dLat / 2) * Math.sin(_dLat / 2)
					+ Math.cos(_lat1) * Math.cos(_lat2) * Math.sin(_dLon / 2)
							* Math.sin(_dLon / 2);
			final double _dist = EARTH_RADIOUS * 2
					* Math.atan2(Math.sqrt(_a), Math.sqrt(1 - _a));
			if (Math.abs(_dist - dist) > PRECISION) {
				String msg = "Revised geo-distance computation (" + dist
						+ ") != deprecrated computation (" + _dist + ")";
				logger.warning(msg);
				assert false : msg;
			}
		}

		return dist;
	}

}
