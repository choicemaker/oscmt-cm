/*******************************************************************************
 * Copyright (c) 2015, 2016 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.en.us;

import java.util.ArrayList;
import java.util.List;

import com.choicemaker.cm.matching.cfg.ContextFreeGrammar;
import com.choicemaker.cm.matching.cfg.ParseTreeNodeStandardizer;
import com.choicemaker.cm.matching.cfg.ParsedData;
import com.choicemaker.cm.matching.cfg.Tokenizer;
import com.choicemaker.cm.matching.cfg.cyk.CykParser;

/**
 * Used to split an address string into its constituent pieces.
 * The AddressParser class is intended to handle all cases. 
 * 
 * Such cases include
 * <ul>
 * <li>
 * street address only
 * <li>
 * the full address (including city, state, and zip)
 * </ul>
 *
 * Note that currently, AddressParser does not parse addresses from single strings.
 * 
 * Things like PO Box addresses, C/O pieces, Rural Routes, etc. generally screw up
 * this AddressParser.  Hopefully we will have time to introduce support for such
 * addresses in the future.
 * 
 * Also, AddressParser does no processing (not even validity checking) of
 * City, State, and Zip Codes.
 *
 * @author   Adam Winkel
 */
public class CfgAddressParser extends CykParser {

	public CfgAddressParser() {
	}

	/**
	 * Creates a new AddressParser with the specified tokenizer, grammar, and standardizer.
	 * 
	 * @param tokenizer the tokenizer
	 * @param grammar the grammar
	 * @param standardizer the standardizer
	 */
	public CfgAddressParser(Tokenizer tokenizer, ContextFreeGrammar grammar, ParseTreeNodeStandardizer standardizer) {
		super(tokenizer, grammar, standardizer, ParsedAddress.class);
	}
	
	/**
	 * Creates a new AddressParser with the specifies tokenizers, grammar, and standardizer.
	 * 
	 * @param tokenizers the tokenizers
	 * @param grammar the grammar
	 * @param standardizer the standardizer
	 */
	public CfgAddressParser(Tokenizer[] tokenizers, ContextFreeGrammar grammar, ParseTreeNodeStandardizer standardizer) {
		super(tokenizers, grammar, standardizer, ParsedAddress.class);
	}

	/**
	 * Returns the best parse of the single address line.
	 * 
	 * @param street
	 * @return the best parse of the single address line
	 */
	public ParsedAddress parseAddress(String street) {
		return parseAddress(street, null, null, null);
	}

	/**
	 * Returns the most probable parsed for the specified city, state, and zip.
	 * 
	 * @param street the street address
	 * @param city the city 
	 * @param state the state
	 * @param zip the zip
	 */
	public ParsedAddress parseAddress(String street, String city, String state, String zip) {
		ParsedAddress addr = (ParsedAddress) getBestParse(street);
		if (addr != null) {
			addr.put(ParsedAddress.CITY, city);
			addr.put(ParsedAddress.STATE, state);
			addr.put(ParsedAddress.ZIP, zip);
		}
		
		return addr;
	}
	
	/**
	 * Returns a List of all ParsedAddress for the specified street, city, state, and zip, 
	 * sorted in decreasing order by probability.
	 * 
	 * @param street the street address
	 * @param city the city 
	 * @param state the state
	 * @param zip the zip 
	 */
	public List<ParsedData> getAllParsedAddresses(String street, String city, String state, String zip) {
		ParsedData[] parses = getAllParses(street);

		List<ParsedData> ret = new ArrayList<>(parses.length);
		for (int i = 0; i < parses.length; i++) {
			parses[i].put(ParsedAddress.CITY, city);
			parses[i].put(ParsedAddress.STATE, state);
			parses[i].put(ParsedAddress.ZIP, zip);
			
			ret.add(parses[i]);
		}
		
		// NOTE: they're already sorted.
		
		return ret;
	}
	
}
