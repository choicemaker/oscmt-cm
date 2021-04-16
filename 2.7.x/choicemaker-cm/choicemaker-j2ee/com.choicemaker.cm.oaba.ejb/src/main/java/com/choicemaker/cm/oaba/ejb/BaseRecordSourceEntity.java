/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.ejb;

import static com.choicemaker.cm.oaba.ejb.BaseRecordSourceJPA.CN_ID;
import static com.choicemaker.cm.oaba.ejb.BaseRecordSourceJPA.CN_TYPE;
import static com.choicemaker.cm.oaba.ejb.BaseRecordSourceJPA.DISCRIMINATOR_COLUMN;
import static com.choicemaker.cm.oaba.ejb.BaseRecordSourceJPA.ID_GENERATOR_NAME;
import static com.choicemaker.cm.oaba.ejb.BaseRecordSourceJPA.ID_GENERATOR_PK_COLUMN_NAME;
import static com.choicemaker.cm.oaba.ejb.BaseRecordSourceJPA.ID_GENERATOR_PK_COLUMN_VALUE;
import static com.choicemaker.cm.oaba.ejb.BaseRecordSourceJPA.ID_GENERATOR_TABLE;
import static com.choicemaker.cm.oaba.ejb.BaseRecordSourceJPA.ID_GENERATOR_VALUE_COLUMN_NAME;
import static com.choicemaker.cm.oaba.ejb.BaseRecordSourceJPA.TABLE_NAME;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import com.choicemaker.cm.args.PersistableRecordSource;
import com.choicemaker.cm.batch.ejb.AbstractPersistentObject;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = DISCRIMINATOR_COLUMN,
		discriminatorType = DiscriminatorType.STRING)
@Table(name = TABLE_NAME)
public class BaseRecordSourceEntity extends AbstractPersistentObject
		implements PersistableRecordSource {

	private static final long serialVersionUID = 271L;

	@Id
	@Column(name = CN_ID)
	@TableGenerator(name = ID_GENERATOR_NAME, table = ID_GENERATOR_TABLE,
			pkColumnName = ID_GENERATOR_PK_COLUMN_NAME,
			valueColumnName = ID_GENERATOR_VALUE_COLUMN_NAME,
			pkColumnValue = ID_GENERATOR_PK_COLUMN_VALUE)
	@GeneratedValue(strategy = GenerationType.TABLE,
			generator = ID_GENERATOR_NAME)
	protected long id;

	@Column(name = CN_TYPE)
	protected final String type;

	protected BaseRecordSourceEntity(String type) {
		if (type == null || !type.equals(type.trim()) || type.isEmpty()) {
			throw new IllegalArgumentException("invalid type: '" + type + "'");
		}
		this.type = type;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return "RECORD_SOURCE_ROLE [id=" + id + ", type=" + type + "]";
	}

}
