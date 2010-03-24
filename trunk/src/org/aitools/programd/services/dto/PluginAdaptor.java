package org.aitools.programd.services.dto;

import java.io.Serializable;

public class PluginAdaptor implements Serializable {

	private static final long serialVersionUID = -5535572260194820198L;
	private String name;
	private Boolean active;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PluginAdaptor other = (PluginAdaptor) obj;
		if (active == null) {
			if (other.active != null)
				return false;
		} else if (!active.equals(other.active))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	public Boolean getActive() {
		return active;
	}
	public String getName() {
		return name;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((active == null) ? 0 : active.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Plugin [active=").append(active).append(", name=")
				.append(name).append("]");
		return builder.toString();
	}

}
