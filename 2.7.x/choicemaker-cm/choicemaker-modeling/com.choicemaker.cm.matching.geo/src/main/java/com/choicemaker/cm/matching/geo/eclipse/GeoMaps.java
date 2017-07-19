/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.geo.eclipse;

import static com.choicemaker.cm.core.ChoiceMakerExtensionPoint.CM_MATCHING_GEO;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import com.choicemaker.cm.core.util.ConvUtils;
import com.choicemaker.cm.matching.geo.GeoMap;
import com.choicemaker.cm.matching.geo.GeoMap.KeyField;
import com.choicemaker.cm.matching.geo.GeoPoint;
import com.choicemaker.e2.CMConfigurationElement;
import com.choicemaker.e2.CMExtension;
import com.choicemaker.e2.platform.CMPlatformUtils;
import com.choicemaker.util.StringUtils;

/**
 * The class GeoMaps contains methods for determining co-ordinates of different
 * geo-entities and calculation of the distance between two points on the earth
 * surface (geo-points). Under geo-entities we understand objects that are
 * associated with an area on the earth surface. Examples of geo-entities are a
 * town, zip code, area code. Co-ordinates of a geo-entity are coordinates of
 * the "centroid" of the corresponding area. Depending upon the shape of the
 * area, the centroid may actually lie outside of the area boundaries.
 * Co-ordinates of a geo-point are point's latitude and longitude. Degrees
 * latitude are numbered from -90 to 90 degrees (or from 90 degrees South to 90
 * degrees North). Zero degrees is the equator. Degrees longitude are numbered
 * from -180 to 180 degrees (or from 180 degrees West to 180 degrees East). Zero
 * degrees longitude is located at Greenwich, England.
 * <p>
 * The list of available geo-entities is created during the loading of the
 * project using "reload" section of project.xml file. Geo-objetcs are defined
 * as child elements of
 * com.choicemaker.cm.matching.geo.xmlconf.XmlGeoInitializer module , as shown
 * in the example:
 * 
 * <pre>
 * &lt;module class="com.choicemaker.cm.xmlconf.XmlGeoInitializer"&gt;
 *    &lt;fileGeo
 *        name="Zip code"
 *        mapType="tree"
 *        keyType="int"
 *        length="5"
 *        file="etc/data/zipCodes.txt" /&gt;
 *    &lt;!-- more sets --&gt;
 * &lt;/module&gt;
 * </pre>
 *
 * This loads the contents of the specified file. Each line contains, entity
 * value, longitude and latitude E.g., the file zipCodes.txt may look like this:
 * 
 * <pre>
 * 00501,408151,-730455
 * ...
 * </pre>
 *
 * Following examples illustrates the usage of GeoMaps methods. In the projects
 * that include only one country: GeoMaps.dist(GeoHepler.geoPoint("zip"
 * ,19067),GeoHepler.geoPoint("town","PANewtown"));
 * GeoMaps.dist(GeoHepler.geoPoint ("postalCode","H1A 1A1"
 * ),GeoHepler.geoPoint("town","QCMontreal"));
 * 
 * In the projects that include multiple countries:
 * GeoMaps.dist(GeoHepler.geoPoint
 * ("USpostalCode","19067"),GeoHepler.geoPoint("CNpostalCode","H1A 1A1"));
 * GeoMaps
 * .dist(GeoHepler.geoPoint("UStown","PANewtown"),GeoHepler.geoPoint("CNtown"
 * ,"QCMontreal"));
 * 
 * @author emoussikaev
 * 
 * @since 2.7
 */

public class GeoMaps {

	private static final Logger logger =
		Logger.getLogger(GeoMaps.class.getName());

	private static Map<String, GeoMap> maps = new HashMap<>();

	static {
		initializeRegisteredGeoMaps();
	}

	public GeoMaps() {
	}

	/**
	 * Adds a map to the collection of maps.
	 *
	 * @param name
	 *            The name of the collection.
	 * @param map
	 *            The map to be added.
	 *
	 */
	public static void addMap(String name, GeoMap map) {
		maps.put(name, map);
	}

	/**
	 * Returns a map of the requested type
	 *
	 * @param geoEntityType
	 *            The type of the map.
	 * 
	 * @return A map of the requested type.
	 *
	 */
	public static GeoMap getMap(String geoEntityType) {
		GeoMap map = (GeoMap) maps.get(geoEntityType);
		return map;
	}

	/**
	 * Returns a Collection containing the names of the Maps contained herein.
	 * 
	 * @return a Collection of the names of all registered maps
	 */

	public static Collection<String> getGeoTypes() {
		return maps.keySet();
	}

