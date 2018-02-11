/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.api;

import java.rmi.RemoteException;
import java.util.Date;

import javax.ejb.EJBObject;

/**
 * Represents a ChoiceMaker serialization job?
 */
public interface CmsJob {

	// CMP fields
	/** For CMP only */
	void setId(Long id) throws RemoteException;
	Long getId() throws RemoteException;

	/** For CMP only */
	void setTransactionId(Long transactionId) throws RemoteException;
	Long getTransactionId() throws RemoteException;	

	/** For CMP only */
	void setExternalId(String externalId) throws RemoteException;
	String getExternalId() throws RemoteException;

	/** For CMP only; */
	void setStatus(String status) throws RemoteException;
	String getStatus() throws RemoteException;

	void setErrorDescription(String error)throws RemoteException;
	String getErrorDescription()throws RemoteException;

	void setStartDate(Date d)throws RemoteException;
	Date getStartDate()throws RemoteException;

	void setFinishDate(Date completed) throws RemoteException;
	Date getFinishDate() throws RemoteException;

	void setAbortRequestDate(Date abortRequested) throws RemoteException;
	Date getAbortRequestDate() throws RemoteException;
	
	void setFractionComplete(int i) throws RemoteException;
	int getFractionComplete() throws RemoteException;

// Business methods

	void markAsStarted() throws RemoteException;
	void markAsCompleted() throws RemoteException;
	void markAsFailed() throws RemoteException;
	void markAsAbortRequested() throws RemoteException;
	void markAsAborted() throws RemoteException;

	boolean isAbortRequested() throws RemoteException;
	
	void updateStepInfo(int fractionComplete) throws RemoteException;
	
}


