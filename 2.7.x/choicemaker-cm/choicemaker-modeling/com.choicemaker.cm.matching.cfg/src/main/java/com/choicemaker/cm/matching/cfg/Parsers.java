/*******************************************************************************
 * Copyright (c) 2003, 2017 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.cfg;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.jdom2.JDOMException;

import com.choicemaker.cm.core.ChoiceMakerExtensionPoint;
import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.matching.cfg.xmlconf.ParserXmlConf;
import com.choicemaker.e2.CMConfigurationElement;
import com.choicemaker.e2.CMExtension;
import com.choicemaker.e2.platform.CMPlatformUtils;

/**
 * @author Adam Winkel
 */
public final class Parsers {

	private static final Logger logger =
		Logger.getLogger(Parsers.class.getName());

	private static final Map<String, Object> parsers = new HashMap<>();

	static {
		initRegisteredParsers();
//		initRegisteredCascadedParsers();
	}

	public static boolean has(String name) {
		return parsers.containsKey(name);
	}

	public static Parser get(String name) {
		Object parser = parsers.get(name);
		if (parser == null) {
			String msg = "No parser named '" + name + "'";
			logger.severe(msg);
			throw new IllegalStateException(msg);
		}
		if (parser instanceof ParserDef) {
			Parser p = ((ParserDef) parser).load();
			assert p != null ;
			put(p);
			return p;
		} else {
			return (Parser) parser;
		}
	}

	public static Set<String> getParserKeys() {
		return parsers.keySet();
	}

	public static void put(Parser parser) {
		String name = parser.getName();
		if (name == null) {
			throw new IllegalArgumentException("A parser must have a name!");
		}

		put(name, parser);
	}

	public static void put(String name, Parser parser) {
		if (name == null) {
			throw new IllegalArgumentException(
					"Cannot add a parser without a name!");
		}

		parsers.put(name, parser);
	}

	private static void put(ParserDef def) {
		parsers.put(def.getName(), def);
	}

	static void initRegisteredParsers() {
		CMExtension[] extensions =
			CMPlatformUtils
					.getExtensions(ChoiceMakerExtensionPoint.CM_MATCHING_CFG_PARSER);
		for (int i = 0; i < extensions.length; i++) {
			CMExtension ext = extensions[i];
			ClassLoader cl = ext.getDeclaringPluginDescriptor().getPluginClassLoader();
			URL pUrl = ext.getDeclaringPluginDescriptor().getInstallURL();
			CMConfigurationElement[] els = ext.getConfigurationElements();
			for (int j = 0; j < els.length; j++) {
				CMConfigurationElement el = els[j];

				String name = el.getAttribute("name");
				String file = el.getAttribute("file");

				Parsers.put(new ParserDef(name, cl, pUrl, file));
			}
		}
	}

//	static void initRegisteredCascadedParsers() {
//		CMExtension[] extensions = CMPlatformUtils.getExtensions(
//				ChoiceMakerExtensionPoint.CM_MATCHING_CFG_CASCADEDPARSER);
//		for (int i = 0; i < extensions.length; i++) {
//			CMExtension ext = extensions[i];
//			CMConfigurationElement[] els = ext.getConfigurationElements();
//			for (int j = 0; j < els.length; j++) {
//				CMConfigurationElement[] parserEls = els[j].getChildren();
//
//				CascadedParserDef cp =
//					new CascadedParserDef(els[j].getAttribute("name"));
//				for (int k = 0; k < parserEls.length; k++) {
//					CMConfigurationElement el = parserEls[k];
//
//					String name = el.getAttribute("name");
//					if (name != null) {
//						if (Parsers.has(name)) {
//							cp.addParser(name);
//						} else {
//							throw new RuntimeException(
//									"Parser named " + name + " doesn't exist!");
//						}
//					} else {
//						throw new RuntimeException(
//								"Parser element must define either a name or a file attribute");
//					}
//				}
//
//				Parsers.put(cp);
//			}
//		}
//	}

	private Parsers() {
	}

	private static class ParserDef {

		private static final Logger logger =
			Logger.getLogger(ParserDef.class.getName());

		protected String name;
		protected URL pUrl;
		protected String relPath;
		protected ClassLoader classLoader;

		public ParserDef(String name, ClassLoader cl, URL pUrl,
				String relPath) {
			this.name = name;
			this.pUrl = pUrl;
			this.relPath = relPath;
			this.classLoader = cl;
		}

		public String getName() {
			return name;
		}

		public Parser load() {
			Parser retVal = null;
			try {
				URL rUrl = new URL(pUrl, relPath);
				Parser p = ParserXmlConf.readFromStream(rUrl.openStream(),
						classLoader, pUrl);
				assert p != null;
				p.setName(name);
				retVal = p;
			} catch (XmlConfException | IOException | JDOMException e) {
				String msg = "Unable to load parser from parser definition '"
						+ this.toString() + "': " + e.toString();
				logger.severe(msg);
				throw new IllegalStateException(msg);
			}
			assert retVal != null;
			return retVal;
		}

		@Override
		public String toString() {
			return "ParserDef [name=" + name + ", pUrl=" + pUrl + ", relPath="
					+ relPath + "]";
		}

	}

//	private static class CascadedParserDef extends ParserDef {
//		List<String> kids = new ArrayList<>();
//
//		public CascadedParserDef(String name) {
//			super(name);
//		}
//
//		public void addParser(String name) {
//			kids.add(name);
//		}
//
//		public Parser load() {
//			CascadedParser cp = new CascadedParser();
//			cp.setName(name);
//			for (int i = 0; i < kids.size(); i++) {
//				Parser kid = Parsers.get((String) kids.get(i));
//				cp.addParser(kid);
//			}
//			return cp;
//		}
//	}

}
