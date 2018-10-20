/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.ejb;

import static com.choicemaker.cm.args.OperationalPropertyNames.PN_TRANSITIVITY_CACHED_GROUPS_FILE;
import static com.choicemaker.cm.args.OperationalPropertyNames.PN_TRANSITIVITY_CACHED_PAIRS_FILE;
import static com.choicemaker.cm.args.ProcessingEventBean.DONE;
import static com.choicemaker.cm.batch.api.BatchJobStatus.ABORT_REQUESTED;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.choicemaker.client.api.IGraphProperty;
import com.choicemaker.cm.args.AnalysisResultFormat;
import com.choicemaker.cm.args.ProcessingEvent;
import com.choicemaker.cm.args.TransitivityParameters;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchJobStatus;
import com.choicemaker.cm.batch.api.EventPersistenceManager;
import com.choicemaker.cm.batch.api.OperationalPropertyController;
import com.choicemaker.cm.batch.api.ProcessingEventLog;
import com.choicemaker.cm.oaba.api.OabaSettingsController;
import com.choicemaker.cm.oaba.api.ServerConfigurationController;
import com.choicemaker.cm.oaba.ejb.data.OabaJobMessage;
import com.choicemaker.cm.oaba.impl.MatchRecord2CompositeSource;
import com.choicemaker.cm.transitivity.api.TransitivityJobManager;
import com.choicemaker.cm.transitivity.api.TransitivityParametersController;
import com.choicemaker.cm.transitivity.core.TransitivityResult;
import com.choicemaker.cm.transitivity.core.TransitivityResultCompositeSerializer;
import com.choicemaker.cm.transitivity.core.TransitivitySortType;
import com.choicemaker.cm.transitivity.ejb.util.ClusteringIteratorFactory;
import com.choicemaker.cm.transitivity.util.CompositeEntityIterator;
import com.choicemaker.cm.transitivity.util.CompositeEntitySource;
import com.choicemaker.cm.transitivity.util.CompositeTextSerializer;
import com.choicemaker.cm.transitivity.util.CompositeXMLSerializer;

/**
 */
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "maxSession",
				propertyValue = "1"), // Singleton (JBoss only)
		@ActivationConfigProperty(propertyName = "destinationLookup",
				propertyValue = "java:/choicemaker/urm/jms/transSerializationQueue"),
		@ActivationConfigProperty(propertyName = "destinationType",
				propertyValue = "javax.jms.Queue") })
