/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.blocking.automated.offline.server.impl;

import static com.choicemaker.cm.io.blocking.automated.offline.core.RECORD_ID_TYPE.TYPE_INTEGER;
import static com.choicemaker.cm.io.blocking.automated.offline.core.RECORD_ID_TYPE.TYPE_LONG;
import static com.choicemaker.cm.io.blocking.automated.offline.core.RECORD_ID_TYPE.TYPE_STRING;
import static com.choicemaker.cm.io.blocking.automated.offline.server.impl.OabaPairResultJPA.CN_ID;
import static com.choicemaker.cm.io.blocking.automated.offline.server.impl.OabaPairResultJPA.DISCRIMINATOR_COLUMN;
import static com.choicemaker.cm.io.blocking.automated.offline.server.impl.OabaPairResultJPA.DV_ABSTRACT;
import static com.choicemaker.cm.io.blocking.automated.offline.server.impl.OabaPairResultJPA.DV_INTEGER;
import static com.choicemaker.cm.io.blocking.automated.offline.server.impl.OabaPairResultJPA.DV_LONG;
import static com.choicemaker.cm.io.blocking.automated.offline.server.impl.OabaPairResultJPA.DV_STRING;
import static com.choicemaker.cm.io.blocking.automated.offline.server.impl.OabaPairResultJPA.ID_GENERATOR_NAME;
import static com.choicemaker.cm.io.blocking.automated.offline.server.impl.OabaPairResultJPA.ID_GENERATOR_PK_COLUMN_NAME;
import static com.choicemaker.cm.io.blocking.automated.offline.server.impl.OabaPairResultJPA.ID_GENERATOR_PK_COLUMN_VALUE;
import static com.choicemaker.cm.io.blocking.automated.offline.server.impl.OabaPairResultJPA.ID_GENERATOR_TABLE;
import static com.choicemaker.cm.io.blocking.automated.offline.server.impl.OabaPairResultJPA.ID_GENERATOR_VALUE_COLUMN_NAME;
import static com.choicemaker.cm.io.blocking.automated.offline.server.impl.OabaPairResultJPA.JPQL_PAIRRESULT_FIND_ALL;
import static com.choicemaker.cm.io.blocking.automated.offline.server.impl.OabaPairResultJPA.JPQL_PAIRRESULT_FIND_BY_JOBID;
import static com.choicemaker.cm.io.blocking.automated.offline.server.impl.OabaPairResultJPA.QN_PAIRRESULT_FIND_ALL;
import static com.choicemaker.cm.io.blocking.automated.offline.server.impl.OabaPairResultJPA.QN_PAIRRESULT_FIND_BY_JOBID;
import static com.choicemaker.cm.io.blocking.automated.offline.server.impl.OabaPairResultJPA.TABLE_NAME;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import com.choicemaker.cm.args.PersistentObject;
import com.choicemaker.cm.core.Decision;
import com.choicemaker.cm.core.base.RECORD_SOURCE_ROLE;
import com.choicemaker.cm.core.util.MatchUtils;
import com.choicemaker.cm.io.blocking.automated.offline.core.RECORD_ID_TYPE;
import com.choicemaker.cm.io.blocking.automated.offline.server.ejb.OabaPairResult;
import com.choicemaker.util.HashUtils;

/**
 * This is the EJB implementation of the OABA OabaProcessing interface.
 * 
 * @author pcheung
 *
 */
@NamedQueries({
		@NamedQuery(name = QN_PAIRRESULT_FIND_ALL,
				query = JPQL_PAIRRESULT_FIND_ALL),
		@NamedQuery(name = QN_PAIRRESULT_FIND_BY_JOBID,
				query = JPQL_PAIRRESULT_FIND_BY_JOBID) })
