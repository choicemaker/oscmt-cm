/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.ejb;

import java.util.List;
import java.util.logging.Logger;

import com.choicemaker.cm.args.OabaParameters;
import com.choicemaker.cm.args.TransitivityParameters;
import com.choicemaker.cm.oaba.api.AbstractParameters;
import com.choicemaker.cm.oaba.api.OabaParametersController;
import com.choicemaker.cm.transitivity.api.TransitivityParametersController;

public class CombinedParametersController implements OabaParametersController {

	private static final Logger logger =
		Logger.getLogger(CombinedParametersController.class.getName());
	private static final String SOURCE =
		CombinedParametersController.class.getSimpleName();

	private final OabaParametersController o;
	private final TransitivityParametersController t;

	CombinedParametersController(OabaParametersController o,
			TransitivityParametersController t) {
		if (o == null || t == null) {
			throw new IllegalArgumentException("null argument");
		}
		this.o = o;
		this.t = t;
	}

	@Override
	public void delete(OabaParameters p) {
		if (p instanceof TransitivityParameters) {
			t.delete((TransitivityParameters) p);
		} else {
			this.o.delete(p);
		}
	}

	@Override
	public void delete(OabaParameters p, boolean doFlush) {
		if (p instanceof TransitivityParameters) {
			t.delete((TransitivityParameters) p, doFlush);
		} else {
			this.o.delete(p, doFlush);
		}
	}

	@Override
	public void detach(OabaParameters p) {
		if (p instanceof TransitivityParameters) {
			t.detach((TransitivityParameters) p);
		} else {
			this.o.detach(p);
		}
	}

	@Override
	public List<OabaParameters> findAllOabaParameters() {
		List<TransitivityParameters> tps = t.findAllTransitivityParameters();
		List<OabaParameters> ops = o.findAllOabaParameters();
		ops.addAll(tps);
		return ops;
	}

	@Override
	public OabaParameters findOabaParameters(long id) {
		final String METHOD = "findOabaParameters(long)";
		OabaParameters retVal = o.findOabaParameters(id);
		if (retVal != null) {
			String msg = String.format(
					"%s.%s: OABA parameters found for parameter id %d", SOURCE,
					METHOD, id);
			logger.fine(msg);
		} else {
			retVal = t.findTransitivityParametersByBatchJobId(id);
			if (retVal != null) {
				String msg = String.format(
						"%s.%s: transivity parameters found for parameter id %d",
						SOURCE, METHOD, id);
				logger.fine(msg);
			}
		}
		if (retVal == null) {
			String msg = String.format(
					"%s.%s: no OABA parameters found for parameter id %d",
					SOURCE, METHOD, id);
			logger.warning(msg);
		}
		return retVal;
	}

	@Override
	public OabaParameters findOabaParametersByBatchJobId(long jobId) {
		final String METHOD = "findOabaParametersByBatchJobId(long)";
		OabaParameters retVal = o.findOabaParametersByBatchJobId(jobId);
		if (retVal != null) {
			String msg =
				String.format("%s.%s: OABA parameters found for job %d", SOURCE,
						METHOD, jobId);
			logger.fine(msg);
		} else {
			retVal = t.findTransitivityParametersByBatchJobId(jobId);
			if (retVal != null) {
				String msg = String.format(
						"%s.%s: transivity parameters found for job %d", SOURCE,
						METHOD, jobId);
				logger.fine(msg);
			}
		}
		if (retVal == null) {
			String msg =
				String.format("%s.%s: no OABA parameters found for job %d",
						SOURCE, METHOD, jobId);
			logger.warning(msg);
		}
		return retVal;
	}

	@Override
	public OabaParameters save(OabaParameters p) {
		OabaParameters retVal;
		if (p instanceof TransitivityParameters) {
			retVal = t.save((TransitivityParameters) p);
		} else {
			retVal = this.o.save(p);
		}
		return retVal;
	}

	@Override
	public List<AbstractParameters> findAllParameters() {
		return o.findAllParameters();
	}

	@Override
	public AbstractParameters findParameters(long id) {
		return o.findParameters(id);
	}

	@Override
	public String getQueryDatabaseConfiguration(OabaParameters oabaParams) {
		return o.getQueryDatabaseConfiguration(oabaParams);
	}

	@Override
	public String getQueryDatabaseAccessor(OabaParameters oabaParams) {
		return o.getQueryDatabaseAccessor(oabaParams);
	}

	@Override
	public String getReferenceDatabaseConfiguration(OabaParameters oabaParams) {
		return o.getReferenceDatabaseConfiguration(oabaParams);
	}

	@Override
	public String getReferenceDatabaseAccessor(OabaParameters oabaParams) {
		return o.getReferenceDatabaseAccessor(oabaParams);
	}

}
