package com.choicemaker.cm.io.xmlenc.base.xmlconf;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.amazonaws.auth.AWSCredentials;
import com.choicemaker.xmlencryption.AwsKmsUtils;
import com.choicemaker.xmlencryption.DocumentDecryptor;
import com.choicemaker.xmlencryption.DocumentEncryptor;
import com.choicemaker.xmlencryption.SecretKeyInfoFactory;

public class InMemoryXmlEncManager implements XmlEncryptionManager {

	private static final Logger logger = Logger
			.getLogger(InMemoryEncryptionManager.class.getName());

	private static final InMemoryXmlEncManager instance = new InMemoryXmlEncManager();
	private static final String SOURCE_CLASS = InMemoryXmlEncManager.class
			.getSimpleName();

	public static XmlEncryptionManager getInstance() {
		return instance;
	}

	private InMemoryXmlEncManager() {
	}

	private final EncryptionManager delegate = InMemoryEncryptionManager
			.getInstance();

	@Override
	public List<EncryptionScheme> getEncryptionSchemes() {
		return delegate.getEncryptionSchemes();
	}

	@Override
	public EncryptionScheme getEncryptionScheme(String name) {
		return delegate.getEncryptionScheme(name);
	}

	@Override
	public void putEncryptionScheme(EncryptionScheme ep) {
		delegate.putEncryptionScheme(ep);
	}

	@Override
	public List<EncryptionCredential> getEncryptionCredentials() {
		return delegate.getEncryptionCredentials();
	}

	@Override
	public EncryptionCredential getEncryptionCredential(String name) {
		return delegate.getEncryptionCredential(name);
	}

	@Override
	public void putEncryptionCredential(EncryptionCredential ec) {
		delegate.putEncryptionCredential(ec);
	}

	@Override
	public DocumentEncryptor getDocumentEncryptor(EncryptionScheme encPolicy,
			String algorithmName, EncryptionCredential encCredential,
			Map<String, String> encContext) {
		final String METHOD = "getDocumentEncryptor";
		logger.entering(SOURCE_CLASS, METHOD);
		// TODO Auto-generated method stub
		SecretKeyInfoFactory skif = encPolicy.getSecretKeyInfoFactory(
				encCredential, algorithmName, encContext);
		DocumentEncryptor retVal = new DocumentEncryptor(skif);
		return retVal;
	}

	@Override
	public DocumentDecryptor getDocumentDecryptor(EncryptionScheme encPolicy,
			EncryptionCredential encCredential) {
		final String METHOD = "getDocumentDecryptor";
		logger.entering(SOURCE_CLASS, METHOD);
		// TODO Auto-generated method stub
		// 	public DocumentDecryptor(String endPoint, AWSCredentials creds) {
		DocumentDecryptor retVal = null;
		throw new Error("not yet implemented");
	}

	// @Override
	// public DocumentDecryptor getDocumentDecryptor(EncryptionScheme ep,
	// EncryptionCredential ec) {
	// final String METHOD = "getDocumentDecryptor";
	// logger.entering(SOURCE_CLASS, METHOD);
	// final SecretKeyInfoFactory skif = new AwsKmsSecretKeyInfoFactory(
	// params.getAwsMasterKeyId(),
	// AwsKmsUtils.DEFAULT_AWS_KEY_ENCRYPTION_ALGORITHM,
	// params.getAwsEndpoint(), creds);
	// //
	// //decryptor = new DocumentDecryptor(params.getAwsEndpoint(), creds);
	// //encryptor = new DocumentEncryptor(skif);
	// throw new Error("not yet implemented");
	// }

	// @Override
	// public DocumentEncryptor getDocumentEncryptor(
	// EncryptionScheme encPolicy, EncryptionCredential encCredential) {
	// final String METHOD = "getDocumentEncryptor";
	// logger.entering(SOURCE_CLASS, METHOD);
	// MasterKeyProvider<?> mkp = encPolicy
	// .getMasterKeyProvider(encCredential);
	//
	// // final SecretKeyInfoFactory skif = new SecretKeyInfoFactory(
	// // params.getAwsMasterKeyId(),
	// // AwsKmsUtils.DEFAULT_AWS_KEY_ENCRYPTION_ALGORITHM,
	// // params.getAwsEndpoint(), creds);
	// //
	// //decryptor = new DocumentDecryptor(params.getAwsEndpoint(), creds);
	// //encryptor = new DocumentEncryptor(skif);
	// throw new Error("not yet implemented");
	// }

}
