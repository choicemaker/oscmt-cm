/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.ejb.util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Comment
 *
 * @author Martin Buechi
 */
public class NameServiceLookup {
	private transient Context initialContext;

	private synchronized void initContext() throws NamingException {
		if (initialContext == null) {
			initialContext = new InitialContext();
		}
	}

	public synchronized Object lookup(String name, Class<?> clazz)
			throws NamingException {
		initContext();
		Object objref = initialContext.lookup(name);
		return objref;
	}

	// Not used
	// public synchronized Object lookupRemote(String name, Class<?> clazz)
	// throws NamingException {
	// initContext();
	// Object objref = initialContext.lookup(name);
	// if (objref != null) {
	// System.err.println("Class: " + objref.getClass());
	// return PortableRemoteObject.narrow(name, clazz);
	// } else {
	// return null;
	// }
	// }

}
