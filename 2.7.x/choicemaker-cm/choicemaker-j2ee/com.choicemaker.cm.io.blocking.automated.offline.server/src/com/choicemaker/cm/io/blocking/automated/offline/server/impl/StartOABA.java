/*
 * Copyright (c) 2001, 2009 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.io.blocking.automated.offline.server.impl;

import java.io.IOException;
import java.rmi.RemoteException;

import javax.ejb.EJBException;
import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.core.IProbabilityModel;
import com.choicemaker.cm.core.PMManager;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.RecordSink;
import com.choicemaker.cm.core.RecordSource;
import com.choicemaker.cm.core.xmlconf.RecordSourceXmlConf;
import com.choicemaker.cm.io.blocking.automated.offline.core.IStatus;
import com.choicemaker.cm.io.blocking.automated.offline.impl.RecordIDTranslator2;
import com.choicemaker.cm.io.blocking.automated.offline.impl.ValidatorBase;
import com.choicemaker.cm.io.blocking.automated.offline.server.data.EJBConfiguration;
import com.choicemaker.cm.io.blocking.automated.offline.server.data.OABAConfiguration;
import com.choicemaker.cm.io.blocking.automated.offline.server.data.StartData;
import com.choicemaker.cm.io.blocking.automated.offline.server.data.UpdateData;
import com.choicemaker.cm.io.blocking.automated.offline.server.ejb.BatchJob;
import com.choicemaker.cm.io.blocking.automated.offline.services.RecValService3;
import com.choicemaker.cm.io.flatfile.base.FlatFileRecordSource;
import com.choicemaker.cm.io.xml.base.XmlRecordSink;

/**
 * This message bean is the first step of the OABA.  It creates rec_id, val_id files using
 * internal id translation.
 * 
 * @author pcheung
 *
 */
public class StartOABA implements MessageDrivenBean, MessageListener {

	private static final Logger log = Logger.getLogger(StartOABA.class);
	private static final Logger jmsTrace = Logger.getLogger("jmstrace." + StartOABA.class.getName());

	private transient MessageDrivenContext mdc = null;
	private transient EJBConfiguration configuration = null;

	public void ejbCreate() {
//	log.debug("starting ejbCreate...");
		try {
			this.configuration = EJBConfiguration.getInstance();
		} catch (Exception e) {
	  		log.error(e.toString(),e);
		}
//	log.debug("...finished ejbCreate");
	}


	/* (non-Javadoc)
	 * @see javax.ejb.MessageDrivenBean#ejbRemove()
	 */
	public void ejbRemove() throws EJBException {
//		log.debug("ejbRemove()");
	}

	/* (non-Javadoc)
	 * @see javax.ejb.MessageDrivenBean#setMessageDrivenContext(javax.ejb.MessageDrivenContext)
	 */
	public void setMessageDrivenContext(MessageDrivenContext mdc) throws EJBException {
//			log.debug("setMessageDrivenContext()");
			this.mdc = mdc;
	}

