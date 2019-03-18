/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.ejb;

import static com.choicemaker.cm.oaba.ejb.AbaSettingsJPA.CN_ABA_LIMIT_BLOCKSET;
import static com.choicemaker.cm.oaba.ejb.AbaSettingsJPA.CN_ABA_LIMIT_SINGLESET;
import static com.choicemaker.cm.oaba.ejb.AbaSettingsJPA.CN_ABA_LIMIT_SINGLETABLE;
import static com.choicemaker.cm.oaba.ejb.AbaSettingsJPA.CN_ABA_MAX_MATCHES;
import static com.choicemaker.cm.oaba.ejb.AbaSettingsJPA.CN_ID;
import static com.choicemaker.cm.oaba.ejb.AbaSettingsJPA.CN_TYPE;
import static com.choicemaker.cm.oaba.ejb.AbaSettingsJPA.DISCRIMINATOR_COLUMN;
import static com.choicemaker.cm.oaba.ejb.AbaSettingsJPA.DISCRIMINATOR_VALUE;
import static com.choicemaker.cm.oaba.ejb.AbaSettingsJPA.ID_GENERATOR_NAME;
import static com.choicemaker.cm.oaba.ejb.AbaSettingsJPA.ID_GENERATOR_PK_COLUMN_NAME;
import static com.choicemaker.cm.oaba.ejb.AbaSettingsJPA.ID_GENERATOR_PK_COLUMN_VALUE;
import static com.choicemaker.cm.oaba.ejb.AbaSettingsJPA.ID_GENERATOR_TABLE;
import static com.choicemaker.cm.oaba.ejb.AbaSettingsJPA.ID_GENERATOR_VALUE_COLUMN_NAME;
import static com.choicemaker.cm.oaba.ejb.AbaSettingsJPA.JPQL_ABA_FIND_ALL;
import static com.choicemaker.cm.oaba.ejb.AbaSettingsJPA.QN_ABA_FIND_ALL;
import static com.choicemaker.cm.oaba.ejb.AbaSettingsJPA.TABLE_NAME;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import com.choicemaker.cm.args.AbaSettings;
import com.choicemaker.cm.batch.ejb.AbstractPersistentObject;

/**
 * Persistent ABA settings.
 */
@NamedQuery(name = QN_ABA_FIND_ALL, query = JPQL_ABA_FIND_ALL)
@Entity
@Table(/* schema = "CHOICEMAKER", */name = TABLE_NAME)
@DiscriminatorColumn(name = DISCRIMINATOR_COLUMN,
		discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(DISCRIMINATOR_VALUE)
public class AbaSettingsEntity extends AbstractPersistentObject
		implements AbaSettings {

	private static final long serialVersionUID = 271L;

	public static boolean isPersistent(AbaSettings aba) {
		boolean retVal = false;
		if (aba != null) {
			retVal = aba.getId() != NONPERSISTENT_ABA_SETTINGS_ID;
		}
		return retVal;
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

	@Column(name = CN_TYPE)
	private final String type;

	@Column(name = CN_ABA_MAX_MATCHES)
	private int abaMaxMatches;

	@Column(name = CN_ABA_LIMIT_BLOCKSET)
	private int limitPerBlockingSet;

	@Column(name = CN_ABA_LIMIT_SINGLESET)
	private int limitSingleBlockingSet;

	@Column(name = CN_ABA_LIMIT_SINGLETABLE)
	private int singleTableBlockingSetGraceLimit;

	// -- Construction

	/**
	 * Constructs an instance with default limits:
	 * <ul>
	 * <li>{@link #DEFAULT_LIMIT_PER_BLOCKING_SET}</li>
	 * <li>{@link #DEFAULT_LIMIT_SINGLE_BLOCKING_SET}</li>
	 * <li>{@link DEFAULT_SINGLE_TABLE_GRACE_LIMIT}</li>
	 * </ul>
	 */
	public AbaSettingsEntity() {
		this(DEFAULT_LIMIT_PER_BLOCKING_SET, DEFAULT_LIMIT_SINGLE_BLOCKING_SET,
				DEFAULT_SINGLE_TABLE_GRACE_LIMIT);
	}

	public AbaSettingsEntity(AbaSettings aba) {
		this(aba.getLimitPerBlockingSet(), aba.getLimitSingleBlockingSet(),
				aba.getSingleTableBlockingSetGraceLimit());
	}

	public AbaSettingsEntity(int limPerBlockingSet, int limSingleBlockingSet,
			int singleTableGraceLimit) {
		this(limPerBlockingSet, limSingleBlockingSet, singleTableGraceLimit,
				DISCRIMINATOR_VALUE);
	}

	protected AbaSettingsEntity(int limPerBlockingSet, int limSingleBlockingSet,
			int singleTableGraceLimit, String type) {
		if (type == null) {
			throw new IllegalArgumentException("null type");
		}
		type = type.trim();
		if (type.isEmpty()) {
			throw new IllegalArgumentException("blank type");
		}
		if (limPerBlockingSet < 0) {
			throw new IllegalArgumentException(
					"invalid limitPerBlockingSet" + limPerBlockingSet);
		}
		if (limSingleBlockingSet < 0) {
			throw new IllegalArgumentException(
					"invalid limitSingleBlockingSet" + limSingleBlockingSet);
		}
		if (singleTableGraceLimit < 0) {
			throw new IllegalArgumentException(
					"invalid singleTableBlockingSetGraceLimit: "
							+ singleTableGraceLimit);
		}
		this.type = type;
		this.limitPerBlockingSet = limPerBlockingSet;
		this.limitSingleBlockingSet = limSingleBlockingSet;
		this.singleTableBlockingSetGraceLimit = singleTableGraceLimit;
	}

	// -- Accessors

	@Override
	public long getId() {
		return id;
	}

	@Override
	public int getAbaMaxMatches() {
		return abaMaxMatches;
	}

	@Override
	public int getLimitPerBlockingSet() {
		return limitPerBlockingSet;
	}

	@Override
	public int getLimitSingleBlockingSet() {
		return limitSingleBlockingSet;
	}

	@Override
	public int getSingleTableBlockingSetGraceLimit() {
		return singleTableBlockingSetGraceLimit;
	}

	// -- Identity

	@Override
	public String toString() {
		return "AbaSettingsEntity [id=" + id + ", type=" + type
				+ ", limitPerBlockingSet=" + limitPerBlockingSet
				+ ", limitSingleBlockingSet=" + limitSingleBlockingSet
				+ ", singleTableBlockingSetGraceLimit="
				+ singleTableBlockingSetGraceLimit + "]";
	}

}
