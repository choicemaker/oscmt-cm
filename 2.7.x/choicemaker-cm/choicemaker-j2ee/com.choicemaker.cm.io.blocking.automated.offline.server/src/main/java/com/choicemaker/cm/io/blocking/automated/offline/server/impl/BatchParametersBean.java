/*
 * Copyright (c) 2001, 2009 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.io.blocking.automated.offline.server.impl;

import static com.choicemaker.cm.io.blocking.automated.offline.server.impl.BatchParametersJPA.ID_GENERATOR_NAME;
import static com.choicemaker.cm.io.blocking.automated.offline.server.impl.BatchParametersJPA.ID_GENERATOR_PK_COLUMN_NAME;
import static com.choicemaker.cm.io.blocking.automated.offline.server.impl.BatchParametersJPA.ID_GENERATOR_PK_COLUMN_VALUE;
import static com.choicemaker.cm.io.blocking.automated.offline.server.impl.BatchParametersJPA.ID_GENERATOR_TABLE;
import static com.choicemaker.cm.io.blocking.automated.offline.server.impl.BatchParametersJPA.ID_GENERATOR_VALUE_COLUMN_NAME;

import java.io.Serializable;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import com.choicemaker.cm.core.SerialRecordSource;
import com.choicemaker.cm.io.blocking.automated.offline.server.ejb.BatchParameters;

/**
 * @author pcheung (original version)
 * @author rphall (migrated to JPA 2.0)
 *
 */
@NamedQuery(name = BatchParametersJPA.QN_BATCHPARAMETERS_FIND_ALL,
		query = BatchParametersJPA.JPQL_BATCHJOB_FIND_ALL)
@Entity
@Table(/* schema = "CHOICEMAKER", */name = BatchParametersJPA.TABLE_NAME)
public class BatchParametersBean implements Serializable, BatchParameters {

	private static final long serialVersionUID = 271L;

	private static final Logger logger = Logger
			.getLogger(BatchParametersBean.class.getName());

	protected static String INVALID_MODEL_CONFIG_NAME = null;

	protected static int INVALID_MAX_SINGLE = -1;

	protected static float INVALID_THRESHOLD = -1f;

	protected static SerialRecordSource INVALID_RECORD_SOURCE = null;

	protected static boolean isInvalidBatchParamsId(long id) {
		return id == BatchParameters.INVALID_PARAMSID;
	}

	public static boolean isPersistent(BatchParameters params) {
		boolean retVal = false;
		if (params != null) {
			retVal = !isInvalidBatchParamsId(params.getId());
		}
		return retVal;
	}

	@Id
	@Column(name = BatchParametersJPA.CN_ID)
	@TableGenerator(name = ID_GENERATOR_NAME, table = ID_GENERATOR_TABLE,
			pkColumnName = ID_GENERATOR_PK_COLUMN_NAME,
			valueColumnName = ID_GENERATOR_VALUE_COLUMN_NAME,
			pkColumnValue = ID_GENERATOR_PK_COLUMN_VALUE)
	@GeneratedValue(strategy = GenerationType.TABLE,
			generator = ID_GENERATOR_NAME)
	private long id;

	@Column(name = BatchParametersJPA.CN_STAGE_MODEL)
	private final String stageModel;

	@Column(name = BatchParametersJPA.CN_MASTER_MODEL)
	private final String masterModel;

	@Column(name = BatchParametersJPA.CN_MAX_SINGLE)
	private final int maxSingle;

	@Column(name = BatchParametersJPA.CN_LOW_THRESHOLD)
	private final float lowThreshold;

	@Column(name = BatchParametersJPA.CN_HIGH_THRESHOLD)
	private final float highThreshold;

	@Transient
	private final SerialRecordSource stageRs;

	@Transient
	private final SerialRecordSource masterRs;

	/** Required by JPA; do not invoke directly */
	protected BatchParametersBean() {
		this(INVALID_MODEL_CONFIG_NAME, INVALID_MAX_SINGLE, INVALID_THRESHOLD,
				INVALID_THRESHOLD, INVALID_RECORD_SOURCE, INVALID_RECORD_SOURCE);
	}

