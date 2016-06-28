package com.choicemaker.cm.io.xmlenc.base.xmlconf;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Element;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.encryptionsdk.CryptoAlgorithm;
import com.choicemaker.util.Precondition;
import com.choicemaker.utilcopy01.StringUtils;
import com.choicemaker.xmlencryption.AwsKmsSecretKeyInfoFactory;
import com.choicemaker.xmlencryption.EncryptionParameters;
import com.choicemaker.xmlencryption.SecretKeyInfo;

public class AwsKmsEncryptionScheme implements EncryptionScheme {

	// private static final Logger logger = Logger
	// .getLogger(AwsKmsEncryptionScheme.class.getName());

	public static final CryptoAlgorithm DEFAULT_ALGORITHM = CryptoAlgorithm.ALG_AES_256_GCM_IV12_TAG16_HKDF_SHA256;

	public static final String PN_ACCESSKEY = EncryptionParameters.PN_ACCESSKEY;
	public static final String PN_SECRETKEY = EncryptionParameters.PN_SECRETKEY;
	public static final String PN_MASTERKEY = EncryptionParameters.PN_MASTERKEY;
	public static final String PN_ENDPOINT = EncryptionParameters.PN_ENDPOINT;

	private static final String[] REQUIRED_PROPERTY_NAMES = new String[] {
			PN_ACCESSKEY, PN_SECRETKEY, PN_MASTERKEY };

	public static SecretKeyInfo createSessionKey(AWSCredentials creds,
			String masterKeyId, String algorithm, String endpoint) {
		return AwsKmsSecretKeyInfoFactory.createSessionKey(creds, masterKeyId,
				algorithm, endpoint);
	}

	// private AtomicReference<MasterKeyProvider<KmsMasterKey>>
	// masterKeyProvider = new AtomicReference<>(
	// null);

	public Set<String> getRequiredPropertyNames() {
		Set<String> retVal = new HashSet<>();
		retVal.addAll(Arrays.asList(REQUIRED_PROPERTY_NAMES));
		return Collections.unmodifiableSet(retVal);
	}

	@Override
	public boolean isConsistentWithEncryption(EncryptionCredential ec) {
		boolean retVal = ec != null;
		if (retVal) {
			for (String pn : getRequiredPropertyNames()) {
				String value = ec.get(pn);
				if (!StringUtils.nonEmptyString(value)) {
					retVal = false;
				}
			}
		}
		return retVal;
	}

	@Override
	public boolean isConsistentWithDecryption(EncryptionCredential ec) {
		return isConsistentWithEncryption(ec);
	}

	// protected MasterKeyProvider<KmsMasterKey> createMasterKeyProvider(
	// EncryptionCredential ec) {
	// String accessKey = ec.get(PN_ACCESSKEY);
	// String secretKey = ec.get(PN_SECRETKEY);
	// final AWSCredentials creds = new BasicAWSCredentials(accessKey,
	// secretKey);
	// String masterKeyId = ec.get(PN_MASTERKEY);
	// final KmsMasterKeyProvider retVal = new KmsMasterKeyProvider(creds,
	// masterKeyId);
	// return retVal;
	// }
	//
	// public MasterKeyProvider<KmsMasterKey> getMasterKeyProvider(
	// EncryptionCredential ec) {
	// MasterKeyProvider<KmsMasterKey> retVal = masterKeyProvider.get();
	// if (retVal == null) {
	// retVal = createMasterKeyProvider(ec);
	// boolean updated = masterKeyProvider.compareAndSet(null, retVal);
	// if (!updated) {
	// retVal = masterKeyProvider.get();
	// }
	// }
	// assert retVal != null;
	// return retVal;
	// }
	//
	// public DataKey<KmsMasterKey> generateDataKey(EncryptionCredential ec,
	// Map<String, String> encryptionContext) {
	// CryptoAlgorithm ca = getDefaultAlgorithm();
	// DataKey<KmsMasterKey> retVal = generateDataKey(ec, ca,
	// encryptionContext);
	// return retVal;
	// }
	//
	// public DataKey<KmsMasterKey> generateDataKey(EncryptionCredential ec,
	// CryptoAlgorithm algorithm, Map<String, String> encryptionContext) {
	// MasterKeyProvider<KmsMasterKey> mkp = getMasterKeyProvider(ec);
	// String masterKeyId = ec.get(PN_MASTERKEY);
	// KmsMasterKey mk = mkp.getMasterKey(masterKeyId);
	// DataKey<KmsMasterKey> retVal = mk.generateDataKey(algorithm,
	// encryptionContext);
	// return retVal;
	// }

	public CryptoAlgorithm getDefaultAlgorithm() {
		return DEFAULT_ALGORITHM;
	}

	@Override
	public String getSchemeId() {
		return AwsKmsEncryptionScheme.class.getName();
	}

	@Override
	public SecretKeyInfo generateSecretKeyInfo(EncryptionCredential ec,
			String algorithmName, Map<String, String> encryptionContext) {
		Precondition.assertNonNullArgument("null credential", ec);
		Precondition.assertBoolean(isConsistentWithEncryption(ec));
		Precondition.assertNonEmptyString("null or blank algorithm name",
				algorithmName);

		String accessKey = ec.get(PN_ACCESSKEY);
		String secretKey = ec.get(PN_SECRETKEY);
		String masterKeyId = ec.get(PN_MASTERKEY);
		String endpoint = ec.get(PN_ENDPOINT);
		final AWSCredentials creds = new BasicAWSCredentials(accessKey,
				secretKey);

		SecretKeyInfo retVal = AwsKmsSecretKeyInfoFactory.createSessionKey(
				creds, masterKeyId, algorithmName, endpoint);
		return retVal;
	}

	@Override
	public SecretKeyInfo recoverSecretKeyInfo(Element encryptedKeyElement) {
		// TODO Auto-generated method stub
		throw new Error("not yet implemented");
	}

	@Override
	public String getDefaultAlgorithmName() {
		return getDefaultAlgorithm().name();
	}

}
