package com.choicemaker.cm.io.xmlenc.mgmt;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import com.choicemaker.util.Precondition;
import com.choicemaker.xmlencryption.CredentialSet;
import com.choicemaker.xmlencryption.DocumentDecryptor;
import com.choicemaker.xmlencryption.DocumentEncryptor;
import com.choicemaker.xmlencryption.EncryptionScheme;

public class InMemoryXmlEncManager implements XmlEncryptionManager {

	private static final Logger logger = Logger
			.getLogger(InMemoryEncryptionManager.class.getName());

	private static final AtomicReference<InMemoryXmlEncManager> instance = new AtomicReference<>(
			null);

	private static final String SOURCE_CLASS = InMemoryXmlEncManager.class
			.getSimpleName();

	public static XmlEncryptionManager getInstance() {
		XmlEncryptionManager retVal = instance.get();
		if (retVal == null) {
			InMemoryXmlEncManager em = new InMemoryXmlEncManager(
					InMemoryEncryptionManager.getInstance());
			instance.compareAndSet(null, em);
			retVal = instance.get();
		}
		return retVal;
	}

	private final EncryptionManager delegate;

	private InMemoryXmlEncManager(EncryptionManager em) {
		Precondition.assertNonNullArgument("null encryption manager", em);
		this.delegate = em;
	}

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
	public List<CredentialSet> getCredentialSets() {
		return delegate.getCredentialSets();
	}

	@Override
	public CredentialSet getCredentialSet(String name) {
		return delegate.getCredentialSet(name);
	}

	@Override
	public void putCredentialSet(CredentialSet ec) {
		delegate.putCredentialSet(ec);
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

	@Override
	public EncryptionScheme getDefaultScheme() {
		return delegate.getDefaultScheme();
	}

	@Override
	public CredentialSet getDefaultCredentialSet() {
		return delegate.getDefaultCredentialSet();
	}

}
