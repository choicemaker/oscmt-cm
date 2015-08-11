/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.ejb;

import static com.choicemaker.cm.args.OperationalPropertyNames.PN_OABA_CACHED_RESULTS_FILE;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.sql.DataSource;

import com.choicemaker.cm.batch.BatchJob;
import com.choicemaker.cm.batch.BatchJobStatus;
import com.choicemaker.cm.batch.OperationalPropertyController;
import com.choicemaker.cm.io.blocking.automated.offline.core.IMatchRecord2Source;
import com.choicemaker.cm.io.blocking.automated.offline.impl.MatchRecord2CompositeSource;
import com.choicemaker.cm.io.blocking.automated.offline.server.ejb.OabaService;
import com.choicemaker.cm.urm.base.DbRecordCollection;
import com.choicemaker.cm.urm.base.IRecordCollection;
import com.choicemaker.cm.urm.base.JobStatus;
import com.choicemaker.cm.urm.base.RefRecordCollection;
import com.choicemaker.cm.urm.base.TextRefRecordCollection;
import com.choicemaker.cm.urm.exceptions.ArgumentException;
import com.choicemaker.cm.urm.exceptions.CmRuntimeException;
import com.choicemaker.cm.urm.exceptions.ConfigException;
import com.choicemaker.cm.urm.exceptions.ModelException;
import com.choicemaker.cm.urm.exceptions.RecordCollectionException;


/**
 * BatchRecordMatcherBean is an implementation of BatchRecordMatcher interface
 * 
 * @author emoussikaev
 * @version Revision: 2.5  Date: Jul 15, 2005 3:41:42 PM
 * @see
 */
@SuppressWarnings({"rawtypes"})
public class BatchRecordMatcherBean extends BatchMatchBaseBean {

	private static final long serialVersionUID = 1L;

	static {
		log = Logger.getLogger(BatchRecordMatcherBean.class.getName());
	}
	
//	@PersistenceContext (unitName = "oaba")
	private EntityManager em;

//	@EJB
	private OabaService batchQuery;

//	@EJB
	private OperationalPropertyController propController;

	public BatchRecordMatcherBean() {
		super();
	}

	public long 	startMatching(
									IRecordCollection qRs, 
									RefRecordCollection mRs,
									String modelName, 
									float differThreshold, 
									float matchThreshold,
									int	  maxSingle,
									String externalId) 
								throws
										ModelException, 	
										RecordCollectionException,
										ConfigException,
										ArgumentException,
										CmRuntimeException, 
										RemoteException
	{
		log.fine("<< startMatching...");
//		TODO: check input parameters
		throw new Error("not yet implemented");
//		long id = startBatchQueryService(
//					qRs,
//					mRs,
//					modelName,
//					differThreshold,
//					matchThreshold,
//					maxSingle,
//					externalId,
//					null);	
//		log.fine (">> startMatching");
//		return id;
	}
	
	public JobStatus getJobStatus(long jobID) throws ArgumentException,
			ConfigException, CmRuntimeException, RemoteException {

		BatchJob batchJob = batchQuery.getOabaJob(jobID);
		JobStatus js =
			new JobStatus(batchJob.getId(), batchJob.getStatus().name(),
					batchJob.getTimeStamp(batchJob.getStatus()));
		js.setAbortRequestDate(null);
		js.setErrorDescription("");// TODO
		js.setStepId(-1);// TODO
		js.setFractionComplete(10);// TODO
		js.setStepDescription("");
		js.setStepStartDate(null);// TODO
		js.setTrackingId("");

		return js;
	}

