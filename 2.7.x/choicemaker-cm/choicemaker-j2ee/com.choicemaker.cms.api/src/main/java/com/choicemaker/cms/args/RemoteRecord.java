package com.choicemaker.cms.args;

import java.io.Serializable;

import com.choicemaker.client.api.Identifiable;

public interface RemoteRecord<T extends Comparable<T> & Serializable>
		extends Identifiable<T>, Serializable {
}
