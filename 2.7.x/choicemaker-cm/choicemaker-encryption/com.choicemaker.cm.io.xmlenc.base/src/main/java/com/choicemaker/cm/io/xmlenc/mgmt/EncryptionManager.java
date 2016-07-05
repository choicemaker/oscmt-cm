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