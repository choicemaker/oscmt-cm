package com.choicemaker.cm.io.xmlenc.base.xmlconf;

import java.util.List;

import com.choicemaker.xmlencryption.DocumentDecryptor;
import com.choicemaker.xmlencryption.DocumentEncryptor;

public interface XmlCredentialsManager {
	
//	int getTimeToLive();
//	
//	void setTimeToLive(int millisecs);
	
	List<EncryptionPolicy> getEncryptionPolicies();
	
	EncryptionPolicy getEncryptionPolicy(String name);
	
	List<EncryptionCredential> getEncryptionCredentials();

	EncryptionCredential getEncryptionCredentials(String name);
	
	void putEncryptionCredential(String name, EncryptionCredential ec);

	DocumentDecryptor getDocumentDecryptor(String credentialsName);

	DocumentEncryptor getDocumentEncryptor(String credentialsName);

}
