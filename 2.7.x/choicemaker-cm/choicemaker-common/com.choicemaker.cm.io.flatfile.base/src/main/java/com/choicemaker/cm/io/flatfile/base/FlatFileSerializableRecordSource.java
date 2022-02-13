/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.flatfile.base;

import java.io.IOException;
import java.util.Properties;

import com.choicemaker.cm.core.ISerializableRecordSource;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.RecordSource;
import com.choicemaker.cm.core.Sink;
import com.choicemaker.cm.core.base.PMManager;

/**
 * This is a wrapper object around FlatFileRecordSource and it can be serialized, because it
 * stores string values with which to create the FlatFileRecordSource.
 * @author pcheung
 * @author rphall (renamed from FlatSerialRecordSource)
 */
public class FlatFileSerializableRecordSource implements ISerializableRecordSource {

	private static final long serialVersionUID = 1L;


	private String delimitedTextDataFile;
	private String modelName;
	private boolean fixedLength;
	private char separator;
	private boolean tagged;
	private boolean singleLine;
	
	private transient FlatFileRecordSource rs;
	private transient ImmutableProbabilityModel model;
	
	
	public FlatFileSerializableRecordSource (String delimitedTextDataFile,boolean fixedLength, char separator, boolean tagged, boolean singleLine, String modelName) {
		this.modelName = modelName;
		this.delimitedTextDataFile = delimitedTextDataFile;
		this.fixedLength = fixedLength;
		this.separator = separator;
		this.tagged = tagged;
		this.singleLine = singleLine;
	}
	
	
	private RecordSource getRS () {
		if (rs == null) {
			rs = new FlatFileRecordSource (delimitedTextDataFile, delimitedTextDataFile,"",false,this.singleLine,this.fixedLength,this.separator, this.tagged, getModel ());
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
	public String toXML() {
		// TODO NOT YET IMPLEMENTED
		throw new RuntimeException("not yet implemented");
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
