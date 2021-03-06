/*******************************************************************************
 * Copyright (c) 2003, 2017 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.geo.eclipse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Vector;

import org.jasypt.encryption.StringEncryptor;
import org.jdom2.Element;

import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.core.util.ConvUtils;
import com.choicemaker.cm.core.xmlconf.XmlModuleInitializer;
import com.choicemaker.cm.matching.geo.GeoHelper;
import com.choicemaker.cm.matching.geo.GeoMap;
import com.choicemaker.cm.matching.geo.GeoPoint;
import com.choicemaker.util.StringUtils;

/**
 * XML initializer for geo-entity maps .
 *
 * @author Elmer Moussikaev
 */
public class XmlGeoInitializer implements XmlModuleInitializer {

	public final static XmlGeoInitializer instance = new XmlGeoInitializer();

	private XmlGeoInitializer() {
	}

	@Override
	public void init(Element e) throws XmlConfException {
		init(e, null);
	}

	/**
	 * Creates GeoMaps for all geo-entities defined in the project.xml file
	 * under the &LTmodule
	 * class="com.choicemaker.cm.xmlconf.XmlGeoInitializer"&GT node.
	 * 
	 * @param e
	 *            &LTmodule
	 *            class="com.choicemaker.cm.xmlconf.XmlGeoInitializer"&GT node
	 */
	@Override
	public void init(Element e, StringEncryptor encryptor) throws XmlConfException {
		@SuppressWarnings("unchecked")
		List<Element> maps = e.getChildren("fileGeo");
		for (Object o : maps) {
			Element c = (Element) o;

			String name = c.getAttributeValue("name");
			String country = c.getAttributeValue("country");
			if (country == null)
				country = "";
			String mapType = c.getAttributeValue("mapType");
			if (mapType == null)
				mapType = "hash";
			String keyType = c.getAttributeValue("keyType").intern();
			String fileName = c.getAttributeValue("file");
			String lenStr = c.getAttributeValue("length");
			int keyLen = -1;
			try {
				keyLen = Integer.parseInt(lenStr);
			} catch (Exception ex) {
			}
			;
			Vector<GeoMap.KeyField> fieldsVect = null;

			try {
				GeoMap m = new GeoMap(mapType, keyType, keyLen);
				if (keyType.intern() == "String") {
					@SuppressWarnings("unchecked")
					List<Element> fields = c.getChildren("keyField");
					for (Object o2 : fields) {
						Element kf = (Element) o2;
						if (fieldsVect == null)
							fieldsVect = new Vector<>();
						String fieldName = kf.getAttributeValue("name");
						String fieldLenStr = kf.getAttributeValue("length");
						int fieldLen = -1;
						try {
							fieldLen = Integer.parseInt(fieldLenStr);
						} catch (Exception ex) {
						}
						;
						GeoMap.KeyField gmk = m.new KeyField(fieldName,
								fieldLen);
						fieldsVect.add(gmk);
					}
				}
				m.setFields(fieldsVect);
				readFileMap(fileName, keyType, m);
				GeoHelper.addMap(name, m);
			} catch (IOException ex) {
				throw new XmlConfException("Error reading file: " + fileName,
						ex);
			}
		}
	}

	/**
	 * Reads GeoMap from the file.
	 * 
	 * @param fileName
	 *            A file name.
	 * @param keyType
	 *            A key type.
	 * @param m
	 *            A map to populate.
	 * @throws IOException
	 */
	public static void readFileMap(String fileName, String keyType, GeoMap m)
			throws IOException {
		InputStream fis = new FileInputStream(
				new File(fileName).getAbsoluteFile());
		readFileMap(fis, keyType, m);
		fis.close();
	}

	/**
	 * Reads GeoMap from the input stream. For the String keys removes all
	 * non-digit, non-letter symbols and converts to upper case.
	 * 
	 * @param stream
	 *            A stream to access map's data
	 * @param keyType
	 *            A key type.
	 * @param m
	 *            A map to populate.
	 * @throws IOException
	 */
	public static void readFileMap(InputStream stream, String keyType, GeoMap m)
			throws IOException {
		InputStreamReader reader = new InputStreamReader(stream);
		BufferedReader in = new BufferedReader(reader);
		while (in.ready()) {
			String line = in.readLine().trim();
			if (line != null) {
				int indexLat = line.indexOf(',');
				if (indexLat < 0) {
					throw new IOException("Problem parsing line:\n\t" + line);
				}
				String key = line.substring(0, indexLat);
				int indexLon = line.indexOf(',', indexLat + 1);
				if (indexLon < 0) {
					throw new IOException("Problem parsing line:\n\t" + line);
				}
				String lat = line.substring(indexLat + 1, indexLon);
				String lon = line.substring(indexLon + 1);

				if (keyType.intern() == "String") {
					key = StringUtils.removeNonDigitsLetters(key);
					key = key.toUpperCase();
				}

				m.getMap().put(
						(Integer) ConvUtils.convertString2Object(key, keyType),
						new GeoPoint(Integer.valueOf(lat).intValue(), Integer
								.valueOf(lon).intValue()));
			}
		}
		reader.close();
		in.close();
	}

}
