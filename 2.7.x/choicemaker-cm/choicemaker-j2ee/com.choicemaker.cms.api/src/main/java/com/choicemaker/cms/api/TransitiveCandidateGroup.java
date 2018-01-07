package com.choicemaker.cms.api;

import java.util.List;

public interface TransitiveCandidateGroup <T extends Comparable<T>> extends MatchCandidateGroup<T> {
	List<MergeGroup<T>> getMergeGroups();
}
