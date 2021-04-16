package com.choicemaker.cms.api;

import com.choicemaker.util.TypedValue;

public interface NamedConfigurationExt extends NamedConfiguration {

	<T> void setTypedAttribute(String attributeName, TypedValue<T> typedValue);
	
	<T> TypedValue<T> getTypedAttributeValue(String attributeName);

}
