/*
 * Copyright (c) 2001, 2009 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.io.blocking.automated.offline.impl;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.io.blocking.automated.offline.core.Constants;
import com.choicemaker.cm.io.blocking.automated.offline.core.IRecordIDSink;
import com.choicemaker.cm.io.blocking.automated.offline.core.IRecordIDSinkSourceFactory;
import com.choicemaker.cm.io.blocking.automated.offline.core.IRecordIDSource;
import com.choicemaker.cm.io.blocking.automated.offline.core.IRecordIDTranslator2;

/**
 * @author pcheung
 *
 */
public class RecordIDTranslator2 implements IRecordIDTranslator2 {
	
	private static final Logger log = Logger.getLogger(RecordIDTranslator2.class);

	private IRecordIDSinkSourceFactory rFactory;

	//These two files store the input record id2.  The first id correspond to internal id 0, etc.
	private IRecordIDSink sink1;
	private IRecordIDSink sink2;
	
	//This is an indicator of the class type of record id.
	private int dataType;

	/** This contains the range of the record ID in sink1.  
	 * range1[0] is min and range1[1] is max.
	 */
	private Comparable [] range1 = new Comparable [2];
	
	/** This contains the range of the record ID in sink2.  
	 * range2[0] is min and range2[1] is max.
	 */
	private Comparable [] range2 = new Comparable [2];

	/**
	 * This contains the mapping from input record id I to internal record id J.
	 * mapping[J] = I.  J starts from 0.
	 */
	private int currentIndex = -1;
	
	/**
	 * This is the point at which the second record source record ids start.  if this is 0, it means
	 * there is only 1 record source.
	 */
	private int splitIndex = 0;

	//	indicates whether initReverseTranslation has happened.
	private boolean initialized = false; 

	//These two lists are use during reverse translation.
	private ArrayList list1;
	private ArrayList list2;


	public RecordIDTranslator2 (IRecordIDSinkSourceFactory rFactory) throws BlockingException {
		this.rFactory = rFactory;
		
		sink1 = rFactory.getNextSink();
		sink2 = rFactory.getNextSink();

		range1[0] = null;
		range1[1] = null;

		range2[0] = null;
		range2[1] = null;
	}
	

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.io.blocking.automated.offline.core.IRecordIDTranslator2#getRange1()
	 */
	public Comparable[] getRange1() {
		return range1;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.io.blocking.automated.offline.core.IRecordIDTranslator2#getRange2()
	 */
	public Comparable[] getRange2() {
		return range2;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.io.blocking.automated.offline.core.IRecordIDTranslator2#getSplitIndex()
	 */
	public int getSplitIndex() {
		return splitIndex;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.io.blocking.automated.offline.core.IRecordIDTranslator2#open()
	 */
	public void open() throws BlockingException {
		currentIndex = -1;
		sink1.open();
		splitIndex = 0;		
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.io.blocking.automated.offline.core.IRecordIDTranslator2#split()
	 */
	public void split() throws BlockingException {
		splitIndex = currentIndex + 1;
		sink1.close();
		sink2.open();
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.io.blocking.automated.offline.core.IRecordIDTranslator2#close()
	 */
	public void close() throws BlockingException {
		if (splitIndex == 0) sink1.close();
		else sink2.close();
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.io.blocking.automated.offline.core.IRecordIDTranslator2#cleanUp()
	 */
	public void cleanUp() throws BlockingException {
		list1 = null;
		list2 = null;
		
		sink1.remove();
		if (splitIndex > 0) sink2.remove();
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.io.blocking.automated.offline.core.IRecordIDTranslator2#recover()
	 */
	public void recover() throws BlockingException {
		IRecordIDSource source = rFactory.getSource(sink1);
		currentIndex = -1;
		splitIndex = 0;		
		if (source.exists()) {
			source.open();
			while (source.hasNext()) {
				currentIndex ++;
				
				Comparable o = source.getNextID ();
				setMinMax (o, range1);
			}
			source.close();
			sink1.append();
		}
		
		source = rFactory.getSource(sink2);
		if (source.exists()) {
			sink1.close();
			splitIndex = currentIndex + 1;
			source.open();
			while (source.hasNext()) {
				currentIndex ++;

				Comparable o = source.getNextID ();
				setMinMax (o, range2);
			}
			source.close();
			sink2.append();
		}
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.io.blocking.automated.offline.core.IRecordIDTranslator2#translate(java.lang.Object)
	 */
	public int translate(Comparable o) throws BlockingException {
		currentIndex ++;

		//figure out the id type for the first file
		if (currentIndex == 0) {
			dataType = Constants.checkType(o);
			sink1.setRecordIDType(dataType);
		}
		
		//figure out the id type for the second file
		if (currentIndex == splitIndex) {
			dataType = Constants.checkType(o);
			sink2.setRecordIDType(dataType);
		}
		
		if (splitIndex == 0) {
			sink1.writeRecordID(o);
			
			setMinMax (o, range1);

		} else {
			sink2.writeRecordID(o);

			setMinMax (o, range2);
			
		} 
		
		return currentIndex;
	}
	
	
	/** This method compares o to the range.  If o is smaller than range[0], then replace range[0] with o.
	 * If o is larger than range[1], then replace range[1] with o.
	 * 
	 * @param o
	 * @param range
	 */
	private void setMinMax (Comparable o, Comparable [] range) {
		if (range[0] == null) range[0] = o;
		else {
			if (o.compareTo(range[0]) < 0) range[0] = o;
		}
		
		if (range[1] == null) range[1] = o;
		else {
			if (o.compareTo(range[1]) > 0) range[1] = o;
		}
	}
	

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.io.blocking.automated.offline.core.IRecordIDTranslator2#initReverseTranslation()
	 */
	public void initReverseTranslation() throws BlockingException {
		if (!initialized) {
			if (splitIndex == 0) list1 = new ArrayList (currentIndex);
			else list1 = new ArrayList (splitIndex);
			
			IRecordIDSource source1 = rFactory.getSource(sink1);
			source1.open();
			
			while (source1.hasNext()) {
				list1.add(source1.getNextID());
			}
		
			source1.close();
		
			//Read the second source if there is one
			if (splitIndex > 0) {
				list2 = new ArrayList (currentIndex - splitIndex + 1);
				IRecordIDSource source2 = rFactory.getSource(sink2);
				source2.open();
		
				while (source2.hasNext()) {
					list2.add(source2.getNextID());
				}
		
				source2.close();
			}
		
			initialized = true;
		}
	}


	/* (non-Javadoc)
	 * @see com.choicemaker.cm.io.blocking.automated.offline.core.IRecordIDTranslator2#reverseLookup(int)
	 */
	public Comparable reverseLookup(int internalID) {
		Comparable o = null;
		
		if (splitIndex == 0) o = (Comparable) list1.get(internalID);
		else {
			if (internalID < splitIndex) o = (Comparable) list1.get(internalID);
			else {
				o = (Comparable) list2.get(internalID - splitIndex);
			}
		}
		return o;
	}


	/** This returns an ArrayList of record IDs from the first source.  Usually, the staging source.
	 * 
	 * @return ArrayList
	 */
	public ArrayList getList1 () {
		return list1;
	}


	/** This returns an ArrayList of record IDs from the second source.  Usually, the master source.
	 * 
	 * @return ArrayList
	 */
	public ArrayList getList2 () {
		return list2;
	}

}