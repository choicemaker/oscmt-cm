/*******************************************************************************
 * Copyright (c) 2003, 2017 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
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