	/**
	 * Finds cental point of the specifies geo-entity.
	 * 
	 * @param geoEntityType
	 *            An entity type. Entity types are defined by the "fileGeo"
	 *            child elements of
	 *            com.choicemaker.cm.matching.geo.xmlconf.XmlGeoInitializer
	 *            module
	 * @param geoEntityDescription
	 *            An integer value that indentify a specific geo-entity. For
	 *            example, 215 for "Zip code".
	 * @return Cental point of the specifies geo-entity.
	 */
	public static GeoPoint geoPoint(String geoEntityType,
			int geoEntityDescription) {
		GeoMap map = (GeoMap) maps.get(geoEntityType);
		if (map == null)
			return null;
		if (map.keyType.intern() == "int") {
			return (GeoPoint) map.getMap()
					.get(new Integer(geoEntityDescription));
		} else {
			try {
				String keyString =
					(new Integer(geoEntityDescription)).toString();
				Object key =
					ConvUtils.convertString2Object(keyString, map.getKeyType());
				return (GeoPoint) map.getMap().get(key);
			} catch (IllegalArgumentException _e) {
				String msg = "Error looking up geoPoint for geoType '"
						+ geoEntityType + "', geoEntityDesc "
						+ geoEntityDescription + ": " + _e.toString();
				logger.severe(msg);
				return null;
			}
		}
	}

	/**
	 * Finds central point of the specifies geo-entity.
	 * 
	 * @param geoEntityType
	 *            An entity type. Entity types are defined by the "fileGeo"
	 *            child elements of
	 *            com.choicemaker.cm.matching.geo.xmlconf.XmlGeoInitializer
	 *            module
	 * @param geoEntityDescription
	 *            A string value that identify a specific geo-entity.For
	 *            example, "PANewtown" for "Zip code". The required format of
	 *            the string is defined by the corresponding file of the
	 *            geoEntityType passed as the first parameter. If datatype of
	 *            the entity is defined as "String" in the project configuration
	 *            then geoEntityDescription will be converted to upper case and
	 *            all non-digit non-letter symbols will be removed.
	 * @return Central point of the specifies geo-entity.
	 */

	public static GeoPoint geoPoint(String geoEntityType,
			String geoEntityDescription) {
		GeoMap map = (GeoMap) maps.get(geoEntityType);
		if (map == null)
			return null;
		try {
			if (map.getKeyLength() != -1) {
				if (map.getKeyLength() != geoEntityDescription.length())
					return null;
			}
			if (map.getKeyType().intern() == "String") {
				geoEntityDescription =
					StringUtils.removeNonDigitsLetters(geoEntityDescription);
				geoEntityDescription = geoEntityDescription.toUpperCase();
			}
			Object key = ConvUtils.convertString2Object(geoEntityDescription,
					map.getKeyType());
			return (GeoPoint) map.getMap().get(key);
		} catch (IllegalArgumentException _e) {
			String msg = "Error looking up geoPoint for geoType '"
					+ geoEntityType + "', geoEntityDesc " + geoEntityDescription
					+ ": " + _e.toString();
			logger.severe(msg);
			logger.severe(_e.toString());
			return null;
		}
	}

	/**
	 * Initializes geo maps defined in the plugin registry.
	 */
	static void initializeRegisteredGeoMaps() {
		CMExtension[] extensions =
			CMPlatformUtils.getExtensions(CM_MATCHING_GEO);

		for (int i = 0; i < extensions.length; i++) {
			CMExtension ext = extensions[i];
			URL pUrl = ext.getDeclaringPluginDescriptor().getInstallURL();
			CMConfigurationElement[] els = ext.getConfigurationElements();
			for (int j = 0; j < els.length; j++) {
				CMConfigurationElement el = els[j];

				String name = el.getAttribute("name");
				String fileName = el.getAttribute("file");
				String country = el.getAttribute("country");
				if (country == null)
					country = "";
				String mapType = el.getAttribute("mapType");
				if (mapType == null)
					mapType = "hash";
				String keyType = el.getAttribute("keyType").intern();
				String lenStr = el.getAttribute("length");
				int keyLen = -1;
				try {
					keyLen = Integer.parseInt(lenStr);
				} catch (Exception ex) {
					String msg =
						"Invalid length '" + lenStr + "': " + ex.toString();
					logger.severe(msg);
					logger.severe(ex.toString());
				}
				;
				Vector<GeoMap.KeyField> fieldsVect = null;
				GeoMap m = new GeoMap(mapType, keyType, keyLen);
				if (keyType.intern() == "String") {
					CMConfigurationElement[] fields =
						el.getChildren("keyField");
					for (int k = 0; k < fields.length; k++) {
						CMConfigurationElement kf = fields[k];
						if (fieldsVect == null)
							fieldsVect = new Vector<>();
						String fieldName = kf.getAttribute("name");
						String fieldLenStr = kf.getAttribute("length");
						int fieldLen = -1;
						try {
							fieldLen = Integer.parseInt(fieldLenStr);
						} catch (Exception ex) {
							String msg = "Invalid length '" + fieldLenStr
									+ "': " + ex.toString();
							logger.severe(msg);
						}
						;
						GeoMap.KeyField gmk =
							m.new KeyField(fieldName, fieldLen);
						fieldsVect.add(gmk);
					}
				}
				m.setFields(fieldsVect);
				try {
					URL url = new URL(pUrl, fileName);
					XmlGeoInitializer.readFileMap(url.openStream(), keyType, m);
					addMap(name, m);
				} catch (IOException ex) {
					logger.severe(
							"Error reading file: '" + fileName + "': " + ex);
				}
			}
		}
	}

}
