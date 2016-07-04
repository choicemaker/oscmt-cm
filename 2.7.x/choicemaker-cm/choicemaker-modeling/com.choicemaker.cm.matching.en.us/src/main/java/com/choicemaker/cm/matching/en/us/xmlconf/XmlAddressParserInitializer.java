/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.en.us.xmlconf;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import org.jasypt.encryption.StringEncryptor;
import org.jdom.Element;

import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.core.configure.ConfigurationUtils;
import com.choicemaker.cm.core.util.NamedResources;
import com.choicemaker.cm.core.xmlconf.XmlModuleInitializer;
import com.choicemaker.cm.matching.cfg.ContextFreeGrammar;
import com.choicemaker.cm.matching.cfg.SymbolFactory;
import com.choicemaker.cm.matching.cfg.xmlconf.ContextFreeGrammarXmlConf;
import com.choicemaker.cm.matching.en.us.AddressParser;
import com.choicemaker.cm.matching.en.us.address.AddressStandardizer;
import com.choicemaker.cm.matching.en.us.address.AddressTokenizer;
import com.choicemaker.cm.matching.gen.Sets;

/**
 * @author Martin Buechi
 * @see com.choicemaker.cm.matching.en.us.NameParser
 */
public class XmlAddressParserInitializer implements XmlModuleInitializer {

	private static Logger logger = Logger
			.getLogger(XmlAddressParserInitializer.class.getName());

	public final static XmlAddressParserInitializer instance = new XmlAddressParserInitializer();

	private XmlAddressParserInitializer() {
	}

	@Override
	public void init(Element e, StringEncryptor encryptor)
			throws XmlConfException {

		try {

			Class<?>[] argTypes = new Class[1];
			Object[] args = new Object[1];

			// Tokenizer(s)
			@SuppressWarnings("unchecked")
			List<Element> tokenizerElements = e.getChildren("tokenizer");
			AddressTokenizer[] tokenizers = new AddressTokenizer[tokenizerElements
					.size()];
			for (int i = 0; i < tokenizers.length; i++) {
				Element tokElement = (Element) tokenizerElements.get(i);
				AddressTokenizer t = new AddressTokenizer();

				String preDirs = ConfigurationUtils.getChildText(tokElement,
						"preDirections", encryptor);
				if (preDirs != null) {
					Collection<String> dirs = Sets.getCollection(preDirs);
					if (dirs != null) {
						t.setSplitPreDirections(dirs);
					} else {
						logger.warning("null collection for preDirections");
					}
				}

				String suffixes = ConfigurationUtils.getChildText(tokElement,
						"streetSuffixes", encryptor);
				if (suffixes != null) {
					Collection<String> suffs = Sets.getCollection(suffixes);
					if (suffs != null) {
						t.setSplitSuffixes(suffs);
					} else {
						logger.warning("null collection for streetSuffixes");
					}
				}

				String postDirs = ConfigurationUtils.getChildText(tokElement, "postDirections", encryptor);
				if (postDirs != null) {
					Collection<String> c = Sets.getCollection(postDirs);
					if (c != null) {
						t.setSplitPostDirections(c);
					} else {
						logger.warning("null collection for postDirections");
					}
				}

				String aptTypes = ConfigurationUtils.getChildText(tokElement, "aptTypes", encryptor);
				if (aptTypes != null) {
					Collection<String> c = Sets.getCollection(aptTypes);
					if (c != null) {
						t.setSplitAptTypes(c);
					} else {
						logger.warning("null collection for aptTypes");
					}
				}

				Element splitDigitsElement = tokElement
						.getChild("splitDigitStrings");
				if (splitDigitsElement != null) {
					String minE = splitDigitsElement
							.getAttributeValue("minLength");
					String lhsE = splitDigitsElement
							.getAttributeValue("lhsLength");

					if (minE != null && lhsE != null) {
						int min = Integer.parseInt(minE);
						int lhsLen = Integer.parseInt(lhsE);
						t.setSplitDigitStrings(min, lhsLen);
					}
				}

				Element legalPuncElement = tokElement
						.getChild("legalPunctuation");
				if (legalPuncElement != null) {
					String punc = legalPuncElement.getAttributeValue("value");
					if (punc == null) {
						punc = "";
					}
					t.setLegalPunctuation(punc);
				}

				tokenizers[i] = t;
			}

			// SymbolFactory
			Element factoryElement = e.getChild("symbolFactory");
			String factoryClassName = factoryElement.getAttributeValue("class");
			Class<?> factoryClass = Class.forName(factoryClassName);
			SymbolFactory factory = (SymbolFactory) factoryClass.newInstance();

			// Grammar
			Element grammarElement = e.getChild("grammar");
			// String grammarFile = grammarElement.getAttributeValue("file");
			String grammarResource = grammarElement
					.getAttributeValue("resource");
			InputStream grammarStream = NamedResources
					.getNamedResource(grammarResource);
			ContextFreeGrammar grammar =
			// ProbabalisticContextFreeGrammar.readFromFile(grammarFile,
			// factory);
			ContextFreeGrammarXmlConf.readFromStream(grammarStream, factory);

			// Standardizer
			Element standardizerElement = e.getChild("standardizer");
			String standardizerClassName = standardizerElement
					.getAttributeValue("class");
			Class<?> standardizerClass = Class.forName(standardizerClassName);
			argTypes[0] = SymbolFactory.class;
			Constructor<?> constructor = standardizerClass
					.getConstructor(argTypes);
			args[0] = factory;
			AddressStandardizer standardizer = (AddressStandardizer) constructor
					.newInstance(args);

			// AddressParser automatically saves it as the default parser.
			// 2014-04-24 rphall: Commented out unused local variable.
			// Any side effects?
			/* AddressParser parser = */
			new AddressParser(tokenizers, grammar, standardizer);

		} catch (ClassNotFoundException ex) {
			throw new XmlConfException("Cannot find class", ex);
		} catch (NoSuchMethodException ex) {
			throw new XmlConfException("Method not found", ex);
		} catch (IllegalAccessException ex) {
			throw new XmlConfException("Illegal access attempted", ex);
		} catch (InvocationTargetException ex) {
			throw new XmlConfException("Invocation Target threw exception", ex);
		} catch (InstantiationException ex) {
			throw new XmlConfException("Instantiation problem", ex);
		} catch (IOException ex) {
			throw new XmlConfException("IO problem", ex);
		} catch (ParseException ex) {
			throw new XmlConfException("Problem parsing grammar", ex);
		}
	}

	@Override
	public void init(Element e) throws XmlConfException {
		init(e, null);
	}
}
