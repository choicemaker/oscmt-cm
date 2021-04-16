package com.choicemaker.cm.oaba.api;

import com.choicemaker.cm.args.OabaLinkageType;
import com.choicemaker.cm.args.PersistentObject;

public interface AbstractParameters extends PersistentObject {

	float getLowThreshold();

	float getHighThreshold();

	OabaLinkageType getOabaLinkageType();

	String getType();

	String getModelConfigName();

	String getBlockingConfiguration();

	long getQueryRsId();

	String getQueryRsType();

	boolean isQueryRsIsDeduplicated();

	String getQueryRsDatabaseConfiguration();

	Long getReferenceRsId();

	String getReferenceRsType();

	String getReferenceRsDatabaseConfiguration();

	String getTask();

	String getFormat();

	String getGraph();

}