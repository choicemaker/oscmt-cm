/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.adaptor.tocmcore;

//import com.choicemaker.cm.core.Accessor;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.urm.base.ConnectedRecordSet;
import com.choicemaker.cm.urm.base.GlobalRecordRef;
import com.choicemaker.cm.urm.base.IRecordHolder;
import com.choicemaker.cm.urm.base.IRecordVisitor;
import com.choicemaker.cm.urm.base.LinkedRecordSet;
import com.choicemaker.cm.urm.base.RecordRef;

/**
 * @author emoussikaev
 */
public class UrmRecordBuilder implements IRecordVisitor {

	private static final long serialVersionUID = 1L;

	private Record resRec;
	private ImmutableProbabilityModel model;
	public UrmRecordBuilder(ImmutableProbabilityModel model) {
		this.model = model;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.urm.base.IRecordVisitor#visit(com.choicemaker.cm.urm.base.IRecordHolder)
	 */
	@Override
	public void visit(IRecordHolder rh) {
		if (rh == null) {
			resRec = null;
		} else {
			resRec = model.getAccessor().toImpl(rh);
		}
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.urm.base.IRecordVisitor#visit(com.choicemaker.cm.urm.base.RecordRef)
	 */
	@Override
	public void visit(RecordRef rRef) {
		if (rRef == null) {
			resRec = null;
		}
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.urm.base.IRecordVisitor#visit(com.choicemaker.cm.urm.base.GlobalRecordRef)
	 */
	@Override
	public void visit(GlobalRecordRef grRef) {
		if (grRef == null) {
			resRec = null;
		} else {
			// TODO retrive record from DB
		}
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.urm.base.IRecordVisitor#visit(com.choicemaker.cm.urm.base.LinkedRecordSet)
	 */
	@Override
	public void visit(LinkedRecordSet lrs) {
		if (lrs == null) {
			resRec = null;
		}
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.urm.base.IRecordVisitor#visit(com.choicemaker.cm.urm.base.ConnectedRecordSet)
	 */
	@Override
	public void visit(ConnectedRecordSet crs) {
		if (crs == null) {
			resRec = null;
		}
	}

	/**
	 * @return
	 */
	public Record getResultRecord() {
		return resRec;
	}

}
