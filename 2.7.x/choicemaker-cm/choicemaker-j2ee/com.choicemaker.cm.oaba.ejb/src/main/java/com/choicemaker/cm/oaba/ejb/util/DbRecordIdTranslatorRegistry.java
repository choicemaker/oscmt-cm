package com.choicemaker.cm.oaba.ejb.util;

import com.choicemaker.cm.aba.DatabaseAccessor;
import com.choicemaker.cm.oaba.api.DbRecordIdTranslator;

public class DbRecordIdTranslatorRegistry {

	/**
	 * Looks up a database record-id translator appropriate for the specified
	 * database accessor.
	 * 
	 * @param accessor
	 *            a non-null accessor
	 * @return may be null if there is no registered translator appropriate for
	 *         the specified accessor.
	 */
	public DbRecordIdTranslator lookupDatabaseIdTranslation(
			DatabaseAccessor<?> accessor) {
		return null;
	}

}
