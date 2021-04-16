package com.choicemaker.cm.matching.geo;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class GeoPointTest {

	@Test
	public void testGeoPointConstructors() {
		GeoPoint gpFloat = new GeoPoint(0.123456, 0.987654);
		GeoPoint gpInt = new GeoPoint(1234, 9876);

		float diffFloat;
		diffFloat = Math.abs(gpFloat.latitude - gpInt.latitude);
		assertTrue(diffFloat <= 1);
		diffFloat = Math.abs(gpFloat.longitude - gpInt.longitude);
		assertTrue(diffFloat <= 1);

		int diffInt;
		diffInt = Math.abs(gpFloat.lat - gpInt.lat);
		assertTrue(diffInt <= 0.0001);
		diffInt = Math.abs(gpFloat.lon - gpInt.lon);
		assertTrue(diffInt <= 0.0001);

	}

}
