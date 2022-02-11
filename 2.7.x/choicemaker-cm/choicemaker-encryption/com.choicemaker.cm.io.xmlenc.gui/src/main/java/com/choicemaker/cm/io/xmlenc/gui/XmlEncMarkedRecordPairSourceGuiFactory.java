/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.xmlenc.gui;

import com.choicemaker.cm.core.MarkedRecordPairSource;
import com.choicemaker.cm.core.Source;
import com.choicemaker.cm.io.xmlenc.base.XmlEncMarkedRecordPairSource;
import com.choicemaker.cm.io.xmlenc.mgmt.InMemoryXmlEncManager;
import com.choicemaker.cm.io.xmlenc.mgmt.XmlEncryptionManager;
import com.choicemaker.cm.modelmaker.gui.ModelMaker;
import com.choicemaker.cm.modelmaker.gui.dialogs.SourceGui;
import com.choicemaker.cm.modelmaker.gui.sources.SourceGuiFactory;
import com.choicemaker.xmlencryption.CredentialSet;
import com.choicemaker.xmlencryption.EncryptionScheme;

/**
 * Description
 *
 * @author rphall
 */
public class XmlEncMarkedRecordPairSourceGuiFactory implements SourceGuiFactory {

	// private static final Logger logger = Logger
	// .getLogger(XmlEncMarkedRecordPairSourceGuiFactory.class.getName());

	/**
	 * Returns the singleton instance of the in-memory XML encryption manager.
	 * FIXME: this should be set by an application, not by a library.
	 */
	protected static XmlEncryptionManager getDefaultEncryptionManager() {
		XmlEncryptionManager retVal = InMemoryXmlEncManager.getInstance();
		return retVal;
	}

	private XmlEncryptionManager xmlEncMgr;

	public XmlEncMarkedRecordPairSourceGuiFactory() {
		this(getDefaultEncryptionManager());
	}

	public XmlEncMarkedRecordPairSourceGuiFactory(XmlEncryptionManager xem) {
		this.xmlEncMgr = xem;
	}

	@Deprecated
	public XmlEncMarkedRecordPairSourceGuiFactory(EncryptionScheme unused,
			CredentialSet unused2, XmlEncryptionManager xem) {
		this(xem);
	}

	@Override
	public String getName() {
		return "XML ENC";
	}

	@Override
	public SourceGui createGui(ModelMaker parent, Source s) {
		return new XmlEncMarkedRecordPairSourceGui(parent,
				(MarkedRecordPairSource) s, false);
	}

	@Override
	public SourceGui createGui(ModelMaker parent) {
		return createGui(parent, new XmlEncMarkedRecordPairSource(xmlEncMgr));
	}

	@Override
	public SourceGui createSaveGui(ModelMaker parent) {
		return new XmlEncMarkedRecordPairSourceGui(parent,
				new XmlEncMarkedRecordPairSource(xmlEncMgr), true);
	}

	@Override
	public Object getHandler() {
		return this;
	}

	@Override
	public Class<?> getHandledType() {
		return XmlEncMarkedRecordPairSource.class;
	}

	@Override
	public String toString() {
		return "XML EMRPS";
	}

	@Override
	public boolean hasSink() {
		return true;
	}
}
