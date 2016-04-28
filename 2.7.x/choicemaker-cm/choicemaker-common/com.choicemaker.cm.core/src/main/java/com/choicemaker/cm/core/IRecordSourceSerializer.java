/*******************************************************************************
 * Copyright (c) 2015, 2016 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.core;

import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.Properties;

/**
 * @author rphall
 */
public interface IRecordSourceSerializer extends Serializable {
	
	/**
	 * Tests whether this serializer can return a serializable record source
	 * for the specified record source
	 */
	boolean canSerialize(String url);

	/**
	 * Tests whether this serializer can return a serializable record source
	 * for the specified record source
	 */
	boolean canSerialize(RecordSource rs);

	/**
	 * Returns serializable record source for the specified records source.
	 */
	ISerializableRecordSource getSerializableRecordSource(RecordSource rs)
		throws NotSerializableException;

	/**
	 * Returns serializable record source for the specified records source.
	 * @param properties that specify a record source 
	 */
	ISerializableRecordSource getSerializableRecordSource(Properties properties)
		throws NotSerializableException;

	Properties getProperties();
	
	void setProperties(Properties p);

}
