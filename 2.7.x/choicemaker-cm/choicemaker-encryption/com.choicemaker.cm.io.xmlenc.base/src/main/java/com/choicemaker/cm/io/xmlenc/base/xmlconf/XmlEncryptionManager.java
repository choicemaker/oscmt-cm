package com.choicemaker.cm.io.xmlenc.base.xmlconf;

import java.util.Map;

import com.choicemaker.xmlencryption.DocumentDecryptor;
import com.choicemaker.xmlencryption.DocumentEncryptor;

public interface XmlEncryptionManager extends EncryptionManager {

	DocumentEncryptor getDocumentEncryptor(EncryptionScheme encPolicy,
			String algorithmName, EncryptionCredential encCredential,
			Map<String, String> encContext);

	DocumentDecryptor getDocumentDecryptor(EncryptionScheme encPolicy,
			EncryptionCredential encCredential);

}
