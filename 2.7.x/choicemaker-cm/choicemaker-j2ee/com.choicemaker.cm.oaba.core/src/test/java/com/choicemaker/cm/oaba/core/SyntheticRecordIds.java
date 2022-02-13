/*******************************************************************************
 * Copyright (c) 2003, 2020 ChoiceMaker LLC and others.
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
package com.choicemaker.cm.oaba.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * A test utility that generates lists of query and reference record ids.
 * <p>
 * Only the sizes of the synthetic id lists matter in tests of
 * {@linkplain ComparisonArray} and {@linkplain ComparisonArrayOS}, so the
 * methods for {@link #compareTo(SyntheticRecordIds) compareTo},
 * {@link #equals(Object) equals}, and {@link #hashCode() hashCode} are very
 * simple.
 */

public class SyntheticRecordIds implements Comparable<SyntheticRecordIds> {

	public static final RECORD_ID_TYPE ID_TYPE = RECORD_ID_TYPE.TYPE_STRING;

	private final List<String> queryIds;
	private final List<String> referenceIds;

	public SyntheticRecordIds(int countQueryIds, int countReferenceIds) {
		assert countQueryIds >= 0;
		assert countReferenceIds >= 0;

		List<String> ids;
		if (countQueryIds <= 0) {
			ids = Collections.emptyList();
		} else {
			ids = new ArrayList<>(countQueryIds);
			for (int i = 0; i < countQueryIds; i++) {
				String id = "Q_" + UUID.randomUUID().toString();
				ids.add(id);
			}
		}
		queryIds = Collections.unmodifiableList(ids);

		if (countReferenceIds <= 0) {
			ids = Collections.emptyList();
		} else {
			ids = new ArrayList<>(countReferenceIds);
			for (int i = 0; i < countReferenceIds; i++) {
				String id = "R_" + UUID.randomUUID().toString();
				ids.add(id);
			}
		}
		referenceIds = Collections.unmodifiableList(ids);
	}

	public List<String> getQueryIds() {
		return queryIds;
	}

	public List<String> getReferenceIds() {
		return referenceIds;
	}

	@Override
	public boolean equals(Object o) {
		boolean retVal = false;
		if (this == o) {
			retVal = true;
		} else if (o == null) {
			assert retVal == false;
		} else if (getClass() != o.getClass()) {
			assert retVal == false;
		} else if (o instanceof SyntheticRecordIds) {
			SyntheticRecordIds that = (SyntheticRecordIds) o;
			int thisQuerySize = this.getQueryIds().size();
			int thatQuerySize = that.getQueryIds().size();
			if (thisQuerySize == thatQuerySize) {
				int thisReferenceSize = this.getReferenceIds().size();
				int thatReferenceSize = that.getReferenceIds().size();
				retVal = thisReferenceSize == thatReferenceSize;
			}
		}
		return retVal;
	}

	/** Only the sizes of the synthetic id lists matter in this test */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((queryIds == null) ? 0 : queryIds.size());
		result =
			prime * result + ((referenceIds == null) ? 0 : referenceIds.size());
		return result;
	}

	/** Only the sizes of the synthetic id lists matter in this test */
	@Override
	public int compareTo(SyntheticRecordIds that) {
		int retVal = -1;
		if (that != null) {
			Integer thisQuerySize = this.getQueryIds().size();
			Integer thatQuerySize = that.getQueryIds().size();
			retVal = thisQuerySize.compareTo(thatQuerySize);
			if (retVal == 0) {
				Integer thisReferenceSize = this.getReferenceIds().size();
				Integer thatReferenceSize = that.getReferenceIds().size();
				retVal = thisReferenceSize.compareTo(thatReferenceSize);
			}
		}
		return retVal;
	}

	@Override
	public String toString() {
		return "SyntheticRecordIds [queryIdCount=" + queryIds.size()
				+ ", referenceIdCount=" + referenceIds.size() + "]";
	}

}