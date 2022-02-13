/*******************************************************************************
 * Copyright (c) 2003, 2016 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc  - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
/*
 * Created on Aug 31, 2009
 */
package com.choicemaker.cmit.online;

/**
 * @author rphall
 * @version $Revision$ $Date$
 */
public class QueryError {

	private final String cls;
	private String msg;
	private String clsRootCause;
	private String msgRootCause;
	
//	public QueryError() {
//		invariant();
//	}

	public QueryError(String className) {
		this.cls = className;
		invariant();
	}
	
	public QueryError(Throwable t) {
		this(t.getClass().getName());
		setMessage(t.getMessage());
		if (t.getCause() != null) {
			setRootCauseClassName(t.getCause().getClass().getName());
			setRootCauseMessage(t.getCause().getMessage());
		}
	}

//	/** Class name of the error */ 
//	private void setClassName(String cls) {
//		this.cls = cls;
//		invariant();
//	}

	/** Class name of the error */ 
	public String getClassName() {
		invariant();
		return cls;
	}

	/** Message reported by the error */
	public void setMessage(String msg) {
		this.msg = msg;
		invariant();
	}

	/** Message reported by the error */
	public String getMessage() {
		invariant();
		return msg;
	}

	/** Class name of the root cause reported by the error (if any) */
	public void setRootCauseClassName(String clsRootCause) {
		this.clsRootCause = clsRootCause;
		invariant();
	}

	/** Class name of the root cause reported by the error (if any) */
	public String getRootCauseClassName() {
		invariant();
		return clsRootCause;
	}

	/** Message reported by the root cause */ 
	public void setRootCauseMessage(String msgRootCause) {
		this.msgRootCause = msgRootCause;
		invariant();
	}

	/** Message reported by the root cause */ 
	public String getRootCauseMessage() {
		invariant();
		return msgRootCause;
	}
	
	/**
	 * Invariant:<ul>
	 * <li/> ClassName can not be null.
	 * <li/> RootCauseMessage can be non-null only if RootCauseClassName is not null.
	 * </ul>
	 * @throws IllegalStateException
	 */
	public void invariant() {
		if (this.cls == null) {
			throw new IllegalArgumentException("null class name");
		}
//		if (this.msg != null && this.cls == null ) {
//			throw new IllegalStateException("Non-null message and null class name");
//		}
//		if (this.clsRootCause != null && this.cls == null) {
//			throw new IllegalStateException("Non-null root cause and null class name");
//		}
		if (this.msgRootCause != null && this.clsRootCause == null) {
			throw new IllegalStateException("Non-null root message and null root cause");
		}
	}
	
	public int hashCode() {
		return this.cls.hashCode();
	}
	
	public boolean equals(Object o) {
		boolean retVal = false;
		if (o instanceof QueryError) {
			QueryError that = (QueryError) o;
			retVal = this.getClassName().equals(that.getClassName());
			if (retVal) {
				boolean t1 = this.getMessage() == null && that.getMessage() == null;
				boolean t2 = this.getMessage() != null && this.getMessage().equals(that.getMessage());
				retVal = retVal && (t1 || t2);
			}
			if (retVal) {
				boolean t1 = this.getRootCauseClassName() == null && that.getRootCauseClassName() == null;
				boolean t2 = this.getRootCauseClassName() != null && this.getMessage().equals(that.getRootCauseClassName());
				retVal = retVal && (t1 || t2);
			}
			if (retVal) {
				boolean t1 = this.getRootCauseMessage() == null && that.getRootCauseMessage() == null;
				boolean t2 = this.getRootCauseMessage() != null && this.getMessage().equals(that.getRootCauseMessage());
				retVal = retVal && (t1 || t2);
			}
		}
		return retVal;
	}

}

