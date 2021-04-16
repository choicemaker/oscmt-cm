/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.ejb;

public interface DefaultServerConfigurationJPA {

	String TABLE_NAME = "CMT_DEFAULT_SERVER_CONFIG";

	String CN_SERVERCONFIG = "SERVER_CONFIG";

	String CN_HOSTNAME = "HOST_NAME";

	String QN_DSC_FIND_ALL = "defaultServerConfigFindAll";

	String JPQL_DSC_FIND_ALL =
		"Select dscb from DefaultServerConfigurationEntity dscb";

}
