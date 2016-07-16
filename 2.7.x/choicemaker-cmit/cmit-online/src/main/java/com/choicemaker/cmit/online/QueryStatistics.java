/*
 * Created on Aug 31, 2009
 */
package com.choicemaker.cmit.online;

/**
 * @author rphall
 * @version $Revision$ $Date$
 */
public class QueryStatistics {
	
	private int countComparisons;
	private int countSameQueryRecords;
	private int countBothSuccessful;
	private int countBothSuccessfulAndSameMatches;
	private int countBothUnsuccessful;
	private int countBothUnsuccessfulAndSameErrors;
	
	public void add(QueryComparison qc) {
		if (qc != null) {
			setCountComparisons(getCountComparisons() + 1);
			if (qc.haveSameQueryRecords()) {
				setCountSameQueryRecords(getCountSameQueryRecords() + 1);
			}
			boolean bothSuccessful = qc.getQuery1().isSuccessful() && qc.getQuery2().isSuccessful();
			boolean bothUnsuccessful = !qc.getQuery1().isSuccessful() && !qc.getQuery2().isSuccessful();
			if (bothSuccessful) {
				setCountBothSuccessful(getCountBothSuccessful() + 1);
				if (qc.haveSameMatches()) {
					setCountBothSuccessfulAndSameMatches(getCountBothSuccessfulAndSameMatches() + 1);
				}
			} else if (bothUnsuccessful) {
				setCountBothUnsuccessful(getCountBothUnsuccessful() + 1);
				if (qc.haveSameErrors()) {
					setCountBothUnsuccessfulAndSameErrors(getCountBothUnsuccessfulAndSameErrors() + 1);
				}
			}
		}
	}

	private void setCountComparisons(int countComparisons) {
		this.countComparisons = countComparisons;
	}

	public int getCountComparisons() {
		return countComparisons;
	}

	private void setCountSameQueryRecords(int countSameQueryRecords) {
		this.countSameQueryRecords = countSameQueryRecords;
	}

	public int getCountSameQueryRecords() {
		return countSameQueryRecords;
	}

	private void setCountBothSuccessful(int countBothSuccessful) {
		this.countBothSuccessful = countBothSuccessful;
	}

	public int getCountBothSuccessful() {
		return countBothSuccessful;
	}

	private void setCountBothSuccessfulAndSameMatches(int countBothSuccessfulAndSameMatches) {
		this.countBothSuccessfulAndSameMatches =
			countBothSuccessfulAndSameMatches;
	}

	public int getCountBothSuccessfulAndSameMatches() {
		return countBothSuccessfulAndSameMatches;
	}

	private void setCountBothUnsuccessful(int countBothUnsuccessful) {
		this.countBothUnsuccessful = countBothUnsuccessful;
	}

	public int getCountBothUnsuccessful() {
		return countBothUnsuccessful;
	}

	private void setCountBothUnsuccessfulAndSameErrors(int countBothUnsuccessfulAndSameErrors) {
		this.countBothUnsuccessfulAndSameErrors =
			countBothUnsuccessfulAndSameErrors;
	}

	public int getCountBothUnsuccessfulAndSameErrors() {
		return countBothUnsuccessfulAndSameErrors;
	}

}

