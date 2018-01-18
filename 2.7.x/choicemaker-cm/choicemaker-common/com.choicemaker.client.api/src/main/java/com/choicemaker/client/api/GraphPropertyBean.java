package com.choicemaker.client.api;

import java.io.Serializable;

import com.choicemaker.util.Precondition;

public final class GraphPropertyBean implements IGraphProperty, Serializable {

	private static final long serialVersionUID = 271L;

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
		if (getClass() != obj.getClass())
			return false;
		GraphPropertyBean other = (GraphPropertyBean) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "GraphPropertyBean [" + name + "]";
	}

}