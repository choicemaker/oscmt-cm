/*******************************************************************************
 * Copyright (c) 2003, 2016 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.en.us.train.address;

import com.choicemaker.cm.matching.cfg.Parser;
import com.choicemaker.cm.matching.cfg.train.FlatFileRawDataReader;
import com.choicemaker.cm.matching.cfg.train.ParsedDataCreator;
import com.choicemaker.cm.matching.cfg.train.ParsedDataWriter;
import com.choicemaker.cm.matching.cfg.train.RawDataReader;

/**
 * @author   Adam Winkel
 */
public final class CfgAddressParsedDataCreator {

	public static final String PARSE_TREES = "-parseTrees";
	public static final String PARSED_DATA = "-parsedData";

	public static void main(String[] args) throws Exception {
		if (args.length < 3 || args.length > 4) {
			System.err.println("At least three args required!");
			System.exit(1);
		}

		String task = args[0].intern();
		if (task != PARSE_TREES && task != PARSED_DATA) {
			System.err.println("First argument must be either '" + PARSE_TREES + "' or '" + PARSED_DATA + "'");
			System.exit(1);
		}

		CfgAddressParserUtils.initRelevantSetsAndMaps();
		Parser parser = CfgAddressParserUtils.createEarleyAddressParser(args[1]);
		RawDataReader reader = new FlatFileRawDataReader(args[2]);
		ParsedDataWriter writer;
		if (args.length > 3) {
			writer = new ParsedDataWriter(args[3]);
		} else {
			writer = new ParsedDataWriter(System.out);
		}

		ParsedDataCreator pdc = new ParsedDataCreator(reader, parser, writer);
		if (task == PARSE_TREES) {
			pdc.setParseTreePolicy(ParsedDataCreator.ALL);
			pdc.setParsedDataPolicy(ParsedDataCreator.NONE);
		} else {
			pdc.setParseTreePolicy(ParsedDataCreator.NONE);
			pdc.setParsedDataPolicy(ParsedDataCreator.ALL);
		}

		pdc.createData();
	}

}
