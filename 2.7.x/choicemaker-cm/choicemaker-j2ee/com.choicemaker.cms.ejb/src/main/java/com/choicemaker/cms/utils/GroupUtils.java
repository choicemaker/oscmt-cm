package com.choicemaker.cms.utils;

import java.io.Serializable;
import java.util.List;

import com.choicemaker.cm.args.IGraphProperty;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cms.args.EvaluatedPair;
import com.choicemaker.cms.args.MergeCandidates;
import com.choicemaker.cms.args.RemoteRecord;

public class GroupUtils {

	public static <S extends Comparable<S> & Serializable> List<RemoteRecord<S>> getRecords(
			List<EvaluatedPair<S>> pairs) {
		throw new Error("not yet implemented");
	}

	public static <S extends Comparable<S> & Serializable> List<MergeCandidates<S>> computeMergeGroups(
			ImmutableProbabilityModel model, IGraphProperty graphConnectivity,
			List<RemoteRecord<S>> pairs) {
		throw new Error("not yet implemented");
	}

	private GroupUtils() {
	}
}
