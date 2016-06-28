package com.choicemaker.cm.io.xmlenc.base.xmlconf;

import com.choicemaker.xmlencryption.DocumentDecryptor;
import com.choicemaker.xmlencryption.DocumentEncryptor;

public interface XmlEncryptionManager extends EncryptionManager {

	DocumentEncryptor getDocumentEncryptor(EncryptionPolicy<?> encPolicy,
			EncryptionCredential encCredential);

	DocumentDecryptor getDocumentDecryptor(EncryptionPolicy<?> encPolicy,
			EncryptionCredential encCredential);

}
