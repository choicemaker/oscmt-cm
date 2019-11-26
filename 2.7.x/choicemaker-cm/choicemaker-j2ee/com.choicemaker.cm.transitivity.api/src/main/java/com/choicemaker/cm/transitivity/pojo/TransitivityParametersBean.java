/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.pojo;

import java.util.UUID;

import com.choicemaker.client.api.GraphPropertyBean;
import com.choicemaker.client.api.IGraphProperty;
import com.choicemaker.cm.args.AnalysisResultFormat;
import com.choicemaker.cm.args.OabaLinkageType;
import com.choicemaker.cm.args.TransitivityParameters;

public class TransitivityParametersBean implements TransitivityParameters {

	// Copied from TransitivityParametersJPA
	public static final String DV_TRANS = "TRANS";

	private static final long serialVersionUID = 271L;

	protected final String blockingConfiguration;

	protected final String format;

	protected String graph;

	protected float highThreshold;

	protected long id = 0;

	protected float lowThreshold;

	protected final String modelConfigName;

	protected int optLock = 0;

	protected final String queryRsDatabaseConfiguration;

	protected final long queryRsId;

	protected final boolean queryRsIsDeduplicated;

	protected final String queryRsType;

	protected final String referenceRsDatabaseConfiguration;

	protected final Long referenceRsId;

	protected final String referenceRsType;

	protected final String task;

	protected final String type;

	protected String uuid = UUID.randomUUID().toString();

	public TransitivityParametersBean(String modelConfigurationName, float lowThreshold,
			float highThreshold, String blocking, long sId, String sType,
			boolean qIsDeduplicated, String queryRsDbConfig, Long mId,
			String mType, String refRsDbConfig, String taskType,
			String format, String graph) {

		this.type = DV_TRANS;
		this.modelConfigName = modelConfigurationName;
		this.lowThreshold = lowThreshold;
		this.highThreshold = highThreshold;
		this.blockingConfiguration = blocking;
		this.queryRsId = sId;
		this.queryRsType = sType;
		this.queryRsIsDeduplicated = qIsDeduplicated;
		this.queryRsDatabaseConfiguration = queryRsDbConfig;
		this.referenceRsId = mId;
		this.referenceRsType = mType;
		this.referenceRsDatabaseConfiguration = refRsDbConfig;
		this.task = taskType;
		this.format = format;
		this.graph = graph;
	}

	public TransitivityParametersBean(TransitivityParameters tp) {
		this(tp.getModelConfigurationName(), tp.getLowThreshold(),
				tp.getHighThreshold(), tp.getBlockingConfiguration(),
				tp.getQueryRsId(), tp.getQueryRsType(),
				tp.isQueryRsDeduplicated(),
				tp.getQueryRsDatabaseConfiguration(), tp.getReferenceRsId(),
				tp.getReferenceRsType(),
				tp.getReferenceRsDatabaseConfiguration(),
				tp.getOabaLinkageType().name(), tp.getAnalysisResultFormat().name(),
				tp.getGraphProperty().getName());
	}

	public AnalysisResultFormat getAnalysisResultFormat() {
		return AnalysisResultFormat.valueOf(format);
	}

	public String getBlockingConfiguration() {
		return this.blockingConfiguration;
	}

	public String getFormat() {
		return format;
	}

	public String getGraph() {
		return graph;
	}

	public IGraphProperty getGraphProperty() {
		return new GraphPropertyBean(graph);
	}

	public final float getHighThreshold() {
		return highThreshold;
	}

	public long getId() {
		return id;
	}

	public final float getLowThreshold() {
		return lowThreshold;
	}

	public String getModelConfigName() {
		return modelConfigName;
	}

	public String getModelConfigurationName() {
		return this.modelConfigName;
	}

	public OabaLinkageType getOabaLinkageType() {
		return OabaLinkageType.valueOf(this.task);
	}

	public final int getOptLock() {
		return optLock;
	}

	public String getQueryRsDatabaseConfiguration() {
		return queryRsDatabaseConfiguration;
	}

	public long getQueryRsId() {
		return this.queryRsId;
	}

	public String getQueryRsType() {
		return this.queryRsType;
	}

	@Deprecated
	public String getQueryToQueryBlockingConfiguration() {
		return null;
	}

	@Deprecated
	public String getQueryToReferenceBlockingConfiguration() {
		return null;
	}

	public String getReferenceRsDatabaseConfiguration() {
		return referenceRsDatabaseConfiguration;
	}

	public Long getReferenceRsId() {
		return this.referenceRsId;
	}

	public String getReferenceRsType() {
		return this.referenceRsType;
	}

	public String getTask() {
		return task;
	}

	public String getType() {
		return type;
	}

	public final String getUUID() {
		return uuid;
	}

	public boolean isPersistent() {
		return false;
	}

	public boolean isQueryRsDeduplicated() {
		return queryRsIsDeduplicated;
	}

	public boolean isQueryRsIsDeduplicated() {
		return queryRsIsDeduplicated;
	}

	public void setGraph(String graph) {
		this.graph = graph;
	}

	public void setHighThreshold(float highThreshold) {
		this.highThreshold = highThreshold;
	}

	public void setLowThreshold(float lowThreshold) {
		this.lowThreshold = lowThreshold;
	}

	public String toString() {
		return "TransitivityParametersEntity [id=" + id + ", uuid=" + getUUID()
				+ ", modelId=" + modelConfigName + ", lowThreshold="
				+ lowThreshold + ", highThreshold=" + highThreshold + ", graph="
				+ graph + "]";
	}

}
