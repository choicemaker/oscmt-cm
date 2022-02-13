/*******************************************************************************
 * Copyright (c) 2003, 2021 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc  - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
package com.choicemaker.cms.webapp.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.choicemaker.cm.batch.api.BatchJob;

@XmlRootElement()
public class UrmBatchModel extends BaseBatchModel {

	private static final long serialVersionUID = 271L;
	private List<BaseBatchModel> children = new ArrayList<>();

	public UrmBatchModel() {
	}

	public UrmBatchModel(BatchJob job) {
		super(job);
	}

	public List<BaseBatchModel> getChildren() {
		return children;
	}

	public void setChildren(List<BaseBatchModel> children) {
		this.children = children;
	}

}
