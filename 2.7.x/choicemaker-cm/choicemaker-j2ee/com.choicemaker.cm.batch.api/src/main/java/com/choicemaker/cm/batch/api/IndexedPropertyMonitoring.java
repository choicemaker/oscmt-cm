package com.choicemaker.cm.batch.api;

import java.util.List;
import java.util.Map;

/**
 * Monitors persistent, indexed properties associated with a batch job.
 * 
 * @author rphall
 */
public interface IndexedPropertyMonitoring {

	String getIndexedPropertyValue(BatchJob job, String pn, int index);

	IndexedProperty findIndexedProperty(long propertyId);

	IndexedProperty findIndexedProperty(BatchJob job, String name, int index);

	Map<Integer, String> findIndexedProperties(BatchJob job, String name);

	List<String> findIndexedPropertyNames(BatchJob job);

}
