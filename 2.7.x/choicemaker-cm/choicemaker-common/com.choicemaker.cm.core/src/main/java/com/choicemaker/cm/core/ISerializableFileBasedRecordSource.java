/*******************************************************************************
 * Copyright (c) 2015, 2016 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.core;



/**
 * A file-based RecordSource that can be serialized, such as a
 * record-source descriptor or an XML or Flatfile record source.
 * Implementors should define a nullity constructor and a one-parameter
 * constructor that takes a Properties object. Most serializable,
 * file-based record sources require at least 2 properties to be
 * configured before they are used:<ul>
 * <li/>{@link #PN_MODEL_NAME modelName}
 * <li/>{@link #PN_DATA_FILE_NAME sqlQuery}</ul>
 * The parameterized constructor for a subclass should throw
 * a IncompleteSpecificationException if the specified set of
 * properties is incomplete:<pre>
 * public SomeSubClass implements ISerializableRecordSource {
 *     public SomeSubClass() {}
 *     public SomeSubClass(Properties p)
 *         throws IncompleteSpecificationException {
 *         // ... ctor method should check properties for completeness
 *     }
 *     // ... other methods
 * }
 * </pre>
 * @author rphall
 *
 */
public interface ISerializableFileBasedRecordSource extends ISerializableRecordSource {
	
	/**
	 * The name of the property that specifies a record-source descriptor file,
	 * <code>descriptorFileName</code>.
	 */
	public static final String PN_DESCRIPTOR_FILE_NAME = "descriptorFileName";
	
	/**
	 * The name of the property that specifies a record-source data file,
	 * <code>dataFileName</code>.
	 */
	public static final String PN_DATA_FILE_NAME = "dataFileName";
	
}
