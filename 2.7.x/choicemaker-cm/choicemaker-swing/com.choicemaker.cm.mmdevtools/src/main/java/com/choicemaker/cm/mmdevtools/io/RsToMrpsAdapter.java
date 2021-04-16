/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.mmdevtools.io;

import java.io.IOException;
import java.util.Date;

import com.choicemaker.client.api.Decision;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.ImmutableRecordPair;
import com.choicemaker.cm.core.MarkedRecordPairSource;
import com.choicemaker.cm.core.MutableMarkedRecordPair;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.RecordSource;
import com.choicemaker.cm.core.Sink;

/**
 * Comment
 *
 * @author   Adam Winkel
 */
public class RsToMrpsAdapter implements MarkedRecordPairSource {

	protected RecordSource rs;
	protected Decision decision;
	protected Date date;
	protected String user;
	protected String src;
	protected String comment;

	public RsToMrpsAdapter(RecordSource rs) {
		this(rs, Decision.MATCH, new Date(), "", "", "");
	}

	public RsToMrpsAdapter(RecordSource rs,
						   Decision decision,
						   Date date,
						   String user,
						   String src,
						   String comment) {
		this.rs = rs;
		this.decision = decision;
		this.date = date;
		this.user = user;
		this.src = src;
		this.comment = comment;
	}

	public RecordSource getRecordSource() {
		return rs;
	}

	@Override
	public MutableMarkedRecordPair getNextMarkedRecordPair() throws IOException {
		Record r = rs.getNext();
		return new MutableMarkedRecordPair(r, r, decision, date, user, src, comment);
	}

	@Override
	public ImmutableRecordPair getNext() throws IOException {
		return getNextMarkedRecordPair();
	}

	public void setDecision(Decision d) {
		this.decision = d;
	}
	
	public void setDate(Date d) {
		this.date = d;
	}
	
	public void setUser(String usr) {
		this.user = usr;
	}
	
	public void setSrc(String src) {
		this.src = src;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public void open() throws IOException {
		rs.open();
	}

	@Override
	public void close() throws IOException {
		rs.close();
	}

	@Override
	public boolean hasNext() throws IOException {
		return rs.hasNext();
	}

	@Override
	public String getName() {
		return "RS to MRPS Adapter";
	}

	@Override
	public void setName(String name) {
		// do nothing...
	}

	@Override
	public ImmutableProbabilityModel getModel() {
		return rs.getModel();
	}

	@Override
	public void setModel(ImmutableProbabilityModel m) {
		rs.setModel(m);
	}

	@Override
	public boolean hasSink() {
		return false;
	}

	@Override
	public Sink getSink() {
		return null;
	}

	@Override
	public String getFileName() {
		return null;
	}

}
