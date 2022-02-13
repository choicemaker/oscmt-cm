/*******************************************************************************
 * Copyright (c) 2003, 2018 ChoiceMaker LLC and others.
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
 * Created on Aug 26, 2009
 */
package com.choicemaker.cmit.online;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Match;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.io.xml.base.XmlSingleRecordWriter;

public class Query {

	private final List<QueryError> errors = new ArrayList<>();
	private final List<Match> matches = new ArrayList<>();
	private final QueryMetrics queryMetrics;
	private final QueryParams queryParams;
	private Record queryRecord;

	public Query(QueryParams queryParams, QueryMetrics queryMetrics) {
		this(queryParams, null, /* queryRecord */
		null, /* arMatch */
		null, /* arError */
		queryMetrics);
	}

	/** @see #isConsistent() */
	public Query(
		QueryParams queryParams,
		Record queryRecord,
		Match[] arMatch,
		QueryError[] arError,
		QueryMetrics queryMetrics) {
		this.queryParams = queryParams;
		this.queryRecord = queryRecord;
		addMatches(arMatch);
		addErrors(arError);
		this.queryMetrics =
			queryMetrics == null ? new QueryMetrics() : queryMetrics;

		if (!this.isConsistent()) {
			throw new IllegalArgumentException("inconsistent set of constructor parameters");
		}
	}
	
	/** @see #isConsistent() */
	public boolean addError(QueryError e) {
		boolean retVal = false;
		if (e != null) {
			retVal = this.errors.add(e);
		}
		if (!this.isConsistent()) {
			throw new IllegalStateException("inconsistent set of query data");
		}
		return retVal;
	}

	/** @see #isConsistent() */
	public boolean addErrors(QueryError[] arError) {
		boolean retVal = false;
		if (arError != null && arError.length > 0) {
			List<QueryError> list = Arrays.asList(arError);
			retVal = this.errors.addAll(list);
		}
		if (!this.isConsistent()) {
			throw new IllegalStateException("inconsistent set of query data");
		}
		return retVal;
	}

	/** @see #isConsistent() */
	public boolean addMatch(Match m) {
		boolean retVal = false;
		if (m != null) {
			retVal = this.matches.add(m);
		}
		if (!this.isConsistent()) {
			throw new IllegalStateException("inconsistent set of query data");
		}
		return retVal;
	}

	/** @see #isConsistent() */
	public boolean addMatches(Match[] arMatch) {
		boolean retVal = false;
		if (arMatch != null && arMatch.length > 0) {
			List<Match> list = Arrays.asList(arMatch);
			retVal = this.matches.addAll(list);
		}
		if (!this.isConsistent()) {
			throw new IllegalStateException("inconsistent set of query data");
		}
		return retVal;
	}

	public QueryError[] getErrors() {
		return (QueryError[]) this.errors.toArray(new QueryError[this.errors.size()]);
	}

	public Match[] getMatches() {
		return (Match[]) this.matches.toArray(new Match[this.matches.size()]);
	}

	public QueryParams getQueryParams() {
		return this.queryParams;
	}

	public Record getQueryRecord() {
		return this.queryRecord;
	}

	/**
	 * A consistent query either:<ul>
	 * <li/> does not have the query record set nor any errors
	 * nor any matches set; or
	 * <li/> has the query record set and possibly some matches
	 * set, but not any errors, or
	 * <li/> has the query record set and some errors, but
	 * not any matches.
	 * </ul>
	 * This invariant is checked each time the query record, error
	 * field or matches collection is modified.
	 */
	public boolean isConsistent() {
		// Check if nothing has been set
		boolean nothingSet =
			this.getQueryRecord() == null
				&& this.getMatches().length == 0
				&& this.getErrors().length == 0;

		// Check if the result fields are set consistently
		boolean consistentResults =
			getErrors().length == 0 || getMatches().length == 0;

		// Check that If the queryRecord is set, then the result fields are also set consistently
		boolean consistentSet = getQueryRecord() != null && consistentResults;

		// Either nothing has been set, or things have been set consistently
		boolean retVal = nothingSet || consistentSet;
		return retVal;
	}

	/**
	 * A query that has the query record set and the error field unset.
	 * (The matches collections can be empty or not)
	 * @return
	 */
	public boolean isSuccessful() {
		// assert isConsistent()
		return getQueryRecord() != null && getErrors().length == 0;
	}

	/** @see #isConsistent() */
	public void setQueryRecord(Record r) {
		this.queryRecord = r;
		if (!this.isConsistent()) {
			throw new IllegalStateException("inconsistent set of query data");
		}
	}

	static void dumpDebugInfo(
		PrintWriter pw,
		String tag,
		ImmutableProbabilityModel model,
		Record record) {
		final boolean withXmlHeader = false;
		if (record != null) {
			pw.println(tag + " record: " + record.getId());
			if (model != null && !(record instanceof RecordStub) ) {
				String details =
					XmlSingleRecordWriter.writeRecord(model, record, withXmlHeader);
				pw.println(details);
			}
		} else if (record instanceof RecordStub) {
			pw.println(tag + " record: " + record.getId());
		}
	}

	static void dumpDebugInfo(
		PrintWriter pw,
		String tag,
		ImmutableProbabilityModel model,
		Match match) {
		pw.println(tag + " match id: " + match.id);
		pw.println(tag + " match probability: " + match.probability);
		pw.println(tag + " match decision: " + match.decision);
		dumpDebugInfo(pw, tag + " match", model, match.m);
	}

	static void dumpDebugInfo(
		PrintWriter pw,
		ImmutableProbabilityModel model,
		Match[] matches) {
		for (int i = 0; i < matches.length; i++) {
			String tag = "" + i;
			dumpDebugInfo(pw, tag, model, matches[i]);
		}
	}

	static void dumpDebugInfo(
		PrintWriter pw,
		String tag,
		QueryError error) {
		pw.println(tag + " error class: " + error.getClassName());
		pw.println(tag + " error message: " + error.getMessage());
		pw.println(tag + " error rootCause: " + error.getRootCauseClassName());
		pw.println(tag + " error rootRootCauseMessage: " + error.getRootCauseMessage());
	}

	static void dumpDebugInfo(
		PrintWriter pw,
		QueryError[] errors) {
		for (int i = 0; i < errors.length; i++) {
			String tag = "" + i;
			dumpDebugInfo(pw, tag, errors[i]);
		}
	}

	public void dumpDebugInfo(PrintWriter pw) {
		queryParams.dumpDebugInfo(pw);
		queryMetrics.dumpDebugInfo(pw);

		pw.println("consistent: " + this.isConsistent());
		pw.println("successful: " + this.isSuccessful());

		ImmutableProbabilityModel model = queryParams.getModel();
		if (model != null) {
			dumpDebugInfo(pw, "query", model, queryRecord);
			dumpDebugInfo(pw, model, (Match[]) this.matches.toArray(new Match[0]));
		}
		
		dumpDebugInfo(pw, (QueryError[]) this.errors.toArray(new QueryError[0]));
	}

}

/*
 * $Log$
 * Revision 1.2.2.3  2009/11/23 19:24:46  rphall
 * Repaired damage caused by automated removal of CVS Log comments
 *
 * Revision 1.2.2.1  2009/11/19 21:07:37  rphall
 * Synchronized with mci_working archive
 *
 * Revision 1.2.2.1  2009/09/21 18:03:47  rphall
 * Removed URM-specific functionality
 *
 * Revision 1.2  2009/08/31 14:25:43  rphall
 * Added parsing of errors.
 *
 * Revision 1.1  2009/08/28 02:21:54  rphall
 * Initial version
 *
 */
