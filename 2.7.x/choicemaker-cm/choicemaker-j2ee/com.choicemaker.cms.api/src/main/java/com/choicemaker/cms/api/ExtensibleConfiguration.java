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
package com.choicemaker.cms.api;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.choicemaker.util.ReflectionUtils;
import com.choicemaker.util.StringUtils;
import com.choicemaker.util.TypedValue;

public final class ExtensibleConfiguration implements NamedConfigurationExt {

	/**
	 * Attribute names defined by NamedConfiguration interface. Populated by a
	 * static class initializer and immutable thereafter.
	 */
	private static final Set<String> _wellKnownAttributeNames = new HashSet<>();

	static {
		final Class<NamedConfiguration> c = NamedConfiguration.class;
		Set<String> retVal = new HashSet<>();
		for (Method m : c.getDeclaredMethods()) {
			final String mName = m.getName();
			String stem;
			// Document assumptions and convert accessor name to attribute name
			if (mName.startsWith("get")) {
				assert mName.length() > 3;
				assert m.getAnnotatedParameterTypes().length == 0;
				stem = mName.substring(3);
			} else if (mName.startsWith("is")) {
				assert mName.length() > 2;
				assert m.getAnnotatedParameterTypes().length == 0;
				stem = mName.substring(2);
			} else {
				throw new Error("unexpected method: " + mName);
			}
			String aName = StringUtils.camelCase(stem);
			boolean b = retVal.add(aName);
			assert b : aName + " must be unique";
		}
		_wellKnownAttributeNames.addAll(retVal);
	}

	public static Set<String> getWellKnownConfigurationAttributeNames() {
		Set<String> retVal =
			Collections.unmodifiableSet(_wellKnownAttributeNames);
		return retVal;
	}

	public static boolean isWellknownAttributeName(String name) {
		boolean retVal = false;
		if (name != null && name.trim().length() > 0) {
			name = StringUtils.camelCase(name);
			retVal = _wellKnownAttributeNames.contains(name);
		}
		return retVal;
	}

	public static Class<?> getWellknownValueType(String name) {
		Class<?> retVal = null;
		String aName = StringUtils.camelCase(name);
		// Work around a bug in ReflectionUtils that handles boolean return
		// types differently
		if (aName.equals("isQueryDeduplicated")) {
			retVal = boolean.class;
		} else if (isWellknownAttributeName(name)) {
			Method m = ReflectionUtils.getAccessor(NamedConfiguration.class,
					Object.class, name);
			retVal = m.getReturnType();
		}
		return retVal;
	}
	
	public static TypedValue<?> createTypedValue(String name, String value) {
		TypedValue<?>  retVal;
		Class<?> valueType = getWellknownValueType(name);
		if (valueType == null) {
			retVal = null;
		} else if (String.class.equals(valueType)) {
			retVal = new TypedValue<String>(String.class, value);
		} else if (int.class.equals(valueType)) {
			retVal = new TypedValue<Integer>(int.class, Integer.valueOf(value));
		} else if (float.class.equals(valueType)) {
			retVal = new TypedValue<Float>(float.class, Float.valueOf(value));
		} else if (long.class.equals(valueType)) {
			retVal = new TypedValue<Long>(long.class, Long.valueOf(value));
		} else if (boolean.class.equals(valueType)) {
			retVal = new TypedValue<Boolean>(boolean.class, Boolean.valueOf(value));
		} else {
			retVal = null;
		}
		return retVal;
	}

	/** Attribute names must be camel-cased */
	private final ConcurrentMap<String, TypedValue<?>> extendedAttributes =
		new ConcurrentHashMap<>();

	public ExtensibleConfiguration() {
	}

