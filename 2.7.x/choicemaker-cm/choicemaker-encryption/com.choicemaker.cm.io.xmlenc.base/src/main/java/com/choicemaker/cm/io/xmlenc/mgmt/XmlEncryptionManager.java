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

import com.choicemaker.xmlencryption.CredentialSet;
import com.choicemaker.xmlencryption.DocumentDecryptor;
import com.choicemaker.xmlencryption.DocumentEncryptor;
import com.choicemaker.xmlencryption.EncryptionScheme;

public interface XmlEncryptionManager extends EncryptionManager {

	DocumentEncryptor getDocumentEncryptor(EncryptionScheme encPolicy,
			CredentialSet encCredential);

	DocumentDecryptor getDocumentDecryptor(EncryptionScheme encPolicy,
			CredentialSet encCredential);

}
