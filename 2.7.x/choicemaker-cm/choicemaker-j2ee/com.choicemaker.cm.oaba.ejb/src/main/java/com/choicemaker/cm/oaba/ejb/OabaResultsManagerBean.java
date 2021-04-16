package com.choicemaker.cm.oaba.ejb;

import static com.choicemaker.cm.args.OperationalPropertyNames.PN_OABA_CACHED_RESULTS_FILE;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchResultsManager;
import com.choicemaker.cm.batch.api.OperationalPropertyController;
import com.choicemaker.cm.batch.ejb.BatchExportUtils;
import com.choicemaker.util.Precondition;

@Stateless
public class OabaResultsManagerBean implements BatchResultsManager {

	public static final Logger logger =
		Logger.getLogger(OabaResultsManagerBean.class.getName());

	@EJB
	private OperationalPropertyController propController;

	@Override
	public void exportResults(BatchJob batchJob, URI container)
			throws IOException, URISyntaxException {
		Precondition.assertBoolean("not an OABA Job entity",
				batchJob instanceof OabaJobEntity);

		BatchExportUtils.exportResults(batchJob, container, propController,
				PN_OABA_CACHED_RESULTS_FILE);
	}

}