	public ExtensibleConfiguration(NamedConfiguration nc) {
		this.setTypedAttribute("id",
				new TypedValue<Long>(long.class, nc.getId()));
		this.setConfigurationName(nc.getConfigurationName());
		this.setConfigurationDescription(nc.getConfigurationDescription());
		this.setModelName(nc.getModelName());
		this.setLowThreshold(nc.getLowThreshold());
		this.setHighThreshold(nc.getHighThreshold());
		this.setTask(nc.getTask());
		this.setRigor(nc.getRigor());
		this.setDataSource(nc.getDataSource());
		this.setJdbcDriverClassName(nc.getJdbcDriverClassName());
		this.setBlockingConfiguration(nc.getBlockingConfiguration());
		this.setQuerySelection(nc.getQuerySelection());
		this.setQueryDatabaseConfiguration(nc.getQueryDatabaseConfiguration());
		this.setQueryDeduplicated(nc.isQueryDeduplicated());
		this.setReferenceSelection(nc.getReferenceSelection());
		this.setReferenceDatabaseConfiguration(
				nc.getReferenceDatabaseConfiguration());
		this.setReferenceDatabaseAccessor(nc.getReferenceDatabaseAccessor());
		this.setTransitivityFormat(nc.getTransitivityFormat());
		this.setTransitivityGraph(nc.getTransitivityGraph());
		this.setAbaMaxMatches(nc.getAbaMaxMatches());
		this.setAbaLimitPerBlockingSet(nc.getAbaLimitPerBlockingSet());
		this.setAbaLimitSingleBlockingSet(nc.getAbaLimitSingleBlockingSet());
		this.setAbaSingleTableBlockingSetGraceLimit(
				nc.getAbaSingleTableBlockingSetGraceLimit());
		this.setOabaMaxSingle(nc.getOabaMaxSingle());
		this.setOabaMaxBlockSize(nc.getOabaMaxBlockSize());
		this.setOabaMaxChunkSize(nc.getOabaMaxChunkSize());
		this.setOabaMaxOversized(nc.getOabaMaxOversized());
		this.setOabaMaxMatches(nc.getOabaMaxMatches());
		this.setOabaMinFields(nc.getOabaMinFields());
		this.setOabaInterval(nc.getOabaInterval());
		this.setServerMaxThreads(nc.getServerMaxThreads());
		this.setServerMaxFileEntries(nc.getServerMaxFileEntries());
		this.setServerMaxFilesCount(nc.getServerMaxFilesCount());
		this.setServerFileURI(nc.getServerFileURI());
		this.setReferenceDatabaseReader(nc.getReferenceDatabaseReader());
	}

	// -- Helper methods

	private String getAttributeValueAsString(String name) {
		assert name != null && name.trim().length() > 0 && name == name.trim();
		assert Character.isLowerCase(name.charAt(0));
		@SuppressWarnings("unchecked")
		TypedValue<String> tv =
			(TypedValue<String>) this.extendedAttributes.get(name);
		return tv.value;
	}

	private void setAttributeValue(String attributeName, String value) {
		TypedValue<String> tv = new TypedValue<>(String.class, value);
		this.extendedAttributes.put(attributeName, tv);
	}

	private int getAttributeValueAsInt(String name) {
		assert name != null && name.trim().length() > 0 && name == name.trim();
		assert Character.isLowerCase(name.charAt(0));
		@SuppressWarnings("unchecked")
		TypedValue<Integer> tv =
			(TypedValue<Integer>) this.extendedAttributes.get(name);
		return tv.value;
	}

	private void setAttributeValue(String attributeName, int value) {
		TypedValue<Integer> tv = new TypedValue<>(int.class, value);
		this.extendedAttributes.put(attributeName, tv);
	}

	private long getAttributeValueAsLong(String name) {
		assert name != null && name.trim().length() > 0 && name == name.trim();
		assert Character.isLowerCase(name.charAt(0));
		@SuppressWarnings("unchecked")
		TypedValue<Long> tv =
			(TypedValue<Long>) this.extendedAttributes.get(name);
		return tv.value;
	}

	// Deliberately not coded
	// private void setAttributeValue(String attributeName, long value) {
	// TypedValue<Long> tv = new TypedValue<>(long.class, value);
	// this.extendedAttributes.put(attributeName, tv);
	// }

	private float getAttributeValueAsFloat(String name) {
		assert name != null && name.trim().length() > 0 && name == name.trim();
		assert Character.isLowerCase(name.charAt(0));
		@SuppressWarnings("unchecked")
		TypedValue<Float> tv =
			(TypedValue<Float>) this.extendedAttributes.get(name);
		return tv.value;
	}

	private void setAttributeValue(String attributeName, float value) {
		TypedValue<Float> tv = new TypedValue<>(float.class, value);
		this.extendedAttributes.put(attributeName, tv);
	}

	private boolean getAttributeValueAsBoolean(String name) {
		assert name != null && name.trim().length() > 0 && name == name.trim();
		assert Character.isLowerCase(name.charAt(0));
		@SuppressWarnings("unchecked")
		TypedValue<Boolean> tv =
			(TypedValue<Boolean>) this.extendedAttributes.get(name);
		return tv.value;
	}

	private void setAttributeValue(String attributeName, boolean value) {
		TypedValue<Boolean> tv = new TypedValue<>(boolean.class, value);
		this.extendedAttributes.put(attributeName, tv);
	}

	// -- Public methods

	@Override
	public int getAbaLimitPerBlockingSet() {
		return getAttributeValueAsInt("abaLimitPerBlockingSet");
	}

	@Override
	public int getAbaLimitSingleBlockingSet() {
		return getAttributeValueAsInt("abaLimitSingleBlockingSet");
	}

	@Override
	public int getAbaMaxMatches() {
		return getAttributeValueAsInt("abaMaxMatches");
	}

