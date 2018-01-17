package com.choicemaker.cms.ejb;

import java.io.Serializable;

import com.choicemaker.client.api.DataAccessObject;
import com.choicemaker.cm.core.DerivedSource;
import com.choicemaker.cm.core.Record;

public class TestRecord<T extends Comparable<T> & Serializable>
		implements Record<T>, DataAccessObject<T> {

	private static final long serialVersionUID = 271L;

	private final T id;

	public TestRecord(T id) {
		// May be null
		this.id = id;
	}

	@Override
	public T getId() {
		return id;
	}

	@Override
	public void computeValidityAndDerived(DerivedSource src) {
	}

	@Override
	public void resetValidityAndDerived(DerivedSource src) {
	}

	@Override
	public void computeValidityAndDerived() {
	}

	@Override
	public DerivedSource getDerivedSource() {
		return null;
	}

}
