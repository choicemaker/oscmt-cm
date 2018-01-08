package com.choicemaker.cms.api;

import java.util.List;

import com.choicemaker.cms.args.MatchCandidates;
import com.choicemaker.cms.args.MergeCandidates;

public interface TransitiveCandidates <T extends Comparable<T>> extends MatchCandidates<T> {
	List<MergeCandidates<T>> getMergeGroups();
}
