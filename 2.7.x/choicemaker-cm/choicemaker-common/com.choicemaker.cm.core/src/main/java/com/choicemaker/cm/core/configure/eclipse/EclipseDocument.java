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
package com.choicemaker.cm.core.configure.eclipse;

import com.choicemaker.cm.core.configure.xml.IDocument;
import com.choicemaker.cm.core.configure.xml.IElement;
import com.choicemaker.e2.CMConfigurationElement;
import com.choicemaker.util.Precondition;

/**
 * @author rphall
 */
public class EclipseDocument implements IDocument {
	
	private final CMConfigurationElement root;

	public EclipseDocument(CMConfigurationElement root) {
		Precondition.assertNonNullArgument("null configuration element",root);
		this.root = root;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.base.configure.IDocument#getConfigurableElement()
	 */
	public IElement getConfigurableElement() {
		IElement retVal = new EclipseElement(root);
		return retVal;
	}

}