	@Override
	public int getAbaSingleTableBlockingSetGraceLimit() {
		return getAttributeValueAsInt("abaSingleTableBlockingSetGraceLimit");
	}

	@Override
	public String getBlockingConfiguration() {
		return getAttributeValueAsString("blockingConfiguration");
	}

	@Override
	public String getConfigurationDescription() {
		return getAttributeValueAsString("configurationDescription");
	}

	@Override
	public String getConfigurationName() {
		return getAttributeValueAsString("configurationName");
	}

	@Override
	public String getDataSource() {
		return getAttributeValueAsString("dataSource");
	}

	@Override
	public float getHighThreshold() {
		return getAttributeValueAsFloat("highThreshold");
	}

	@Override
	public long getId() {
		return getAttributeValueAsLong("id");
	}

	@Override
	public String getJdbcDriverClassName() {
		return getAttributeValueAsString("jdbcDriverClassName");
	}

	@Override
	public float getLowThreshold() {
		return getAttributeValueAsFloat("lowThreshold");
	}

	@Override
	public String getModelName() {
		return getAttributeValueAsString("modelName");
	}

	@Override
	public int getOabaInterval() {
		return getAttributeValueAsInt("oabaInterval");
	}

	@Override
	public int getOabaMaxBlockSize() {
		return getAttributeValueAsInt("oabaMaxBlockSize");
	}

	@Override
	public int getOabaMaxChunkSize() {
		return getAttributeValueAsInt("oabaMaxChunkSize");
	}

	@Override
	public int getOabaMaxMatches() {
		return getAttributeValueAsInt("oabaMaxMatches");
	}

	@Override
	public int getOabaMaxOversized() {
		return getAttributeValueAsInt("oabaMaxOversized");
	}

	@Override
	public int getOabaMaxSingle() {
		return getAttributeValueAsInt("oabaMaxSingle");
	}

	@Override
	public int getOabaMinFields() {
		return getAttributeValueAsInt("oabaMinFields");
	}

	@Override
	public String getQueryDatabaseConfiguration() {
		return getAttributeValueAsString("queryDatabaseConfiguration");
	}

	@Override
	public String getQuerySelection() {
		return getAttributeValueAsString("querySelection");
	}

	@Override
	public String getRecordSourceType() {
		return getAttributeValueAsString("recordSourceType");
	}

	@Override
	public String getReferenceDatabaseAccessor() {
		return getAttributeValueAsString("referenceDatabaseAccessor");
	}

	@Override
	public String getReferenceDatabaseConfiguration() {
		return getAttributeValueAsString("referenceDatabaseConfiguration");
	}

	@Override
	public String getReferenceDatabaseReader() {
		return getAttributeValueAsString("referenceDatabaseReader");
	}

	@Override
	public String getReferenceSelection() {
		return getAttributeValueAsString("referenceSelection");
	}

	@Override
	public String getRigor() {
		return getAttributeValueAsString("rigor");
	}

	@Override
	public String getServerFileURI() {
		return getAttributeValueAsString("serverFileURI");
	}

	@Override
	public int getServerMaxFileEntries() {
		return getAttributeValueAsInt("serverMaxFileEntries");
	}

	@Override
	public int getServerMaxFilesCount() {
		return getAttributeValueAsInt("serverMaxFilesCount");
	}

	@Override
	public int getServerMaxThreads() {
		return getAttributeValueAsInt("serverMaxThreads");
	}

	@Override
	public String getTask() {
		return getAttributeValueAsString("task");
	}

	@Override
	public String getTransitivityFormat() {
		return getAttributeValueAsString("transitivityFormat");
	}

	@Override
	public String getTransitivityGraph() {
		return getAttributeValueAsString("transitivityGraph");
	}

	@Override
	public boolean isQueryDeduplicated() {
		return getAttributeValueAsBoolean("queryDeduplicated");
	}

	// -- Manipulators

	public void setConfigurationName(String configurationName) {
		setAttributeValue("configurationName", configurationName);
	}

	public void setConfigurationDescription(String configurationDescription) {
		setAttributeValue("configurationDescription", configurationDescription);
	}

	public void setModelName(String modelName) {
		setAttributeValue("modelName", modelName);
	}

	public void setLowThreshold(float lowThreshold) {
		setAttributeValue("lowThreshold", lowThreshold);
	}

	public void setHighThreshold(float highThreshold) {
		setAttributeValue("highThreshold", highThreshold);
	}

	public void setTask(String task) {
		setAttributeValue("task", task);
	}

	public void setRigor(String rigor) {
		setAttributeValue("rigor", rigor);
	}

	public void setDataSource(String dataSource) {
		setAttributeValue("dataSource", dataSource);
	}

