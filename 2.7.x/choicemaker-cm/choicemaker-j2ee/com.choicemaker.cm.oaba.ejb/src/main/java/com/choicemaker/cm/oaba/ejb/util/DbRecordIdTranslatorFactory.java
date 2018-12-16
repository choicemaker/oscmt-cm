package com.choicemaker.cm.oaba.ejb.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.choicemaker.cm.aba.DatabaseAccessor;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.oaba.api.MutableRecordIdTranslatorLocal;
import com.choicemaker.cm.oaba.core.IRecordIdSink;
import com.choicemaker.cm.oaba.core.IRecordIdSinkSourceFactory;
import com.choicemaker.cm.oaba.ejb.DefaultRecordIdTranslator;

public class DbRecordIdTranslatorFactory {

	/**
	 * Name of the system property that specifies the type of record id
	 * translator to use OABA and Transitivity processing.
	 */
	public static final String PN_RECORD_ID_TRANSLATOR =
		"com.choicemaker.cm.oaba.recordIdTranslatorType";

	public final static String DEFAULT = "default";
	public final static String ORACLE = "oracle";
	public final static String SQLSERVER = "sqlserver";
	public final static String POSTGRES = "postgres";

	private static enum KNOWN_TRANSLATOR_TYPE {
		DEFAULT_TYPE, ORACLE_TYPE, SQLSERVER_TYPE, POSTGRES_TYPE
	};

	private static Map<String, KNOWN_TRANSLATOR_TYPE> mapSymbol;
	static {
		Map<String, KNOWN_TRANSLATOR_TYPE> m = new HashMap<>();
		m.put(DEFAULT, KNOWN_TRANSLATOR_TYPE.DEFAULT_TYPE);
		m.put(SQLSERVER, KNOWN_TRANSLATOR_TYPE.SQLSERVER_TYPE);
		m.put(ORACLE, KNOWN_TRANSLATOR_TYPE.ORACLE_TYPE);
		m.put(POSTGRES, KNOWN_TRANSLATOR_TYPE.POSTGRES_TYPE);
		mapSymbol = Collections.unmodifiableMap(m);
	}

	private static Map<String, KNOWN_TRANSLATOR_TYPE> mapAccessor;
	static {
		Map<String, KNOWN_TRANSLATOR_TYPE> m = new HashMap<>();
		m.put("SqlDatabaseAccessor", KNOWN_TRANSLATOR_TYPE.SQLSERVER_TYPE);
		m.put("OraDatabaseAccessor", KNOWN_TRANSLATOR_TYPE.ORACLE_TYPE);
		m.put("PostgresDatabaseAccessor", KNOWN_TRANSLATOR_TYPE.POSTGRES_TYPE);
		mapAccessor = Collections.unmodifiableMap(m);
	}

	/**
	 * Creates a record-id translator appropriate for the specified database
	 * accessor unless overridden by the system property
	 * {@link #PN_RECORD_ID_TRANSLATOR}.
	 * 
	 * @param accessor
	 *            a non-null accessor
	 * @return may be null if there is no registered translator appropriate for
	 *         the specified accessor.
	 */
	public static MutableRecordIdTranslatorLocal<?> createDatabaseIdTranslator(
			DatabaseAccessor<?> accessor, BatchJob job,
			IRecordIdSinkSourceFactory factory, IRecordIdSink s1,
			IRecordIdSink s2, boolean doKeepFiles) throws BlockingException {

		KNOWN_TRANSLATOR_TYPE type = null;

		String pv = System.getProperty(PN_RECORD_ID_TRANSLATOR);
		if (pv != null) {
			type = mapSymbol.get(pv.toLowerCase());
		}

		if (type == null && accessor != null) {
			String simpleName = accessor.getClass().getSimpleName();
			type = mapAccessor.get(simpleName);
		}

		if (type == null) {
			type = KNOWN_TRANSLATOR_TYPE.DEFAULT_TYPE;
		}

		MutableRecordIdTranslatorLocal<?> retVal;
		switch (type) {
		case SQLSERVER_TYPE:
//			retVal = new SqlServerRecordIdTranslator(job, factory, s1, s2,
//					doKeepFiles);
//			break;
		case ORACLE_TYPE:
		case POSTGRES_TYPE:
		case DEFAULT_TYPE:
		default:
			retVal = new DefaultRecordIdTranslator(job, factory, s1, s2,
					doKeepFiles);
		}

		return retVal;
	}

}
