package com.choicemaker.cm.io.xmlenc.base.xmlconf;

import java.util.List;

public interface EncryptionManager {

	// int getTimeToLive();

	// void setTimeToLive(int millisecs);

	List<EncryptionScheme> getEncryptionSchemes();

	EncryptionScheme getEncryptionScheme(String name);

	void putEncryptionScheme(EncryptionScheme ep);

	List<EncryptionCredential> getEncryptionCredentials();

	EncryptionCredential getEncryptionCredential(String name);

	void putEncryptionCredential(EncryptionCredential ec);

//	public <K extends MasterKey<K>> MasterKeyProvider<K> createMasterKeyProvider(
//			EncryptionScheme ep, EncryptionCredential ec);

}