/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.en.us.train.name;

import java.io.FileInputStream;

import com.choicemaker.cm.core.util.CommandLineArguments;
import com.choicemaker.cm.matching.cfg.ContextFreeGrammar;
import com.choicemaker.cm.matching.cfg.SymbolFactory;
import com.choicemaker.cm.matching.cfg.train.GrammarTrainer;
import com.choicemaker.cm.matching.cfg.train.ParsedDataReader;
import com.choicemaker.cm.matching.cfg.xmlconf.ContextFreeGrammarXmlConf;
import com.choicemaker.cm.matching.en.us.name.NameSymbolFactory;
import com.choicemaker.e2.CMPlatformRunnable;


/**
 * .
 *
 * @author   Adam Winkel
 * @version  $Revision: 1.1.1.1 $ $Date: 2009/05/03 16:03:04 $
 */
public class NameGrammarTrainer implements CMPlatformRunnable {
		
	public Object run(Object argObj) throws Exception {
		String[] args = CommandLineArguments.eclipseArgsMapper(argObj);
		
		if (args.length < 2) {
			System.err.println("Need at least two arguments: grammar file and parsed data file(s)");	
			System.exit(1);
		}
		
		String grammarFileName = args[0];
		
		SymbolFactory factory = new NameSymbolFactory();
		ContextFreeGrammar grammar = ContextFreeGrammarXmlConf.readFromFile(grammarFileName, factory);
				
		GrammarTrainer trainer = new GrammarTrainer(grammar);

		for (int i = 1; i < args.length; i++) {
			FileInputStream is = new FileInputStream(args[i]);
			ParsedDataReader rdr = new ParsedDataReader(is, factory, grammar);
			trainer.readParseTrees(rdr);
			is.close();
		}

		trainer.writeAll();
		
		return null;
	}

}
