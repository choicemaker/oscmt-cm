/*******************************************************************************
 * Copyright (c) 2003, 2021 ChoiceMaker LLC and others.
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
package com.choicemaker.cms.webapp.view;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.NamingException;

import com.choicemaker.cm.oaba.api.ServerConfigurationException;
import com.choicemaker.cms.api.NamedConfiguration;
import com.choicemaker.cms.api.NamedConfigurationController;
import com.choicemaker.cms.webapp.util.NameType;
import com.choicemaker.cms.webapp.util.NameValue;
import com.choicemaker.cms.webapp.util.NameValueComparison;
import com.choicemaker.cms.webapp.util.NamedConfigPresentation;
import com.choicemaker.util.ReflectionUtils;

@Named
@RequestScoped
public class BatchConfiguration {

	private static final Logger logger =
		Logger.getLogger(BatchConfiguration.class.getName());

	private static final AtomicReference<Map<String, Class<?>>> mapPropertyNameType =
		new AtomicReference<>();

	private Long namedConfigurationId;
	private Long comparisonConfigurationId;
	private List<SelectItem> configurations;

	@Inject
	private NamedConfigurationController ncController;

	private final NamedConfigPresentation presentation =
		new NamedConfigPresentation();

	@PostConstruct
	public void init() {
		this.configurations = new ArrayList<>();
		List<NamedConfiguration> ncs =
			ncController.findAllNamedConfigurations();
		for (NamedConfiguration nc : ncs) {
			SelectItem si =
				new SelectItem(nc.getId(), nc.getConfigurationName());
			this.configurations.add(si);
		}
	}

	public void submit() throws NamingException, ServerConfigurationException,
			URISyntaxException {
		logger.info("NOP");
	}

	public List<NamedConfiguration> getBatchConfigurations() {
		List<NamedConfiguration> retVal =
			ncController.findAllNamedConfigurations();
		return retVal;
	}

	public Long getNamedConfigurationId() {
		return namedConfigurationId;
	}

	public void setNamedConfigurationId(Long namedConfigurationId) {
		this.namedConfigurationId = namedConfigurationId;
	}

	public Long getComparisonConfigurationId() {
		return comparisonConfigurationId;
	}

	public void setComparisonConfigurationId(Long comparisonConfigurationId) {
		this.comparisonConfigurationId = comparisonConfigurationId;
	}

	public List<SelectItem> getConfigurations() {
		return configurations;
	}

	public void setConfigurations(List<SelectItem> configurations) {
		this.configurations = configurations;
	}

	public Map<String, Class<?>> getOrderedConfigurationTypeMap() {
		Map<String, Class<?>> retVal = mapPropertyNameType.get();
		if (retVal == null) {
			Set<NameType> nameTypes = presentation.getNCPropertyNameTypes();
			Map<String, Class<?>> update = new LinkedHashMap<>();
			for (NameType nameType : nameTypes) {
				update.put(nameType.getName(), nameType.getClass());
			}
			boolean updated = mapPropertyNameType.compareAndSet(null, update);
			if (updated) {

			} else {

			}
			retVal = mapPropertyNameType.get();
		}
		assert retVal != null;
		return Collections.unmodifiableMap(retVal);
	}

	public List<String> getOrderedConfigurationPropertyNames() {
		Set<NameType> nameTypes = presentation.getNCPropertyNameTypes();

		List<String> retVal = new ArrayList<>();
		for (NameType nameType : nameTypes) {
			retVal.add(nameType.getName());
		}

		return Collections.unmodifiableList(retVal);
	}

	public List<NameValue> getOrderedConfigurationNameValues(Long id) {
		List<NameValue> retVal = new ArrayList<>();
		for (String name : getOrderedConfigurationPropertyNames()) {
			String value = getConfigurationValue(id, name);
			NameValue nameValue = new NameValue(name, value);
			retVal.add(nameValue);
		}

		return Collections.unmodifiableList(retVal);
	}

	public List<NameValue> getOrderedConfigurationNameValues() {
		return getOrderedConfigurationNameValues(getNamedConfigurationId());
	}

	public List<NameValueComparison> getOrderedNameValueComparisons(Long id, Long compareId) {
		List<NameValueComparison> retVal = new ArrayList<>();
		for (String name : getOrderedConfigurationPropertyNames()) {
			String value = getConfigurationValue(id, name);
			String comparison = getConfigurationValue(compareId, name);
			NameValueComparison nvc = new NameValueComparison(name, value, comparison);
			retVal.add(nvc);
		}

		return Collections.unmodifiableList(retVal);
	}

	public List<NameValueComparison> getOrderedNameValueComparisons() {
		return getOrderedNameValueComparisons(getNamedConfigurationId(),
				getComparisonConfigurationId());		
	}

	public String getConfigurationValue(Long id, String propertyName) {
		String retVal = null;
		if (id != null && propertyName != null) {
			NamedConfiguration nc = ncController.findNamedConfiguration(id);
			Class<?> c = getOrderedConfigurationTypeMap().get(propertyName);
			if (nc != null && c != null) {
				Object o = null;
				try {
					o = ReflectionUtils.getProperty(nc, c, propertyName);
				} catch (Error | Exception x) {
					logger.warning(x.toString());
				}
				if (o != null) {
					retVal = o.toString();
				}
			}
		}
		return retVal;
	}

}
