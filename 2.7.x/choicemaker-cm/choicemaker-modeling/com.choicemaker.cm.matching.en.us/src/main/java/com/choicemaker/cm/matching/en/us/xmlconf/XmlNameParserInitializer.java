/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.en.us.xmlconf;

import java.util.Collection;
import java.util.logging.Logger;

import org.jasypt.encryption.StringEncryptor;
import org.jdom.Element;

import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.core.configure.ConfigurationUtils;
import com.choicemaker.cm.core.xmlconf.XmlModuleInitializer;
import com.choicemaker.cm.matching.en.us.AdhocNameParser;
import com.choicemaker.cm.matching.gen.Sets;

/**
 * XML initializer for collections (sets).
 * 
 * The name parser can be customized through the configuration file. The
 * following gives a sample configuration:
 * 
 * <pre>
 * &LTmodule class="com.choicemaker.cm.xmlconf.XmlNameParserInitializer"&GT
 * 		&LTgenericFirstNames&GTgenericFirstNames&LT/genericFirstNames&GT
 * 		&LTinvalidLastNames&GTinvalidLastNames&LT/invalidLastNames&GT
 * 		&LTnameTitles&GTnameTitles&LT/nameTitles&GT
 * 		&LTchildOfIndicators&GTchildOfIndicators&LT/childOfIndicators&GT
 * &LT/module&GT
 * </pre>
 *
 * The value of <code>genericFirstNames</code> defines the set of generic first
 * names, such as "unknown" or "baby", that are to be filtered out. Dually, the
 * field <code>invalidLastNames</code> defines the set of invalid last names.
 * The <code>nameTitles</code> defines the set of titles, such as "MR" and "JR".
 * The <code>childOfIndicators</code> is specific to parsing names of children.
 * In some applications values like "MC Amanda" standing for
 * "male child of Amanda" are common. In this case, the child's first name is
 * undefined and the mother's first name is set to Amanda.
 * 
 * <br/>
 * All four elements are optional. If present, their values must define sets
 * (Section 5.1) that are defined before the name parser initialization.
 *
 * @author Martin Buechi
 * @see com.choicemaker.cm.custom.mci.nameparser.NameParser
 */
public class XmlNameParserInitializer implements XmlModuleInitializer {

	private final static Logger logger = Logger.getLogger(XmlNameParserInitializer.class.getName());

	public final static XmlNameParserInitializer instance = new XmlNameParserInitializer();

	private XmlNameParserInitializer() {
	}

	@Override
	public void init(Element e) throws XmlConfException {
		init(e, null);
	}

	@Override
	public void init(Element e, StringEncryptor encryptor)
			throws XmlConfException {
		String msg = "Ad hoc name parsers are no longer configured as CM Analyzer "
				+ "modules. They should be configured as plugins. "
				+ "See the com.choicemaker.cm.matching.en.us module for details.";
		logger.warning(msg);
		//String gfn = ConfigurationUtils.getChildText(e, "genericFirstNames",
		//		encryptor);
		//if (gfn != null) {
		//	Collection<String> c = Sets.getCollection(gfn);
		//	AdhocNameParser.getDefaultInstance().setGenericFirstNames(c);
		//}
		//String coi = ConfigurationUtils.getChildText(e, "childOfIndicators",
		//		encryptor);
		//if (coi != null) {
		//	Collection<String> c = Sets.getCollection(coi);
		//	AdhocNameParser.getDefaultInstance().setChildOfIndicators(c);
		//}
		//String iln = ConfigurationUtils.getChildText(e, "invalidLastNames",
		//		encryptor);
		//if (iln != null) {
		//	Collection<String> c = Sets.getCollection(iln);
		//	AdhocNameParser.getDefaultInstance().setInvalidLastNames(c);
		//}
		//String nt = ConfigurationUtils.getChildText(e, "nameTitles", encryptor);
		//if (nt != null) {
		//	Collection<String> c = Sets.getCollection(nt);
		//	AdhocNameParser.getDefaultInstance().setNameTitles(c);
		//}
		//String lnp = ConfigurationUtils.getChildText(e, "lastNamePrefixes",
		//		encryptor);
		//if (lnp != null) {
		//	Collection<String> c = Sets.getCollection(lnp);
		//	AdhocNameParser.getDefaultInstance().setLastNamePrefixes(c);
		//}
	}
}
