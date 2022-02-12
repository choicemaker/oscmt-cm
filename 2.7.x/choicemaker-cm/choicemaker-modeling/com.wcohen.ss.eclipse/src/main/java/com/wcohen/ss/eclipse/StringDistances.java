/*******************************************************************************
 * Copyright (c) 2007, 2014 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
package com.wcohen.ss.eclipse;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.choicemaker.cm.core.ChoiceMakerExtensionPoint;
import com.choicemaker.e2.CMConfigurationElement;
import com.choicemaker.e2.CMExtension;
import com.choicemaker.e2.platform.CMPlatformUtils;
import com.wcohen.ss.api.StringDistance;

/**
 * Collection of {@link StringDistance} instances, implemented by
 * various SecondString classes.
 * 
 * StringDistances work similar to maps, sets and relations. The module is loaded through
 * the class <code>com.choicemakdistancesmlconf.XmlStringDistanceInitializer</code>.
 * The actual sets are defined as child elements, as shown in the example:
 * <pre>
 &LTmodule class="com.choicemaker.cm.xmlconf.XmlStringDistanceInitializer"&GT
	&LTinstance name="nameDistance" file="etc/data/nameDistance.ser"
	  fileFormat="serialized" fileFormatVersion="1.5" /&GT
	&LT!-- more string distances --&GT
 &LT/module&GT
   </pre>
 * This loads the contents of the specifiedistancesinto memory.
 *
 * Based on this, we can then use an expression like 
 * <code>StringDistances.score("nameDistance", q.full_name, m.full_name)</code>
 * in a clue/rule.
 * 
 * @author  	Rick Hall
 */
public final class StringDistances {

	private StringDistances() {
	}

	private static Map distances = new HashMap();

	static {
		initRegisteredStringDistances();
	}

	/**
	 * Adds a StringDistance to the collection of distances.
	 *
	 * @param   name  The name of the collection.
	 * @param   stringDistance  The stringDistance to be added.
	 *
	 */
	public static void addStringDistance(String name, StringDistance stringDistance) {
		distances.put(name, stringDistance);
	}

	/**
	 * Reads a file, creates a StringDistance and adds it to the collection of distances.
	 * @param name the StringDistance name
	 * @param fUrl a URL pointing to the StringDistance file
	 * @param lazy whether initialization of the StringDistance should be deferred to first use
	 */
	static void addStringDistance(
		String name,
		URL fileUrl,
		FileFormat fileFormat,
		String fileFormatVersion) {

		StringDistance p = null;
		if (fileFormat.equals(FileFormat.BINARY)) {
			p = new BinarySerializedStringDistance(name, fileUrl);
		} else if (fileFormat.equals(FileFormat.BINARY)) {
			throw new RuntimeException("not yet implemented");
		} else {
			throw new RuntimeException("Unknown format: '" + fileFormat + "'");
		}
		addStringDistance(name, p);

		return;
	} // addParser(String,URL,FileFormat,String)

	/**
	 * Returns the StringDistance named by <code>name</code>.
	 * 
	 * @param name the name of the collection
	 * @return the collection named by name
	 */
	public static StringDistance getStringDistance(String name) {
		StringDistance retVal = (StringDistance) distances.get(name);
		return retVal;
	}

	/**
	 * Returns a Collection containing the names of the StringDistance instances contained
	 * herein.
	 * @return a Collection of the names of all registered distances
	 */
	public static Collection getStringDistanceNames() {
		return distances.keySet();
	}

	/**
	 * Retrieves the StringDistance named by <code>name</code> and uses it to
	 * calculate the distance between the specified texts.
	 * 
	 * @param name the name of the StringDistance with which to perform the parse
	 * @param text1 the first text
	 * @param text2 the second text
	 * @return the distance score
	 * @throws IllegalArgumentException if no StringDistance named <code>name</code> is registered.
	 */
	public static double score(String name, String text1, String text2) {
		StringDistance distance = (StringDistance) distances.get(name);
		if (distance == null) {
			throw new IllegalArgumentException(
				"There is no StringDistance named " + name + " registered");
		} else {
			return distance.score(text1,text2);
		}
	}

	/**
	 * Loads  distances registered by plugins.
	 */
	static void initRegisteredStringDistances() {
		CMExtension[] extensions =
			CMPlatformUtils
					.getExtensions(ChoiceMakerExtensionPoint.SECONDSTRING_STRINGDISTANCE);
		for (int i = 0; i < extensions.length; i++) {
			CMExtension ext = extensions[i];
			URL pUrl = ext.getDeclaringPluginDescriptor().getInstallURL();
			CMConfigurationElement[] els = ext.getConfigurationElements();
			for (int j = 0; j < els.length; j++) {
				CMConfigurationElement el = els[j];

				String name = el.getAttribute("name");
				String filterFile = el.getAttribute("file");
				String grammarFile = el.getAttribute("fileFormat");
				String fileFormatVersion = el.getAttribute("fileFormatVersion");;

				URL fUrl = null;
				FileFormat fileFormat = null;
				try {
					fUrl = new URL(pUrl, filterFile);
					fileFormat = FileFormat.getInstance(grammarFile);
					addStringDistance(name, fUrl, fileFormat, fileFormatVersion);
				} catch (IOException ex) {
					ex.printStackTrace();
				}

			} // for j
		} // for i

		return;
	} // initRegisteredStringDistances

} // StringDistances

