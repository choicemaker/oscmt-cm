/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.validation.eclipse.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.choicemaker.cm.validation.IValidator;
import com.choicemaker.cm.validation.ValidatorCreationException;
import com.choicemaker.util.Precondition;

/**
 * Collection of labeled validators. The semantics of the class is similar to
 * the Validators collection, but currently without the capability to look up
 * ILabelledValidators in the plugin registry.
 *
 * @author Rick Hall
 */
public final class LabeledValidators {

	private static Logger logger =
		Logger.getLogger(LabeledValidators.class.getName());

	/**
	 * The name of a system property that can be set to "true" to check for
	 * unexpected validator labels. By default, validator labels are not
	 * checked.
	 */
	public static final String PN_CHECK_VALIDATOR_LABELS =
		"cm.validation.checkValidatorLabels";

	/** Checks the system property {@link #PN_CHECK_VALIDATOR_LABELS} */
	public static boolean isCheckValidatorLabels() {
		String value = System.getProperty(PN_CHECK_VALIDATOR_LABELS, "true");
		Boolean _keepFiles = Boolean.valueOf(value);
		boolean retVal = _keepFiles.booleanValue();
		return retVal;
	}

	public static final boolean checkValidatorLabels = isCheckValidatorLabels();

	/**
	 * Cached map of label-qualifier keys to validators. Do not use directly
	 * (except in {@link #addValidator(String,IValidator)}; use
	 * {@link #getValidators()} instead.
	 * 
	 * @see #getValidators()
	 */
	private static Map<LabelQualifierKey, IValidator<?>> validators = null;

	/** Synchronization object for creating the validatorFactories singleton */
	private static final Object validatorsInit = new Object();

	/**
	 * Returns an unmodifiable copy of the labeled validator collection managed
	 * by this class. The map keys are instances of LabelQualifierKey and the
	 * values are validator instances.
	 * 
	 * @return a non-null, but possibly empty map
	 * @throws ValidatorCreationException
	 * @see createValidatorMap()
	 */
	public static Map<LabelQualifierKey, IValidator<?>> getValidators()
			throws ValidatorCreationException {
		if (validators == null) {
			synchronized (validatorsInit) {
				if (validators == null) {
					validators = createValidatorMapFromRegistry();
				}
			} // synchronized
		}
		return Collections.unmodifiableMap(validators);
	}

	public static void addValidator(String label, String qualifier,
			IValidator<?> validator) throws ValidatorCreationException {
		LabelQualifierKey key = new LabelQualifierKey(label, qualifier);
		addValidator(key, validator);
	}

	/**
	 * Adds a labeled validator to the collection of validators. If two
	 * validators are added under the same key, the second addition replaces the
	 * first.
	 * 
	 * @param key
	 *            the key of the validator configuration to be added. As a
	 *            general practice, names should be trimmed and case should be
	 *            standardized, but these recommendations aren't enforced.
	 * @param validator
	 *            The validator to be added.
	 * @throws ValidatorCreationException
	 *             if the validator collection managed by this class can not be
	 *             created.
	 */
	public static void addValidator(LabelQualifierKey key,
			IValidator<?> validator) throws ValidatorCreationException {
		Precondition.assertNonNullArgument("null validator key", key);
		Precondition.assertNonNullArgument("null validator", validator);

		synchronized (LabeledValidators.validatorsInit) {
			Map<LabelQualifierKey, IValidator<?>> newValidatorMap =
				cloneMap(getValidators());
			Object alreadyPresent = newValidatorMap.put(key, validator);
			if (alreadyPresent != null) {
				logger.warning(
						"An instance of " + alreadyPresent.getClass().getName()
								+ "was replaced as a validator by an instance of "
								+ validator.getClass().getName()
								+ " with the key '" + key + "'");
			}
			LabeledValidators.validators = newValidatorMap;
		}

	}

	private static Map<LabelQualifierKey, IValidator<?>> cloneMap(
			Map<LabelQualifierKey, IValidator<?>> map) {
		Map<LabelQualifierKey, IValidator<?>> retVal = new HashMap<>();
		retVal.putAll(map);
		return retVal;
	}

	/**
	 * Returns the validator identified by <code>key</code>.
	 * 
	 * @param key
	 *            the validator configuration for which a validator instance
	 *            should be returned
	 * @return a validator corresponding to the configuration named by
	 *         <code>key</code>, or null.
	 * @throws ValidatorCreationException
	 *             if the validator collection managed by this class can not be
	 *             created.
	 */
	public static IValidator<?> getValidator(LabelQualifierKey key)
			throws ValidatorCreationException {
		IValidator<?> retVal = getValidators().get(key);
		if (retVal == null) {
			String msg = "unknown validator '" + key + "'";
			logger.severe(msg);
		}
		return retVal;
	}

	/**
	 * Returns all the names of validator configurations managed by this class.
	 * 
	 * @return all the names of validators managed by this class
	 * @throws ValidatorCreationException
	 *             if the validator collection managed by this class can not be
	 *             created.
	 */
	public static Collection<LabelQualifierKey> getValidatorNames()
			throws ValidatorCreationException {
		return getValidators().keySet();
	}

	/**
	 * Currently unimplemented.
	 * 
	 * @see Validators#createValidatorMapFromRegistry()
	 */
	// @SuppressWarnings("unchecked")
	private static Map<LabelQualifierKey, IValidator<?>> createValidatorMapFromRegistry()
			throws ValidatorCreationException {

		Map<LabelQualifierKey, IValidator<?>> retVal = new HashMap<>();
		// Map<?, ?> factories =
		// AbstractValidatorFactory.createValidatorFactoryMap();
		// Set<String> handledExtensionPoints = (Set<String>)
		// factories.keySet();
		// for (String handledExtensionPoint : handledExtensionPoints) {
		// IValidatorFactory<?> factory =
		// (IValidatorFactory<?>) factories.get(handledExtensionPoint);
		// Map v = factory.createValidators();
		// retVal.putAll(v);
		// }
		return retVal;
	} // createValidatorMap()

	public static boolean isValid(String label, String qualifier, Object value)
			throws ValidatorCreationException {
		LabelQualifierKey key = new LabelQualifierKey(label, qualifier);
		return isValid(key, value);
	}

	/**
	 * Answers whether a specified value is valid according to the specified
	 * validator.
	 *
	 * @param key
	 *            The validationTag of the validator to be used.
	 * @param value
	 *            The value to be checked.
	 * @return whether the specified value is valid according to the specified
	 *         validator.
	 * @throws ValidatorCreationException
	 *             if the validator collection managed by this class can not be
	 *             created.
	 */
	@SuppressWarnings({
			"rawtypes", "unchecked" })
	public static boolean isValid(LabelQualifierKey key, Object value)
			throws ValidatorCreationException {

		boolean retVal = false;
		if (key == null) {
			if (checkValidatorLabels) {
				logger.warning("null validator key");
			}
			assert retVal == false;
		}
		else {
			IValidator validator = getValidators().get(key);
			if (validator != null) {
				retVal = validator.isValid(value);
			} else {
				if (checkValidatorLabels) {
					String msg = "missing IValidator '" + key + "'";
					logger.warning(msg);
				}
				assert retVal == false;
			}
		}

		return retVal;
	}

	private LabeledValidators() {
	}

}