	public BatchParametersBean(String modelConfigurationName, int maxSingle,
			float lowThreshold, float highThreshold,
			SerialRecordSource stageRs, SerialRecordSource masterRs) {

		this.stageModel = modelConfigurationName;
		this.masterModel = null;
		this.maxSingle = maxSingle;
		this.lowThreshold = lowThreshold;
		this.highThreshold = highThreshold;
		this.stageRs = stageRs;
		this.masterRs = masterRs;
	}

	public BatchParametersBean(BatchParameters bp) {
		this.stageModel = bp.getModelConfigurationName();
		if (bp instanceof BatchParametersBean) {
			this.masterModel = ((BatchParametersBean)bp).masterModel;
			if (masterModel != null) {
				logger.warning("non-null masterModel value: '" + masterModel + "'");
			}
		} else {
			logger.warning("masterModel value may not be correct");
			this.masterModel = null;
		}
		this.maxSingle = bp.getMaxSingle();
		this.lowThreshold = bp.getLowThreshold();
		this.highThreshold = bp.getHighThreshold();
		this.stageRs = bp.getStageRs();
		this.masterRs = bp.getMasterRs();
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public String getModelConfigurationName() {
		return stageModel;
	}

	@Override
	@Deprecated
	public String getStageModel() {
		return getModelConfigurationName();
	}

	@Override
	@Deprecated
	public String getMasterModel() {
		return getModelConfigurationName();
	}

	@Override
	public int getMaxSingle() {
		return maxSingle;
	}

	@Override
	public float getLowThreshold() {
		return lowThreshold;
	}

	@Override
	public float getHighThreshold() {
		return highThreshold;
	}

	@Override
	public SerialRecordSource getStageRs() {
		return stageRs;
	}

	@Override
	public SerialRecordSource getMasterRs() {
		return masterRs;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		if (id == 0) {
			result = hashCode0();
		} else {
			result = prime * result + (int) (id ^ (id >>> 32));
		}
		return result;
	}

	/**
	 * Hashcode for instances with id == 0
	 */
	protected int hashCode0() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(highThreshold);
		result = prime * result + Float.floatToIntBits(lowThreshold);
		// result =
		// prime * result
		// + ((masterModel == null) ? 0 : masterModel.hashCode());
		result =
			prime * result + ((masterRs == null) ? 0 : masterRs.hashCode());
		result = prime * result + maxSingle;
		result =
			prime * result + ((stageModel == null) ? 0 : stageModel.hashCode());
		result = prime * result + ((stageRs == null) ? 0 : stageRs.hashCode());
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
		BatchParametersBean other = (BatchParametersBean) obj;
		if (id != other.id) {
			return false;
		}
		if (id == 0) {
			return equals0(other);
		}
		return true;
	}

	/**
	 * Equality test for instances with id == 0
	 */
	protected boolean equals0(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		BatchParametersBean other = (BatchParametersBean) obj;
		if (Float.floatToIntBits(highThreshold) != Float
				.floatToIntBits(other.highThreshold)) {
			return false;
		}
		if (Float.floatToIntBits(lowThreshold) != Float
				.floatToIntBits(other.lowThreshold)) {
			return false;
		}
		// if (masterModel == null) {
		// if (other.masterModel != null) {
		// return false;
		// }
		// } else if (!masterModel.equals(other.masterModel)) {
		// return false;
		// }
		if (masterRs == null) {
			if (other.masterRs != null) {
				return false;
			}
		} else if (!masterRs.equals(other.masterRs)) {
			return false;
		}
		if (maxSingle != other.maxSingle) {
			return false;
		}
		if (stageModel == null) {
			if (other.stageModel != null) {
				return false;
			}
		} else if (!stageModel.equals(other.stageModel)) {
			return false;
		}
		if (stageRs == null) {
			if (other.stageRs != null) {
				return false;
			}
		} else if (!stageRs.equals(other.stageRs)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "BatchParametersBean [id=" + id + ", model=" + stageModel
				+ ", lowThreshold=" + lowThreshold + ", highThreshold="
				+ highThreshold + "]";
	}

}
