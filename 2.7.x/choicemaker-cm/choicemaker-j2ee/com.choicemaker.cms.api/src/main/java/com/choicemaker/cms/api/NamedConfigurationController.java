package com.choicemaker.cms.api;

import java.util.List;

/**
 * In addition to managing NamedConfiguration entities, this interface also
 * provides lists of other named entities such as model configurations and
 * server configurations.
 *
 * @author rphall
 *
 */
public interface NamedConfigurationController {

	NamedConfiguration findNamedConfiguration(long id);

	NamedConfiguration findNamedConfigurationByName(String configName);

	List<NamedConfiguration> findAllNamedConfigurations();

	NamedConfiguration clone(NamedConfiguration nc);

	NamedConfiguration save(NamedConfiguration nc);

	void remove(NamedConfiguration nc);

	List<String> findAllModelConfigurationNames();

	List<String> findAllServerConfigurationNames();

}
