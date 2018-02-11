/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.api;

import java.rmi.RemoteException;
import java.util.Collection;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;
import javax.ejb.FinderException;

/**
 * Represents a ChoiceMaker serialization job
 */
@SuppressWarnings({"rawtypes"})
public interface CmsJobHome {
	String DEFAULT_EJB_REF_NAME = "ejb/UrmSerializationJob";
	String DEFAULT_JNDI_COMP_NAME = "java:comp/env/" + DEFAULT_EJB_REF_NAME;

	String AUTONUMBER_IDENTIFIER = "UrmSerializationID";

	CmsJob create(String externalId, long transId)
		throws RemoteException, CreateException;

	Collection findAll() throws RemoteException, FinderException;

	CmsJob findByPrimaryKey(Long id) throws RemoteException, FinderException;
}
