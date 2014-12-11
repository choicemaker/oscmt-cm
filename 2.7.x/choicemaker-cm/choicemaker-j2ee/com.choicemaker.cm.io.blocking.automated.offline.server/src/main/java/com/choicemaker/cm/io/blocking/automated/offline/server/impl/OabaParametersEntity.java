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

import static com.choicemaker.cm.io.blocking.automated.offline.server.impl.OabaParametersJPA.ID_GENERATOR_NAME;
import static com.choicemaker.cm.io.blocking.automated.offline.server.impl.OabaParametersJPA.ID_GENERATOR_PK_COLUMN_NAME;
import static com.choicemaker.cm.io.blocking.automated.offline.server.impl.OabaParametersJPA.ID_GENERATOR_PK_COLUMN_VALUE;
import static com.choicemaker.cm.io.blocking.automated.offline.server.impl.OabaParametersJPA.ID_GENERATOR_TABLE;
import static com.choicemaker.cm.io.blocking.automated.offline.server.impl.OabaParametersJPA.ID_GENERATOR_VALUE_COLUMN_NAME;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import com.choicemaker.cm.args.OabaLinkageType;
import com.choicemaker.cm.args.OabaParameters;
import com.choicemaker.cm.args.PersistableRecordSource;
import com.choicemaker.cm.core.base.Thresholds;

/**
 * @author pcheung (original version)
 * @author rphall (migrated to JPA 2.0)
 *
 */
@NamedQuery(name = OabaParametersJPA.QN_BATCHPARAMETERS_FIND_ALL,
		query = OabaParametersJPA.JPQL_BATCHPARAMETERS_FIND_ALL)
@Entity
@Table(/* schema = "CHOICEMAKER", */name = OabaParametersJPA.TABLE_NAME)
public class OabaParametersEntity implements Serializable, OabaParameters {

	private static final long serialVersionUID = 271L;

	// private static final Logger logger = Logger
	// .getLogger(OabaParametersEntity.class.getName());

	protected static final int INVALID_MAX_SINGLE = -1;

	protected static final float INVALID_THRESHOLD = -1f;

	protected static boolean isInvalidBatchParamsId(long id) {
		return id == NONPERSISTENT_ID;
	}

	public static boolean isPersistent(OabaParameters params) {
		boolean retVal = false;
		if (params != null) {
			retVal = !isInvalidBatchParamsId(params.getId());
		}
		return retVal;
	}

	@Id
	@Column(name = OabaParametersJPA.CN_ID)
	@TableGenerator(name = ID_GENERATOR_NAME, table = ID_GENERATOR_TABLE,
			pkColumnName = ID_GENERATOR_PK_COLUMN_NAME,
			valueColumnName = ID_GENERATOR_VALUE_COLUMN_NAME,
			pkColumnValue = ID_GENERATOR_PK_COLUMN_VALUE)
	@GeneratedValue(strategy = GenerationType.TABLE,
			generator = ID_GENERATOR_NAME)
	private long id;

	@Column(name = OabaParametersJPA.CN_MODEL)
	private final String modelConfigName;

	@Column(name = OabaParametersJPA.CN_LOW_THRESHOLD)
	private final float lowThreshold;

	@Column(name = OabaParametersJPA.CN_HIGH_THRESHOLD)
	private final float highThreshold;

	@Column(name = OabaParametersJPA.CN_STAGE_RS)
	private final long stageRsId;

	@Column(name = OabaParametersJPA.CN_STAGE_RS_TYPE)
	private final String stageRsType;

	@Column(name = OabaParametersJPA.CN_MASTER_RS)
	private final Long masterRsId;

	/*
	 * The <code>masterRsType</code> field acts as a flag. If it is null, then
	 * the value returned by getMasterRs() will be null. For this flag to be
	 * consistent, the public constructors of this class and subclasses must
	 * ensure that any non-null record source with a null record-source type is
	 * rejected as an illegal argument.
	 */
	@Column(name = OabaParametersJPA.CN_MASTER_RS_TYPE)
	private final String masterRsType;