	public void copyResult(long jobID, RefRecordCollection resRc)
			throws ModelException, RecordCollectionException, ConfigException,
			ArgumentException, CmRuntimeException, RemoteException {
		try {
			BatchJob job = batchQuery.getOabaJob(jobID);
			if (!job.getStatus().equals(BatchJobStatus.COMPLETED)) {
				throw new ArgumentException("The job has not completed.");
			} else {
				final String cachedResultsFileName =
					propController.getJobProperty(job,
							PN_OABA_CACHED_RESULTS_FILE);
				log.info("Cached OABA results file: " + cachedResultsFileName);

				int extBegin = cachedResultsFileName.lastIndexOf(".");
				String fileName = cachedResultsFileName.substring(0, extBegin);
				String ext = cachedResultsFileName.substring(extBegin + 1);

				if (resRc instanceof DbRecordCollection) {
					String urlString = resRc.getUrl();
					DbRecordCollection dbRc = (DbRecordCollection) resRc;
					Context ctx = new InitialContext();
					DataSource ds = (DataSource) ctx.lookup(urlString);

					IMatchRecord2Source mr2s =
						new MatchRecord2CompositeSource(fileName, ext);

					MatchDBWriter dbw =
						new MatchDBWriter(mr2s, ds, dbRc.getName(), jobID);
					dbw.writeToDB();
				} else if (resRc instanceof TextRefRecordCollection) {

					// FIXME HACK use System file separator instead
					String dirName;
					int slashInd = fileName.lastIndexOf("\\");
					if (slashInd == -1)
						slashInd = fileName.lastIndexOf("/");
					if (slashInd == -1) {
						dirName = ".";
					} else {
						dirName = fileName.substring(0, slashInd + 1);
						fileName = fileName.substring(slashInd + 1);
					}
					log.fine("(" + dirName + ")(" + fileName + ")(" + ext + ")");
					// END FIXME HACK

					copyResultFromFile(dirName, fileName, ext,
							(TextRefRecordCollection) resRc);
				} else
					throw new RecordCollectionException(
							"unknown record collection class");
			}

		} catch (NamingException | IOException e) {
			log.severe(e.toString());
			throw new ConfigException(e.toString());
		}
	}
	
	public boolean abortJob(long jobId) {
		return abortBatchJob(jobId);
	}

	public boolean suspendJob(long jobId) {
		return batchQuery.suspendJob(jobId) == 0;
	}

	public boolean resumeJob(long jobId) throws ArgumentException,
			ConfigException, CmRuntimeException, RemoteException {
		// try {
		// TODO talk with Put regarding modelname parameter
		return (batchQuery.resumeJob(jobId) == 1);
		// } catch (NamingException e) {
		// log.severe(e.toString());
		// throw new ConfigException(e.toString());
		// } catch (CreateException e) {
		// log.severe(e.toString());
		// throw new ConfigException(e.toString());
		// } catch (JMSException e) {
		// log.severe(e.toString());
		// throw new ConfigException(e.toString());
		// } catch (FinderException e) {
		// log.severe(e.toString());
		// throw new CmRuntimeException(e.toString());
		// }
	}

	public long[] 		getJobList()
						throws	ArgumentException,
								ConfigException,
								CmRuntimeException, 
								RemoteException

	{
		long[] res;
		log.fine("<<getJobList");
		Collection jobColl = Single.getInst().getBatchJobList(em);
		res = new long[jobColl.size()];
		Iterator jobIter = jobColl.iterator();
		int ind = 0;
		BatchJob oabaJob;
		while (jobIter.hasNext()) {
			oabaJob = (BatchJob) jobIter.next();
			res[ind++] = oabaJob.getId(); 
		}
		log.fine(">>getJobList");
		return res;
	}

	/**
	 * Cleans serialized data and database entries related to the process with
	 * the give job ID (including the file with matching results)
	 * 
	 * @param jobID
	 *            Job ID.
	 * @return true if the serialized data was removed successfully (the job
	 * itself is removed regardless)
	 * @throws RemoteException
	 */
	public boolean cleanJob(long jobID) throws CmRuntimeException {
		try {
			// FIXME
			// boolean ret = batchQuery.removeDir(jobID);
			// BatchJob bj = Single.getInst().findBatchJobById(em,
			// OabaJobEntity.class, jobID);
			// Single.getInst().deleteBatchJob(em, bj);
			// return ret;
			return false;
		} catch (Exception e) {
			log.severe(e.toString());
			throw new CmRuntimeException(e.toString());
		}
	}

	public Iterator	getResultIter(RefRecordCollection rc)
					throws
						RecordCollectionException,
						ArgumentException,
						CmRuntimeException, 
						RemoteException{
							
		return null; //TODO:implement					
	}
	
	public Iterator	getResultIter(long jobId)
					throws
						RecordCollectionException,
						ArgumentException,
						CmRuntimeException, 
						RemoteException {
		return null;//TODO:implement					
	}
						
	public String getVersion(Object context)
						throws  RemoteException {
		return Single.getInst().getVersion();					
	}											
						
}



