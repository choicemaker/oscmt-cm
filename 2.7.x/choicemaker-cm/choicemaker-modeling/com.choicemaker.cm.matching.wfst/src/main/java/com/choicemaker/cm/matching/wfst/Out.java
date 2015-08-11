/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.wfst;
import java.util.LinkedList;

public class Out {
    public int weight;
    public LinkedList list;

    public Out(LinkedList l, int w) {
	list = l;
	weight = w;
    }

    public boolean equals(Out out) {
	return list.equals(out.list);
    }

    public String toString() {
	return "weight=" + weight + ", list=" + list;
    }

}
