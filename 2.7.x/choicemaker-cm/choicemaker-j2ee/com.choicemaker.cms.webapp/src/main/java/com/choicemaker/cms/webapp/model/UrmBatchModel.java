package com.choicemaker.cms.webapp.model;

import java.util.ArrayList;
import java.util.List;

import com.choicemaker.cm.batch.api.BatchJob;

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
