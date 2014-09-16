/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     ChoiceMaker Technologies, Inc. - alternate minimal runtime engine
 *******************************************************************************/
package com.choicemaker.eclipse2.core.runtime;

/**
 * A configuration element, with its attributes and children, 
 * directly reflects the content and structure of the extension section
 * within the declaring plug-in's manifest (<code>plugin.xml</code>) file.
 * <p>
 * This interface also provides a way to create executable extension
 * objects.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface CMConfigurationElement {
	/**
	 * Creates and returns a new instance of the executable extension 
	 * identified by the named attribute of this configuration element.
	 * The named attribute value must contain a fully qualified name
	 * of a Java class implementing the executable extension.
	 * <p>
	 * The specified class is instantiated using its 0-argument public 
	 * constructor. If the specified class implements the
	 * <code>CMExecutableExtension</code> interface, the method
	 * <code>setInitializationData</code> is called, passing to the object
	 * the configuration information that was used to create it. 
	 * </p>
	 * <p>
	 * Unlike other methods on this object, invoking this method may activate 
	 * the plug-in.
	 * </p>
	 *
	 * @param propertyName the name of the property
	 * @return the executable instance
	 * @exception CMCoreException if an instance of the executable extension
	 *   could not be created for any reason.
	 * @see CMExecutableExtension#setInitializationData
	 */
	public Object createExecutableExtension(String propertyName) throws CMCoreException;
	/**
	 * Returns the named attribute of this configuration element, or
	 * <code>null</code> if none. 
	 * <p>
	 * The names of configuration element attributes
	 * are the same as the attribute names of the corresponding XML element.
	 * For example, the configuration markup 
	 * <pre>
	 * &lt;bg pattern="stripes"/&gt;
	 * </pre>
	 * corresponds to a configuration element named <code>"bg"</code>
	 * with an attribute named <code>"pattern"</code>
	 * with attribute value <code>"stripes"</code>.
	 * </p>
	 * <p> Note that any translation specified in the plug-in manifest
	 * file is automatically applied.
	 * </p>
	 *
	 * @see CMPluginDescriptor#getResourceString 
	 *
	 * @param name the name of the attribute
	 * @return attribute value, or <code>null</code> if none
	 */
	public String getAttribute(String name);
	/**
	 * Returns the named attribute of this configuration element, or
	 * <code>null</code> if none. 
	 * <p>
	 * The names of configuration element attributes
	 * are the same as the attribute names of the corresponding XML element.
	 * For example, the configuration markup 
	 * <pre>
	 * &lt;bg pattern="stripes"/&gt;
	 * </pre>
	 * corresponds to a configuration element named <code>"bg"</code>
	 * with an attribute named <code>"pattern"</code>
	 * with attribute value <code>"stripes"</code>.
	 * </p>
	 * <p>
	 * Note that any translation specified in the plug-in manifest
	 * file for this attribute is <b>not</b> automatically applied.
	 * </p>
	 *
	 * @param name the name of the attribute
	 * @return attribute value, or <code>null</code> if none
	 */
	public String getAttributeAsIs(String name);
	/**
	 * Returns the names of the attributes of this configuration element.
	 * Returns an empty array if this configuration element has no attributes.
	 * <p>
	 * The names of configuration element attributes
	 * are the same as the attribute names of the corresponding XML element.
	 * For example, the configuration markup 
	 * <pre>
	 * &lt;bg color="blue" pattern="stripes"/&gt;
	 * </pre>
	 * corresponds to a configuration element named <code>"bg"</code>
	 * with attributes named <code>"color"</code>
	 * and <code>"pattern"</code>.
	 * </p>
	 *
	 * @return the names of the attributes 
	 */
	public String[] getAttributeNames();
	/**
	 * Returns all configuration elements that are children of this
	 * configuration element. 
	 * Returns an empty array if this configuration element has no children.
	 * <p>
	 * Each child corresponds to a nested
	 * XML element in the configuration markup.
	 * For example, the configuration markup 
	 * <pre>
	 * &lt;view&gt;
	 * &nbsp&nbsp&nbsp&nbsp&lt;verticalHint&gt;top&lt;/verticalHint&gt;
	 * &nbsp&nbsp&nbsp&nbsp&lt;horizontalHint&gt;left&lt;/horizontalHint&gt;
	 * &lt;/view&gt;
	 * </pre>
	 * corresponds to a configuration element, named <code>"view"</code>,
	 * with two children.
	 * </p>
	 *
	 * @return the child configuration elements
	 */
	public CMConfigurationElement[] getChildren();
	/**
	 * Returns all child configuration elements with the given name. 
	 * Returns an empty array if this configuration element has no children
	 * with the given name.
	 *
	 * @param name the name of the child configuration element
	 * @return the child configuration elements with that name
	 * @see #getChildren
	 */
	public CMConfigurationElement[] getChildren(String name);
	/** 
	 * Returns the extension that declares this configuration element.
	 *
	 * @return the extension
	 */
	public CMExtension getDeclaringExtension();
	/**
	 * Returns the name of this configuration element. 
	 * The name of a configuration element is the same as
	 * the XML tag of the corresponding XML element. 
	 * For example, the configuration markup 
	 * <pre>
	 * &lt;wizard name="Create Project"/&gt; 
	 * </pre>
	 * corresponds to a configuration element named <code>"wizard"</code>.
	 *
	 * @return the name of this configuration element
	 */
	public String getName();
	/**
	 * Returns the text value of this configuration element.
	 * For example, the configuration markup 
	 * <pre>
	 * &lt;script lang="javascript"&gt;.\scripts\cp.js&lt;/script&gt;
	 * </pre>
	 * corresponds to a configuration element <code>"script"</code>
	 * with value <code>".\scripts\cp.js"</code>.
	 * <p> Values may span multiple lines (i.e., contain carriage returns
	 * and/or line feeds).
	 * <p> Note that any translation specified in the plug-in manifest
	 * file is automatically applied.
	 * </p>
	 *
	 * @see CMPluginDescriptor#getResourceString 
	 *
	 * @return the text value of this configuration element or <code>null</code>
	 */
	public String getValue();
	/**
	 * Returns the untranslated text value of this configuration element.
	 * For example, the configuration markup 
	 * <pre>
	 * &lt;script lang="javascript"&gt;.\scripts\cp.js&lt;/script&gt;
	 * </pre>
	 * corresponds to a configuration element <code>"script"</code>
	 * with value <code>".\scripts\cp.js"</code>.
	 * <p> Values may span multiple lines (i.e., contain carriage returns
	 * and/or line feeds).
	 * <p>
	 * Note that translation specified in the plug-in manifest
	 * file is <b>not</b> automatically applied.
	 * For example, the configuration markup 
	 * <pre>
	 * &lt;tooltip&gt;#hattip&lt;/tooltip&gt;
	 * </pre>
	 * corresponds to a configuration element, named <code>"tooltip"</code>,
	 * with value <code>"#hattip"</code>.
	 * </p>
	 *
	 * @return the untranslated text value of this configuration element or <code>null</code>
	 */
	public String getValueAsIs();
}
