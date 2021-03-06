/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.xml.base;

import java.io.IOException;
import java.util.Properties;

import com.choicemaker.cm.core.ISerializableRecordSource;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.RecordSource;
import com.choicemaker.cm.core.Sink;
import com.choicemaker.cm.core.base.AbstractRecordSourceSerializer;
import com.choicemaker.cm.core.base.PMManager;

/**
 * This is a wrapper object around XMLRecordSource and it can be serialized, because it
 * stores string values with which to create the DbRecordSource.
 * 
 * @author pcheung
 *
 */
public class XMLSerializableRecordSource implements ISerializableRecordSource {
	
	private static final long serialVersionUID = 1L;


	private String xmlFile;
	private String modelName;
	
	private transient XmlRecordSource rs;
	private transient ImmutableProbabilityModel model;
	
	
	public XMLSerializableRecordSource (String xmlFile, String modelName) {
		this.modelName = modelName;
		this.xmlFile = xmlFile;
	}
	
	
	private RecordSource getRS () {
		if (rs == null) {
			rs = new XmlRecordSource (xmlFile, xmlFile, getModel ());
		}
		return rs;
	}

	@Override
	public ImmutableProbabilityModel getModel () {
		if (model == null) {
			model = PMManager.getModelInstance(modelName);
		}
		return model;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.RecordSource#getNext()
	 */
	@Override
	public Record getNext() throws IOException {
		return getRS().getNext();
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.Source#open()
	 */
	@Override
	public void open() throws IOException {
		getRS().open ();
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.Source#close()
	 */
	@Override
	public void close() throws IOException {
		getRS().close();
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.Source#hasNext()
	 */
	@Override
	public boolean hasNext() throws IOException {
		return getRS().hasNext();
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.Source#getName()
	 */
	@Override
	public String getName() {
		return getRS().getName();
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.Source#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		getRS().setName(name);
	}


	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.Source#setModel(com.choicemaker.cm.core.ImmutableProbabilityModel)
	 */
	@Override
	public void setModel(ImmutableProbabilityModel m) {
		getRS().setModel(m);
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.Source#hasSink()
	 */
	@Override
	public boolean hasSink() {
		return getRS().hasSink();
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.Source#getSink()
	 */
	@Override
	public Sink getSink() {
		return getRS().getSink();
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.Source#getFileName()
	 */
	@Override
	public String getFileName() {
		return getRS().getFileName();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((modelName == null) ? 0 : modelName.hashCode());
		result = prime * result + ((xmlFile == null) ? 0 : xmlFile.hashCode());
		return result;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		XMLSerializableRecordSource other = (XMLSerializableRecordSource) obj;
		if (modelName == null) {
			if (other.modelName != null)
 			return false;
		} else if (!modelName.equals(other.modelName))
			return false;
		if (xmlFile == null) {
			if (other.xmlFile != null)
				return false;
		} else if (!xmlFile.equals(other.xmlFile))
			return false;
		return true;
	}

	public boolean equals_00 (Object o) {
		if (o instanceof XMLSerializableRecordSource) {
			XMLSerializableRecordSource rs = (XMLSerializableRecordSource) o;
			return rs.xmlFile.equals(this.xmlFile) && 
				rs.modelName.equals(this.modelName);
		} else {
			return false;
		}
	}

	@Override
	public String toXML() {
		String retVal = AbstractRecordSourceSerializer.toXML(this);
		return retVal;
	}
	
	@Override
	public void setProperties(Properties p) {
		// TODO NOT YET IMPLEMENTED
		throw new RuntimeException("not yet implemented");
	}
	
	@Override
	public Properties getProperties() {
		// TODO NOT YET IMPLEMENTED
		throw new RuntimeException("not yet implemented");
	}

}
