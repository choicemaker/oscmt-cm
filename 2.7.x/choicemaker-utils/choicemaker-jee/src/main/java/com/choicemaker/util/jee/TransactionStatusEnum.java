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
 *     ChoiceMaker LLC - initial API and implementation
 *******************************************************************************/
package com.choicemaker.util.jee;

import javax.transaction.Status;

public enum TransactionStatusEnum {
	STATUS_ACTIVE, STATUS_MARKED_ROLLBACK, STATUS_PREPARED, STATUS_COMMITTED,
	STATUS_ROLLEDBACK, STATUS_UNKNOWN, STATUS_NO_TRANSACTION, STATUS_PREPARING,
	STATUS_COMMITTING, STATUS_ROLLING_BACK;

	public boolean equals(int status) {
		boolean retVal = false;
		if (Status.STATUS_ACTIVE <= status
				&& status <= Status.STATUS_ROLLING_BACK) {
			TransactionStatusEnum tse = TransactionStatusEnum.values()[status];
			retVal = this == tse;
		}
		return retVal;
	}
}
