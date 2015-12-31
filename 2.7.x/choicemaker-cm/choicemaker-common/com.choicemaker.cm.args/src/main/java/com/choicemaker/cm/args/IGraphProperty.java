package com.choicemaker.cm.args;

import java.io.Serializable;

/**
 * A type of the graph topology that can be used for identifying when a set of
 * linked records represents represents a single entity.
 * <p>
 *
 * @author emoussikaev
 */
public interface IGraphProperty extends Serializable {

	String getName();

}