	public void setJdbcDriverClassName(String jdbcDriverClassName) {
		setAttributeValue("jdbcDriverClassName", jdbcDriverClassName);
	}

	public void setBlockingConfiguration(String blockingConfiguration) {
		setAttributeValue("blockingConfiguration", blockingConfiguration);
	}

	public void setQuerySelection(String querySelection) {
		setAttributeValue("querySelection", querySelection);
	}

	public void setQueryDatabaseConfiguration(
			String queryDatabaseConfiguration) {
		setAttributeValue("queryDatabaseConfiguration",
				queryDatabaseConfiguration);
	}

	public void setQueryDeduplicated(boolean queryIsDeduplicated) {
		setAttributeValue("queryIsDeduplicated", queryIsDeduplicated);
	}

	public void setReferenceSelection(String referenceSelection) {
		setAttributeValue("referenceSelection", referenceSelection);
	}

	public void setReferenceDatabaseConfiguration(
			String referenceDatabaseConfiguration) {
		setAttributeValue("referenceDatabaseConfiguration",
				referenceDatabaseConfiguration);
	}

	public void setReferenceDatabaseAccessor(String rdba) {
		setAttributeValue("referenceDatabaseAccessor", rdba);
	}

	public void setReferenceDatabaseReader(String rdbr) {
		setAttributeValue("referenceDatabaseReader", rdbr);
	}

	public void setTransitivityFormat(String transitivityFormat) {
		setAttributeValue("transitivityFormat", transitivityFormat);
	}

	public void setTransitivityGraph(String transitivityGraph) {
		setAttributeValue("transitivityGraph", transitivityGraph);
	}

	public void setAbaMaxMatches(int abaMaxMatches) {
		setAttributeValue("abaMaxMatches", abaMaxMatches);
	}

	public void setAbaLimitPerBlockingSet(int abaLimitPerBlockingSet) {
		setAttributeValue("abaLimitPerBlockingSet", abaLimitPerBlockingSet);
	}

	public void setAbaLimitSingleBlockingSet(int abaLimitSingleBlockingSet) {
		setAttributeValue("abaLimitSingleBlockingSet",
				abaLimitSingleBlockingSet);
	}

	public void setAbaSingleTableBlockingSetGraceLimit(
			int abaSingleTableBlockingSetGraceLimit) {
		setAttributeValue("abaSingleTableBlockingSetGraceLimit",
				abaSingleTableBlockingSetGraceLimit);
	}

	public void setOabaMaxSingle(int oabaMaxSingle) {
		setAttributeValue("oabaMaxSingle", oabaMaxSingle);
	}

	public void setOabaMaxBlockSize(int oabaMaxBlockSize) {
		setAttributeValue("oabaMaxBlockSize", oabaMaxBlockSize);
	}

	public void setOabaMaxChunkSize(int oabaMaxChunkSize) {
		setAttributeValue("oabaMaxChunkSize", oabaMaxChunkSize);
	}

	public void setOabaMaxOversized(int oabaMaxOversized) {
		setAttributeValue("oabaMaxOversized", oabaMaxOversized);
	}

	public void setOabaMaxMatches(int oabaMaxMatches) {
		setAttributeValue("oabaMaxMatches", oabaMaxMatches);
	}

	public void setOabaMinFields(int oabaMinFields) {
		setAttributeValue("oabaMinFields", oabaMinFields);
	}

	public void setOabaInterval(int oabaInterval) {
		setAttributeValue("oabaInterval", oabaInterval);
	}

	public void setServerMaxThreads(int serverMaxThreads) {
		setAttributeValue("serverMaxThreads", serverMaxThreads);
	}

	public void setServerMaxFileEntries(int serverMaxFileEntries) {
		setAttributeValue("serverMaxFileEntries", serverMaxFileEntries);
	}

	public void setServerMaxFilesCount(int serverMaxFilesCount) {
		setAttributeValue("serverMaxFilesCount", serverMaxFilesCount);
	}

	public void setServerFileURI(String serverFileURI) {
		setAttributeValue("serverFileURI", serverFileURI);
	}

	@Override
	public <T> void setTypedAttribute(String attributeName,
			TypedValue<T> typedValue) {
		String name = StringUtils.camelCase(attributeName);
		this.extendedAttributes.put(name, typedValue);
	}

	@Override
	public <T> TypedValue<T> getTypedAttributeValue(String attributeName) {
		String name = StringUtils.camelCase(attributeName);
		@SuppressWarnings("unchecked")
		TypedValue<T> retVal =
			(TypedValue<T>) this.extendedAttributes.get(name);
		return retVal;
	}

	// -- Identity

	@Override
	public String toString() {
		return "NamedConfigurationBean [" + getId() + ", "
				+ getConfigurationName() + "]";
	}

}