	/* (non-Javadoc)
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
	public void onMessage(Message inMessage) {
		jmsTrace.info("Entering onMessage for " + this.getClass().getName());
		ObjectMessage msg = null;
		StartData data = null;
		BatchJob batchJob = null;
		
		log.info("StartOABA In onMessage");

		try {

			if (inMessage instanceof ObjectMessage) {
				msg = (ObjectMessage) inMessage;
				data = (StartData) msg.getObject();
				
				batchJob = configuration.findBatchJobById(data.jobID);
				//update status to mark as start
				batchJob.markAsStarted();
				
				IProbabilityModel stageModel = PMManager.getModelInstance(data.stageModelName);
				IProbabilityModel masterModel = PMManager.getModelInstance(data.masterModelName);
				OABAConfiguration oabaConfig = new OABAConfiguration (data.stageModelName, data.jobID);
				oabaConfig.saveStartData(data);
				
				//debug - extract data to flatfile
				//saveToFiles (data);
				//sendToUpdateStatus (data.jobID, 100);
				//if (true) return;


				//get the status
				IStatus status = configuration.getStatusLog(data);
				
				log.info(data.jobID + " " + data.stageModelName + " " + data.masterModelName + " " + data.low + 
					" " + data.high  + " " + data.runTransitivity);
				log.info(data.staging + " " + data.master);

				//check to see if there are a lot of records in stage.
				//if not use single record matching instead of batch.
				if (!isMoreThanThreshold(data.staging, stageModel, data.maxCountSingle)) {
					log.info("Using single record matching");
					sendToSingleRecordMatching (data);

				} else {
					log.info("Using batch record matching");
					 
					RecordIDTranslator2 translator = new RecordIDTranslator2 (oabaConfig.getTransIDFactory()); 

					//create rec_id, val_id files
					RecValService3 rvService = new RecValService3 (data.staging, data.master, 
						stageModel, masterModel, 
						oabaConfig.getRecValFactory(), translator, status, batchJob);
					rvService.runService();
					
					data.stageType = rvService.getStageType();
					data.masterType = rvService.getMasterType();
				
					data.numBlockFields = rvService.getNumBlockingFields();
								
					log.info("Done creating rec_id, val_id files: " + rvService.getTimeElapsed());
					
					//create the validator after rvService 
					//Validator validator = new Validator (true, translator);
					ValidatorBase validator = new ValidatorBase (true, translator);
					data.validator = validator;
				
					//save the data
					oabaConfig.saveStartData(data);

					sendToUpdateStatus (data.jobID, 10);
					sendToBlocking (data);
				}

			} else {
				log.warn("wrong type: " + inMessage.getClass().getName());
			}

		} catch (JMSException e) {
			log.error(e.toString(),e);
			mdc.setRollbackOnly();
		} catch (BlockingException e) {
			log.error(e);
			try {
				if (batchJob != null) batchJob.markAsFailed();
			} catch (RemoteException e1) {
				log.error(e1.toString(),e1);
			}
		} catch (Exception e) {
			log.error(e.toString(),e);
			e.printStackTrace();
		}
		jmsTrace.info("Exiting onMessage for " + this.getClass().getName());
	}
	
	
	/** This is a debug method that writes the data to output xml and text files.
	 * 
	 * @param data
	 */
	private void saveToFiles (StartData data) {
		try {
			RecordSource rs = data.staging;
			IProbabilityModel stageModel = PMManager.getModelInstance(data.stageModelName);
		
			XmlRecordSink xSink = new XmlRecordSink ("xmlstage","stage.xml",stageModel);
			xSink.open();
			
			FlatFileRecordSource tmpRS = new FlatFileRecordSource("stage.rs", "stage", 
			".txt", false, false, false, '|', true, stageModel);
			RecordSourceXmlConf.add(tmpRS);
			RecordSink fSink  = (RecordSink)tmpRS.getSink();
			fSink.open();
			
			rs.open();
			while (rs.hasNext()) {
				Record r= rs.getNext();
				xSink.put(r);
				fSink.put(r);
			}
			
			xSink.close();
			fSink.close();
			
			// master
			rs = data.master;
			if (rs == null) return;
			
			IProbabilityModel masterModel = PMManager.getModelInstance(data.masterModelName);
		
			xSink = new XmlRecordSink ("xmlmaster","master.xml",masterModel);
			xSink.open();
			
			tmpRS = new FlatFileRecordSource("master.rs", "master", 
			".txt", false, false, false, '|', true, masterModel);
			RecordSourceXmlConf.add(tmpRS);
			fSink  = (RecordSink)tmpRS.getSink();
			fSink.open();
			
			rs.open();
			while (rs.hasNext()) {
				Record r= rs.getNext();
				xSink.put(r);
				fSink.put(r);
			}
			
			xSink.close();
			fSink.close();
			
		} catch (Exception ex) {
			log.error(ex.toString());
		}
		
	}
	


	
	/** This method checks to see if the number of records in the RecordSource is greater than the
	 * threshold.
	 * 
	 * @param rs - RecordSource
	 * @param accessProvider - Probability Model of this RecordSource
	 * @param threshold - The number of records threshold
	 * @return boolean - true if the RecordSource contains more than the threshold
	 * @throws OABABlockingException
	 */
	private boolean isMoreThanThreshold (RecordSource rs, IProbabilityModel model, int threshold) 
		throws BlockingException {
		
		if (threshold == 0) return true;
		
		boolean ret = false;
		
		log.info("Checking if moreThanThreshold " + threshold);

		try {
			rs.setModel(model);
			rs.open();
			int count = 1;
			
			while (count <= threshold && rs.hasNext()) {
				Record r = rs.getNext();
				count ++;
			}
			
			if (rs.hasNext()) ret = true;
			
			rs.close();
			
		} catch (IOException ex) {
			throw new BlockingException (ex.toString(),ex);
		}
		
		return ret;
	}
	
	

	/** This method sends a message to the UpdateStatus message bean.
	 * 
	 * @param jobID
	 * @param percentComplete
	 * @throws NamingException
	 */
	private void sendToUpdateStatus (long jobID, int percentComplete) throws NamingException, JMSException {
		Queue queue = configuration.getUpdateMessageQueue();

		UpdateData data = new UpdateData();
		data.jobID = jobID;
		data.percentComplete = percentComplete;

		configuration.sendMessage(queue, data);
	} 


	/** This method sends a message to the BlockingOABA message bean.
	 * 
	 * @param data
	 * @throws NamingException
	 */
	private void sendToBlocking (StartData data) throws NamingException, JMSException{
		Queue queue = configuration.getBlockingMessageQueue();
		configuration.sendMessage(queue, data);
	} 
	
	
	/** This method sends a message to the BlockingOABA message bean.
	 * 
	 * @param data
	 * @throws NamingException
	 */
	private void sendToSingleRecordMatching (StartData data) throws NamingException, JMSException{
		Queue queue = configuration.getSingleMatchMessageQueue();
		configuration.sendMessage(queue, data);
	} 
	
	


}