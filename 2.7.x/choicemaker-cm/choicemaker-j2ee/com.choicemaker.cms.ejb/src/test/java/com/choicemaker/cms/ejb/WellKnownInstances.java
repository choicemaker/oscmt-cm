package com.choicemaker.cms.ejb;

import com.choicemaker.cms.api.AbaParameters;
import com.choicemaker.cms.beans.AbaParametersBean;

public class WellKnownInstances {

	public WellKnownInstances() {
		// TODO Auto-generated constructor stub
	}

	public static final TestRecord<Integer> query01 =
		new TestRecord<Integer>(0);

	public static final TestRecord<Integer> dbRecord01 =
		new TestRecord<Integer>(1);
	public static final TestRecord<Integer> dbRecord02 =
		new TestRecord<Integer>(2);
	public static final TestRecord<Integer> dbRecord03 =
		new TestRecord<Integer>(3);
	public static final TestRecord<Integer> dbRecord04 =
		new TestRecord<Integer>(4);
	public static final TestRecord<Integer> dbRecord05 =
		new TestRecord<Integer>(5);
	public static final TestRecord<Integer> dbRecord06 =
		new TestRecord<Integer>(6);
	public static final TestRecord<Integer> dbRecord07 =
		new TestRecord<Integer>(7);
	public static final TestRecord<Integer> dbRecord08 =
		new TestRecord<Integer>(8);
	public static final TestRecord<Integer> dbRecord09 =
		new TestRecord<Integer>(9);

	private static final AbaParametersBean parametersBean01 =
		new AbaParametersBean();
	static {
		parametersBean01.setLowThreshold(0.2f);
		parametersBean01.setHighThreshold(0.8f);
	}
	public static final AbaParameters parameters01 = parametersBean01;

}
