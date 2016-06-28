package com.choicemaker.cm.io.xmlenc.base.xmlconf;

import java.util.Map;

import org.w3c.dom.Element;

import com.choicemaker.xmlencryption.SecretKeyInfo;

/**
 * An EncryptionScheme generates and recovers secret key information.
 */
public interface EncryptionScheme /*<K extends MasterKey<K>>*/ {

	/**
	 * Generally the id of an encryption policy should be the fully qualified
	 * class name that uniquely identifies a particular encryption scheme.
	 */
	String getSchemeId();

	// Set<String> getRequiredPropertyNames();

	/**
	 * Checks that an encryption credential contains enough information that
	 * it might be valid for encryption.
	 */
	boolean isConsistentWithEncryption(EncryptionCredential ec);

	/**
	 * Checks that an encryption credential contains enough information that
	 * it might be valid for decryption.
	 */
	boolean isConsistentWithDecryption(EncryptionCredential ec);

	// MasterKeyProvider<K> getMasterKeyProvider(EncryptionCredential ec);

	// DataKey<K> generateDataKey(EncryptionCredential ec,
	// final Map<String, String> encryptionContext);
	//
	// DataKey<K> generateDataKey(EncryptionCredential ec,
	// CryptoAlgorithm algorithm, Map<String, String> encryptionContext);

	SecretKeyInfo generateSecretKeyInfo(EncryptionCredential ec,
			String algorithmName, Map<String, String> encryptionContext);

	SecretKeyInfo recoverSecretKeyInfo(Element encryptedKeyElement);

//	CryptoAlgorithm getDefaultAlgorithm();

	String getDefaultAlgorithmName();

}
