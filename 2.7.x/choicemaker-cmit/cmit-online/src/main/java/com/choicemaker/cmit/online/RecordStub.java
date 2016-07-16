/*
 * Created on Sep 19, 2009
 */
package com.choicemaker.cmit.online;

import com.choicemaker.cm.core.DerivedSource;
import com.choicemaker.cm.core.Record;


public class RecordStub implements Record {
	private static final long serialVersionUID = 271L;
	private Comparable<?> id;
	public RecordStub(Comparable<?> id) {
		this.id = id;
		if (id == null) {
			throw new IllegalArgumentException("invalid null argument");
		}
	}
	public DerivedSource getDerivedSource() {
		return DerivedSource.NONE;
	}
	public void computeValidityAndDerived() {
	}
	public void computeValidityAndDerived(DerivedSource unused) {
	}
	public void resetValidityAndDerived(DerivedSource unused) {
	}
	public Comparable<?> getId() {
		return this.id;
	}
}
