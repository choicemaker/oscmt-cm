package com.choicemaker.cmit;

import java.util.List;

import javax.ejb.Local;

import com.choicemaker.cm.args.OabaParameters;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.core.base.Thresholds;
import com.choicemaker.cm.io.blocking.automated.offline.core.OabaProcessing;
import com.choicemaker.cm.io.blocking.automated.offline.server.ejb.OabaJob;
import com.choicemaker.cm.io.blocking.automated.offline.server.impl.OabaParametersEntity;
import com.choicemaker.cmit.utils.TestEntityCounts;

@Local
public interface OabaTestController {

	/**
	 * Synthesizes the name of a fake modelId configuration using the specified
	 * tag which may be null
	 */
	String createRandomModelConfigurationName(String tag);

	Thresholds createRandomThresholds();

	OabaParametersEntity createBatchParameters(String tag, TestEntityCounts te);

	ServerConfiguration getDefaultServerConfiguration();

	List<OabaParameters> findAllOabaParameters();

	List<OabaJob> findAllOabaJobs();

	List<OabaProcessing> findAllOabaProcessing();

}