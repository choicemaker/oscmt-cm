package com.choicemaker.util.jee;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.choicemaker.util.Precondition;

public class LogTransactionStatus {
	
	public static final String MSG0 = "Transaction status is %s";
	public static final String MSG1 = "Invalid transaction status: %d";
	public static final String MSG2 = "Unexpected transaction status: %s (expected %s)";
	
	public static void logTransactionStatus(Logger logger, Level level, int status) {
		Precondition.assertNonNullArgument(logger);
		Precondition.assertNonNullArgument(level);
		try {
			TransactionStatusEnum tse = TransactionStatusEnum.values()[status];
			String msg = String.format(MSG0, tse);
			logger.log(level, msg);
		} catch(ArrayIndexOutOfBoundsException x) {
			String msg = String.format(MSG1, status);
			logger.severe(msg);
		}
	}

	public static void logTransactionStatus(Logger logger, int status, TransactionStatusEnum expected) {
		logTransactionStatus(logger, Level.FINE, status, expected);
	}

	public static void logTransactionStatus(Logger logger, Level level, int status, TransactionStatusEnum expected) {
		Precondition.assertNonNullArgument(logger);
		Precondition.assertNonNullArgument(level);
		Precondition.assertNonNullArgument(expected);
		try {
			TransactionStatusEnum tse = TransactionStatusEnum.values()[status];
			if (tse == expected) {
				String msg = String.format(MSG0, tse);
				logger.log(level, msg);
			} else {
				String msg = String.format(MSG2, tse, expected);
				logger.warning(msg);
			}
		} catch(ArrayIndexOutOfBoundsException x) {
			String msg = String.format(MSG1, status);
			logger.severe(msg);
		}
	}

	private LogTransactionStatus() {}
}
