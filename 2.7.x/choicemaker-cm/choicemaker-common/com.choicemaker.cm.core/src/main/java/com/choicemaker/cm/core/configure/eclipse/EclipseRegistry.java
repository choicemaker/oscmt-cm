/*
 * Copyright (c) 2001, 2009 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.core.configure.eclipse;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.choicemaker.cm.core.configure.xml.IDocument;
import com.choicemaker.cm.core.configure.xml.NotFoundException;
import com.choicemaker.cm.core.configure.xml.NotUniqueException;
import com.choicemaker.cm.core.configure.xml.XmlConfigurable;
import com.choicemaker.cm.core.configure.xml.XmlConfigurablesRegistry;
import com.choicemaker.e2.CMConfigurationElement;
import com.choicemaker.e2.CMExtension;
import com.choicemaker.e2.CMPluginDescriptor;
import com.choicemaker.e2.platform.CMPlatformUtils;
import com.choicemaker.util.Precondition;

/**
 * An eclipse-based registry of XmlConfigurables.
 * @author rphall
 */
public class EclipseRegistry
	implements XmlConfigurablesRegistry {

	private static final Logger logger =
		Logger.getLogger(EclipseRegistry.class.getName());

	private final Object _mapSynch = new Object();
	private final Map _configurables = new LinkedHashMap();
	private final String uniqueExtensionPointId;

	protected EclipseRegistry(String uniqueExtensionPointId) {
		Precondition.assertNonNullArgument(
			"null extension point",
			uniqueExtensionPointId);
		this.uniqueExtensionPointId = uniqueExtensionPointId;
		initialize();
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.base.configure.XmlConfigurablesRegistry#get(java.lang.String)
	 */
	@Override
	public XmlConfigurable get(String uniqueId) throws NotFoundException {
		Precondition.assertNonEmptyString(uniqueId);
		XmlConfigurable retVal = null;
		synchronized (this._mapSynch) {
			retVal = (XmlConfigurable) this._getConfigurables().get(uniqueId);
			if (retVal == null) {
				String msg =
					"No configurable registered under the id '"
						+ uniqueId
						+ "', returning null";
				logger.severe(msg);
				throw new NotFoundException(msg);
			}
		}
		return retVal;
	}

	/** Protected use only -- the internal map is not cloned */
	protected Map _getConfigurables() {
		return this._configurables;
	}

	/** Returns a shallow copy of the entries in this registry */
	public Map getConfigurables() {
		Map retVal =  new LinkedHashMap();
		retVal.putAll(this._configurables);
		return retVal;
	}

	public String getUniqueExtensionPointId() {
		return uniqueExtensionPointId;
	}

	private void initialize() {
		try {

			final EclipseXmlSpecificationParser parser =
				new EclipseXmlSpecificationParser();

			final CMExtension[] extensions =
				CMPlatformUtils.getExtensions(this.getUniqueExtensionPointId());
			if (extensions == null || extensions.length == 0) {
				String msg =
					"No such extensions for '"
							+ this.getUniqueExtensionPointId() + "'";
				logger.warning(msg);
			}

			for (int i = 0; i < extensions.length; i++) {
				try {
					CMExtension ext = extensions[i];
					CMPluginDescriptor descriptor =
						ext.getDeclaringPluginDescriptor();
					ClassLoader pluginClassLoader =
						descriptor.getPluginClassLoader();

					CMConfigurationElement[] els =
						ext.getConfigurationElements();
					// assert els.length == 1;
					CMConfigurationElement elConfigurable = els[0];
					try {
						IDocument document =
							new EclipseDocument(elConfigurable);
						XmlConfigurable configurable =
							parser.fromXML(pluginClassLoader, document);
						register(ext.getUniqueIdentifier(), configurable);
					} catch (Exception x2) {
						String msg =
							"Unable to register configurable for extension "
								+ i
								+ " of the extension point '"
								+ this.getUniqueExtensionPointId()
								+ "' in the plug-in '"
								+ descriptor.getUniqueIdentifier()
								+ "' -- CONTINUING: ";
						logger.severe(msg + x2);
					}

				} catch (Exception x1) {
					String msg =
						"Unable to get extension "
							+ i
							+ " of the extension point '"
							+ this.getUniqueExtensionPointId()
							+ "' -- CONTINUING: ";
					logger.severe(msg + x1);
				}
			} // i, extensions

		} catch (Exception x0) {
			String msg =
				"Unable to get extension point '"
					+ this.getUniqueExtensionPointId()
					+ "' -- FAILED: ";
			logger.severe(msg + x0);
			throw new RuntimeException(msg + ": " + x0.toString());
		}
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.base.configure.XmlConfigurablesRegistry#register(java.lang.String, com.choicemaker.cm.core.base.configure.XmlConfigurable)
	 */
	@Override
	public void register(String uniqueId, XmlConfigurable configurable)
		throws NotUniqueException {
		Precondition.assertNonEmptyString(uniqueId);
		Precondition.assertNonNullArgument("null configurable", configurable);
		synchronized (this._mapSynch) {
			// Check if a configurable is already registered under this uniqueId
			XmlConfigurable test =
				(XmlConfigurable) this._getConfigurables().get(uniqueId);
			if (test != null && !test.equals(configurable)) {
				String msg = "non-unique id: '" + uniqueId + "'";
				logger.severe(msg);
				throw new NotUniqueException(msg);
			} else if (test != null) {
				String msg =
					"This configurable is already registered under the id ("
						+ uniqueId
						+ ")";
				logger.warning(msg);
			} else {
				this._getConfigurables().put(uniqueId, configurable);
				String msg =
					"Registered a configurable under the id '" + uniqueId + "'";
				logger.fine(msg);
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.base.configure.XmlConfigurablesRegistry#remove(java.lang.String)
	 */
	@Override
	public void remove(String uniqueId) {
		Precondition.assertNonEmptyString(uniqueId);
		synchronized (this._mapSynch) {
			Object test = this._getConfigurables().remove(uniqueId);
			if (test == null) {
				String msg =
					"No configurable registered under the id '"
						+ uniqueId
						+ "'";
				logger.fine(msg);
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.base.configure.XmlConfigurablesRegistry#update(java.lang.String, com.choicemaker.cm.core.base.configure.XmlConfigurable)
	 */
	@Override
	public void update(String uniqueId, XmlConfigurable configurable) {
		Precondition.assertNonEmptyString(uniqueId);
		Precondition.assertNonNullArgument("null configurable", configurable);
		synchronized (this._mapSynch) {
			// Check if a configurable is already registered under this uniqueId
			XmlConfigurable test =
				(XmlConfigurable) this._getConfigurables().get(uniqueId);
			if (test != null && !test.equals(configurable)) {
				this._getConfigurables().put(uniqueId, configurable);
				String msg =
					"Modified the configurable registered under the id '"
						+ uniqueId
						+ "'";
				logger.fine(msg);
			} else if (test != null) {
				String msg =
					"No change to the configurable registered under the id '"
						+ uniqueId
						+ "'";
				logger.info(msg);
			} else {
				this._getConfigurables().put(uniqueId, configurable);
				String msg =
					"Registered a configurable under the id '" + uniqueId + "'";
				logger.fine(msg);
			}
		}
	}

}
