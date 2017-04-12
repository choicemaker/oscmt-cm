/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.db.postgres;

import java.io.NotSerializableException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.choicemaker.cm.core.ISerializableRecordSource;
import com.choicemaker.cm.core.RecordSource;
import com.choicemaker.cm.core.base.AbstractRecordSourceSerializer;
import com.choicemaker.cm.io.db.postgres.PostgresRecordSource;
import com.choicemaker.util.Precondition;

/**
 * @author rphall
 */
public class PostgresRecordSourceSerializer
	extends AbstractRecordSourceSerializer {
	
	private static final long serialVersionUID = 271L;

	private static final Logger logger = Logger.getLogger(PostgresRecordSourceSerializer.class.getName());

	public static final Class[] HANDLED_CLASSES = new Class[] {
		PostgresSerializableRecordSource.class };

	public static final String URI_REGEX = "jdbc:postgres:.*";

	public static final Pattern URI_PATTERN = Pattern.compile(URI_REGEX);

	public PostgresRecordSourceSerializer() {
		super(URI_PATTERN, HANDLED_CLASSES, null);
	}

	public PostgresRecordSourceSerializer(Properties ignored) {
		this();
		logIgnoredProperties(logger, Level.FINE, ignored);
	}

	public Properties getProperties() {
		return new Properties();
	}
	
	public void setProperties(Properties p) {
		if (p != null) {
			logger.fine("Ignoring properties: [" + p.toString() + "]");
		}
	}

	/** Always returns null */
	public String getProperty(String name) {
		logNullPropertyReturned(logger, Level.WARNING, name);
		return null;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.IRecordSourceSerializer#getSerializableRecordSource(java.lang.String)
	 */
	public ISerializableRecordSource getSerializableRecordSource(Properties properties)
		throws NotSerializableException {
		// TODO NOT YET IMPLEMENTED
		throw new RuntimeException("not yet implemented");
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.IRecordSourceSerializer#getSerializableRecordSource(com.choicemaker.cm.core.RecordSource)
	 */
	public ISerializableRecordSource getSerializableRecordSource(RecordSource rs)
		throws NotSerializableException {
		Precondition.assertNonNullArgument("null record source", rs);

		ISerializableRecordSource retVal;
		if (rs instanceof PostgresSerializableRecordSource) {
			// Trivial case
			retVal = (PostgresSerializableRecordSource) rs;
		} else if (rs instanceof PostgresRecordSource) {
			// TODO FIXME handle this case if dsJnidName is known
			// i.e. if it is passed as a constructor property or it is
			// set as a property immediately after construction.
			String msg =
				"Unable to determine dsJndiName: "
					+ msgCanNotSerializeInstance(rs);
			logger.severe(msg);
			throw new NotSerializableException(msg);
		} else {
			String msg = msgCanNotSerializeInstance(rs);
			logger.severe(msg);
			throw new NotSerializableException(msg);
		}
		return retVal;
	}

	/** Does nothing */
	public void setProperty(String name, String value) {
		logIgnoredPropertyChange(logger, Level.WARNING, name, value);
	}

}
