/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.en.us.train.address;

import java.io.IOException;
import java.text.ParseException;

import com.choicemaker.cm.matching.cfg.Parser;
import com.choicemaker.cm.matching.cfg.train.FlatFileRawDataReader;
import com.choicemaker.cm.matching.cfg.train.ParserBenchmarkTest;
import com.choicemaker.cm.matching.cfg.train.RawDataReader;

/**
 * .
 * 
 * @author Adam Winkel
 */
public class CfgAddressParserBenchmarkTest extends ParserBenchmarkTest {

	public CfgAddressParserBenchmarkTest(String[] args) throws IOException, ParseException {
		super();
		
		if (args.length != 3) {
			System.err.println("Must have exactly three args");
			System.exit(1);
		}
		
		String type = args[0];
		String grammarFile = args[1];
		String rawDataFile = args[2];
		
		CfgAddressParserUtils.initRelevantSetsAndMaps();
		
		Parser parser = CfgAddressParserUtils.createDefaultAddressParser(grammarFile);
		RawDataReader reader = new FlatFileRawDataReader(rawDataFile);
		
		setType(type);
		setParser(parser);
		setRawDataReader(reader);
	}

	public static void main(String[] args) throws Exception {
		CfgAddressParserBenchmarkTest test = new CfgAddressParserBenchmarkTest(args);

		test.runTest();
		test.printResults();
	}

}
