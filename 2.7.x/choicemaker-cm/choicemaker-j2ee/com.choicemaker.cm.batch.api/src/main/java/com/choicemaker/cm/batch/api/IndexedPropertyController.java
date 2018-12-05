package com.choicemaker.cm.batch.api;

import java.util.List;
import java.util.Map;

//import javax.ejb.Local;

/**
 * Manages persistent, indexed properties.
 * 
 * @author rphall
 */
// @Local
public interface IndexedPropertyController {

	void setJobProperty(BatchJob job, String pn, String pv);

	String getJobProperty(BatchJob job, String pn);

	IndexedProperty save(IndexedProperty property);

	void remove(IndexedProperty property);

	IndexedProperty find(long propertyId);

	IndexedProperty find(BatchJob job, String name, int index);

	Map<Integer, String> find(BatchJob job, String name);

	/** Returns a count of the number of properties deleted */
	int deleteOperationalPropertiesByJobId(long jobId, String name);

}
