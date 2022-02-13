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
 * Created on Aug 27, 2009
 */
package com.choicemaker.cmit.online;

import java.io.PrintWriter;
import java.util.Date;

/**
 * A struct to hold metrics parsed from a query entry
 * in a ChoiceMaker report. The parameters held by this struct are
 * restricted to ones that characterize the response from a URM
 * findMatches or findCompositeMatches operation.
 * @author rphall
 * @version $Revision$ $Date$
 */
public class QueryMetrics {

	public static long UNKNOWN_DURATION = -1;
	public static int UNKNOWN_NUMBER_BLOCKED = -1;

	private long duration = UNKNOWN_DURATION;
	private int numberBlocked = UNKNOWN_NUMBER_BLOCKED;
	private Date timestamp;

	public QueryMetrics() {
	}

//	public QueryMetrics(Date timestamp, long duration, int numberBlocked) {
//		setTimestamp(timestamp);
//		setDuration(duration);
//		setNumberBlocked(numberBlocked);
//	}

	public long getDuration() {
		return duration;
	}

	public int getNumberBlocked() {
		return numberBlocked;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public void setNumberBlocked(int numberBlocked) {
		this.numberBlocked = numberBlocked;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	public void dumpDebugInfo(PrintWriter pw) {
		pw.println("duration: " + duration);
		pw.println("numberBlocked: " + numberBlocked);
		pw.println("timestamp: " + timestamp);
	}

}

