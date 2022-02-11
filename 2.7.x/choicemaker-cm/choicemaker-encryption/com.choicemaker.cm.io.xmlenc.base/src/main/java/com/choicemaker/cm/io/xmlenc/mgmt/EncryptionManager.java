/*******************************************************************************
 * Copyright (c) 2014, 2016 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker LLC - initial API and implementation
 *******************************************************************************/
package com.choicemaker.cm.io.xmlenc.mgmt;

import java.util.List;

import com.choicemaker.xmlencryption.CredentialSet;
import com.choicemaker.xmlencryption.EncryptionScheme;

public interface EncryptionManager {

	// int getTimeToLive();

	// void setTimeToLive(int millisecs);

	List<EncryptionScheme> getEncryptionSchemes();

	EncryptionScheme getEncryptionScheme(String name);

	void putEncryptionScheme(EncryptionScheme ep);

	List<CredentialSet> getCredentialSets();

	CredentialSet getCredentialSet(String name);

	void putCredentialSet(CredentialSet ec);

	EncryptionScheme getDefaultScheme();

	CredentialSet getDefaultCredentialSet();

	// public <K extends MasterKey<K>> MasterKeyProvider<K>
	// createMasterKeyProvider(
	// EncryptionScheme ep, EncryptionCredential ec);

}
