package org.aitools.programd.server.core.lsa;

import java.io.FileInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.aitools.programd.dao.LsaSpaceDao;
import org.aitools.programd.dao.impl.LsaSpaceDaoImpl;
import org.aitools.programd.domain.LsaSpace;
import org.aitools.programd.domain.LsaSpace.LsaSpaceType;
import org.aitools.programd.lsa.util.LsaProperties;
import org.aitools.programd.server.core.lsa.util.StringDouble;
import org.aitools.programd.util.PluginsSupport;

public class LsaSupport {
	
	private Map<String, CompareConcept> compareMap;
	private LsaSpaceDao lsaSpaceDao;	
	private String dbUsername;
	private String dbPwd;
	private String dbDriver;
	private String dbUrl;
	private String propertiesPath;
	
	private static final String pluginName = LsaProperties.pluginName;
	//private String pluginPath;
		
	private String space;
	private double competence;
	
	/**
	 * TODO javadoc
	 */
	public LsaSupport(){		
		this(0.01);				
	}
	
	public LsaSupport(String propertiesPath){
		this(propertiesPath, 0.01);
	}
	
	/**
	 * TODO javadoc
	 * @param mt
	 * @param competence
	 */
	public LsaSupport(double competence){
		propertiesPath = PluginsSupport.getPluginPropertiesPath(pluginName);
		loadCompareMap(propertiesPath);
		this.setCompetence(competence);
	}
	
	public LsaSupport(String propertiesPath, double competence){
		this.propertiesPath = propertiesPath;
		loadCompareMap(this.propertiesPath);
		this.setCompetence(competence);
	}
	
	
	/**
	 * TODO javadoc
	 * @return
	 */
	public String getSpace() {
		return space;
	}

	/**
	 * TODO javadoc
	 * @param mt
	 */
	public synchronized boolean setSpace(String space) {
		if(this.containsSpace(space)){
			this.space = space;
			return true;
		}else{
			return false;
		}
	}

	
	/**
	 * TODO javadoc
	 * @return
	 */
	public double getCompetence() {
		return competence;
	}

	/**
	 * TODO javadoc
	 * @param competence
	 */
	public synchronized void setCompetence(double competence) {
		this.competence = competence;
	}
	
	/**
	 * TODO javadoc
	 * @param concept
	 * @return
	 */
	public String[] getQueryRelated(String concept){		
		return this.getQueryRelated(concept, this.competence);
	}
	
	/**
	 * TODO javadoc
	 * @param concept
	 * @param competence
	 * @return
	 */
	public String[] getQueryRelated(String concept, double competence){
		
		String[] ret = new String[0];
		if(space!=null){
			CompareConcept compareConcept = compareMap.get(this.space);			
			
			List<StringDouble> list = compareConcept.getQueryRelated(concept, competence);
			
			if(list!=null && list.size()>0){
				Collections.sort(list, Collections.reverseOrder());
			
				ret = new String[list.size()];
			
				for(int i=0;i<list.size();i++){
					ret[i]=list.get(i).name;
				}			
				return ret;
			}
			else{
				return ret;
			}
		}
		else{
			return ret;
		}		
	}
	
	/**
	 * TODO javadoc
	 * @param concept
	 * @return
	 */
	public String[] getRelated(String concept){		
		return this.getRelated(concept, this.competence);
	}
	
	/**
	 * TODO javadoc
	 * @param concept
	 * @param competence
	 * @return
	 */
	public String[] getRelated(String concept, double competence){
		
		String[] ret = new String[0];
		if(space!=null){
			CompareConcept compareConcept = compareMap.get(this.space);			
			
			List<StringDouble> list = compareConcept.getRelated(concept, competence);
			
			if(list!=null && list.size()>0){
				Collections.sort(list, Collections.reverseOrder());
			
				ret = new String[list.size()];
			
				for(int i=0;i<list.size();i++){
					ret[i]=list.get(i).name;
				}			
				return ret;
			}
			else{
				return ret;
			}
		}
		else{
			return ret;
		}		
	}
	
	public boolean containsSpace(String name){
		return compareMap.containsKey(name.trim());
	}
	
	public synchronized void loadCompareMap(String propertiesPath){
		System.out.println("Loading compareMap...");
		
		if(lsaSpaceDao==null){
			try{
				Properties properties= new Properties();			
				
				properties.load(new FileInputStream(propertiesPath));
			
				String dbName = properties.getProperty("lsa.db_name");
				dbUrl = properties.getProperty("lsa.db_url");
				dbUsername = properties.getProperty("lsa.db_username");
				dbPwd = properties.getProperty("lsa.db_pwd");
				dbDriver = properties.getProperty("lsa.db_driver");
				
				lsaSpaceDao = new LsaSpaceDaoImpl(dbUrl, dbName, dbUsername, dbPwd, dbDriver);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		compareMap = new HashMap<String, CompareConcept>();
		
		List<LsaSpace> spaces = lsaSpaceDao.getAll();
		
		for(LsaSpace space:spaces){
			
			CompareConcept compareConcept = null;
			
			if(space.getType().equalsIgnoreCase(LsaSpaceType.ParolaParola.name())){				
				compareConcept = new CompareConceptPP(dbUrl, space.getName().toLowerCase().trim(), dbUsername, dbPwd, dbDriver, space.getNsigma(), 0.5);				
			}else if(space.getType().equalsIgnoreCase(LsaSpaceType.ParolaDocumento.name())){
				compareConcept = new CompareConceptPD(dbUrl, space.getName().toLowerCase().trim(), dbUsername, dbPwd, dbDriver, space.getNsigma(),  0.5);
			}
			
			if(compareConcept!=null){
				compareMap.put(space.getName(), compareConcept);
			}
		}	
		
		System.out.println("CompareMap loaded.");		
	}
}
