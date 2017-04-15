package com.choicemaker.cm.matching.geo;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class GeoHelperTest {

	@Test
	public void testMileToKm() {
		final float miles = 11.0f;
		final float expectedKM = 17.702784f;
		final float computedKM = (float) GeoHelper.mileToKm(miles);
		assertTrue(expectedKM == computedKM);
	}

	@Test
	public void testKmToMile() {
		final float kms = 17.0f;
		final float expectedMiles = 10.56331f;
		final float computedMiles = (float) GeoHelper.kmToMile(kms);
		final float PRECISION = 0.002f; // 0.002 miles = 10 ft
		final float diff = (expectedMiles - computedMiles);
		assertTrue(diff <= PRECISION);
	}

	@Test
	public void testDist() {
		final float floatLatitude1 = 40.286020f;
		final float floatLongitude1 = -74.857119f;
		// CORRECT ORDER
		GeoPoint gp1 = new GeoPoint(floatLatitude1, floatLongitude1);

		final float floatLatitude2 = 40.314526f;
		final float floatLongitude2 = -74.637735f;
		// CORRECT ORDER
		GeoPoint gp2 = new GeoPoint(floatLatitude2, floatLongitude2);

		float expectedDistance = 11.74f;
		float computedDistance = (float) GeoHelper.dist(gp1, gp2);
		float diffDistance = Math.abs(computedDistance - expectedDistance);
		final float PRECISION = 0.01f; // 0.01 miles = 53 ft
		assertTrue(diffDistance <= PRECISION);

		// Check effect of reversing latitude and longitude

		// INCORRECT ORDER
		GeoPoint _gp1 = new GeoPoint(floatLongitude1, floatLatitude1);
		// INCORRECT ORDER
		GeoPoint _gp2 = new GeoPoint(floatLongitude2, floatLatitude2);
		float _expectedDistance = 15.18f;
		float _computedDistance = (float) GeoHelper.dist(_gp1, _gp2);
		float _diffDistance = Math.abs(_computedDistance - _expectedDistance);
		assertTrue(_diffDistance <= PRECISION);

		// Check effect of nulls
		assertTrue(GeoHelper.dist(gp1, null) == -1f);
		assertTrue(GeoHelper.dist(null, gp2) == -1f);
		assertTrue(GeoHelper.dist(null, null) == -1f);
	}

}
