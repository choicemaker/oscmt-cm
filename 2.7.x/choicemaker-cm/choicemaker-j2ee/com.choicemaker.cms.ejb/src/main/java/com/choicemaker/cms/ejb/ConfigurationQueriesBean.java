package com.choicemaker.cms.ejb;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import com.choicemaker.cm.args.AbaSettings;
import com.choicemaker.cm.args.OabaParameters;
import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.PersistableRecordSource;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.args.TransitivityParameters;
import com.choicemaker.cm.oaba.api.AbstractParameters;
import com.choicemaker.cm.oaba.api.OabaParametersController;
import com.choicemaker.cm.oaba.api.OabaSettingsController;
import com.choicemaker.cm.oaba.api.ServerConfigurationController;
import com.choicemaker.cm.oaba.api.SqlRecordSourceController;
import com.choicemaker.cm.transitivity.api.TransitivityParametersController;
import com.choicemaker.cms.api.ConfigurationQueries;
import com.choicemaker.cms.api.NamedConfiguration;
import com.choicemaker.cms.api.NamedConfigurationController;
import com.choicemaker.cms.api.remote.ConfigurationQueriesRemote;

@Stateless
@Local(ConfigurationQueries.class)
@Remote(ConfigurationQueriesRemote.class)
public class ConfigurationQueriesBean implements ConfigurationQueriesRemote {

	@EJB
	private NamedConfigurationController ncController;

	@EJB
	private OabaParametersController opController;

	@EJB
	private OabaSettingsController osController;

	@EJB
	private ServerConfigurationController scController;

	@EJB
	private SqlRecordSourceController sqlController;

	@EJB
	private TransitivityParametersController tpController;

	// -- Named aggregations

	@Override
	public NamedConfiguration findNamedConfiguration(long id) {
		return ncController.findNamedConfiguration(id);
	}

	@Override
	public NamedConfiguration findNamedConfigurationByName(String configName) {
		return ncController.findNamedConfigurationByName(configName);
	}

	@Override
	public List<NamedConfiguration> findAllNamedConfigurations() {
		return ncController.findAllNamedConfigurations();
	}

	// -- Model configurations

	@Override
	public List<String> findAllModelConfigurationNames() {
		return ncController.findAllModelConfigurationNames();
	}

	// -- Transitivity parameters

	@Override
	public List<TransitivityParameters> findAllTransitivityParameters() {
		return tpController.findAllTransitivityParameters();
	}

	@Override
	public TransitivityParameters findTransitivityParameters(long id) {
		return tpController.findTransitivityParameters(id);
	}

	@Override
	public TransitivityParameters findTransitivityParametersByBatchJobId(
			long jobId) {
		return tpController.findTransitivityParametersByBatchJobId(jobId);
	}

	// -- Asynchronous Offline Automated Blocking Algorithm (OABA) parameters

	@Override
	public List<OabaParameters> findAllOabaParameters() {
		return opController.findAllOabaParameters();
	}

	@Override
	public OabaParameters findOabaParameters(long id) {
		return opController.findOabaParameters(id);
	}

	@Override
	public OabaParameters findOabaParametersByBatchJobId(long jobId) {
		return opController.findOabaParametersByBatchJobId(jobId);
	}

	// -- General (OABA, Transitivity, etc) parameters

	@Override
	public List<AbstractParameters> findAllParameters() {
		throw new Error("not yet implemented");
	}

	@Override
	public AbstractParameters findParameters(long id) {
		throw new Error("not yet implemented");
	}

	// -- Synchronous Automated Blocking Algorithm (ABA) settings

	@Override
	public List<AbaSettings> findAllAbaSettings() {
		throw new Error("not yet implemented");
	}

	@Override
	public AbaSettings findAbaSettings(long id) {
		throw new Error("not yet implemented");
	}

	// -- Asynchronous Offline Automated Blocking Algorithm (OABA) settings

	@Override
	public List<OabaSettings> findAllOabaSettings() {
		return osController.findAllOabaSettings();
	}

	@Override
	public OabaSettings findOabaSettings(long id) {
		return osController.findOabaSettings(id);
	}

	@Override
	public OabaSettings findOabaSettingsByJobId(long jobId) {
		return osController.findOabaSettingsByJobId(jobId);
	}

	// -- Server configurations

	@Override
	public List<ServerConfiguration> findAllServerConfigurations() {
		throw new Error("not yet implemented");
	}

	@Override
	public List<String> findAllServerConfigurationNames() {
		return ncController.findAllServerConfigurationNames();
	}

	@Override
	public ServerConfiguration findServerConfiguration(long id) {
		return scController.findServerConfiguration(id);
	}

	@Override
	public ServerConfiguration findServerConfigurationByName(
			String configName) {
		throw new Error("not yet implemented");
	}

	@Override
	public ServerConfiguration findServerConfigurationByJobId(long jobId) {
		throw new Error("not yet implemented");
	}

	@Override
	public List<ServerConfiguration> findServerConfigurationsByHostName(
			String hostName) {
		throw new Error("not yet implemented");
	}

	@Override
	public List<ServerConfiguration> findServerConfigurationsByHostName(
			String hostName, boolean strict) {
		throw new Error("not yet implemented");
	}

	// -- SQL record sources

	@Override
	public PersistableRecordSource findPersistableSqlRecordSource(Long id,
			String type) {
		return sqlController.find(id, type);
	}

	@Override
	public List<PersistableRecordSource> findAllPersistableSqlRecordSources() {
		return sqlController.findAll();
	}

}
