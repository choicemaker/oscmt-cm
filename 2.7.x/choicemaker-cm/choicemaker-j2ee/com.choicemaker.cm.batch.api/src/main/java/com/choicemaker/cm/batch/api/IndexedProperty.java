package com.choicemaker.cm.batch.api;

import java.io.Serializable;

import com.choicemaker.cm.args.PersistentObject;

/**
 * An indexed property is a property that is an element in an associative array
 * that is keyed by integers. Indexed properties are often used to store results
 * of a batch job.
 *
 * @author rphall
 */
public interface IndexedProperty extends PersistentObject, Serializable {

	/** The identifier of the job that owns this operational property */
	long getJobId();

	/** The property name */
	String getName();

	/** The property index */
	int getIndex();

	/** The property value */
	String getValue();

	void updateValue(String v);

}