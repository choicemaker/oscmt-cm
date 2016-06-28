package com.choicemaker.cm.io.xmlenc.base.xmlconf;

import java.util.List;

import com.amazonaws.encryptionsdk.MasterKey;
import com.amazonaws.encryptionsdk.MasterKeyProvider;

public interface EncryptionManager {

	// int getTimeToLive();

	// void setTimeToLive(int millisecs);

	List<EncryptionPolicy<?>> getEncryptionPolicies();

	EncryptionPolicy<?> getEncryptionPolicy(String name);

	void putEncryptionPolicy(EncryptionPolicy<?> ep);

	List<EncryptionCredential> getEncryptionCredentials();

	EncryptionCredential getEncryptionCredential(String name);

	void putEncryptionCredential(EncryptionCredential ec);

	public <K extends MasterKey<K>> MasterKeyProvider<K> createMasterKeyProvider(
			EncryptionPolicy<K> ep, EncryptionCredential ec);

}