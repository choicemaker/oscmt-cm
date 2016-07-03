package com.choicemaker.cm.io.xmlenc.xmlconf;

import java.util.List;
import java.util.logging.Logger;

import com.choicemaker.xmlencryption.CredentialSet;
import com.choicemaker.xmlencryption.DocumentDecryptor;
import com.choicemaker.xmlencryption.DocumentEncryptor;
import com.choicemaker.xmlencryption.EncryptionScheme;

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
	public List<CredentialSet> getEncryptionCredentials() {
		return delegate.getEncryptionCredentials();
	}

	@Override
	public CredentialSet getEncryptionCredential(String name) {
		return delegate.getEncryptionCredential(name);
	}

	@Override
	public void putEncryptionCredential(CredentialSet ec) {
		delegate.putEncryptionCredential(ec);
	}

	@Override
	public DocumentEncryptor getDocumentEncryptor(EncryptionScheme encPolicy,
			CredentialSet encCredential) {
		final String METHOD = "getDocumentEncryptor";
		logger.entering(SOURCE_CLASS, METHOD);
		DocumentEncryptor retVal = new DocumentEncryptor(encPolicy,
				encCredential);
		return retVal;
	}

	@Override
	public DocumentDecryptor getDocumentDecryptor(EncryptionScheme encPolicy,
			CredentialSet encCredential) {
		final String METHOD = "getDocumentDecryptor";
		logger.entering(SOURCE_CLASS, METHOD);
		DocumentDecryptor retVal = new DocumentDecryptor(encPolicy,
				encCredential);
		return retVal;
	}

}
