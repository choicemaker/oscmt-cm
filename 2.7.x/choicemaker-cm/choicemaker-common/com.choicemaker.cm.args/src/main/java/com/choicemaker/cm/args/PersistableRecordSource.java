package com.choicemaker.cm.args;

import java.io.Serializable;

public interface PersistableRecordSource
		extends PersistentObject, Serializable {

	@Override
	long getId();

	String getType();

}
