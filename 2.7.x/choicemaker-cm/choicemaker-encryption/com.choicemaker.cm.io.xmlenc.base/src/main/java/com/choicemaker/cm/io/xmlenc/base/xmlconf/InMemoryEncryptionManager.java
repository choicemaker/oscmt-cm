package com.choicemaker.cm.io.xmlenc.base.xmlconf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.amazonaws.encryptionsdk.MasterKey;
import com.amazonaws.encryptionsdk.MasterKeyProvider;
import com.choicemaker.utilcopy01.Precondition;

/**
 * An in-memory implementation of an EncryptionManager. This class is a
 * singleton.
 * 
 * @author rphall
 */
public class InMemoryEncryptionManager implements EncryptionManager {

	private static final Logger logger = Logger
			.getLogger(InMemoryEncryptionManager.class.getName());

	private static final InMemoryEncryptionManager instance = new InMemoryEncryptionManager();
	private static final String SOURCE_CLASS = InMemoryEncryptionManager.class
			.getSimpleName();

	public static EncryptionManager getInstance() {
		return instance;
	}

	private final Map<String, EncryptionScheme> encPolicies = new HashMap<>();
	private final Map<String, EncryptionCredential> encCredentials = new HashMap<>();

	private InMemoryEncryptionManager() {
	}

	@Override
	public List<EncryptionScheme> getEncryptionSchemes() {
		final String METHOD = "getEncryptionPolicies";
		logger.entering(SOURCE_CLASS, METHOD);
		List<EncryptionScheme> retVal = new ArrayList<>();
		retVal.addAll(encPolicies.values());
		return Collections.unmodifiableList(retVal);
	}

	@Override
	public EncryptionScheme getEncryptionScheme(String name) {
		final String METHOD = "getEncryptionPolicy";
		logger.entering(SOURCE_CLASS, METHOD);
		Precondition.assertNonEmptyString("null or blank name", name);
		EncryptionScheme retVal = encPolicies.get(name);
		assert name.equals(retVal.getSchemeId());
		return retVal;
	}

	@Override
	public void putEncryptionScheme(EncryptionScheme ep) {
		final String METHOD = "putEncryptionPolicy";
		logger.entering(SOURCE_CLASS, METHOD);
		String name = ep.getSchemeId();
		encPolicies.put(name, ep);
	}

	@Override
	public List<EncryptionCredential> getEncryptionCredentials() {
		final String METHOD = "getEncryptionCredentials";
		logger.entering(SOURCE_CLASS, METHOD);
		List<EncryptionCredential> retVal = new ArrayList<>();
		retVal.addAll(encCredentials.values());
		return Collections.unmodifiableList(retVal);
	}

	@Override
	public EncryptionCredential getEncryptionCredential(String name) {
		final String METHOD = "getEncryptionCredentials(String)";
		logger.entering(SOURCE_CLASS, METHOD);
		Precondition.assertNonEmptyString("null or blank name", name);
		EncryptionCredential retVal = encCredentials.get(name);
		assert name.equals(retVal.getCredentialName());
		return retVal;
	}

	@Override
	public void putEncryptionCredential(EncryptionCredential ec) {
		final String METHOD = "putEncryptionCredential";
		logger.entering(SOURCE_CLASS, METHOD);
		String name = ec.getCredentialName();
		encCredentials.put(name, ec);
	}

}
