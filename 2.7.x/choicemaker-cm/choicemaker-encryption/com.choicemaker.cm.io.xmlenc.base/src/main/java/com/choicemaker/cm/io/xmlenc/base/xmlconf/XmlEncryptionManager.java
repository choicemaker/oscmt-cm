package com.choicemaker.cm.io.xmlenc.base.xmlconf;

import com.choicemaker.xmlencryption.DocumentDecryptor;
import com.choicemaker.xmlencryption.DocumentEncryptor;

public interface XmlEncryptionManager extends EncryptionManager {

	DocumentEncryptor getDocumentEncryptor(EncryptionScheme encPolicy,
			EncryptionCredential encCredential);

	DocumentDecryptor getDocumentDecryptor(EncryptionScheme encPolicy,
			EncryptionCredential encCredential);

}
