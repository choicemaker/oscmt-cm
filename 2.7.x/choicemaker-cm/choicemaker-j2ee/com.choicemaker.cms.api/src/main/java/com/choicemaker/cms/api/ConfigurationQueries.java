package com.choicemaker.cms.api;

import java.util.List;

import com.choicemaker.cm.args.AbaSettings;
import com.choicemaker.cm.args.OabaParameters;
import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.PersistableRecordSource;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.args.TransitivityParameters;
import com.choicemaker.cm.oaba.api.AbstractParameters;

public interface ConfigurationQueries {

	// -- Named aggregations

	NamedConfiguration findNamedConfiguration(long id);

	NamedConfiguration findNamedConfigurationByName(String configName);

	List<NamedConfiguration> findAllNamedConfigurations();

	// -- Model configurations

	List<String> findAllModelConfigurationNames();

	// -- Transitivity parameters

	List<TransitivityParameters> findAllTransitivityParameters();

	TransitivityParameters findTransitivityParameters(long id);

	TransitivityParameters findTransitivityParametersByBatchJobId(long jobId);

	// -- Asynchronous Offline Automated Blocking Algorithm (OABA) parameters

	List<OabaParameters> findAllOabaParameters();

	OabaParameters findOabaParameters(long id);

	OabaParameters findOabaParametersByBatchJobId(long jobId);

	// -- General (OABA, Transitivity, etc) parameters

	List<AbstractParameters> findAllParameters();

	AbstractParameters findParameters(long id);

	List<AbaSettings> findAllAbaSettings();

	// -- Synchronous Automated Blocking Algorithm (ABA) settings

	AbaSettings findAbaSettings(long id);

	List<OabaSettings> findAllOabaSettings();

	// -- Asynchronous Offline Automated Blocking Algorithm (OABA) settings

	OabaSettings findOabaSettings(long id);

	OabaSettings findOabaSettingsByJobId(long jobId);

	// -- Server configurations

	List<ServerConfiguration> findAllServerConfigurations();

	List<String> findAllServerConfigurationNames();

	ServerConfiguration findServerConfiguration(long id);

	ServerConfiguration findServerConfigurationByName(String configName);

	ServerConfiguration findServerConfigurationByJobId(long jobId);

	List<ServerConfiguration> findServerConfigurationsByHostName(
			String hostName);

	List<ServerConfiguration> findServerConfigurationsByHostName(
			String hostName, boolean strict);

	// -- Persistable record sources

	PersistableRecordSource findPersistableSqlRecordSource(Long id, String type);

	List<PersistableRecordSource> findAllPersistableSqlRecordSources();

}