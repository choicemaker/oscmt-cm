package com.choicemaker.cmtblocking;

import static com.choicemaker.cmtblocking.LogUtil.logExtendedException;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;

import com.choicemaker.util.Precondition;

/**
 * Result of parsing a configuration file:
 * <ul>
 * <li>Configuration</li>
 * <li>Jdbc parameters</li>
 * <li>Blocking parameters</li>
 * <li>Script iterator</li>
 * </ul>
 * Any of these object may be null.
 * 
 * @author rphall
 *
 */
public class CJBS {
	public final Configuration config;
	public final JdbcParams jdbcParams;
	public final BlockingParams blockingParams;
	public final Iterator<String> scriptIterator;

	public CJBS(Configuration config, JdbcParams jdbcParams,
			BlockingParams blockingParams,
			Iterator<String> scriptIterator) {
		this.config = config;
		this.jdbcParams = jdbcParams;
		this.blockingParams = blockingParams;
		this.scriptIterator = scriptIterator;
	}

	public static CJBS parseArgs(String source, String[] args) {
		if (args == null || args.length > 1) {
			String msg = printUsage(source);
			throw new IllegalArgumentException(msg);
		}

		Configuration config = null;
		try {
			String configFileName = args.length == 1 ? args[0] : null;
			config = new Configuration(configFileName);
			config.logInfo();
		} catch (Exception x) {
			logExtendedException(source, "Unable to construct configuration", x);
		}

		JdbcParams jdbcParams = null;
		if (config != null) {
			try {
				jdbcParams = config.getJdbcParams();
				jdbcParams.logInfo();
			} catch (Exception x) {
				logExtendedException(source, "Unable to get JDBC parameters", x);
			}
		}

		BlockingParams blockingParams = null;
		if (config != null) {
			try {
				blockingParams = config.getBlockingParams();
				blockingParams.logInfo();
			} catch (Exception x) {
				logExtendedException(source, "Unable to get Blocking parameters", x);
			}
		}

		Iterator<String> scriptIterator = null;
		if (config != null) {
			try {
				BlockingScript script = config.getBlockingScript();
				script.logInfo();
				scriptIterator = script.getIterator();
			} catch (FileNotFoundException x) {
				logExtendedException(source, "Unable to get blocking script", x);
			}
		}

		CJBS retVal =
			new CJBS(config, jdbcParams, blockingParams, scriptIterator);
		return retVal;
	}

	/**
	 * Prints the following usage message:
	 * 
	 * <pre>
		* Usage: java com.choicemaker.cmtblocking.Main [<configFile>]
		*   where [<configFile>] is an optional properties file
		*   that specifies the name of a jdbcProperties file
		*   and the name of a blockingScript file
	 * </pre>
	 * 
	 * @see Configuration#PN_JDBC_PROPERTIES
	 * @see Configuration#PN_SCRIPT_FILE
	 * @return a usage message
	 */
	static String printUsage(String className) {
		Precondition.assertNonEmptyString("null or blank class name",
				className);
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.println("Usage: java " + Main.class.getName() + " [<configFile>]");
		pw.println("  where [<configFile>] is an optional properties file");
		pw.println("  that specifies the name of a jdbcProperties file");
		pw.println("  and the name of a blockingScript file");
		return sw.toString();
	}
}