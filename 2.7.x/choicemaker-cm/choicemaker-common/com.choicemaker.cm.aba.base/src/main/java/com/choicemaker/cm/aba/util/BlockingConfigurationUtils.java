package com.choicemaker.cm.aba.util;

import java.util.logging.Logger;

import com.choicemaker.cm.aba.IBlockingConfiguration;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.util.Precondition;

public class BlockingConfigurationUtils {

	private static final Logger logger =
		Logger.getLogger(BlockingConfigurationUtils.class.getName());

	public static final String BLOCKING_SYMBOL = "b";

	public static final String BLOCKING_COMPONENT_SEPARATOR = ":";

	public static String createBlockingConfigurationId(
			ImmutableProbabilityModel m, String bcName, String dbName) {
		Precondition.assertNonNullArgument("null or blank model", m);
		Precondition.assertNonEmptyString(
				"null or blank blocking configuration", bcName);
		Precondition.assertNonEmptyString(
				"null or blank database configuration", dbName);

		String schemaName = m.getAccessor().getSchemaName();
		logger.fine("Model name: " + m.getModelName());
		logger.fine("Model schema: " + schemaName);
		logger.fine("Blocking configuration: " + bcName);
		logger.fine("Database configuration: " + dbName);

		StringBuilder sb = new StringBuilder().append(schemaName)
				.append(BLOCKING_COMPONENT_SEPARATOR).append(BLOCKING_SYMBOL)
				.append(BLOCKING_COMPONENT_SEPARATOR).append(bcName)
				.append(BLOCKING_COMPONENT_SEPARATOR).append(dbName);
		String retVal = sb.toString();
		logger.fine("Blocking configuration ID: " + retVal);
		return retVal;
	}

	/**
	 * If two implementations return the same name, they are evaluated as equal.
	 * If either, but not both, are null, they are evaluated as unequal. If both
	 * are null, they are evaluated as equal.
	 *
	 * @param bc1
	 *            possibly null
	 * @param bc2
	 *            possibly null
	 * @return false if either argument, but not both, is null, otherwise true
	 *         if the names of the configurations match or if both
	 *         configurations are null
	 */
	public static boolean equals(IBlockingConfiguration bc1,
			IBlockingConfiguration bc2) {
		if (bc1 == bc2)
			return true;
		if (bc1 == null && bc2 != null)
			return false;
		if (bc1 != null && bc2 == null)
			return false;
		assert bc1 != null && bc2 != null;
		if (bc1.getBlockingConfiguationId() == null) {
			if (bc2.getBlockingConfiguationId() != null)
				return false;
		} else if (!bc1.getBlockingConfiguationId()
				.equals(bc2.getBlockingConfiguationId()))
			return false;
		return true;
	}

	/**
	 * Computed from the name the specified configuration.
	 *
	 * @param bc
	 *            possibly null
	 * @return 0 if the argument is null, otherwise computed from fields and
	 *         tables of the argument.
	 */
	public static int hashCode(IBlockingConfiguration bc) {
		int result = 0;
		if (bc != null) {
			final int prime = 31;
			result = 1;
			result = prime * result + ((bc.getBlockingConfiguationId() == null)
					? 0 : bc.getBlockingConfiguationId().hashCode());
		}
		return result;
	}

	private BlockingConfigurationUtils() {
	}

}
