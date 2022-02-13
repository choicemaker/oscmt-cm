/*******************************************************************************
 * Copyright (c) 2003, 2018 ChoiceMaker LLC and others.
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
package com.choicemaker.cm.batch.ejb;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.choicemaker.cm.batch.api.BatchJobStatus;
import com.choicemaker.util.Precondition;

public class BatchJobUtils {

	private static final Logger logger =
		Logger.getLogger(BatchJobUtils.class.getName());

	public static void updateBatchJobStatusFromTo(final BatchJobEntity batchJob,
			final BatchJobStatus current, final BatchJobStatus next) {
		updateBatchJobStatusFromTo(batchJob, current, next, 0);
	}

	public static final int MAX_RETRY = 5;

	private static void updateBatchJobStatusFromTo(
			final BatchJobEntity batchJob, final BatchJobStatus current,
			final BatchJobStatus next, final int count) {
		Precondition.assertNonNullArgument("null batch job", batchJob);
		Precondition.assertNonNullArgument("null current status", current);
		Precondition.assertNonNullArgument("null next status", next);
		Precondition.assertBoolean("negative count: " + count, count >= 0);

		final String METHOD_PREFIX =
			"BatchJobUtils.updateBatchJobStatusFromTo: ";

		logger.fine(METHOD_PREFIX + "BatchJob: " + batchJob);
		logger.fine(METHOD_PREFIX + "current: " + current);
		logger.fine(METHOD_PREFIX + "next: " + next);
		logger.fine(METHOD_PREFIX + "count: " + count);

		if (count >= MAX_RETRY) {
			String msg = "Unable to update status (BatchJob " + batchJob + "): "
					+ current + " ==> " + next + " (attempts: " + count + ")";
			logger.warning(msg);
			return;
		}

		if (!BatchJobEntity.isAllowedTransition(current, next)) {
			if (logger.isLoggable(Level.FINE)) {
				String msg = "Invalid transition (BatchJob " + batchJob + "): "
						+ current + " ==> " + next;
				logger.fine(msg);
			}
		} else {
			if (logger.isLoggable(Level.FINE)) {
				String msg = "Attempting transition (BatchJob " + batchJob
						+ "): " + current + " ==> " + next + " (attempts: "
						+ count + ")";
				logger.fine(msg);
			}
			try {
				switch (next) {
				case NEW:
					logger.warning("Weird transition (BatchJob " + batchJob
							+ "): * ==> NEW");
				case QUEUED:
				case PROCESSING:
				case ABORT_REQUESTED:
				case ABORTED:
				case FAILED:
				case COMPLETED:
					batchJob.setStatusInternal(next);
					break;
				default:
					String msg = "unexpected status (BatchJob " + batchJob
							+ "): " + next;
					logger.severe(msg);
					new Error(msg);
				}
			} catch (org.eclipse.persistence.exceptions.OptimisticLockException
					| javax.persistence.OptimisticLockException x) {
				logger.fine("optimistic lock exception (BatchJob " + batchJob
						+ "): " + x.toString());
				BatchJobStatus existing = batchJob.getStatus();
				if (current != existing) {
					int newCount = count + 1;
					String msg = "Retry: update status (BatchJob " + batchJob
							+ "): " + existing + " ==> " + next + " (attempts: "
							+ newCount + ")";
					logger.info(msg);
					updateBatchJobStatusFromTo(batchJob, existing, next,
							newCount);
				} else {
					String msg = "Unable to update status (BatchJob " + batchJob
							+ "): " + current + " ==> " + next;
					logger.warning(msg);
				}
			}

			if (logger.isLoggable(Level.FINE)) {
				String msg = "Accomplished transition (BatchJob " + batchJob
						+ "): " + current + " ==> " + next;
				logger.fine(msg);
			}
		}
	}

	private BatchJobUtils() {
	}
}
