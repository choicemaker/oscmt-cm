package com.choicemaker.cm.io.xmlenc.base.xmlconf;

import java.util.List;

import com.choicemaker.xmlencryption.CredentialSet;
import com.choicemaker.xmlencryption.EncryptionScheme;

public interface EncryptionManager {

	// int getTimeToLive();

	// void setTimeToLive(int millisecs);

	List<EncryptionScheme> getEncryptionSchemes();

	EncryptionScheme getEncryptionScheme(String name);

	void putEncryptionScheme(EncryptionScheme ep);

	List<CredentialSet> getEncryptionCredentials();

	CredentialSet getEncryptionCredential(String name);

	void putEncryptionCredential(CredentialSet ec);

	// public <K extends MasterKey<K>> MasterKeyProvider<K>
	// createMasterKeyProvider(
	// EncryptionScheme ep, EncryptionCredential ec);

}