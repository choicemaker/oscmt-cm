package com.choicemaker.cms.ejb;

//import static com.choicemaker.cm.args.OperationalPropertyNames.PN_TRANSITIVITY_CACHED_GROUPS_FILE;
//import static com.choicemaker.cm.args.OperationalPropertyNames.PN_TRANSITIVITY_CACHED_PAIRS_FILE;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchResultsManager;
import com.choicemaker.cm.oaba.ejb.OabaJobEntity;
import com.choicemaker.cm.oaba.ejb.OabaResultsManagerBean;
import com.choicemaker.cm.transitivity.ejb.TransitivityJobEntity;
import com.choicemaker.cm.transitivity.ejb.TransitivityResultsManagerBean;
import com.choicemaker.cms.api.UrmJobManager;
import com.choicemaker.util.Precondition;

@Stateless
public class UrmResultsManagerBean implements BatchResultsManager {

	private static final Logger logger =
		Logger.getLogger(UrmResultsManagerBean.class.getName());

	@EJB
	private UrmJobManager urmJobManager;

	@EJB
	private OabaResultsManagerBean oabaResultsManager;

	@EJB
	private TransitivityResultsManagerBean transitivityResultsManager;

	@Override
	public void exportResults(BatchJob batchJob, URI container)
			throws IOException, URISyntaxException {
		Precondition.assertBoolean("not a Transitivity Job entity",
				batchJob instanceof UrmJobEntity);

		long urmId = batchJob.getId();
		logger.finer("ExportResults: urm job id: " + urmId);
		List<BatchJob> delegates = urmJobManager.findAllLinkedByUrmId(urmId);
		for (BatchJob delegate : delegates) {
			assert delegate != null;
			logger.finer("ExportResults: delegate job id: " + delegate.getId()
					+ "(" + delegate.getClass() + ")");
			if (delegate instanceof OabaJobEntity) {
				oabaResultsManager.exportResults(delegate, container);
			} else if (delegate instanceof TransitivityJobEntity) {
				transitivityResultsManager.exportResults(delegate, container);
			} else {
				String msg = "ExportResults: unexpected delegate type: "
						+ delegate.getClass();
				logger.warning(msg);
				// Keep processing...
			}
		}
	}

}
