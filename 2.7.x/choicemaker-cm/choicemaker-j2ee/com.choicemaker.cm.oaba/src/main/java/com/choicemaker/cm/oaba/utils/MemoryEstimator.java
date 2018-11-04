/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.utils;

import java.util.HashMap;
import java.util.logging.Logger;

import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.RecordSource;

/**
 * This object estimates how many records can fit safely into a hashmap in
 * memory.
 *
 * @author pcheung
 *
 */
@SuppressWarnings({
		"rawtypes", "unchecked" })
public class MemoryEstimator {

	private static final Logger log =
		Logger.getLogger(MemoryEstimator.class.getName());

	private static int INTERVAL = 100;

	/**
	 * This method estimate how big of a hashmap of records we can store in
	 * memory.
	 *
	 * @param rs
	 *            a record source, used to pull one record as a sample
	 * @return int - the maximum size of hashmap that can fit into memory.
	 */
	public static int estimate(RecordSource rs, ImmutableProbabilityModel model,
			float limit) {
		int ret = 0;

		Runtime.getRuntime().gc();

		HashMap map = new HashMap();

		try {
			rs.setModel(model);
			rs.open();
			int recID = 0;
			boolean stop = false;

			while (rs.hasNext() && !stop) {
				Record r = rs.getNext();
				Object O = r.getId();
				ret++;

				if (O.getClass().equals(java.lang.Long.class)) {
					Long L = (Long) r.getId();
					recID = L.intValue();
				} else if (O.getClass().equals(java.lang.Integer.class)) {
					recID = ((Integer) r.getId()).intValue();
				}

				map.put(new Integer(recID), r);

				if (ret % INTERVAL == 0) {
					stop = isFull(limit);
				}

			} // end while next

			rs.close();
		} catch (Exception ex) {
			log.severe(ex.toString());
		}

		return ret;
	}

	/**
	 * This method estimate2 is like an inverse function of estimate. It puts
	 * the given number of record in hashmap and checks how much memory is left
	 *
	 * @param rs
	 * @return float - returns the percentage of memory used up.
	 */
	public static float estimate2(RecordSource rs,
			ImmutableProbabilityModel model, int count) {
		Runtime.getRuntime().gc();
		HashMap map = new HashMap();

		int i = 0;
		try {
			rs.setModel(model);
			rs.open();
			int recID = 0;
			boolean stop = false;

			while (rs.hasNext() && !stop) {
				Record r = rs.getNext();
				Object O = r.getId();

				if (O.getClass().equals(java.lang.Long.class)) {
					Long L = (Long) r.getId();
					recID = L.intValue();
				} else if (O.getClass().equals(java.lang.Integer.class)) {
					recID = ((Integer) r.getId()).intValue();
				}

				map.put(new Integer(recID), r);
				i++;

				if (i > count) {
					stop = true;
				}

			} // end while next

			rs.close();
		} catch (Exception ex) {
			log.severe(ex.toString());
		}

		return memLeft();
	}

	/**
	 * This method checks to see if memory is getting full. If the total memory
	 * in used is greater than 75% of maximum memory then return true. This is
	 * inexact since memory is dependent on JVM gc.
	 *
	 * @return boolean - true if the memory is over limit.
	 */
	public static boolean isFull(float limit) {
		boolean ret = false;

		long m = Runtime.getRuntime().maxMemory();
		long t = Runtime.getRuntime().totalMemory();
		long f = Runtime.getRuntime().freeMemory();
		float pct = (1.0f * t - f) / m;
		if (pct > limit)
			ret = true;

		return ret;
	}

	public static float memLeft() {
		long m = Runtime.getRuntime().maxMemory();
		long t = Runtime.getRuntime().totalMemory();
		long f = Runtime.getRuntime().freeMemory();
		float pct = (1.0f * t - f) / m;

		return pct;
	}

	private static final String msg0 =
		"Max %dB, total %dB, free %dB, fraction %3.2f";

	public static void writeMem() {
		long max = Runtime.getRuntime().maxMemory();
		long total = Runtime.getRuntime().totalMemory();
		long free = Runtime.getRuntime().freeMemory();
		float pct = ((1.0f * total - free) / max);
		log.info(String.format(msg0, max, total, free, pct));
	}

}
