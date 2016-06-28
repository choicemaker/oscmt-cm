package com.choicemaker.cm.io.xmlenc.base.xmlconf;

import java.util.List;
import java.util.logging.Logger;

import com.amazonaws.encryptionsdk.MasterKey;
import com.amazonaws.encryptionsdk.MasterKeyProvider;
import com.choicemaker.xmlencryption.DocumentDecryptor;
import com.choicemaker.xmlencryption.DocumentEncryptor;

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
	public List<EncryptionPolicy<?>> getEncryptionPolicies() {
		return delegate.getEncryptionPolicies();
	}

	@Override
	public EncryptionPolicy<?> getEncryptionPolicy(String name) {
		return delegate.getEncryptionPolicy(name);
	}

	@Override
	public void putEncryptionPolicy(EncryptionPolicy<?> ep) {
		delegate.putEncryptionPolicy(ep);
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
	public <K extends MasterKey<K>> MasterKeyProvider<K> createMasterKeyProvider(
			EncryptionPolicy<K> ep, EncryptionCredential ec) {
		return delegate.createMasterKeyProvider(ep, ec);
	}

	@Override
	public DocumentDecryptor getDocumentDecryptor(EncryptionPolicy<?> unused,
			EncryptionCredential encCredential) {
		final String METHOD = "getDocumentDecryptor";
		logger.entering(SOURCE_CLASS, METHOD);
		// TODO Auto-generated method stub
		throw new Error("not yet implemented");
	}

	@Override
	public DocumentEncryptor getDocumentEncryptor(
			EncryptionPolicy<?> encPolicy, EncryptionCredential encCredential) {
		final String METHOD = "getDocumentEncryptor";
		logger.entering(SOURCE_CLASS, METHOD);
		MasterKeyProvider<?> mkp = encPolicy
				.getMasterKeyProvider(encCredential);

		// TODO Auto-generated method stub
		throw new Error("not yet implemented");
	}

}
