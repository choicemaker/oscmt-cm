/*
 * Copyright (c) 2001, 2022 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.core.base;

import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Profile;
import com.choicemaker.cm.core.Record;

/**
 * Profile that represents a query record as a Java bean using the
 * generated holder classes.
 *
 * @author   Martin Buechi
 */
public class BeanProfile implements Profile {
	private static final long serialVersionUID = 1L;
	private Object profile;

	/**
	 * Constructs a <code>BeanProfile</code> without an inner Java Bean representation of the query.
	 */	
	public BeanProfile() { }
	
	/**
	 * Constructs a <code>BeanProfile</code> with the specified Java bean representaion of the query record.
	 * Note that <code>profile</code> must be constructed using the generated holder classes.
	 * 
	 * This constructor does not clone the passed argument. 
	 * 
	 * @param   profile  The Java bean representation of the query record.
	 */
	public BeanProfile(Object profile) {
		this.profile = profile;
	}
	
	/**
	 * Returns the Java bean representation of the query record.
	 * 
	 * @return  The Java bean representation of the query record.
	 */
	public Object getProfile() {
		return profile;
	}
	
	/**
	 * Sets the Java bean representation of the query.
	 * 
	 * @param profile  the Java bean representation of the query record
	 */
	public void setProfile(Object profile) {
		this.profile = profile;
	}
	
	@Override
	public String toString() {
		return "beanProfile";
	}
	
	@Override
	public Record getRecord(ImmutableProbabilityModel model) {
		return model.getAccessor().toImpl(getProfile());
	}
}