	@Column(name = OabaParametersJPA.CN_TASK)
	private final String task;

	/** Required by JPA; do not invoke directly */
	protected OabaParametersEntity() {
		this.modelConfigName = null;
		this.lowThreshold = INVALID_THRESHOLD;
		this.highThreshold = INVALID_THRESHOLD;
		this.stageRsId = NONPERSISTENT_ID;
		this.stageRsType = null;
		this.masterRsId = null;
		this.masterRsType = null;
		this.task = null;
	}

	public OabaParametersEntity(String modelConfigurationName,
			float lowThreshold, float highThreshold,
			PersistableRecordSource stageRs) {
		this(modelConfigurationName, lowThreshold, highThreshold, stageRs,
				null, OabaLinkageType.STAGING_DEDUPLICATION);
	}

	public OabaParametersEntity(OabaParameters bp) {
		this(bp.getModelConfigurationName(), bp.getLowThreshold(), bp
				.getHighThreshold(), bp.getStageRsId(), bp.getStageRsType(), bp
				.getMasterRsId(), bp.getMasterRsType(), bp.getOabaLinkageType());
	}

	public OabaParametersEntity(String modelConfigurationName,
			float lowThreshold, float highThreshold,
			PersistableRecordSource stageRs, PersistableRecordSource masterRs,
			OabaLinkageType taskType) {
		this(modelConfigurationName, lowThreshold, highThreshold, stageRs
				.getId(), stageRs.getType(), masterRs == null ? null : masterRs
				.getId(), masterRs == null ? null : masterRs.getType(),
				taskType);
	}

