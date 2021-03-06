/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.ejb;

import static com.choicemaker.cm.oaba.ejb.OabaSettingsJPA.CN_OABA_INTERVAL;
import static com.choicemaker.cm.oaba.ejb.OabaSettingsJPA.CN_OABA_MAX_BLOCKSIZE;
import static com.choicemaker.cm.oaba.ejb.OabaSettingsJPA.CN_OABA_MAX_CHUNKSIZE;
import static com.choicemaker.cm.oaba.ejb.OabaSettingsJPA.CN_OABA_MAX_MATCHES;
import static com.choicemaker.cm.oaba.ejb.OabaSettingsJPA.CN_OABA_MAX_OVERSIZE;
import static com.choicemaker.cm.oaba.ejb.OabaSettingsJPA.CN_OABA_MIN_FIELDS;
import static com.choicemaker.cm.oaba.ejb.OabaSettingsJPA.DISCRIMINATOR_VALUE;
import static com.choicemaker.cm.oaba.ejb.OabaSettingsJPA.JPQL_OABA_FIND_ALL;
import static com.choicemaker.cm.oaba.ejb.OabaSettingsJPA.QN_OABA_FIND_ALL;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;

import com.choicemaker.cm.args.AbaSettings;
import com.choicemaker.cm.args.OabaSettings;

