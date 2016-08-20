/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.en.us;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import com.choicemaker.cm.matching.cfg.ContextFreeGrammar;
import com.choicemaker.cm.matching.cfg.ParseTreeNode;
import com.choicemaker.cm.matching.cfg.ParseTreeNodeStandardizer;
import com.choicemaker.cm.matching.cfg.ParsedData;
import com.choicemaker.cm.matching.cfg.SymbolFactory;
import com.choicemaker.cm.matching.cfg.Tokenizer;
import com.choicemaker.util.Precondition;

/**
 * Used to split an address string into its constituent pieces. The
 * AddressParser class is intended to handle all cases.
 * 
 * Such cases include
 * <ul>
 * <li>street address only
 * <li>the full address (including city, state, and zip)
 * </ul>
 *
 * Note that currently, AddressParser does not parse addresses from single
 * strings.
 * 
 * Things like PO Box addresses, C/O pieces, Rural Routes, etc. generally screw
 * up this AddressParser. Hopefully we will have time to introduce support for
 * such addresses in the future.
 * 
 * Also, AddressParser does no processing (not even validity checking) of City,
 * State, and Zip Codes.
 *
 * @author Adam Winkel
 */
public class AddressParser {

	private static final Logger logger =
		Logger.getLogger(AddressParser.class.getName());

	public static final String MCI_CFG_ADDRESS_PARSER = "mciCfgAddressParser";

	private static AtomicReference<AddressParser> defaultParserRef =
		new AtomicReference<>(null);

	public static synchronized AddressParser getDefaultParser() {
		AddressParser retVal = defaultParserRef.get();
		if (retVal == null) {
			CfgAddressParser defaultDelegate = CfgAddressParsers
					.lookupCfgAddressParser(MCI_CFG_ADDRESS_PARSER);
			final AddressParser defaultParser =
				new AddressParser(defaultDelegate);
			retVal = defaultParserRef.get();
			boolean isSet = defaultParserRef.compareAndSet(null, defaultParser);
			if (isSet) {
				assert retVal == defaultParser;
				String msg =
						"default MCI address parser is " + retVal;
				logger.info(msg);
			} else {
				if (retVal == null) {
					String msg =
						"race condition: default MCI address parser is null";
					logger.severe(msg);
					throw new IllegalStateException(msg);
				} else {
					String msg =
							"race condition: default MCI address parser is " + retVal;
					logger.warning(msg);
				}
			}
		}
		return retVal;
	}

	private final CfgAddressParser delegate;

	public AddressParser(CfgAddressParser cfgAddressParser) {
		Precondition.assertNonNullArgument("null delegate", cfgAddressParser);
		delegate = cfgAddressParser;
	}

	public int hashCode() {
		return delegate.hashCode();
	}

	public void setGrammar(ContextFreeGrammar g) {
		delegate.setGrammar(g);
	}

	public void setName(String name) {
		delegate.setName(name);
	}

	public String getName() {
		return delegate.getName();
	}

	public void setSymbolFactory(SymbolFactory sf) {
		delegate.setSymbolFactory(sf);
	}

	public SymbolFactory getSymbolFactory() {
		return delegate.getSymbolFactory();
	}

	public void addTokenizer(Tokenizer t) {
		delegate.addTokenizer(t);
	}

	public void setTokenizer(Tokenizer t) {
		delegate.setTokenizer(t);
	}

	public void setTokenizers(Tokenizer[] t) {
		delegate.setTokenizers(t);
	}

	public Tokenizer[] getTokenizers() {
		return delegate.getTokenizers();
	}

	public ContextFreeGrammar getGrammar() {
		return delegate.getGrammar();
	}

	public void setStandardizer(ParseTreeNodeStandardizer s) {
		delegate.setStandardizer(s);
	}

	public ParseTreeNodeStandardizer getStandardizer() {
		return delegate.getStandardizer();
	}

	public void setParsedDataClass(Class cls) {
		delegate.setParsedDataClass(cls);
	}

	public Class getParsedDataClass() {
		return delegate.getParsedDataClass();
	}

	public ParsedData getBestParse(String s) {
		return delegate.getBestParse(s);
	}

	public ParsedAddress parseAddress(String street) {
		return delegate.parseAddress(street);
	}

	public ParsedData getBestParse(String[] s) {
		return delegate.getBestParse(s);
	}

	public ParsedAddress parseAddress(String street, String city, String state,
			String zip) {
		return delegate.parseAddress(street, city, state, zip);
	}

	public boolean equals(Object obj) {
		return delegate.equals(obj);
	}

	public ParsedData[] getAllParses(String s) {
		return delegate.getAllParses(s);
	}

	public List<ParsedData> getAllParsedAddresses(String street, String city,
			String state, String zip) {
		return delegate.getAllParsedAddresses(street, city, state, zip);
	}

	public ParsedData[] getAllParses(String[] s) {
		return delegate.getAllParses(s);
	}

	public ParseTreeNode getBestParseTree(String s) {
		return delegate.getBestParseTree(s);
	}

	public ParseTreeNode getBestParseTree(String[] s) {
		return delegate.getBestParseTree(s);
	}

	public ParseTreeNode[] getAllParseTrees(String s) {
		return delegate.getAllParseTrees(s);
	}

	public ParseTreeNode[] getAllParseTrees(String[] s) {
		return delegate.getAllParseTrees(s);
	}

	public List getTokenization(String s) {
		return delegate.getTokenization(s);
	}

	public List getTokenization(String[] s) {
		return delegate.getTokenization(s);
	}

	public List[] getAllTokenizations(String s) {
		return delegate.getAllTokenizations(s);
	}

	public List[] getAllTokenizations(String[] s) {
		return delegate.getAllTokenizations(s);
	}

	public String toString() {
		return delegate.toString();
	}

}
