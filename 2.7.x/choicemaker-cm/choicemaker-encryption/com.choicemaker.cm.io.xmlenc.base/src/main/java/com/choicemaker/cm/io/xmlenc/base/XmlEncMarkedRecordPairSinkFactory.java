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
import com.choicemaker.cm.io.xmlenc.base.xmlconf.EncryptionCredential;
import com.choicemaker.cm.io.xmlenc.base.xmlconf.EncryptionPolicy;
import com.choicemaker.cm.io.xmlenc.base.xmlconf.XmlEncryptionManager;
import com.choicemaker.utilcopy01.Precondition;

public class XmlEncMarkedRecordPairSinkFactory implements SinkFactory {

	private final EncryptionPolicy<?> policy;
	private final EncryptionCredential credential;
	private final XmlEncryptionManager crdsMgr;

	private String fileNameBase;
	private String xmlFileName;
	private String extension;
	private ImmutableProbabilityModel model;
	private int num;
	private List<Source> sources;

	public XmlEncMarkedRecordPairSinkFactory(String fileNameBase,
			String xmlFileName, String extension,
			ImmutableProbabilityModel model, EncryptionPolicy<?> ep,
			EncryptionCredential ec, XmlEncryptionManager xcm) {
		Precondition.assertNonNullArgument("null policy", ep);
		Precondition.assertNonNullArgument("null credential", ec);
		Precondition.assertNonNullArgument("null encryption manager", xcm);
		this.policy = ep;
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
				policy, credential, crdsMgr));
		return new XmlEncMarkedRecordPairSink(tName, tFileName, model, policy,
				credential, crdsMgr);
	}

	public Source[] getSources() {
		return (Source[]) sources.toArray(new Source[sources.size()]);
	}
}
