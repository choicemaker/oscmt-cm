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
package com.choicemaker.cm.core.base;

import java.io.NotSerializableException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import com.choicemaker.cm.core.ChoiceMakerExtensionPoint;
import com.choicemaker.cm.core.IRecordSourceSerializationRegistry;
import com.choicemaker.cm.core.IRecordSourceSerializer;
import com.choicemaker.cm.core.RecordSource;
import com.choicemaker.e2.CMConfigurationElement;
import com.choicemaker.e2.CMExtension;
import com.choicemaker.e2.CMPluginDescriptor;
import com.choicemaker.e2.platform.CMPlatformUtils;
import com.choicemaker.util.Precondition;
import com.choicemaker.util.StringUtils;

/**
 * An eclipse-based registry of record source serializers.
 * @author rphall
 */
public class DefaultRecordSourceSerializationRegistry
	implements IRecordSourceSerializationRegistry {

	private static DefaultRecordSourceSerializationRegistry _instance;
	private static Object _instanceSynch = new Object();

	private static final Logger log =
		Logger.getLogger(DefaultRecordSourceSerializationRegistry.class.getName());

	/**
	 * The extension point,
	 * <code>com.choicemaker.cm.urm.updateDerivedFields</code>
	 */
	public static final String SERIALIZABLE_RECORD_SOURCE_EXTENSION_POINT =
		ChoiceMakerExtensionPoint.CM_CORE_RSSERIALIZER;

	/** The serializer priority attribute, <code>priority</code> */
	public static final String SERIALIZABLE_RECORD_SOURCE_PRIORITY = "priority"; //$NON-NLS-1$

	// For brevity in the code that follows
	private static final String CLASS =
		AbstractRecordSourceSerializer.SERIALIZABLE_RECORD_SOURCE_CLASS;
	private static final int DEFAULT_PRIORITY =
		IRecordSourceSerializationRegistry
			.SERIALIZABLE_RECORD_SOURCE_DEFAULT_PRIORITY;
	private static final String POINT =
		SERIALIZABLE_RECORD_SOURCE_EXTENSION_POINT;
	private static final String PRIORITY = SERIALIZABLE_RECORD_SOURCE_PRIORITY;
//	private static final String PROPERTIES =
//		AbstractRecordSourceSerializer.SERIALIZABLE_RECORD_SOURCE_PROPERTIES;
	private static final String PROPERTY =
		AbstractRecordSourceSerializer.SERIALIZABLE_RECORD_SOURCE_PROPERTY;
	private static final String PROPERTY_NAME =
		AbstractRecordSourceSerializer
			.SERIALIZABLE_RECORD_SOURCE_PROPERTY_NAME;
	private static final String PROPERTY_VALUE =
		AbstractRecordSourceSerializer
			.SERIALIZABLE_RECORD_SOURCE_PROPERTY_VALUE;
//	private static final String SERIALIZER =
//		AbstractRecordSourceSerializer.RECORD_SOURCE_SERIALIZER;

	public static DefaultRecordSourceSerializationRegistry getInstance() {
		if (_instance == null) {
			synchronized (_instanceSynch) {
				if (_instance == null) {
					_instance = new DefaultRecordSourceSerializationRegistry();
				}
			}
		}
		return _instance;
	}

	private final List serializerRegistry = new LinkedList();

	private DefaultRecordSourceSerializationRegistry() {
		try {
			initialize();
		} catch (Exception x) {
			String msg =
				"Exception thrown during registry initialization: "
					+ x.toString();
			log.severe(msg);
			throw new RuntimeException(msg);
		}
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.base.IRecordSourceSerializer#hasSerializer(com.choicemaker.cm.core.base.RecordSource)
	 */
	@Override
	public boolean hasSerializer(RecordSource rs) {
		boolean retVal = false;
		for (Iterator i = this.serializerRegistry.iterator();
			!retVal && i.hasNext();
			) {
			PrioritizedSerializer ps = (PrioritizedSerializer) i.next();
			retVal = ps.serializer.canSerialize(rs);
		}
		return retVal;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.base.IRecordSourceSerializer#hasSerializer(java.lang.String)
	 */
	@Override
	public boolean hasSerializer(String url) {
		boolean retVal = false;
		for (Iterator i = this.serializerRegistry.iterator();
			!retVal && i.hasNext();
			) {
			PrioritizedSerializer ps = (PrioritizedSerializer) i.next();
			retVal = ps.serializer.canSerialize(url);
		}
		return retVal;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.base.IRecordSourceSerializationRegistry#getPrioritizedInstanceSerializers()
	 */
	@Override
	public PrioritizedSerializer[] getPrioritizedSerializers() {
		PrioritizedSerializer[] retVal =
			new PrioritizedSerializer[this.serializerRegistry.size()];
		retVal =
			(PrioritizedSerializer[]) this.serializerRegistry.toArray(retVal);
		return retVal;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.base.IRecordSourceSerializer#getSerializableRecordSource(com.choicemaker.cm.core.base.RecordSource)
	 */
	@Override
	public IRecordSourceSerializer getRecordSourceSerializer(RecordSource rs)
		throws NotSerializableException {
		Precondition.assertNonNullArgument("null record source", rs);

		IRecordSourceSerializer retVal = null;
		for (Iterator i = this.serializerRegistry.iterator();
			(retVal == null) && i.hasNext();
			) {
			PrioritizedSerializer ps = (PrioritizedSerializer) i.next();
			if (ps.serializer.canSerialize(rs)) {
				retVal = ps.serializer;
			}
		}
		if (retVal == null) {
			String msg =
				"Unable to find record source serializer for '"
					+ (rs == null ? null : rs.toString())
					+ "'";
			log.severe(msg);
			throw new NotSerializableException(msg);
		}
		return retVal;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.base.IRecordSourceSerializer#getSerializableRecordSource(java.lang.String)
	 */
	@Override
	public IRecordSourceSerializer getRecordSourceSerializer(String recordsourceURI)
		throws NotSerializableException {
		Precondition.assertNonEmptyString(recordsourceURI);

		IRecordSourceSerializer retVal = null;
		for (Iterator i = this.serializerRegistry.iterator();
			(retVal == null) && i.hasNext();
			) {
			PrioritizedSerializer ps = (PrioritizedSerializer) i.next();
			if (ps.serializer.canSerialize(recordsourceURI)) {
				retVal = ps.serializer;
			}
		}
		if (retVal == null) {
			String msg =
				"Unable to find record source serializer for '"
					+ recordsourceURI
					+ "'";
			log.severe(msg);
			throw new NotSerializableException(msg);
		}
		return retVal;
	}

	private void initialize() {
		try {
			CMExtension[] extensions = CMPlatformUtils.getExtensions(POINT);

			for (int i = 0; i < extensions.length; i++) {
				try {
					CMExtension ext = extensions[i];
					CMPluginDescriptor descriptor =
						ext.getDeclaringPluginDescriptor();
					ClassLoader pluginClassLoader =
						descriptor.getPluginClassLoader();

					CMConfigurationElement[] els =
						ext.getConfigurationElements();
					// assert els.length >= 1;
					// assert els.length <= 2;
					CMConfigurationElement elSerializer = els[0];
					CMConfigurationElement elProperties = null;
					if (els.length == 2) {
						elProperties = els[1];
					}
					try {
						PrioritizedSerializer serializer =
							instantiateSerializer(
								elSerializer,
								elProperties,
								pluginClassLoader);

						// Don't use registerPrioritizedSerializer(..),
						// because it sorts on every registration.
						// Instead, defer sorting until all extensions
						// have been read.
						serializerRegistry.add(serializer);
					} catch (Exception x2) {
						String msg =
							"Unable to register serializer for extension "
								+ i
								+ " of the extension point '"
								+ POINT
								+ "' in the plug-in '"
								+ descriptor.getUniqueIdentifier()
								+ "' -- CONTINUING: ";
						log.severe(msg + x2);
					}

				} catch (Exception x1) {
					String msg =
						"Unable to get extension "
							+ i
							+ " of the extension point '"
							+ POINT
							+ "' -- CONTINUING: ";
					log.severe(msg + x1);
				}
			} // i, extensions
			Collections.sort(serializerRegistry);

		} catch (Exception x0) {
			String msg =
				"Unable to get extension point '" + POINT + "' -- FAILED: ";
			log.severe(msg + x0);
			throw new RuntimeException(x0);
		}
	}

	private PrioritizedSerializer instantiateSerializer(
		CMConfigurationElement elSerializer,
		CMConfigurationElement elProperties,
		ClassLoader pluginClassLoader)
		throws Exception {

		IRecordSourceSerializer serializer;

		String className = elSerializer.getAttribute(CLASS);
		Class serializerClass =
			Class.forName(className, true, pluginClassLoader);

		int priority = DEFAULT_PRIORITY;
		String priorityValue = elSerializer.getAttribute(PRIORITY);
		if (!StringUtils.nonEmptyString(priorityValue)) {
			String msg =
				"null priority value -- DEFAULTING TO " + DEFAULT_PRIORITY;
			log.fine(msg);
		} else {
			try {
				priority = Integer.parseInt(priorityValue);
			} catch (NumberFormatException nfe) {
				String msg =
					"Bad priority value '"
						+ priorityValue
						+ "' -- DEFAULTING TO "
						+ DEFAULT_PRIORITY;
				log.severe(msg);
			}
		}

		Properties properties;
		if (elProperties == null) {
			properties = null;
		} else {
			properties = new Properties();
			CMConfigurationElement[] els = elProperties.getChildren(PROPERTY);
			for (int i = 0; i < els.length; i++) {
				String key = els[i].getAttribute(PROPERTY_NAME);
				String value = els[i].getAttribute(PROPERTY_VALUE);
				properties.setProperty(key, value);
			}
		}

		serializer = (IRecordSourceSerializer) serializerClass.newInstance();
		if (properties != null) {
			Properties propertiesWithDefaults = serializer.getProperties();
			for (Iterator i = properties.keySet().iterator(); i.hasNext();) {
				String key = (String) i.next();
				String value = properties.getProperty(key);
				propertiesWithDefaults.setProperty(key, value);
			}
			serializer.setProperties(propertiesWithDefaults);
		}
		PrioritizedSerializer retVal =
			new PrioritizedSerializer(serializer, priority);

		return retVal;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.base.IRecordSourceSerializationRegistry#registerRecordSourceSerializer(com.choicemaker.cm.core.base.IRecordSourceSerializer, int)
	 */
	@Override
	public void registerRecordSourceSerializer(
		IRecordSourceSerializer serializer,
		int priority) {
		Precondition.assertNonNullArgument("null serializer", serializer);

		PrioritizedSerializer ps =
			new PrioritizedSerializer(serializer, priority);
		this.serializerRegistry.add(ps);
		Collections.sort(this.serializerRegistry);
	}

}
