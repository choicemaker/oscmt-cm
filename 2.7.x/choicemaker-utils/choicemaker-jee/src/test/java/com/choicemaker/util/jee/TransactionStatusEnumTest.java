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

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TransactionStatusEnumTest {

	@Test
	public void test() {
		assertTrue(TransactionStatusEnum.STATUS_ACTIVE == TransactionStatusEnum
				.values()[javax.transaction.Status.STATUS_ACTIVE]);
		assertTrue(TransactionStatusEnum.STATUS_MARKED_ROLLBACK == TransactionStatusEnum
				.values()[javax.transaction.Status.STATUS_MARKED_ROLLBACK]);
		assertTrue(TransactionStatusEnum.STATUS_PREPARED == TransactionStatusEnum
				.values()[javax.transaction.Status.STATUS_PREPARED]);
		assertTrue(TransactionStatusEnum.STATUS_COMMITTED == TransactionStatusEnum
				.values()[javax.transaction.Status.STATUS_COMMITTED]);
		assertTrue(TransactionStatusEnum.STATUS_ROLLEDBACK == TransactionStatusEnum
				.values()[javax.transaction.Status.STATUS_ROLLEDBACK]);
		assertTrue(TransactionStatusEnum.STATUS_UNKNOWN == TransactionStatusEnum
				.values()[javax.transaction.Status.STATUS_UNKNOWN]);
		assertTrue(TransactionStatusEnum.STATUS_NO_TRANSACTION == TransactionStatusEnum
				.values()[javax.transaction.Status.STATUS_NO_TRANSACTION]);
		assertTrue(TransactionStatusEnum.STATUS_PREPARING == TransactionStatusEnum
				.values()[javax.transaction.Status.STATUS_PREPARING]);
		assertTrue(TransactionStatusEnum.STATUS_COMMITTING == TransactionStatusEnum
				.values()[javax.transaction.Status.STATUS_COMMITTING]);
		assertTrue(TransactionStatusEnum.STATUS_ROLLING_BACK == TransactionStatusEnum
				.values()[javax.transaction.Status.STATUS_ROLLING_BACK]);
	}

}
