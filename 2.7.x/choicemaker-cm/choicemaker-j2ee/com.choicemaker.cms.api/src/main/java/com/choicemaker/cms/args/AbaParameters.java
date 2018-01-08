package com.choicemaker.cms.args;

import java.io.Serializable;

import com.choicemaker.cm.args.OabaLinkageType;

public interface AbaParameters extends Serializable {

	String getModelConfigurationName();

	OabaLinkageType getOabaLinkageType();

	float getLowThreshold();

	float getHighThreshold();

	String getBlockingConfiguration();

	Long getReferenceRsId();

	String getReferenceRsType();

	String getReferenceRsDatabaseConfiguration();

	String getQueryToReferenceBlockingConfiguration();

	String getDatabaseAccessorName();

	String getReferenceDatasource();

	String getDatabaseReaderName();

	String getReferenceQuery();

}
