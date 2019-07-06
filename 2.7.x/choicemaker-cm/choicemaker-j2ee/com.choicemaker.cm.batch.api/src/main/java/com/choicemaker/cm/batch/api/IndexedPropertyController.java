package com.choicemaker.cm.batch.api;

/**
 * Manages persistent, indexed properties associated with a batch job.
 * 
 * @author rphall
 */
public interface IndexedPropertyController extends IndexedPropertyMonitoring {

	void setIndexedPropertyValue(BatchJob job, String pn, int index, String pv);

	IndexedProperty saveIndexedProperty(IndexedProperty property);

	void removeIndexedProperty(IndexedProperty property);

	/** Returns a count of the number of properties deleted */
	int deleteIndexedPropertiesByJobIdName(long jobId, String name);

}
