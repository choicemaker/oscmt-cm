/*******************************************************************************
 * Copyright (c) 2003, 2018 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc  - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
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
