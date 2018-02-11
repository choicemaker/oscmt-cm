/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.api;

import java.rmi.RemoteException;

import javax.ejb.EJBObject;

public interface UrmStepJob {

	// CMP fields
	void setId(Long id) throws RemoteException;
	Long getId() throws RemoteException;
	
	void setStepIndex(Long stepIndex) throws RemoteException;
	Long getStepIndex() throws RemoteException;	

	void setStepJobId(Long stepJobId) throws RemoteException;
	Long getStepJobId() throws RemoteException;	
	
	void setUrmJobId(Long urmJobId) throws RemoteException;
	Long getUrmJobId() throws RemoteException;
	
}
