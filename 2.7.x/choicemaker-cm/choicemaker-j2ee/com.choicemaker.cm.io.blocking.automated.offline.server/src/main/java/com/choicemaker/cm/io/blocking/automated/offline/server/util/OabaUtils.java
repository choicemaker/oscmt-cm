/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.blocking.automated.offline.server.util;

/** @deprecated unused */
@Deprecated
public class OabaUtils {

	private OabaUtils() {
	}

	// /**
	// * Looks up or computes default OABA settings for the specified model. (If
	// * default settings are computed, they are likely to be less than
	// optimal.)
	// *
	// * @param oabaSettingsController
	// * a non-null settings controller.
	// * @param modelId
	// * a valid model name or configuration identifier.
	// * @return
	// */
	// public static OabaSettings getDefaultOabaSettings(
	// OabaSettingsController oabaSettingsController, String modelId) {
	// if (oabaSettingsController == null) {
	// throw new IllegalArgumentException("null controller");
	// }
	// if (modelId == null || !modelId.equals(modelId.trim())
	// || modelId.isEmpty()) {
	// throw new IllegalArgumentException("invalid model '" + modelId
	// + "'");
	// }
	//
	// // Get the default OABA settings, ignoring the maxSingle value.
	// // If no default settings exist, create them using the maxSingle value.
	// ImmutableProbabilityModel model =
	// PMManager.getImmutableModelInstance(modelId);
	// OabaSettings retVal =
	// oabaSettingsController.findDefaultOabaSettings(model);
	// if (retVal == null) {
	// // Creates generic settings and saves them
	// retVal = new OabaSettingsEntity();
	// retVal = oabaSettingsController.save(retVal);
	// }
	// return retVal;
	// }

	// /**
	// * Looks up or computes the default OABA server configuration <em><strong>
	// * for the host on which this method is invoked</strong></em>. In other
	// * words, this is a server-side utility, not a client-side utility.
	// *
	// * @param serverController
	// * a non-null manager of server configurations
	// * @return
	// */
	// public static ServerConfiguration getDefaultServerConfiguration(
	// ServerConfigurationController serverController) {
	// if (serverController == null) {
	// throw new IllegalArgumentException("null controller");
	// }
	//
	// // Get the default server configuration for this host (or guess at it)
	// final String hostName =
	// ServerConfigurationControllerBean.computeHostName();
	// final boolean computeFallback = true;
	// ServerConfiguration retVal =
	// serverController.getDefaultConfiguration(hostName, computeFallback);
	// return retVal;
	// }

}
