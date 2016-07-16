/*
 * Created on Aug 31, 2009
 */
package com.choicemaker.cmit.online;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.choicemaker.cm.core.base.Match;
import com.choicemaker.cm.io.xml.base.XmlSingleRecordWriter;

/**
 * @author rphall
 * @version $Revision$ $Date$
 */
public class QueryComparison {

	private static Logger logger = Logger.getLogger(QueryComparison.class
			.getName());

	private final Query query1;
	private final Query query2;

	public Query getQuery1() {
		return query1;
	}

	public Query getQuery2() {
		return query2;
	}

	public QueryComparison(Query q1, Query q2) {
		this.query1 = q1;
		this.query2 = q2;

		if (q1 == null || q2 == null) {
			throw new IllegalArgumentException("null query");
		}

		if (logger.isLoggable(Level.FINE)) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			getQuery1().dumpDebugInfo(pw);
			logger.fine("QUERY1: ");
			logger.fine(sw.toString());

			sw = new StringWriter();
			pw = new PrintWriter(sw);
			getQuery2().dumpDebugInfo(pw);
			logger.fine("QUERY2: ");
			logger.fine(sw.toString());
		}

	}

	public boolean comparableQueries() {
		logger.warning("NOT YET IMPLEMENTED; returning true by default");
		return true;
	}

	public int countQ1Matches() {
		return getQuery1().getMatches().length;
	}

	public int countQ2Matches() {
		return getQuery2().getMatches().length;
	}

	public int countQ1Errors() {
		return getQuery1().getErrors().length;
	}

	public int countQ2Errors() {
		return getQuery2().getErrors().length;
	}

	public boolean haveSameQueryRecords() {
		boolean retVal = false;
		String s1 = queryRecordAsString(getQuery1());
		String s2 = queryRecordAsString(getQuery2());
		if (s1 != null) {
			retVal = s1.equals(s2);
		}
		return retVal;
	}

	public boolean haveSameMatches() {
		// Doesn't work for CORBA, which just returns record id's, not full
		// records
		// // In principle, queries could have the same matches,
		// // but arranged in a different order. This possiblity is
		// // ignored in this implementation.
		// String[] matches1 = matchesAsStrings(getQuery1());
		// String[] matches2 = matchesAsStrings(getQuery2());
		// int count1 = matches1.length;
		// int count2 = matches2.length;
		// boolean retVal = getQuery1().getMatches().length == count1 && count1
		// == count2;
		// for (int i=0; retVal && i<count1; i++) {
		// String match1 = sortMatch(matches1[i]);
		// String match2 = sortMatch(matches2[i]);
		// retVal = match1 != null && match1.equals(match2);
		// }

		Comparable<?>[] matches1 = getMatchIds(getQuery1());
		Comparable<?>[] matches2 = getMatchIds(getQuery2());
		int count1 = matches1.length;
		int count2 = matches2.length;
		boolean retVal =
			getQuery1().getMatches().length == count1 && count1 == count2;
		for (int i = 0; retVal && i < count1; i++) {
			Comparable<?> id1 = matches1[i];
			Comparable<?> id2 = matches2[i];
			retVal = id1 != null && id1.equals(id2);
		}

		return retVal;
	}

	// Unused
	// private String sortMatch(String match) {
		// String retVal = match;
		// if (match != null) {
			// try {
				// StringReader sr = new StringReader(match);
				// LineNumberReader lnr = new LineNumberReader(sr);
				// List lines = new ArrayList();
				// String line = lnr.readLine();
				// while (line != null) {
					// lines.add(line);
					// line = lnr.readLine();
				// }
				// Collections.sort(lines);
				// StringBuffer sb = new StringBuffer();
				// for (Iterator i = lines.iterator(); i.hasNext();) {
					// String l2 = (String) i.next();
					// sb.append(l2);
				// }
				// retVal = sb.toString();
			// } catch (IOException x) {
				// logger.warning("unable to sort '" + match + "'");
			// }
		// }
		// return retVal;
	// }

	public boolean haveSameErrors() {
		// In principle, queries could have the same
		// errors, but arranged in a different order.
		// In practice, queries have either 0 or 1 errors.
		QueryError[] errors1 = getQuery1().getErrors();
		QueryError[] errors2 = getQuery2().getErrors();
		int count1 = errors1.length;
		int count2 = errors2.length;
		boolean retVal = count1 == count2;
		for (int i = 0; retVal && i < count1; i++) {
			retVal = errors1[i] != null && errors1[i].equals(errors2[i]);
		}
		return retVal;
	}

	String queryRecordAsString(Query q) {
		String retVal = null;
		try {
			boolean _doXmlHeader = false;
			retVal =
				XmlSingleRecordWriter.writeRecord(
						q.getQueryParams().getModel(), q.getQueryRecord(),
						_doXmlHeader);
		} catch (Exception x) {
			logger.warning("unable to get query record as string: "
					+ x.toString());
		}
		return retVal;
	}

	String[] getMatchesAsStrings(Query q) {
		final boolean _doXmlHeader = false;
		String[] retVal = null;
		try {
			List<String> list = new ArrayList<>();
			Match[] matches = q.getMatches();
			for (int i = 0; i < matches.length; i++) {
				String record =
					XmlSingleRecordWriter.writeRecord(q.getQueryParams()
							.getModel(), matches[i].m, _doXmlHeader);
				String s =
					"<match probability=\"" + matches[i].probability + "\"";
				s += " decision=\"" + matches[i].decision.toString() + "\">";
				s += "<record>" + record + "</record>";
				list.add(s);
			}
			retVal = (String[]) list.toArray(new String[list.size()]);
		} catch (Exception x) {
			logger.warning("unable to get match records as strings: "
					+ x.toString());
		}
		return retVal;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	Comparable<?>[] getMatchIds(Query q) {
		Comparable<?>[] retVal = null;
		try {
			List<Comparable<?>> list = new ArrayList<>();
			Match[] matches = q.getMatches();
			for (int i = 0; i < matches.length; i++) {
				Comparable<?> id = matches[i].id;
				list.add(id);
			}
			Collections.sort((List) list);
			retVal = (Comparable[]) list.toArray(new Comparable[list.size()]);
		} catch (Exception x) {
			logger.warning("unable to get match record ids: " + x.toString());
		}
		return retVal;
	}

}
