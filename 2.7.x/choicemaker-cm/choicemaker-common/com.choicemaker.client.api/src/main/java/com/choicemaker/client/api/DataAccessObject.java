package com.choicemaker.client.api;

import java.io.Serializable;

/**
 * The interface implemented by generated record-holder classes, which are
 * serializable and identifiable (with serializable identifiers).
 */
public interface DataAccessObject<T extends Comparable<T> & Serializable>
	extends Identifiable<T>, Serializable {
}
