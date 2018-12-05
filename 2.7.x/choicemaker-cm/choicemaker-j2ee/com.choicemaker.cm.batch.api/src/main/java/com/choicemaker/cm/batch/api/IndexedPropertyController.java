package com.choicemaker.cm.batch.api;

import java.util.Map;

//import javax.ejb.Local;

/**
 * Manages persistent, indexed properties associated with a batch job.
 * 
 * @author rphall
 */
// @Local
public interface IndexedPropertyController {

	void setIndexedPropertyValue(BatchJob job, String pn, int index, String pv);

	String getIndexedPropertyValue(BatchJob job, String pn, int index);

	IndexedProperty save(IndexedProperty property);

	void remove(IndexedProperty property);

	IndexedProperty find(long propertyId);

	IndexedProperty find(BatchJob job, String name, int index);

	Map<Integer, String> find(BatchJob job, String name);

	/** Returns a count of the number of properties deleted */
	int deleteIndexedPropertiesByJobIdName(long jobId, String name);

}
