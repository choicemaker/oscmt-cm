/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cms.ejb;

import java.util.logging.Logger;

import com.choicemaker.cm.core.configure.xml.AbstractXmlSpecification;
import com.choicemaker.cm.core.configure.xml.XmlConfigurable;
import com.choicemaker.cms.api.UpdateDerivedFields;

/**
 * Provides standardized methods for
 * {@link UpdateDerivedFields.toXML toXML()},
 * {@link UpdateDerivedFields.getProperties() getProperties()} and
 * {@link UpdateDerivedFields.setProperties(Properties) setProperties(..)}
 * operations.
 * @author rphall
 */
public abstract class AbstractUpdateDerivedFields
	extends AbstractXmlSpecification
	implements UpdateDerivedFields {
		
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(AbstractUpdateDerivedFields.class.getName());
		
	/**
	 * Most implementations of UpdateDerivedFields will not need
	 * XmlConfigurable delegates, so this default implementation
	 * does nothing beside logging the ignored argument.
	 * see #getChildren()
	 */
	public void add(XmlConfigurable ignored) {
		String msg = "Ignoring XmlConfigurable delegate (type "
		+ (ignored == null ? "null" : ignored.getClass().getName() + ")");
		logger.warning(msg);
	}

	/**
	 * Most implementations of UpdateDerivedFields will not need
	 * XmlConfigurable delegates, so this default implementation
	 * does nothing beside logging the ignored argument.
	 * @see #add(XmlConfigurable)
	 */
	public XmlConfigurable[] getChildren() {
		return new XmlConfigurable[0];
	}

	/**
	 * Most implementations of UpdateDerivedFields will not <em>require</em>
	 * any configuration properties, so this method returns an
	 * empty (but non-null) array.
	 */
	public String[] getRequiredPropertyNames() {
		return new String[0];
	}

	/**
	 * Some implementations of UpdateDerivedFields <em>might</em> use
	 * optional configuration properties, so this method returns an
	 * empty (but non-null) array, which indicates all property names
	 * are allowed.
	 */
	public String[] getAllowedPropertyNames() {
		return new String[0];
	}

	/**
	 * Some implementations of UpdateDerivedFields <em>might</em> use
	 * optional configuration properties, so this default method always
	 * returns for any non-blank property name and any non-null property
	 * value. (Subclasses may be more fastidious about checking
	 * property values.)
	 * @param name the property name
	 * @param value the property value
	 */
	public boolean isAllowedPropertyValue(String name, String value) {
		return (name != null && name.trim().length() > 0 && value != null);
	}

}
