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
