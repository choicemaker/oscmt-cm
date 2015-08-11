/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.e2.mbd;

import com.choicemaker.e2.E2Exception;
import com.choicemaker.e2.mbd.runtime.CoreException;
import com.choicemaker.e2.mbd.runtime.IStatus;
/**
 * A checked exception representing a failure.
 * <p>
 * Core exceptions contain a status object describing the 
 * cause of the exception.
 * </p>
 *
 * @see IStatus
 */
public class CoreExceptionAdapter {

	public static E2Exception convert(CoreException x) {
		return new E2Exception(StatusAdapter.convert(x.getStatus()));
	}
	
	public static CoreException convert(E2Exception x) {
		return new CoreException(StatusAdapter.convert(x.getStatus()));
	}
	
	private CoreExceptionAdapter() {
	}

}