@NamedQuery(name = QN_OABA_FIND_ALL, query = JPQL_OABA_FIND_ALL)
@Entity
@DiscriminatorValue(value = DISCRIMINATOR_VALUE)
public class OabaSettingsEntity extends AbaSettingsEntity
		implements OabaSettings {

	private static final long serialVersionUID = 271L;

	public static String dump(OabaSettings s) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);

		pw.println("ABA and OABA settings");
		if (s == null) {
			pw.println("null ABA/OABA settings");
		} else {
			pw.println("ABA/OABA: Settings id: " + s.getId());
			pw.println("ABA: Limit per blocking set: "
					+ s.getLimitPerBlockingSet());
			pw.println("ABA: Limit for a single blocking set: "
					+ s.getLimitSingleBlockingSet());
			pw.println("ABA: Single-table blocking set limit: "
					+ s.getSingleTableBlockingSetGraceLimit());
			pw.println("OABA: Threshold for batched-record blocking: "
					+ s.getMaxSingle());
			pw.println("OABA: Control loop interval: " + s.getInterval());
			pw.println("OABA: Max block size: " + s.getMaxBlockSize());
			pw.println(
					"OABA: Max oversized block size: " + s.getMaxOversized());
			pw.println("OABA: Min field count (oversized blocks): "
					+ s.getMinFields());
			pw.println("OABA: Max chunk size: " + s.getMaxChunkSize());
			pw.println(
					"OABA: Max matches per export file: " + s.getMaxMatches());
		}
		String retVal = sw.toString();
		return retVal;
	}

	// -- Instance data

	@Column(name = AbstractParametersJPA.CN_OABA_MAX_SINGLE)
	private final int maxSingle;

	@Column(name = CN_OABA_MAX_BLOCKSIZE)
	private final int maxBlockSize;

	@Column(name = CN_OABA_MAX_CHUNKSIZE)
	private final int maxChunkSize;

	@Column(name = CN_OABA_MAX_OVERSIZE)
	private final int maxOversized;

	@Column(name = CN_OABA_MAX_MATCHES)
	private final int maxMatches;

	@Column(name = CN_OABA_MIN_FIELDS)
	private final int minFields;

	@Column(name = CN_OABA_INTERVAL)
	private final int interval;

	// -- Construction

	/**
	 * Constructs an instance with default limits:
	 * <ul>
	 * <li>{@link #DEFAULT_LIMIT_PER_BLOCKING_SET}</li>
	 * <li>{@link #DEFAULT_LIMIT_SINGLE_BLOCKING_SET}</li>
	 * <li>{@link #DEFAULT_SINGLE_TABLE_GRACE_LIMIT}</li>
	 * <li>{@link # DEFAULT_MAX_BLOCKSIZE = 100}</li>
	 * <li>{@link # DEFAULT_MAX_CHUNKSIZE = 100000}</li>
	 * <li>{@link # DEFAULT_MAX_OVERSIZED = 1000}</li>
	 * <li>{@link # DEFAULT_MIN_FIELDS = 3}</li>
	 * <li>{@link # DEFAULT_INTERVAL = 100}</li>
	 * </ul>
	 */
	public OabaSettingsEntity() {
		this(DEFAULT_LIMIT_PER_BLOCKING_SET, DEFAULT_LIMIT_SINGLE_BLOCKING_SET,
				DEFAULT_SINGLE_TABLE_GRACE_LIMIT, DEFAULT_MAX_SINGLE,
				DEFAULT_MAX_BLOCKSIZE, DEFAULT_MAX_CHUNKSIZE,
				DEFAULT_MAX_MATCHES, DEFAULT_MAX_OVERSIZED, DEFAULT_MIN_FIELDS,
				DEFAULT_INTERVAL);
	}

	public OabaSettingsEntity(AbaSettings aba) {
		this(aba.getLimitPerBlockingSet(), aba.getLimitSingleBlockingSet(),
				aba.getSingleTableBlockingSetGraceLimit(), DEFAULT_MAX_SINGLE,
				DEFAULT_MAX_BLOCKSIZE, DEFAULT_MAX_CHUNKSIZE,
				DEFAULT_MAX_MATCHES, DEFAULT_MAX_OVERSIZED, DEFAULT_MIN_FIELDS,
				DEFAULT_INTERVAL);
	}

	public OabaSettingsEntity(AbaSettings aba, int maxSingle, int maxBlockSize,
			int maxChunkSize, int maxMatches, int maxOversized, int minFields,
			int interval) {
		this(aba.getLimitPerBlockingSet(), aba.getLimitSingleBlockingSet(),
				aba.getSingleTableBlockingSetGraceLimit(), maxSingle,
				maxBlockSize, maxChunkSize, maxMatches, maxOversized, minFields,
				interval);
	}

	public OabaSettingsEntity(OabaSettings oaba) {
		this(oaba, oaba.getMaxSingle(), oaba.getMaxBlockSize(),
				oaba.getMaxChunkSize(), oaba.getMaxMatches(),
				oaba.getMaxOversized(), oaba.getMinFields(),
				oaba.getInterval());
	}

	public OabaSettingsEntity(OabaSettings oaba, int maxSingle) {
		this(oaba, maxSingle, oaba.getMaxBlockSize(), oaba.getMaxChunkSize(),
				oaba.getMaxMatches(), oaba.getMaxOversized(),
				oaba.getMinFields(), oaba.getInterval());
	}

	public OabaSettingsEntity(int limPerBlockingSet, int limSingleBlockingSet,
			int singleTableGraceLimit, int maxSingle, int maxBlockSize,
			int maxChunkSize, int maxMatches, int maxOversized, int minFields,
			int interval) {
		this(limPerBlockingSet, limSingleBlockingSet, singleTableGraceLimit,
				maxSingle, maxBlockSize, maxChunkSize, maxMatches, maxOversized,
				minFields, interval, DISCRIMINATOR_VALUE);
	}

	protected OabaSettingsEntity(int limPerBlockingSet,
			int limSingleBlockingSet, int singleTableGraceLimit, int maxSingle,
			int maxBlockSize, int maxChunkSize, int maxMatches,
			int maxOversized, int minFields, int interval, String type) {
		super(limPerBlockingSet, limSingleBlockingSet, singleTableGraceLimit,
				type);
		if (maxSingle < 0) {
			throw new IllegalArgumentException("invalid maxSingle" + maxSingle);
		}
		if (maxBlockSize < 0) {
			throw new IllegalArgumentException(
					"invalid maxBlockSize" + maxBlockSize);
		}
		if (maxChunkSize < 0) {
			throw new IllegalArgumentException(
					"invalid maxChunkSize" + maxChunkSize);
		}
		if (maxMatches < 0) {
			throw new IllegalArgumentException(
					"invalid maxMatches: " + maxOversized);
		}
		if (maxOversized < 0) {
			throw new IllegalArgumentException(
					"invalid maxOversized: " + maxOversized);
		}
		if (minFields < 0) {
			throw new IllegalArgumentException(
					"invalid minFields: " + minFields);
		}
		if (interval < 0) {
			throw new IllegalArgumentException("invalid interval: " + interval);
		}
		this.maxSingle = maxSingle;
		this.maxBlockSize = maxBlockSize;
		this.maxChunkSize = maxChunkSize;
		this.maxMatches = maxMatches;
		this.maxOversized = maxOversized;
		this.minFields = minFields;
		this.interval = interval;
	}

	// -- Accessors

	@Override
	public int getMaxSingle() {
		return maxSingle;
	}

	@Override
	public int getMaxBlockSize() {
		return maxBlockSize;
	}

	@Override
	public int getMaxChunkSize() {
		return maxChunkSize;
	}

	@Override
	public int getMaxMatches() {
		return maxMatches;
	}

	@Override
	public int getMaxOversized() {
		return maxOversized;
	}

	@Override
	public int getMinFields() {
		return minFields;
	}

	@Override
	public int getInterval() {
		return interval;
	}

	// -- Identity

	@Override
	public String toString() {
		return "OabaSettingsEntity [id=" + getId() + ", maxSingle=" + maxSingle
				+ ", maxBlockSize=" + maxBlockSize + ", maxChunkSize="
				+ maxChunkSize + ", maxMatches=" + maxMatches
				+ ", maxOversized=" + maxOversized + ", minFields=" + minFields
				+ ", interval=" + interval + "]";
	}

}