@Entity
@Table(/* schema = "CHOICEMAKER", */name = TABLE_NAME)
@DiscriminatorColumn(name = DISCRIMINATOR_COLUMN,
		discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(DV_ABSTRACT)
public abstract class AbstractPairResultEntity<T extends Comparable<T>>
		implements OabaPairResult<T> {

	private static final Logger logger = Logger
			.getLogger(AbstractPairResultEntity.class.getName());

	private static final long serialVersionUID = 271L;

	public static final String INVALID_RECORD_ID = null;
	public static final String INVALID_SHA1 = null;
	public static final char INVALID_RECORD_SOURCE = '\0';
	public static final float INVALID_PROBABILITY = Float.NaN;
	public static final char INVALID_DECISION = '\0';

	/**
	 * Computes a base-64 SHA1 signature for a pair based on all fields
	 * excluding the persistence id, jobId, and pair and equivalence class SHA1
	 * signatures. The signature depends on the field ordering; in this method,
	 * the ordering is:
	 * <ol>
	 * <li>the fully qualified class name of the record id
	 * {@link #getRecordIdType() type}</li>
	 * <li>the {@link #getRecord1Id() first record id}
	 * {@link #idToString(Comparable) as a String}</li>
	 * <li>the {@link #getRecord2Id() second record id}
	 * {@link #idToString(Comparable) as a String}</li>
	 * <li>the {@link #getRecord2Source() source} (as a String) of the second
	 * record</li>
	 * <li>the pair probability</li>
	 * <li>the pair decision</li>
	 * <li>an ordered set formed from the pair {@link #getNotes() notes}</li>
	 * <li>
	 * </ol>
	 * 
	 * @param pair
	 * @return
	 */
	protected static <T extends Comparable<T>> String computePairSHA1(
			char recordType, String record1Id, String record2Id,
			char record2Source, float probability, char decision,
			SortedSet<String> notes) {

		StringBuilder sb = new StringBuilder();
		sb.append(recordType);
		sb.append(record1Id);
		sb.append(record2Id);
		sb.append(record2Source);
		sb.append(probability);
		sb.append(decision);

		for (String note : notes) {
			sb.append(note);
		}

		String retVal = HashUtils.toBase64SHA1Hash(sb.toString(), false);
		return retVal;
	}

	/**
	 * Computes a base-64 SHA1 signature for an array of pairs. This method
	 * converts the array to a sorted set and then concatenates the SHA1
	 * signature of each pair into a single String, from which a base-64 SHA1
	 * signature return value is computed and then returned.
	 */
	public static <T extends Comparable<T>> String computeEquivalenceClassSHA1(
			OabaPairResult<T>[] pairs) {
		if (pairs == null) {
			throw new IllegalArgumentException("null pairs");
		}
		SortedSet<OabaPairResult<T>> sortedPairs =
			new TreeSet<>(new OabaPairResultComparator<T>());
		for (OabaPairResult<T> pair : pairs) {
			if (pair == null) {
				throw new IllegalArgumentException("null pair");
			}
			sortedPairs.add(pair);
		}
		StringBuilder sb = new StringBuilder();
		for (OabaPairResult<T> pair : sortedPairs) {
			if (pair == null) {
				throw new IllegalArgumentException("null pair");
			}
			sb.append(pair.getPairSHA1());
		}
		String retVal = HashUtils.toBase64SHA1Hash(sb.toString(), false);
		return retVal;
	}

	/**
	 * Checks for consistency between the JPA interface and the RECORD_ID_TYPE
	 * interface
	 */
	public static boolean isClassValid() {
		// Counts the number of discriminator values
		int dvCount = 0;

		boolean retVal = DV_ABSTRACT.length() == 1;
		++dvCount;

		retVal = retVal && DV_INTEGER.equals(TYPE_INTEGER.getStringSymbol());
		retVal = retVal && !DV_INTEGER.equals(DV_ABSTRACT);
		++dvCount;

		retVal = retVal && DV_LONG.equals(TYPE_LONG.getStringSymbol());
		retVal = retVal && !DV_LONG.equals(DV_ABSTRACT);
		++dvCount;

		retVal = retVal && DV_STRING.equals(TYPE_STRING.getStringSymbol());
		retVal = retVal && !DV_STRING.equals(DV_ABSTRACT);
		++dvCount;

		// One more discriminator (ABSTRACT) value than enum type
		int ritCount = RECORD_ID_TYPE.values().length;
		retVal = retVal && dvCount == ritCount + 1;

		return retVal;
	}

	static {
		if (!isClassValid()) {
			String msg =
				"AbstractRecordIdTranslationEntity class is inconsistent with "
						+ " RECORD_ID_TYPE enumeration";
			logger.severe(msg);
			// An exception is not thrown here because it would create a class
			// initialization exception, which can be hard to debug
		}
	}

	// -- Instance data

	@Id
	@Column(name = CN_ID)
	@TableGenerator(name = ID_GENERATOR_NAME, table = ID_GENERATOR_TABLE,
			pkColumnName = ID_GENERATOR_PK_COLUMN_NAME,
			valueColumnName = ID_GENERATOR_VALUE_COLUMN_NAME,
			pkColumnValue = ID_GENERATOR_PK_COLUMN_VALUE)
	@GeneratedValue(strategy = GenerationType.TABLE,
			generator = ID_GENERATOR_NAME)
	private long id;

	private final long jobId;
	private final char recordType;
	private final String record1Id;
	private final String record2Id;
	private final char record2Source;
	private final float probability;
	private final char decision;
	private final String notes;
	private final String pairSHA1;
	private final String ecSHA1;

	// -- Construction

	/** Required by JPA; do not invoke directly */
	protected AbstractPairResultEntity() {
		this.jobId = PersistentObject.NONPERSISTENT_ID;
		this.recordType = DV_ABSTRACT.charAt(0);
		this.record1Id = INVALID_RECORD_ID;
		this.record2Id = INVALID_RECORD_ID;
		this.record2Source = INVALID_RECORD_SOURCE;
		this.probability = INVALID_PROBABILITY;
		this.decision = INVALID_DECISION;
		this.notes = null;
		this.pairSHA1 = INVALID_SHA1;
		this.ecSHA1 = INVALID_SHA1;
	}

	protected AbstractPairResultEntity(long jobId, char recordType,
			String record1Id, String record2Id, char record2Source,
			float probability, char decision, String[] notes, String ecSHA1) {

		assert recordType != DV_ABSTRACT.charAt(0);
		assert record1Id != INVALID_RECORD_ID;
		assert record2Id != INVALID_RECORD_ID;
		assert record2Source != INVALID_RECORD_SOURCE;
		assert probability != INVALID_PROBABILITY;
		assert decision != INVALID_DECISION;

		this.jobId = jobId;
		this.recordType = recordType;
		this.record1Id = record1Id;
		this.record2Id = record2Id;
		this.record2Source = record2Source;
		this.probability = probability;
		this.decision = decision;
		SortedSet<String> sortedNotes =
			MatchUtils.notesToSortedSet(notes);
		this.notes = MatchUtils.notesToString(sortedNotes);
		this.pairSHA1 =
			computePairSHA1(recordType, record1Id, record2Id, record2Source,
					probability, decision, sortedNotes);
		this.ecSHA1 = ecSHA1;
	}

	// -- Abstract call-back methods

	protected abstract T idFromString(String s);

	protected abstract String idToString(T id);

	// -- Template methods

	@Override
	public long getId() {
		return id;
	}

	@Override
	public long getJobId() {
		return jobId;
	}

	@Override
	public RECORD_ID_TYPE getRecordIdType() {
		RECORD_ID_TYPE retVal = RECORD_ID_TYPE.fromSymbol(recordType);
		return retVal;
	}

	@Override
	public T getRecord1Id() {
		return idFromString(record1Id);
	}

	@Override
	public T getRecord2Id() {
		return idFromString(record2Id);
	}

	@Override
	public RECORD_SOURCE_ROLE getRecord2Source() {
		return RECORD_SOURCE_ROLE.fromSymbol(record2Source);
	}

	@Override
	public float getProbability() {
		return probability;
	}

	@Override
	public Decision getDecision() {
		return Decision.valueOf(decision);
	}

	@Override
	public String[] getNotes() {
		return MatchUtils.notesFromDelimitedString(notes);
	}

	@Override
	public String getNotesAsDelimitedString() {
		return notes;
	}

	@Override
	public String getPairSHA1() {
		return pairSHA1;
	}

	// Override is not declared here -- see TransitivityPairResultEntity
	public String getEquivalenceClassSHA1() {
		return ecSHA1;
	}

	@Override
	public String exportToString() {
		throw new Error("not yet implemented");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + decision;
		result = prime * result + ((ecSHA1 == null) ? 0 : ecSHA1.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + (int) (jobId ^ (jobId >>> 32));
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		result =
			prime * result + ((pairSHA1 == null) ? 0 : pairSHA1.hashCode());
		result = prime * result + Float.floatToIntBits(probability);
		result =
			prime * result + ((record1Id == null) ? 0 : record1Id.hashCode());
		result =
			prime * result + ((record2Id == null) ? 0 : record2Id.hashCode());
		result = prime * result + record2Source;
		result = prime * result + recordType;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		@SuppressWarnings("rawtypes")
		AbstractPairResultEntity other = (AbstractPairResultEntity) obj;
		if (decision != other.decision) {
			return false;
		}
		if (ecSHA1 == null) {
			if (other.ecSHA1 != null) {
				return false;
			}
		} else if (!ecSHA1.equals(other.ecSHA1)) {
			return false;
		}
		if (id != other.id) {
			return false;
		}
		if (jobId != other.jobId) {
			return false;
		}
		if (notes == null) {
			if (other.notes != null) {
				return false;
			}
		} else if (!notes.equals(other.notes)) {
			return false;
		}
		if (pairSHA1 == null) {
			if (other.pairSHA1 != null) {
				return false;
			}
		} else if (!pairSHA1.equals(other.pairSHA1)) {
			return false;
		}
		if (Float.floatToIntBits(probability) != Float
				.floatToIntBits(other.probability)) {
			return false;
		}
		if (record1Id == null) {
			if (other.record1Id != null) {
				return false;
			}
		} else if (!record1Id.equals(other.record1Id)) {
			return false;
		}
		if (record2Id == null) {
			if (other.record2Id != null) {
				return false;
			}
		} else if (!record2Id.equals(other.record2Id)) {
			return false;
		}
		if (record2Source != other.record2Source) {
			return false;
		}
		if (recordType != other.recordType) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "AbstractPairResultEntity [record1Id=" + record1Id
				+ ", record2Id=" + record2Id + ", probability=" + probability
				+ ", decision=" + decision + "]";
	}

}
