/*
 * Copyright (c) 2001, 2019 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.core.util;

import java.io.NotSerializableException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.choicemaker.cm.core.ISerializableRecordSource;
import com.choicemaker.cm.core.RecordSource;
import com.choicemaker.cm.core.base.AbstractRecordSourceSerializer;

/**
 * @author rphall
 */
public class DoNothingRecordSourceSerializer
	extends AbstractRecordSourceSerializer {

	private static final long serialVersionUID = 1L;
	private static Logger logger =
		Logger.getLogger(DoNothingRecordSourceSerializer.class.getName());
		
	@Override
	public Properties getProperties() {
		return new Properties();
	}
	
	@Override
	public void setProperties(Properties p) {
		if (p != null) {
			logger.fine("Ignoring properties: [" + p.toString() + "]");
		}
	}

	public static void logIgnored(
		Pattern uriPattern,
		Class[] handledClasses,
		Properties properties) {
		logIgnoredPattern(logger, Level.FINE, uriPattern);
		logIgnoredClasses(logger, Level.FINE, handledClasses);
		logIgnoredProperties(logger, Level.FINE, properties);
	}

	/**
	 * @param uriPattern
	 * @param handledClasses
	 * @param properties
	 */
	public DoNothingRecordSourceSerializer(
		Pattern uriPattern,
		Class[] handledClasses,
		Properties properties) {
		super(null, (Class[]) null, null);
		logIgnored(uriPattern, handledClasses, properties);
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.base.IRecordSourceSerializer#getSerializableRecordSource(java.lang.String)
	 */
	@Override
	public ISerializableRecordSource getSerializableRecordSource(Properties properties)
		throws NotSerializableException {
		String msg =
			AbstractRecordSourceSerializer.msgCanNotSerializeProperties(
				properties);
		logger.severe(msg);
		throw new NotSerializableException(msg);
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.base.IRecordSourceSerializer#getSerializableRecordSource(com.choicemaker.cm.core.base.RecordSource)
	 */
	@Override
	public ISerializableRecordSource getSerializableRecordSource(RecordSource rs)
		throws NotSerializableException {
		String msg =
			AbstractRecordSourceSerializer.msgCanNotSerializeInstance(rs);
		logger.severe(msg);
		throw new NotSerializableException(msg);
	}

}
