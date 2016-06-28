package com.choicemaker.cm.io.xmlenc.base.xmlconf;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.encryptionsdk.CryptoAlgorithm;
import com.amazonaws.encryptionsdk.DataKey;
import com.amazonaws.encryptionsdk.MasterKeyProvider;
import com.amazonaws.encryptionsdk.kms.KmsMasterKey;
import com.amazonaws.encryptionsdk.kms.KmsMasterKeyProvider;
import com.choicemaker.utilcopy01.StringUtils;
import com.choicemaker.xmlencryption.EncryptionParameters;

public class DefaultEncryptionPolicy implements EncryptionPolicy<KmsMasterKey> {

	public static final CryptoAlgorithm DEFAULT_ALGORITHM = CryptoAlgorithm.ALG_AES_256_GCM_IV12_TAG16_HKDF_SHA256;

	public static final String PN_ACCESSKEY = EncryptionParameters.PN_ACCESSKEY;
	public static final String PN_SECRETKEY = EncryptionParameters.PN_SECRETKEY;
	public static final String PN_MASTERKEY = EncryptionParameters.PN_MASTERKEY;
	public static final String PN_ENDPOINT = EncryptionParameters.PN_ENDPOINT;
	// public static final String PN_ESCROWKEY =
	// EncryptionParameters.PN_ESCROWKEY;

	private static final String[] REQUIRED_PROPERTY_NAMES = new String[] {
			PN_ACCESSKEY, PN_SECRETKEY, PN_MASTERKEY };

	private AtomicReference<MasterKeyProvider<KmsMasterKey>> masterKeyProvider = new AtomicReference<>(
			null);

	@Override
	public String getPolicyId() {
		String retVal = KmsMasterKey.class.getName();
		return retVal;
	}

	@Override
	public Set<String> getRequiredPropertyNames() {
		Set<String> retVal = new HashSet<>();
		retVal.addAll(Arrays.asList(REQUIRED_PROPERTY_NAMES));
		return Collections.unmodifiableSet(retVal);
	}

	@Override
	public boolean isValid(EncryptionCredential ec) {
		boolean retVal = true;
		for (String pn : getRequiredPropertyNames()) {
			String value = ec.get(pn);
			if (!StringUtils.nonEmptyString(value)) {
				retVal = false;
			}
		}
		return retVal;
	}

	protected MasterKeyProvider<KmsMasterKey> createMasterKeyProvider(
			EncryptionCredential ec) {
		String accessKey = ec.get(PN_ACCESSKEY);
		String secretKey = ec.get(PN_SECRETKEY);
		final AWSCredentials creds = new BasicAWSCredentials(accessKey,
				secretKey);
		String masterKeyId = ec.get(PN_MASTERKEY);
		final KmsMasterKeyProvider retVal = new KmsMasterKeyProvider(creds,
				masterKeyId);
		return retVal;
	}

	@Override
	public MasterKeyProvider<KmsMasterKey> getMasterKeyProvider(
			EncryptionCredential ec) {
		MasterKeyProvider<KmsMasterKey> retVal = masterKeyProvider.get();
		if (retVal == null) {
			retVal = createMasterKeyProvider(ec);
			boolean updated = masterKeyProvider.compareAndSet(null, retVal);
			if (!updated) {
				retVal = masterKeyProvider.get();
			}
		}
		assert retVal != null;
		return retVal;
	}

	@Override
	public DataKey<KmsMasterKey> generateDataKey(EncryptionCredential ec,
			Map<String, String> encryptionContext) {
		CryptoAlgorithm ca = getDefaultAlgorithm();
		DataKey<KmsMasterKey> retVal = generateDataKey(ec, ca,
				encryptionContext);
		return retVal;
	}

	@Override
	public DataKey<KmsMasterKey> generateDataKey(EncryptionCredential ec,
			CryptoAlgorithm algorithm, Map<String, String> encryptionContext) {
		MasterKeyProvider<KmsMasterKey> mkp = getMasterKeyProvider(ec);
		String masterKeyId = ec.get(PN_MASTERKEY);
		KmsMasterKey mk = mkp.getMasterKey(masterKeyId);
		DataKey<KmsMasterKey> retVal = mk.generateDataKey(algorithm,
				encryptionContext);
		return retVal;
	}

	@Override
	public CryptoAlgorithm getDefaultAlgorithm() {
		return DEFAULT_ALGORITHM;
	}

}
