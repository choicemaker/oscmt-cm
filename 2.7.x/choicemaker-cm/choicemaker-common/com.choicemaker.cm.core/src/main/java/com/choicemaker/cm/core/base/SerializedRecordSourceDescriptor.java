/*
 * Copyright (c) 2001, 2022 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.core.base;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import com.choicemaker.cm.core.ISerializableFileBasedRecordSource;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.IncompleteSpecificationException;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.RecordSource;
import com.choicemaker.cm.core.Sink;
import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.core.xmlconf.RecordSourceXmlConf;
import com.choicemaker.util.Equal;
import com.choicemaker.util.HashUtils;
import com.choicemaker.util.Precondition;
import com.choicemaker.util.StringUtils;

/**
 * This is a wrapper object around a Record Source Descriptor. It can be serialized, because it
 * stores string values with which to create a RecordSource.
 * @author pcheung
 * @author rphall (Renamed from FlatFileSerialRecordSource)
 *
 */
public class SerializedRecordSourceDescriptor implements ISerializableFileBasedRecordSource {

	private static final long serialVersionUID = 1L;

	private static final Logger log =
		Logger.getLogger(SerializedRecordSourceDescriptor.class.getName());

	private String descriptorFileName;
	private String modelName;

	private transient RecordSource rs;
	private transient ImmutableProbabilityModel model;

	public SerializedRecordSourceDescriptor(String rsFile, String modelName) {
		setModelName(modelName);
		setRsFile(rsFile);
	}

	private RecordSource getRS() {
		if (rs == null) {
			try {
				rs = RecordSourceXmlConf.getRecordSource(getRsFile());
				rs.setModel(getModel());
			} catch (XmlConfException e) {
				log.severe(e.toString());
			}
		}
		return rs;
	}

	@Override
	public ImmutableProbabilityModel getModel() {
		if (model == null) {
			model = PMManager.getModelInstance(getModelName());
		}
		return model;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.base.RecordSource#getNext()
	 */
	@Override
	public Record getNext() throws IOException {
		return getRS().getNext();
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.base.Source#open()
	 */
	@Override
	public void open() throws IOException {
		getRS().open();
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.base.Source#close()
	 */
	@Override
	public void close() throws IOException {
		getRS().close();
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.base.Source#hasNext()
	 */
	@Override
	public boolean hasNext() throws IOException {
		return getRS().hasNext();
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.base.Source#getName()
	 */
	@Override
	public String getName() {
		return getRS().getName();
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.base.Source#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		getRS().setName(name);
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.base.Source#setModel(com.choicemaker.cm.core.base.ProbabilityModel)
	 */
	@Override
	public void setModel(ImmutableProbabilityModel m) {
		getRS().setModel(m);
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.base.Source#hasSink()
	 */
	@Override
	public boolean hasSink() {
		return getRS().hasSink();
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.base.Source#getSink()
	 */
	@Override
	public Sink getSink() {
		return getRS().getSink();
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.base.Source#getFileName()
	 */
	@Override
	public String getFileName() {
		return getRS().getFileName();
	}

	@Override
	public boolean equals(Object o) {
		boolean retVal = false;
		if (o instanceof SerializedRecordSourceDescriptor) {
			SerializedRecordSourceDescriptor rs = (SerializedRecordSourceDescriptor) o;
			retVal = Equal.and(retVal, this.getModelName(), rs.getModelName());
			if (retVal) {
				if (this.getRsFile() == null || rs.getRsFile() == null) {
					retVal = false;
				} else {
					try {
						File thisFile = new File(this.getRsFile()).getAbsoluteFile();
						File thatFile = new File(rs.getRsFile()).getAbsoluteFile();
						retVal = Equal.and(retVal, thisFile, thatFile);
					} catch (Exception x) {
						log.warning(
							"Unable to get absolute files for equality test: "
							+ x.toString());
						retVal = Equal.and(retVal, this.getRsFile(), rs.getRsFile());
					}
				}
			}
		}
		return retVal;
	}

	@Override
	public int hashCode() {
		int retVal = HashUtils.hashCode(this.getModelName());
		retVal = HashUtils.hashCode(retVal, this.getRsFile());
		return retVal;
	}

	@Override
	public Properties getProperties() {
		Properties retVal = new Properties();
		retVal.setProperty(PN_DESCRIPTOR_FILE_NAME, this.getRsFile());
		retVal.setProperty(PN_MODEL_NAME, this.getModelName());
		return retVal;
	}

	@Override
	public void  setProperties(Properties properties) throws IncompleteSpecificationException {

		Precondition.assertNonNullArgument("null properties",properties);

		String s = properties.getProperty(PN_DESCRIPTOR_FILE_NAME);
		if (!StringUtils.nonEmptyString(s)) {
			String msg = "Missing property '" + PN_DESCRIPTOR_FILE_NAME + "'";
			log.severe(msg);
			throw new IncompleteSpecificationException(msg);
		}
		setRsFile(s);

		s = properties.getProperty(PN_MODEL_NAME);
		if (!StringUtils.nonEmptyString(s)) {
			String msg = "Missing property '" + PN_MODEL_NAME + "'";
			log.severe(msg);
			throw new IncompleteSpecificationException(msg);
		}
		setModelName(s);

		try {
			rs.setModel(null);
			if (rs != null) {
				rs.close();
			}
		} catch (Exception x) {
			String msg = "Unable to close " + (rs == null ? "record source" : rs.getName());
			log.warning(msg);
		} finally {
			rs = null;
		}
	}

	@Override
	public String toXML() {
		String retVal = AbstractRecordSourceSerializer.toXML(this);
		return retVal;
	}

	private void setModelName(String s) {
		Precondition.assertNonEmptyString(s);
		this.modelName = s;
	}

	public String getModelName() {
		return this.modelName;
	}

	private void setRsFile(String s) {
		Precondition.assertNonEmptyString(s);
		this.descriptorFileName = s;
	}

	public String getRsFile() {
		return this.descriptorFileName;
	}

}
