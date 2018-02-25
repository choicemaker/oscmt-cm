package com.choicemaker.client.api;

import java.io.Serializable;

import com.choicemaker.util.Precondition;

public final class GraphPropertyBean implements IGraphProperty, Serializable {

	private static final long serialVersionUID = 271L;
	
	public static final boolean equalOrNull(IGraphProperty igp1, IGraphProperty igp2) {
		boolean retVal;
		if (igp1 == null) {
			retVal = igp2 == null;
		} else if (igp2 == null) {
			assert igp1 != null;
			retVal = false;
		} else {
			assert igp1 != null;
			assert igp2 != null;
			final String name1 = igp1.getName();
			final String name2 = igp2.getName();
			if (name1 == null) {
				retVal = name2 == null;
			} else {
				retVal = name1.equals(name2);
			}
		}
		return retVal;
	}

	private final String name;

	public GraphPropertyBean(String _name) {
		Precondition.assertNonEmptyString("empty graph property name", _name);
		this.name = _name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof IGraphProperty))
			return false;
		IGraphProperty other = (IGraphProperty) obj;
		return equalOrNull(this,other);
	}

	@Override
	public String toString() {
		return "GraphPropertyBean [" + name + "]";
	}

}