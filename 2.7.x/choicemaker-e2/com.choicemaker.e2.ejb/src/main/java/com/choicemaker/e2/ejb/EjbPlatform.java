/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.e2.ejb;

import javax.ejb.Local;

import com.choicemaker.e2.CMPlatform;

/**
 * A singleton implementation that uses an embedded Eclipse2 implementation to
 * implement CMPlatform methods.
 *
 * @author rphall
 *
 */
@Local
public interface EjbPlatform extends CMPlatform {
}
