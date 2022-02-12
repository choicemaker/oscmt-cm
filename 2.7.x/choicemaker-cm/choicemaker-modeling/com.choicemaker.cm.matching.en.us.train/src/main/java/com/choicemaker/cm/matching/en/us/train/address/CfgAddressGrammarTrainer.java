/*******************************************************************************
 * Copyright (c) 2003, 2016 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.en.us.train.address;

import com.choicemaker.cm.matching.cfg.ContextFreeGrammar;
import com.choicemaker.cm.matching.cfg.SymbolFactory;
import com.choicemaker.cm.matching.cfg.train.GrammarTrainer;
import com.choicemaker.cm.matching.cfg.train.ParsedDataReader;
import com.choicemaker.cm.matching.cfg.xmlconf.ContextFreeGrammarXmlConf;
import com.choicemaker.cm.matching.en.us.address.AddressSymbolFactory;

/**
 * .
 * 
 * @author Adam Winkel
 */
public class CfgAddressGrammarTrainer {

	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.err.println("Need at least two arguments: grammar file and parsed data files");	
			System.exit(1);
		}
		
		CfgAddressParserUtils.initRelevantSetsAndMaps();
		
		SymbolFactory factory = new AddressSymbolFactory();
		ContextFreeGrammar grammar = ContextFreeGrammarXmlConf.readFromFile(args[0], factory);
		
		GrammarTrainer trainer = new GrammarTrainer(grammar);

		for (int i = 1; i < args.length; i++) {
			ParsedDataReader rdr = new ParsedDataReader(args[i], factory, grammar);
			trainer.readParseTrees(rdr);
		}
		
		trainer.writeAll();
	}

}
