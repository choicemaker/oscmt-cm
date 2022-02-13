/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.api;

import javax.ejb.Local;

import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.oaba.api.OabaSettingsController;

/**
 * Manages a database of ABA and OABA settings.
 * 
 * @author rphall
 *
 */
@Local
public interface TransitivitySettingsController extends OabaSettingsController {

	OabaSettings findSettingsByTransitivityJobId(long jobId);

}
