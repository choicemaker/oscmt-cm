/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.en.us.train.name;

import com.choicemaker.cm.core.util.CommandLineArguments;
import com.choicemaker.cm.matching.cfg.Parser;
import com.choicemaker.cm.matching.cfg.Parsers;
import com.choicemaker.cm.matching.cfg.train.FlatFileRawDataReader;
import com.choicemaker.cm.matching.cfg.train.ParsedDataCreator;
import com.choicemaker.cm.matching.cfg.train.ParsedDataWriter;
import com.choicemaker.cm.matching.cfg.train.RawDataReader;
import com.choicemaker.e2.CMPlatformRunnable;

/**
 * .
 *
 * @author   Adam Winkel
 */
public final class NameParsedDataCreator implements CMPlatformRunnable {

	public static final String PARSE_TREES = "-parseTrees";
	public static final String PARSED_DATA = "-parsedData";
	public static final String RAW = "-raw";
	public static final String PARSER = "-parser";
	public static final String OUT = "-out";

	public Object run(Object argObj) throws Exception {
		CommandLineArguments cla = new CommandLineArguments();
		cla.addOption(PARSE_TREES);
		cla.addOption(PARSED_DATA);
		cla.addArgument(RAW);
		cla.addArgument(PARSER);
		cla.addArgument(OUT);

		String[] args = CommandLineArguments.eclipseArgsMapper(argObj);
		cla.enter(args);
		if (cla.isError()) {
			System.err.println("Unusable args");
			System.exit(1);
		}

		boolean parseTrees = cla.optionSet(PARSE_TREES);
		boolean parsedData = cla.optionSet(PARSED_DATA);
		if (!parseTrees && !parsedData) {
			parseTrees = true;
		}
		
		String rawFile = cla.getArgument(RAW);
		String parserName = cla.getArgument(PARSER);
		String parsedFile = cla.getArgument(OUT);

		RawDataReader reader = new FlatFileRawDataReader(rawFile);
		Parser parser = Parsers.get(parserName);
		ParsedDataWriter writer;
		if (parsedFile == null) {
			writer = new ParsedDataWriter(System.out);
		} else {
			writer = new ParsedDataWriter(parsedFile);
		}
		
		ParsedDataCreator pdc = new ParsedDataCreator(reader, parser, writer);
		pdc.setParseTreePolicy(parseTrees ? ParsedDataCreator.ALL : ParsedDataCreator.NONE);
		pdc.setParsedDataPolicy(parsedData ? ParsedDataCreator.ALL : ParsedDataCreator.NONE);		
		pdc.createData();
		
		return null;
	}
	
}
