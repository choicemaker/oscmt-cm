package com.choicemaker.cms.zzz_todo;

import java.util.List;

//import com.choicemaker.cms.urm.impl.NamedConfigurationEntity;

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

//	NamedConfigurationEntity clone(NamedConfiguration nc);

	NamedConfiguration save(NamedConfiguration nc);

	void remove(NamedConfiguration nc);

	List<String> findAllModelConfigurationNames();

	List<String> findAllServerConfigurationNames();

}