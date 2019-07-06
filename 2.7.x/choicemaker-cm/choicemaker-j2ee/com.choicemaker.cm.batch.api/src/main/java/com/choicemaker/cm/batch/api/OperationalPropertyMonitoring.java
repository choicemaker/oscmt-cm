package com.choicemaker.cm.batch.api;

import java.util.List;

/**
 * Monitors persistent, operational properties.
 * 
 * @author rphall
 */
public interface OperationalPropertyMonitoring {

	String getOperationalPropertyValue(BatchJob job, String pn);

	OperationalProperty findOperationalProperty(long propertyId);

	OperationalProperty findOperationalProperty(BatchJob job, String name);

	List<OperationalProperty> findOperationalProperties(BatchJob job);

	@Deprecated
	List<OperationalProperty> findAllOperationalProperties();

}
