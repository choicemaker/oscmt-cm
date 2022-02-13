/*
 * Copyright (c) 2001, 2019 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.core.configure.jdom;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jdom2.Element;

import com.choicemaker.cm.core.configure.xml.IElement;
import com.choicemaker.util.Precondition;

/**
 * @author rphall
 */
public class JdomElement implements IElement {
	
	private final Element element;

	public JdomElement(Element element) {
		Precondition.assertNonNullArgument("null element", element);
		this.element = element;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.base.configure.IElement#getName()
	 */
	@Override
	public String getName() {
		return this.getElement().getName();
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.base.configure.IElement#getChildren()
	 */
	@Override
	public List getChildren() {
		List retVal = new LinkedList();
		List jdomChildren = this.getElement().getChildren();
		for (Iterator i=jdomChildren.iterator(); i.hasNext(); ) {
			Element jdomChild = (Element) i.next();
			JdomElement returnable = new JdomElement(jdomChild);
			retVal.add(returnable);
		}
		return retVal;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.base.configure.IElement#getChildren(java.lang.String)
	 */
	@Override
	public List getChildren(String name) {
		List retVal = new LinkedList();
		List jdomChildren = this.getElement().getChildren(name);
		for (Iterator i=jdomChildren.iterator(); i.hasNext(); ) {
			Element jdomChild = (Element) i.next();
			JdomElement returnable = new JdomElement(jdomChild);
			retVal.add(returnable);
		}
		return retVal;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.base.configure.IElement#getChild(java.lang.String)
	 */
	@Override
	public IElement getChild(String name) {
		Element child = this.getElement().getChild(name);
		IElement retVal = new JdomElement(child);
		return retVal;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.base.configure.IElement#getAttributeValue(java.lang.String)
	 */
	@Override
	public String getAttributeValue(String name) {
		return this.getElement().getAttributeValue(name);
	}

	protected Element getElement() {
		return element;
	}

}
