package org.aitools.programd.services.dto;

import java.io.Serializable;

import org.aitools.programd.domain.LsaSpace;

public class LsaSpaceAdaptor implements Serializable{

	private static final long serialVersionUID = -1594617335661253579L;
	private Integer id;
	private String name;
	private String dbUrl;
	private String type;
	private String status;
	private Integer nsigma;
	
	public LsaSpaceAdaptor(){
		this(-1,"","","","",0);
	}
	
	public LsaSpaceAdaptor(Integer id, String name, String dbUrl, String type, String status, Integer nsigma) {
		super();
		this.id = id;
		this.name = name;
		this.dbUrl = dbUrl;
		this.setType(type);
		this.setStatus(status);
		this.setNsigma(nsigma);
	}
	
	public LsaSpaceAdaptor(LsaSpace lsaSpace){
		this(lsaSpace.getId(), lsaSpace.getName(), lsaSpace.getDbUrl(), lsaSpace.getType(), lsaSpace.getStatus(), lsaSpace.getNsigma());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LsaSpaceAdaptor other = (LsaSpaceAdaptor) obj;
		if (dbUrl == null) {
			if (other.dbUrl != null)
				return false;
		} else if (!dbUrl.equals(other.dbUrl))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (nsigma == null) {
			if (other.nsigma != null)
				return false;
		} else if (!nsigma.equals(other.nsigma))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	public String getDbUrl() {
		return dbUrl;
	}
	public Integer getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public Integer getNsigma() {
		return nsigma;
	}
	public String getStatus() {
		return status;
	}
	public String getType() {
		return type;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dbUrl == null) ? 0 : dbUrl.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((nsigma == null) ? 0 : nsigma.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}
	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNsigma(Integer nsigma) {
		this.nsigma = nsigma;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LsaSpaceAdaptor [dbUrl=").append(dbUrl).append(", id=")
				.append(id).append(", name=").append(name).append(", nsigma=")
				.append(nsigma).append(", status=").append(status).append(
						", type=").append(type).append("]");
		return builder.toString();
	}
	
	
}
