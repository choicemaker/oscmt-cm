/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.api;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;



/**
 * @author emoussikaev
 */
public interface TransSerializerHome {
  String DEFAULT_EJB_REF_NAME = "ejb/TransSerializer";
  String DEFAULT_JNDI_COMP_NAME = "java:comp/env/" + DEFAULT_EJB_REF_NAME ;
	TransSerializer create() throws CreateException, RemoteException;
}