	/**
	 * 
	 * @param modelConfigurationName
	 *            model configuration name
	 * @param lowThreshold
	 *            differ threshold
	 * @param highThreshold
	 *            match threshold
	 * @param sId
	 *            persistence id of the staging record source
	 * @param sType
	 *            the type of the staging record source (FlatFile, XML, DB)
	 * @param mId
	 *            the persistence id of the master record. Must be null if the
	 *            <code>taskType</code> is STAGING_DEDUPLICATION; otherwise must
	 *            be non-null.
	 * @param mType
	 *            the type of the master record source. Must be null if the
	 *            <code>taskType</code> is STAGING_DEDUPLICATION; otherwise must
	 *            be non-null.
	 * @param taskType
	 *            the record matching task: duplication of a staging source;
	 *            linkage of a staging source to a master source; or linkage of
	 *            two master sources.
	 */
	public OabaParametersEntity(String modelConfigurationName,
			float lowThreshold, float highThreshold, long sId, String sType,
			Long mId, String mType, OabaLinkageType taskType) {

		if (modelConfigurationName == null
				|| modelConfigurationName.trim().isEmpty()) {
			throw new IllegalArgumentException("null or blank modelId");
		}
		Thresholds.validate(lowThreshold, highThreshold);
		if (sType == null || !sType.equals(sType.trim()) || sType.isEmpty()) {
			throw new IllegalArgumentException("invalid stage RS type: "
					+ sType);
		}
		if (taskType == null) {
			throw new IllegalArgumentException("null task type");
		}

		// The masterRsId, masterRsType and taskType must be consistent.
		// If the task type is STAGING_DEDUPLICATION, then the masterRsId
		// and the masterRsType must be null. Otherwise, the masterRsId and
		// the masterRsType must be non-null.
		if (taskType == OabaLinkageType.STAGING_DEDUPLICATION) {
			if (mId != null) {
				String msg =
					"non-null master source id '" + mId + "' (taskType is '"
							+ taskType + "')";
				throw new IllegalArgumentException(msg);
			}
			if (mType != null) {
				String msg =
					"non-null master source type '" + mType
							+ "' (taskType is '" + taskType + "')";
				throw new IllegalArgumentException(msg);
			}
		} else {
			if (mId == null) {
				String msg =
					"null master source id '" + mId + "' (taskType is '"
							+ taskType + "')";
				throw new IllegalArgumentException(msg);
			}
			if (mType == null || !mType.equals(mType.trim()) || mType.isEmpty()) {
				String msg =
					"invalid master source type '" + mType + "' (taskType is '"
							+ taskType + "')";
				throw new IllegalArgumentException(msg);
			}
		}

		this.modelConfigName = modelConfigurationName.trim();
		this.lowThreshold = lowThreshold;
		this.highThreshold = highThreshold;
		this.stageRsId = sId;
		this.stageRsType = sType;
		this.masterRsId = mId;
		this.masterRsType = mType;
		this.task = taskType.name();
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public String getModelConfigurationName() {
		return modelConfigName;
	}

	@Override
	public String getStageModel() {
		return getModelConfigurationName();
	}

	@Override
	public String getMasterModel() {
		return getModelConfigurationName();
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
	public long getStageRsId() {
		return stageRsId;
	}

	@Override
	public String getStageRsType() {
		return stageRsType;
	}

	@Override
	public Long getMasterRsId() {
		return masterRsId;
	}

	@Override
	public String getMasterRsType() {
		return masterRsType;
	}

	@Override
	public OabaLinkageType getOabaLinkageType() {
		return OabaLinkageType.valueOf(this.task);
	}

	// -- Identity

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		if (id == NONPERSISTENT_ID) {
			result = prime * result + hashCode0();
		}
		return result;
	}

	protected int hashCode0() {
		final int prime = 31;
		int result = 1;

		result = prime * result + Float.floatToIntBits(highThreshold);
		result = prime * result + Float.floatToIntBits(lowThreshold);
		result = prime * result
					+ ((masterRsId == null) ? 0
							: (int) (masterRsId ^ (masterRsId >>> 32)));
		result = prime * result
					+ ((masterRsType == null) ? 0 : masterRsType.hashCode());
		result = prime * result
					+ ((modelConfigName == null) ? 0 : modelConfigName
							.hashCode());
		result = prime * result + (int) (stageRsId ^ (stageRsId >>> 32));
		result = prime * result
					+ ((stageRsType == null) ? 0 : stageRsType.hashCode());
		result = prime * result + ((task == null) ? 0 : task.hashCode());

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
		OabaParametersEntity other = (OabaParametersEntity) obj;
		if (id != other.id) {
			return false;
		}
		if (id == NONPERSISTENT_ID) {
			return equals0(other);
		}
		return true;
	}

	protected boolean equals0(OabaParametersEntity other) {
		assert other != null;
		if (Float.floatToIntBits(highThreshold) != Float
				.floatToIntBits(other.highThreshold)) {
			return false;
		}
		if (Float.floatToIntBits(lowThreshold) != Float
				.floatToIntBits(other.lowThreshold)) {
			return false;
		}
		if (masterRsId != other.masterRsId) {
			return false;
		}
		if (masterRsType == null) {
			if (other.masterRsType != null) {
				return false;
			}
		} else if (!masterRsType.equals(other.masterRsType)) {
			return false;
		}
		if (modelConfigName == null) {
			if (other.modelConfigName != null) {
				return false;
			}
		} else if (!modelConfigName.equals(other.modelConfigName)) {
			return false;
		}
		if (stageRsId != other.stageRsId) {
			return false;
		}
		if (stageRsType == null) {
			if (other.stageRsType != null) {
				return false;
			}
		} else if (!stageRsType.equals(other.stageRsType)) {
			return false;
		}
		if (task == null) {
			if (other.task != null) {
				return false;
			}
		} else if (!task.equals(other.task)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "OabaParametersEntity [id=" + id + ", modelId="
				+ modelConfigName + ", lowThreshold=" + lowThreshold
				+ ", highThreshold=" + highThreshold + "]";
	}

}
