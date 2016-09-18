package com.choicemaker.cmtblocking;

import static com.choicemaker.cmtblocking.LogUtil.logExtendedException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.logging.Logger;

import com.choicemaker.util.Precondition;
import com.choicemaker.util.StringUtils;

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

	public static final String DEFAULT_MAP_FILE_PREFIX = "SqlIdMap_";
	public static final String DEFAULT_MAP_FILE_SUFFIX = ".txt";

	static void logInfo(Logger log, Configuration config) {
		log.info("configuration repetitionCount = '"
				+ config.getRepetitionCount() + "'");
		log.info("configuration jdbcFileName = '" + config.getJdbcFileName()
				+ "'");
		log.info("configuration blockingFileName = '"
				+ config.getBlockingFileName() + "'");
		log.info("configuration scriptFileName = '" + config.getScriptFileName()
				+ "'");
		log.info("configuration sqlIdMapFile = '" + config.getSqlIdMapFileName()
				+ "'");
	}

	public static CJBS parseArgs(String source, Logger logger, String[] args) {
		return parseArgs(source, logger, args, DEFAULT_MAP_FILE_PREFIX,
				DEFAULT_MAP_FILE_SUFFIX);
	}

	public static CJBS parseArgs(String source, Logger logger, String[] args,
			String defaultMapFilePrefix, String defaultMapFileSuffix) {

		Precondition.assertNonEmptyString(defaultMapFilePrefix);
		Precondition.assertNonEmptyString(defaultMapFileSuffix);

		if (args == null || args.length > 1) {
			String msg = printUsage(source);
			throw new IllegalArgumentException(msg);
		}

		Configuration config = null;
		try {
			String configFileName = args.length == 1 ? args[0] : null;
			config = new Configuration(configFileName);
			logInfo(logger, config);
		} catch (Exception x) {
			logExtendedException(logger, "Unable to construct configuration",
					x);
		}

		JdbcParams jdbcParams = null;
		if (config != null) {
			try {
				jdbcParams = config.getJdbcParams();
				jdbcParams.logInfo();
			} catch (Exception x) {
				logExtendedException(logger, "Unable to get JDBC parameters",
						x);
			}
		}

		BlockingParams blockingParams = null;
		if (config != null) {
			try {
				blockingParams = config.getBlockingParams();
				blockingParams.logInfo();
			} catch (Exception x) {
				logExtendedException(logger,
						"Unable to get Blocking parameters", x);
			}
		}

		Iterator<String> scriptIterator = null;
		if (config != null) {
			try {
				BlockingScript script = config.getBlockingScript();
				script.logInfo();
				scriptIterator = script.getIterator();
			} catch (FileNotFoundException x) {
				logExtendedException(logger, "Unable to get blocking script",
						x);
			}
		}

		File sqlIdMapFile = null;
		if (config != null) {
			String fileName = config.getSqlIdMapFileName();
			if (StringUtils.nonEmptyString(fileName)) {
				sqlIdMapFile = new File(fileName);
			}
		}

		CJBS retVal =
			new CJBS(config, jdbcParams, blockingParams, scriptIterator,
					sqlIdMapFile, defaultMapFilePrefix, defaultMapFileSuffix);
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

	public final Configuration config;
	public final JdbcParams jdbcParams;
	public final BlockingParams blockingParams;
	public final Iterator<String> scriptIterator;
	public final File sqlIdMap;

	public CJBS(Configuration config, JdbcParams jdbcParams,
			BlockingParams blockingParams, Iterator<String> scriptIterator) {
		this(config, jdbcParams, blockingParams, scriptIterator, null,
				DEFAULT_MAP_FILE_PREFIX, DEFAULT_MAP_FILE_SUFFIX);
	}

	public CJBS(Configuration config, JdbcParams jdbcParams,
			BlockingParams blockingParams, Iterator<String> scriptIterator,
			String defaultMapFilePrefix, String defaultMapFileSuffix) {
		this(config, jdbcParams, blockingParams, scriptIterator, null,
				defaultMapFilePrefix, defaultMapFileSuffix);
	}

	public CJBS(Configuration config, JdbcParams jdbcParams,
			BlockingParams blockingParams, Iterator<String> scriptIterator,
			File mapFile) {
		this(config, jdbcParams, blockingParams, scriptIterator, mapFile, null,
				null);
	}

	protected CJBS(Configuration config, JdbcParams jdbcParams,
			BlockingParams blockingParams, Iterator<String> scriptIterator,
			File mapFile, String defaultMapFilePrefix,
			String defaultMapFileSuffix) {
		Precondition.assertBoolean(mapFile != null
				|| (StringUtils.nonEmptyString(defaultMapFilePrefix)
						&& StringUtils.nonEmptyString(defaultMapFileSuffix)));
		this.config = config;
		this.jdbcParams = jdbcParams;
		this.blockingParams = blockingParams;
		this.scriptIterator = scriptIterator;
		if (mapFile == null) {
			try {
				mapFile = File.createTempFile(defaultMapFilePrefix,
						defaultMapFileSuffix);
			} catch (IOException e) {
				throw new Error("Unexpected: " + e.toString());
			}
		}
		assert mapFile != null;
		this.sqlIdMap = mapFile;
	}

}