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

import com.choicemaker.cm.urm.BatchRecordMatcher;

/**
 * @author emoussikaev
 * @version Revision: 2.5  Date: Jul 7, 2005 9:25:22 AM
 */
public interface BatchRecordMatcherHome extends EJBHome {
  String DEFAULT_EJB_REF_NAME = "ejb/BatchRecordMatcher";
  String DEFAULT_JNDI_COMP_NAME = "java:comp/env/" + DEFAULT_EJB_REF_NAME ;
	BatchRecordMatcher create() throws CreateException, RemoteException;
}
