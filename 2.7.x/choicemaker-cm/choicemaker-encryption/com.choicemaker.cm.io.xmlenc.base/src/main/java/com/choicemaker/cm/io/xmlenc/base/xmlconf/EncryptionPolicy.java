package com.choicemaker.cm.io.xmlenc.base.xmlconf;

import java.util.Map;
import java.util.Set;

import com.amazonaws.encryptionsdk.CryptoAlgorithm;
import com.amazonaws.encryptionsdk.DataKey;
import com.amazonaws.encryptionsdk.MasterKey;
import com.amazonaws.encryptionsdk.MasterKeyProvider;
import com.amazonaws.encryptionsdk.kms.KmsMasterKey;

/**
 * An EncryptionPolicy is factory for an AWS MasterKeyProvider. It defines set
 * of properties that an encryption credential must define in order to be valid.
 */
public interface EncryptionPolicy<K extends MasterKey<K>> {

	/**
	 * Generally the id of encryption policy should be the fully qualified class
	 * name of MasterKeyProvider that this factory produces.
	 */
	String getPolicyId();

	Set<String> getRequiredPropertyNames();

	boolean isValid(EncryptionCredential ec);

	MasterKeyProvider<K> getMasterKeyProvider(EncryptionCredential ec);

	DataKey<K> generateDataKey(EncryptionCredential ec,
			final Map<String, String> encryptionContext);

	DataKey<KmsMasterKey> generateDataKey(EncryptionCredential ec,
			CryptoAlgorithm algorithm, Map<String, String> encryptionContext);

	CryptoAlgorithm getDefaultAlgorithm();

}
