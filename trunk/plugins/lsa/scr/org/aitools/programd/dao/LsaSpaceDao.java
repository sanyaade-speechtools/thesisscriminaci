package org.aitools.programd.dao;

import java.util.List;

import org.aitools.programd.domain.LsaSpace;

public interface LsaSpaceDao {
	
	public List<LsaSpace> getAll();
	public LsaSpace get(Integer id);
	public LsaSpace getByName(String name);	
	public LsaSpace save(String name, String dbUrl, String type, String status, Integer nsigma);
	public LsaSpace update(LsaSpace lsaSpace);
	public Boolean remove(Integer id);
	public Boolean createDb(String name, String sql);
	public Boolean clearDb(String name);
	public Boolean removeDb(String name);
	
}
