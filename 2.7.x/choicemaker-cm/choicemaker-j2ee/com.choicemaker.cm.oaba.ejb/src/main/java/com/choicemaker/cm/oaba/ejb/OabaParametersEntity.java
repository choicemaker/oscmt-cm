/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.ejb;

import static com.choicemaker.cm.oaba.ejb.AbstractParametersJPA.DV_OABA;
import static com.choicemaker.cm.oaba.ejb.AbstractParametersJPA.JPQL_OABAPARAMETERS_FIND_ALL;
import static com.choicemaker.cm.oaba.ejb.AbstractParametersJPA.QN_OABAPARAMETERS_FIND_ALL;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;

import com.choicemaker.cm.args.OabaLinkageType;
import com.choicemaker.cm.args.OabaParameters;
import com.choicemaker.cm.args.PersistableRecordSource;

/**
 * @author pcheung (original version)
 * @author rphall (migrated to JPA 2.0)
 *
 */
@NamedQuery(name = QN_OABAPARAMETERS_FIND_ALL,
		query = JPQL_OABAPARAMETERS_FIND_ALL)
@Entity
@DiscriminatorValue(DV_OABA)
public class OabaParametersEntity extends AbstractParametersEntity implements
		Serializable, OabaParameters {

	private static final long serialVersionUID = 271L;

	// private static final Logger logger = Logger
	// .getLogger(OabaParametersEntity.class.getName());

	public static String dump(OabaParameters p) {
		return dump(DEFAULT_DUMP_TAG, p);
	}

	public static String dump(String tag, OabaParameters p) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);

		pw.println("Blocking parameters (" + tag + ")");
		if (p == null) {
			pw.println(tag + ": null batch parameters");
		} else {
			pw.println(tag + ": DIFFER threshold: " + p.getLowThreshold());
			pw.println(tag + ": MATCH threshold: " + p.getHighThreshold());
			pw.println(tag + ": Model configuration name: "
					+ p.getModelConfigurationName());
			final OabaLinkageType task = p.getOabaLinkageType();
			pw.print(tag + ": Linkage task: " + task);
			switch (task) {
			case STAGING_DEDUPLICATION:
			case TA_STAGING_DEDUPLICATION:
				pw.println(" (deduplicating a single record source)");
				pw.println(tag + ": Query record source: " + p.getQueryRsId());
				pw.println(tag + ": Query record source type: "
						+ p.getQueryRsType());
				break;

			case STAGING_TO_MASTER_LINKAGE:
			case TA_STAGING_TO_MASTER_LINKAGE:
				pw.println(" (linking a query source to a reference source)");
				pw.println(tag + ": Query record source: " + p.getQueryRsId());
				pw.println(tag + ": Query record source type: "
						+ p.getQueryRsType());
				pw.println(tag + ": Reference record source: "
						+ p.getReferenceRsId());
				pw.println(tag + ": Reference record source type: "
						+ p.getReferenceRsType());
				break;

			case MASTER_TO_MASTER_LINKAGE:
			case TA_MASTER_TO_MASTER_LINKAGE:
				pw.println(" (linking a reference source to a reference source)");
				pw.println(tag + ": Reference record source 1: "
						+ p.getQueryRsId());
				pw.println(tag + ": Reference record source 1 type: "
						+ p.getQueryRsType());
				pw.println(tag + ": Reference record source 2: "
						+ p.getReferenceRsId());
				pw.println(tag + ": Reference record source 2 type: "
						+ p.getReferenceRsType());
				break;

			default:
				throw new IllegalArgumentException("unexpected task type: "
						+ task);
			}
		}
		String retVal = sw.toString();
		return retVal;
	}

	/** Required by JPA; do not invoke directly */
	protected OabaParametersEntity() {
		super();
	}

	public OabaParametersEntity(String modelConfigurationName,
			float lowThreshold, float highThreshold, String blocking,
			PersistableRecordSource stageRs, String queryRsDbConfig) {
		this(modelConfigurationName, lowThreshold, highThreshold, blocking,
				stageRs, OabaParameters.DEFAULT_QUERY_RS_IS_DEDUPLICATED,
				queryRsDbConfig, null, null,
				OabaLinkageType.STAGING_DEDUPLICATION);
	}

	public OabaParametersEntity(String modelConfigurationName,
			float lowThreshold, float highThreshold, String blocking,
			PersistableRecordSource stageRs, String queryRsDbConfig,
			boolean isQueryDeduped) {
		this(modelConfigurationName, lowThreshold, highThreshold, blocking,
				stageRs, isQueryDeduped, queryRsDbConfig, null, null,
				OabaLinkageType.STAGING_DEDUPLICATION);
	}

	public OabaParametersEntity(OabaParameters bp) {
		this(bp.getModelConfigurationName(), bp.getLowThreshold(), bp
				.getHighThreshold(), bp.getBlockingConfiguration(), bp
				.getQueryRsId(), bp.getQueryRsType(), bp
				.isQueryRsDeduplicated(), bp.getQueryRsDatabaseConfiguration(),
				bp.getReferenceRsId(), bp.getReferenceRsType(), bp
						.getReferenceRsDatabaseConfiguration(), bp
						.getOabaLinkageType());
	}

	public OabaParametersEntity(String modelConfigurationName,
			float lowThreshold, float highThreshold, String blocking,
			PersistableRecordSource stageRs, String queryRsDbConfig,
			PersistableRecordSource masterRs, String refRsDbConfig,
			OabaLinkageType taskType) {
		this(modelConfigurationName, lowThreshold, highThreshold, blocking,
				stageRs, OabaParameters.DEFAULT_QUERY_RS_IS_DEDUPLICATED,
				queryRsDbConfig, masterRs, refRsDbConfig, taskType);
	}

	public OabaParametersEntity(String modelConfigurationName,
			float lowThreshold, float highThreshold, String blocking,
			PersistableRecordSource stageRs, boolean isQueryDeduped,
			String queryRsDbConfig, PersistableRecordSource masterRs,
			String refRsDbConfig, OabaLinkageType taskType) {
		this(modelConfigurationName, lowThreshold, highThreshold, blocking,
				stageRs.getId(), stageRs.getType(), isQueryDeduped,
				queryRsDbConfig, masterRs == null ? null : masterRs.getId(),
				masterRs == null ? null : masterRs.getType(), refRsDbConfig,
				taskType);
	}

	/**
	 * A internal constructor that allows field values to be set directly.
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
	 * @param qIsDeduped
	 *            if true, the query source is already deduplicated; if false,
	 *            the query source should be deduplicated before it is matched
	 *            against the reference source
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
	OabaParametersEntity(String modelConfigurationName, float lowThreshold,
			float highThreshold, String blocking, long sId, String sType,
			boolean qIsDeduped, String queryRsDbConfig, Long mId, String mType,
			String refRsDbConfig, OabaLinkageType taskType) {
		super(DV_OABA, modelConfigurationName, lowThreshold, highThreshold,
				blocking, sId, sType, qIsDeduped, queryRsDbConfig, mId, mType,
				refRsDbConfig, taskType, null, null);
	}

	@Override
	public String getModelConfigurationName() {
		return modelConfigName;
	}

	@Override
	public String getBlockingConfiguration() {
		return blockingConfiguration;
	}

	@Override
	public long getQueryRsId() {
		return queryRsId;
	}

	@Override
	public String getQueryRsType() {
		return queryRsType;
	}

	@Override
	public boolean isQueryRsDeduplicated() {
		return queryRsIsDeduplicated;
	}

	@Override
	public String getQueryRsDatabaseConfiguration() {
		return queryRsDatabaseConfiguration;
	}

	@Deprecated
	@Override
	public String getQueryToQueryBlockingConfiguration() {
		return null;
	}

	@Override
	public Long getReferenceRsId() {
		return referenceRsId;
	}

	@Override
	public String getReferenceRsType() {
		return referenceRsType;
	}

	@Override
	public String getReferenceRsDatabaseConfiguration() {
		return referenceRsDatabaseConfiguration;
	}

	@Deprecated
	@Override
	public String getQueryToReferenceBlockingConfiguration() {
		return null;
	}

	@Override
	public String toString() {
		return "OabaParametersEntity [id=" + getId() + ", uuid=" + getUUID()
				+ ", modelId=" + modelConfigName + ", lowThreshold="
				+ lowThreshold + ", highThreshold=" + highThreshold + ", task="
				+ task + "]";
	}

}
