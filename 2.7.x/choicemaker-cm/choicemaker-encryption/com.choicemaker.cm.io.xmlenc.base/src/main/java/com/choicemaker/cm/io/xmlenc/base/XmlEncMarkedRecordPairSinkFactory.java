/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.xmlenc.base;

import java.util.ArrayList;
import java.util.List;

import com.choicemaker.cm.core.Constants;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Sink;
import com.choicemaker.cm.core.SinkFactory;
import com.choicemaker.cm.core.Source;
import com.choicemaker.cm.io.xmlenc.base.xmlconf.XmlEncryptionManager;
import com.choicemaker.utilcopy01.Precondition;
import com.choicemaker.xmlencryption.CredentialSet;
import com.choicemaker.xmlencryption.EncryptionScheme;

public class XmlEncMarkedRecordPairSinkFactory implements SinkFactory {

	private final EncryptionScheme scheme;
	private final CredentialSet credential;
	private final XmlEncryptionManager crdsMgr;

	private String fileNameBase;
	private String xmlFileName;
	private String extension;
	private ImmutableProbabilityModel model;
	private int num;
	private List<Source> sources;

	public XmlEncMarkedRecordPairSinkFactory(String fileNameBase,
			String xmlFileName, String extension,
			ImmutableProbabilityModel model, EncryptionScheme ep,
			CredentialSet ec, XmlEncryptionManager xcm) {
		Precondition.assertNonNullArgument("null scheme", ep);
		Precondition.assertNonNullArgument("null credential", ec);
		Precondition.assertNonNullArgument("null encryption manager", xcm);
		this.scheme = ep;
		this.credential = ec;
		this.crdsMgr = xcm;
		this.fileNameBase = fileNameBase;
		this.xmlFileName = xmlFileName;
		this.extension = extension;
		this.model = model;
		this.sources = new ArrayList<>();
	}

	public Sink getSink() {
		String tName = fileNameBase + num + "." + Constants.MRPS_EXTENSION;
		String tFileName = xmlFileName + num + extension;
		++num;
		sources.add(new XmlEncMarkedRecordPairSource(tName, tFileName, model,
				scheme, credential, crdsMgr));
		return new XmlEncMarkedRecordPairSink(tName, tFileName, model, scheme,
				credential, crdsMgr);
	}

	public Source[] getSources() {
		return (Source[]) sources.toArray(new Source[sources.size()]);
	}
}
