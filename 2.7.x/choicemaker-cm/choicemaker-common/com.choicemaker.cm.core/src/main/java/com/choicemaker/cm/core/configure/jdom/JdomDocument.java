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
package com.choicemaker.cm.core.configure.jdom;

import org.jdom2.Document;
import org.jdom2.Element;

import com.choicemaker.cm.core.configure.xml.IDocument;
import com.choicemaker.cm.core.configure.xml.IElement;
import com.choicemaker.util.Precondition;

/**
 * @author rphall
 */
public class JdomDocument implements IDocument {
	
	private final Document document;

	public JdomDocument(Document document) {
		Precondition.assertNonNullArgument("null document", document);
		this.document = document;
	}

	@Override
	public IElement getConfigurableElement() {
		Element element = document.getRootElement();
		IElement retVal = new JdomElement(element);
		return retVal;
	}

}
