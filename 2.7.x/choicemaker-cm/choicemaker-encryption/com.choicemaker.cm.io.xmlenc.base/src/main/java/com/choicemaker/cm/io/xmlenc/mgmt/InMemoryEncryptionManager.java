package com.choicemaker.cm.io.xmlenc.mgmt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.choicemaker.utilcopy01.Precondition;
import com.choicemaker.xmlencryption.AwsKmsEncryptionScheme;
import com.choicemaker.xmlencryption.CredentialSet;
import com.choicemaker.xmlencryption.EncryptionScheme;

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

	private final Map<String, EncryptionScheme> encSchemes = new HashMap<>();
	private final Map<String, CredentialSet> encCredentials = new HashMap<>();

	private InMemoryEncryptionManager() {
		// HACK FIXME read encryption schemes from plugin registry
		AwsKmsEncryptionScheme scheme = new AwsKmsEncryptionScheme();
		putEncryptionScheme(scheme);
	}

	@Override
	public List<EncryptionScheme> getEncryptionSchemes() {
		final String METHOD = "getEncryptionPolicies";
		logger.entering(SOURCE_CLASS, METHOD);
		List<EncryptionScheme> retVal = new ArrayList<>();
		retVal.addAll(encSchemes.values());
		return Collections.unmodifiableList(retVal);
	}

	@Override
	public EncryptionScheme getEncryptionScheme(String name) {
		final String METHOD = "getEncryptionPolicy";
		logger.entering(SOURCE_CLASS, METHOD);
		Precondition.assertNonEmptyString("null or blank name", name);
		EncryptionScheme retVal = encSchemes.get(name);
		assert name.equals(retVal.getSchemeId());
		return retVal;
	}

	@Override
	public void putEncryptionScheme(EncryptionScheme es) {
		final String METHOD = "putEncryptionPolicy";
		logger.entering(SOURCE_CLASS, METHOD);
		if (es != null) {
			String name = es.getSchemeId();
			if (name != null) {
				encSchemes.put(name, es);
			}
		}
	}

	@Override
	public List<CredentialSet> getCredentialSets() {
		final String METHOD = "getEncryptionCredentials";
		logger.entering(SOURCE_CLASS, METHOD);
		List<CredentialSet> retVal = new ArrayList<>();
		retVal.addAll(encCredentials.values());
		return Collections.unmodifiableList(retVal);
	}

	@Override
	public CredentialSet getCredentialSet(String name) {
		final String METHOD = "getEncryptionCredentials(String)";
		logger.entering(SOURCE_CLASS, METHOD);
		Precondition.assertNonEmptyString("null or blank name", name);
		CredentialSet retVal = encCredentials.get(name);
		assert name.equals(retVal.getCredentialName());
		return retVal;
	}

	@Override
	public void putCredentialSet(CredentialSet ec) {
		final String METHOD = "putEncryptionCredential";
		logger.entering(SOURCE_CLASS, METHOD);
		if (ec != null) {
			String name = ec.getCredentialName();
			if (name != null) {
				encCredentials.put(name, ec);
			}
		}
	}

	/**
	 * Returns an arbitrary scheme from the encryption manager, or null if the
	 * manager is null or the manager holds no schemes.
	 */
	@Override
	public EncryptionScheme getDefaultScheme() {
		EncryptionScheme retVal = null;
		List<EncryptionScheme> schemes = getEncryptionSchemes();
		if (schemes.size() > 0) {
			retVal = schemes.get(0);
		}
		return retVal;
	}

	/**
	 * Returns an arbitrary credential set from the encryption manager, or null
	 * if the manager is null or the manager holds no credentials.
	 */
	@Override
	public CredentialSet getDefaultCredentialSet() {
		CredentialSet retVal = null;
		List<CredentialSet> csets = getCredentialSets();
		if (csets.size() > 0) {
			retVal = csets.get(0);
		}
		return retVal;
	}

}
