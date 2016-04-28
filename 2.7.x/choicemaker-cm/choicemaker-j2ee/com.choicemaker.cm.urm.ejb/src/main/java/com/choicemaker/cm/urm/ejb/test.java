/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.ejb;

import com.choicemaker.cm.urm.base.SubsetDbRecordCollection;
import com.choicemaker.cm.urm.exceptions.RecordCollectionException;

/**
 * @author emoussikaev
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class test {

	public static void main(String[] args) {
		try {
			SubsetDbRecordCollection qRs = new SubsetDbRecordCollection("","",10,""); 
			SerialRecordSourceBuilder	rcb = new SerialRecordSourceBuilder(null,true);
			qRs.accept(rcb);
		} catch (RecordCollectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}
}
