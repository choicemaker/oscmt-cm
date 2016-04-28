/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.home;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;

import com.choicemaker.cm.urm.OnlineRecordMatcher;
//import javax.naming.NamingException;

/**
 * @author emoussikaev
 * @see
 */
public interface OnlineRecordMatcherHome extends EJBHome {
  String DEFAULT_EJB_REF_NAME = "ejb/OnlineRecordMatcher";
  String DEFAULT_JNDI_COMP_NAME = "java:comp/env/" + DEFAULT_EJB_REF_NAME ;
	OnlineRecordMatcher create() throws CreateException, RemoteException;
}
