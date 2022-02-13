/*******************************************************************************
 * Copyright (c) 2003, 2019 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc  - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
package com.choicemaker.cm.core.util;

import java.util.Arrays;

import com.choicemaker.cm.core.ImmutableProbabilityModel;

public class ModelUtils {
	
	private static class SortableModel implements Comparable/*<SortableModel>*/ {
		
		private final ImmutableProbabilityModel model;

		public SortableModel(ImmutableProbabilityModel m) {
			if (m == null) {
				throw new IllegalArgumentException("null model");
			}
			if (m.getModelName() == null) {
				throw new Error("null model name");
			}
			this.model = m;
		}

		// FIXME Remove this method in Java 5 or later
		@Override
		public int compareTo(Object o) {
			return compareTo((SortableModel) o);
		}
		
//		@Override
		public int compareTo(SortableModel m) {
			if (m == null) {
				throw new IllegalArgumentException("null model");
			}
			return this.model.getModelName().compareTo(m.model.getModelName());
		}
		
	}
	
	/** Sorts an array of model by model name */
	public static void sort(ImmutableProbabilityModel[] models) {
		if (models == null) {
			throw new IllegalArgumentException("null models");
		}
		SortableModel[] smodels = new SortableModel[models.length];
		for (int i=0; i<models.length; i++) {
			smodels[i] = new SortableModel(models[i]);
		}
		Arrays.sort(smodels);
		for (int i=0; i<smodels.length; i++) {
			models[i] = smodels[i].model;
		}
	}

	private ModelUtils() {
	}

}
