/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.server.ejb;

import java.util.List;

import javax.ejb.Local;

import com.choicemaker.cm.args.TransitivityParameters;

@Local
public interface TransitivityParametersController {

	void delete(TransitivityParameters tp);

	void detach(TransitivityParameters tp);

	List<TransitivityParameters> findAllTransitivityParameters();

	TransitivityParameters findTransitivityParameters(long id);

	TransitivityParameters findTransitivityParametersByBatchJobId(long jobId);

	TransitivityParameters save(TransitivityParameters tp);

}