@SuppressWarnings({
		"rawtypes" })
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class TransSerializerMDB implements MessageListener, Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger log =
		Logger.getLogger(TransSerializerMDB.class.getName());

	private static final Logger jmsTrace =
		Logger.getLogger("jmstrace." + TransSerializerMDB.class.getName());

	public static final int DEFAULT_MAX_RECORD_COUNT = 100000000;

	private static Map<AnalysisResultFormat, TransitivityResultCompositeSerializer> formatSerializer =
		new HashMap<>();
	static {
		formatSerializer.put(AnalysisResultFormat.SORT_BY_HOLD_GROUP,
				new CompositeTextSerializer(
						TransitivitySortType.SORT_BY_HOLD_MERGE_ID));
		formatSerializer.put(AnalysisResultFormat.SORT_BY_RECORD_ID,
				new CompositeTextSerializer(TransitivitySortType.SORT_BY_ID));
		formatSerializer.put(AnalysisResultFormat.XML,
				new CompositeXMLSerializer());
	}

	public static TransitivityResultCompositeSerializer getTransitivityResultSerializer(
			AnalysisResultFormat format) /* throws ConfigException */ {
		TransitivityResultCompositeSerializer retVal =
			formatSerializer.get(format);
		if (retVal == null) {
			String msg = "No serializer for analysis format '" + format + "'";
			throw new IllegalStateException(msg);
		}
		return retVal;
	}

	@PersistenceContext(unitName = "oaba")
	private EntityManager em;

	@EJB
	private TransitivityJobManager jobManager;

	@EJB
	private OabaSettingsController oabaSettingsController;

	@EJB
	private TransitivityParametersController paramsController;

	@EJB
	private EventPersistenceManager eventManager;

	@EJB
	private ServerConfigurationController serverController;

	@EJB
	private OperationalPropertyController propController;

	@Override
	public void onMessage(Message inMessage) {
		jmsTrace.info("Entering onMessage for " + this.getClass().getName());
		ObjectMessage msg = null;

		log.fine("MatchDedupMDB In onMessage");

		BatchJob batchJob = null;
		try {
			if (inMessage instanceof ObjectMessage) {
				msg = (ObjectMessage) inMessage;
				Object o = msg.getObject();

				if (o instanceof OabaJobMessage) {
					OabaJobMessage data = (OabaJobMessage) o;
					long jobId = data.jobID;
					batchJob = jobManager.findTransitivityJob(jobId);
					processOabaMessage(batchJob);
				} else {
					log.warning(
							"wrong message body: " + o.getClass().getName());
				}

			} else {
				log.warning("wrong type: " + inMessage.getClass().getName());
			}

		} catch (Exception e) {
			String msg0 = throwableToString(e);
			log.severe(msg0);
			if (batchJob != null) {
				batchJob.markAsFailed();
				jobManager.save(batchJob);
			}
		}
		jmsTrace.info("Exiting onMessage for " + this.getClass().getName());
	}

	protected String throwableToString(Throwable throwable) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.println(throwable.toString());
		throwable.printStackTrace(pw);
		pw.close();
		return sw.toString();
	}

	protected void processOabaMessage(BatchJob batchJob) {
		assert batchJob != null;

		final long jobId = batchJob.getId();
		final TransitivityParameters params =
			this.paramsController.findTransitivityParametersByBatchJobId(jobId);
		final ProcessingEventLog processingEntry =
			eventManager.getProcessingLog(batchJob);
		final IGraphProperty graph = params.getGraphProperty();
		final AnalysisResultFormat format = params.getAnalysisResultFormat();
		final String modelConfigId = params.getModelConfigurationName();

		try {
			if (log.isLoggable(Level.FINE)) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				pw.println("Transitivity serialization jobId: " + jobId);
				pw.println("Trans serialization groupProperty: '"
						+ graph.getName() + "'");
				pw.println(
						"Trans serialization resultFormat: '" + format + "'");
				String s = sw.toString();
				log.fine(s);
			}

			final BatchJobStatus jobStatus = batchJob.getStatus();
			if (jobStatus == ABORT_REQUESTED) {
				batchJob.markAsAborted();
				jobManager.save(batchJob);
				log.fine("Transitivity serialization job marked as aborted: "
						+ batchJob);
				return;
			}

			final String cachedPairsFileName = propController.getJobProperty(
					batchJob, PN_TRANSITIVITY_CACHED_PAIRS_FILE);
			if (cachedPairsFileName == null
					|| cachedPairsFileName.trim().isEmpty()) {
				String msg = "Missing pair-wise result file (property name '"
						+ PN_TRANSITIVITY_CACHED_PAIRS_FILE + "'";
				log.severe(msg);
				throw new IllegalStateException(msg);
			}
			log.info("Cached transitivity pairs file: " + cachedPairsFileName);

			String analysisResultFileName =
				TransitivityFileUtils.getGroupResultFileName(batchJob);

			MatchRecord2CompositeSource mrs =
				new MatchRecord2CompositeSource(cachedPairsFileName);

			// TODO: replace by extension point
			CompositeEntitySource ces = new CompositeEntitySource(mrs);
			CompositeEntityIterator ceIter = new CompositeEntityIterator(ces);
			String name = graph.getName();
			ClusteringIteratorFactory f =
				ClusteringIteratorFactory.getInstance();
			Iterator clusteringIterator;
			try {
				clusteringIterator = f.createClusteringIterator(name, ceIter);
			} catch (Exception x) {
				log.severe("Unable to create clustering iterator: " + x);
				batchJob.markAsFailed();
				jobManager.save(batchJob);
				return;
			}

			TransitivityResult tr =
				new TransitivityResult(modelConfigId, params.getLowThreshold(),
						params.getHighThreshold(), clusteringIterator);

			log.fine("serialize to " + format + "format");

			TransitivityResultCompositeSerializer sr =
				getTransitivityResultSerializer(format);
			sr.serialize(tr, analysisResultFileName, DEFAULT_MAX_RECORD_COUNT);
			String resultFile = sr.getCurrentFileName();
			propController.setJobProperty(batchJob,
					PN_TRANSITIVITY_CACHED_GROUPS_FILE, resultFile);

			// mark as done
			batchJob.markAsCompleted();
			jobManager.save(batchJob);
			final Date now = new Date();
			final String info = null;
			sendToUpdateStatus(batchJob, DONE, now, info);
			processingEntry.setCurrentProcessingEvent(DONE);

		} catch (Exception e) {
			log.severe(e.toString());
			if (batchJob != null) {
				batchJob.markAsFailed();
				jobManager.save(batchJob);
			}
		}
		jmsTrace.info("Exiting onMessage for " + this.getClass().getName());
	}

	protected void sendToUpdateStatus(BatchJob job, ProcessingEvent event,
			Date timestamp, String info) {
		eventManager.updateStatusWithNotification(job, event, timestamp, info);
	}

}
