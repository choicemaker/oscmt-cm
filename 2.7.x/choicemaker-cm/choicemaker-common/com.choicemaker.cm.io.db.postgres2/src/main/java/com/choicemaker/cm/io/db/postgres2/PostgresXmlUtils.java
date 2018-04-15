/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.db.postgres2;

public class PostgresXmlUtils {

	public static final String EN_SQLSERVERCOMPOSITERECORDSOURCE =
		"sqlServerCompositeRecordSource";
	
	public static final String EN_MARKEDRECORDPAIRSOURCE =
			"MarkedRecordPairSource";
		
	public static final String EN_RECORDSOURCE =
			"RecordSource";
		
	public static final String EN_MRPSQUERY =
			"mrpsQuery";

	// XML Attributes

	public static final String AN_RS_CLASS = "class";
	public static final String AN_RS_DATASOURCENAME = "dataSourceName";
	public static final String AN_RS_MODEL = "model";
	public static final String AN_RS_DBCONFIGURATION = "dbConfiguration";
	public static final String AN_RS_IDSQUERY = "idsQuery";
	public static final String AN_RS_MAXCOMPOSITESIZE = "maxCompositeSize";

	public static final String AN_MRPS_CLASS = "class";
	public static final String AN_MRPS_DATASOURCENAME = "connectionName";
//	public static final String AN_MRPS_MODEL = "model";
	public static final String AN_MRPS_DBCONFIGURATION = "conf";
	public static final String AN_MRPS_IDSQUERY = "selection";

	// Deprecated model properties

	/** @deprecated */
	public static final String PN_BLOCKING_CONFIGURATION = "blockingConfiguration";

	/** @deprecated */
	public static final String PN_DB_CONFIGURATION = AN_RS_DBCONFIGURATION;

	/** @deprecated */
	public static final String PN_LIMITPERBLOCKINGSET = "limitPerBlockingSet";

	/** @deprecated */
	public static final String PN_SINGLETABLEBLOCKINGSETGRACELIMIT = "limitSingleBlockingSet";

	/** @deprecated */
	public static final String PN_LIMITSINGLEBLOCKINGSET = "limitPerBlockingSet";

	private PostgresXmlUtils() {
	}

	public static String xmlElementStart(String entityName) {
		String retVal =
			new StringBuilder().append("<").append(entityName).append(" ")
					.toString();
		return retVal;
	}
	
	public static String xmlElementEnd(String entityName) {
		String retVal =
				new StringBuilder().append("</").append(entityName).append(">")
						.toString();
			return retVal;
	}

	public static String xmlElementEndInline() {
		return "/>";
	}

	public static String xmlAttribute(String name, String value) {
		String retVal =
			new StringBuilder().append(name).append("=\"").append(value)
					.append("\" ").toString();
		return retVal;
	}

}
