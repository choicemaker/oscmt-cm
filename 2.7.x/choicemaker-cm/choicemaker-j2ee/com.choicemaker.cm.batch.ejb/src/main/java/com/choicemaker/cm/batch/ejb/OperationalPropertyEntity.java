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
package com.choicemaker.cm.batch.ejb;

import static com.choicemaker.cm.batch.ejb.OperationalPropertyJPA.CN_ID;
import static com.choicemaker.cm.batch.ejb.OperationalPropertyJPA.CN_JOB_ID;
import static com.choicemaker.cm.batch.ejb.OperationalPropertyJPA.CN_NAME;
import static com.choicemaker.cm.batch.ejb.OperationalPropertyJPA.CN_VALUE;
import static com.choicemaker.cm.batch.ejb.OperationalPropertyJPA.ID_GENERATOR_NAME;
import static com.choicemaker.cm.batch.ejb.OperationalPropertyJPA.ID_GENERATOR_PK_COLUMN_NAME;
import static com.choicemaker.cm.batch.ejb.OperationalPropertyJPA.ID_GENERATOR_PK_COLUMN_VALUE;
import static com.choicemaker.cm.batch.ejb.OperationalPropertyJPA.ID_GENERATOR_TABLE;
import static com.choicemaker.cm.batch.ejb.OperationalPropertyJPA.ID_GENERATOR_VALUE_COLUMN_NAME;
import static com.choicemaker.cm.batch.ejb.OperationalPropertyJPA.JPQL_OPPROP_DELETE_BY_JOB;
import static com.choicemaker.cm.batch.ejb.OperationalPropertyJPA.JPQL_OPPROP_FINDALL;
import static com.choicemaker.cm.batch.ejb.OperationalPropertyJPA.JPQL_OPPROP_FINDALL_BY_JOB;
import static com.choicemaker.cm.batch.ejb.OperationalPropertyJPA.JPQL_OPPROP_FIND_BY_JOB_PNAME;
import static com.choicemaker.cm.batch.ejb.OperationalPropertyJPA.QN_OPPROP_DELETE_BY_JOB;
import static com.choicemaker.cm.batch.ejb.OperationalPropertyJPA.QN_OPPROP_FINDALL;
import static com.choicemaker.cm.batch.ejb.OperationalPropertyJPA.QN_OPPROP_FINDALL_BY_JOB;
import static com.choicemaker.cm.batch.ejb.OperationalPropertyJPA.QN_OPPROP_FIND_BY_JOB_PNAME;
import static com.choicemaker.cm.batch.ejb.OperationalPropertyJPA.TABLE_NAME;

import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import com.choicemaker.cm.args.PersistentObject;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.OperationalProperty;

@NamedQueries({
		@NamedQuery(name = QN_OPPROP_FINDALL, query = JPQL_OPPROP_FINDALL),
		@NamedQuery(name = QN_OPPROP_FIND_BY_JOB_PNAME,
				query = JPQL_OPPROP_FIND_BY_JOB_PNAME),
		@NamedQuery(name = QN_OPPROP_FINDALL_BY_JOB,
				query = JPQL_OPPROP_FINDALL_BY_JOB),
		@NamedQuery(name = QN_OPPROP_DELETE_BY_JOB,
				query = JPQL_OPPROP_DELETE_BY_JOB) })
@Entity
@Table(/* schema = "CHOICEMAKER", */name = TABLE_NAME)
public class OperationalPropertyEntity extends AbstractPersistentObject
		implements OperationalProperty {

	private static final long serialVersionUID = 1L;

	private static final Logger logger =
		Logger.getLogger(OperationalPropertyEntity.class.getName());

	public static final String INVALID_NAME = null;
	public static final String INVALID_VALUE = null;

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

	@Column(name = CN_JOB_ID)
	private final long jobId;

	@Column(name = CN_NAME)
	private final String name;

	@Column(name = CN_VALUE)
	private String value;

	// -- Constructors

	protected OperationalPropertyEntity() {
		this.id = PersistentObject.NONPERSISTENT_ID;
		this.jobId = PersistentObject.NONPERSISTENT_ID;
		this.name = INVALID_NAME;
		this.value = INVALID_VALUE;
	}

	public OperationalPropertyEntity(BatchJob job, final String pn,
			final String pv) {
		if (job == null || !job.isPersistent()) {
			throw new IllegalArgumentException("invalid job: " + job);
		}
		if (pn == null || !pn.equals(pn.trim()) || pn.isEmpty()) {
			throw new IllegalArgumentException(
					"invalid property name: '" + pn + "'");
		}
		final String stdName = pn.toUpperCase();
		if (!pn.equals(stdName)) {
			logger.warning("Converting property name '" + pn
					+ "' to upper-case '" + stdName + "'");
		}

		this.jobId = job.getId();
		this.name = stdName;
		updateValue(pv);
	}

	// -- Modifiers

	@Override
	public void updateValue(String s) {
		if (s == null) {
			throw new IllegalArgumentException(
					"invalid property value: '" + s + "'");
		}
		if (s.trim().isEmpty()) {
			logger.warning("Blank value for '" + name + "'");
		}
		this.value = s;
	}

	// -- Accessors

	public OperationalPropertyEntity(OperationalProperty p) {
		this.id = p.getId();
		this.jobId = p.getJobId();
		this.name = p.getName();
		this.value = p.getValue();
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public long getJobId() {
		return jobId;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "OperationalPropertyEntity [id=" + getId() + ", jobId="
				+ getJobId() + ", name=" + getName() + ", value=" + getValue()
				+ "]";
	}

}
