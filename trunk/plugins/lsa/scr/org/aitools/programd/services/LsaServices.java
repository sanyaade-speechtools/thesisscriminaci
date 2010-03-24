package org.aitools.programd.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.aitools.programd.Core;
import org.aitools.programd.dao.LsaSpaceDao;
import org.aitools.programd.dao.impl.LsaSpaceDaoImpl;
import org.aitools.programd.domain.LsaSpace;
import org.aitools.programd.domain.LsaSpace.LsaSpaceStatus;
import org.aitools.programd.domain.LsaSpace.LsaSpaceType;
import org.aitools.programd.lsa.util.LsaProperties;
import org.aitools.programd.lsa.util.WordDocumentSpace;
import org.aitools.programd.lsa.util.WordWordSpace;
import org.aitools.programd.server.core.lsa.LsaSupport;
import org.aitools.programd.services.dto.LsaSpaceAdaptor;
import org.aitools.programd.util.GlobalProperties;
import org.aitools.programd.util.PluginsSupport;

public class LsaServices implements Serializable{
	
	
	private static final String pluginName = LsaProperties.pluginName;
	private static final long serialVersionUID = -8201877079956464011L;
	private LsaSpaceDao lsaSpaceDao;
	private String dbUrl;
	private String dbUsername;
	private String dbPwd;
	private String dbDriver;
	private String dbParolaParolaSql;
	private String dbParolaDocumentoSql;
	private String pluginPath;
	private int nsigma;
	private boolean order;
	private int maxd;
	
	
	public LsaServices(){
		String propertiesPath = PluginsSupport.getPluginPropertiesPath(pluginName);
		pluginPath = PluginsSupport.getPluginPath(pluginName);
		Properties properties = new Properties();
		
		try{
			properties.load(new FileInputStream(propertiesPath));
			
			String dbName = properties.getProperty(pluginName+".db_name");
			this.dbUrl = properties.getProperty(pluginName+".db_url");
			this.dbUsername = properties.getProperty(pluginName+".db_username");
			this.dbPwd = properties.getProperty(pluginName+".db_pwd");
			this.dbDriver = properties.getProperty(pluginName+".db_driver");
			
			this.nsigma = Integer.parseInt(properties.getProperty(pluginName+".nsigma"));
			this.order = Boolean.parseBoolean(properties.getProperty(pluginName+".order"));
			this.maxd = Integer.parseInt(properties.getProperty(pluginName+".maxd"));
			
			this.dbParolaParolaSql = properties.getProperty(pluginName+".db_parola_parola_sql");
			this.dbParolaDocumentoSql = properties.getProperty(pluginName+".db_parola_documento_sql");		
						
			lsaSpaceDao = new LsaSpaceDaoImpl(dbUrl, dbName, dbUsername, dbPwd, dbDriver);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<LsaSpaceAdaptor> getAllLsaSpaces(HttpServletRequest request){
		
		List<LsaSpace> list = lsaSpaceDao.getAll();
		
		List<LsaSpaceAdaptor> ret = new ArrayList<LsaSpaceAdaptor>();
		for(LsaSpace lsaSpace:list){
			ret.add(new LsaSpaceAdaptor(lsaSpace));
		}
		
		return ret;
	}
	
	public LsaSpaceAdaptor addLsaSpace(HttpServletRequest request, String name, String type, Integer nsigma){
		
		LsaSpace lsaSpace = lsaSpaceDao.getByName(name);
		if(lsaSpace==null){
			
			//Devo creare il db
			String sql = "";
			if(type.trim().equalsIgnoreCase(LsaSpaceType.ParolaParola.name())){
				sql = this.dbParolaParolaSql;
			}else if(type.trim().equalsIgnoreCase(LsaSpaceType.ParolaDocumento.name())){
				sql = this.dbParolaDocumentoSql;
			}else{
				return null;
			}	
			
			String dbUrl=this.dbUrl+name.toLowerCase();			
			lsaSpace = lsaSpaceDao.save(name, dbUrl, type, LsaSpaceStatus.Online.name(), nsigma);			
			
			//create db
			lsaSpaceDao.createDb(name.toLowerCase(), sql);
		 
			//create, if not exist, space directory and var directory
			String spacesDirPath=pluginPath+File.separator+LsaProperties.spacesDir+File.separator+name;
			File dirSpace = new File(spacesDirPath);
			if(!dirSpace.exists()){
				dirSpace.mkdirs();
			}
			
			String varDirPath=pluginPath+File.separator+LsaProperties.varDir+File.separator+name;
 			File dirVar = new File(varDirPath);
			if(!dirVar.exists()){
				dirVar.mkdirs();
			}
			
			return new LsaSpaceAdaptor(lsaSpace);
		}else{
			return null;
		}
		
	}
	
	public Boolean editLsaSpace(HttpServletRequest request, String name, String type, Integer nsigma){
		Boolean ret = false;
		
		LsaSpace lsaSpace = lsaSpaceDao.getByName(name);
		
		if(lsaSpace!=null){
			lsaSpace.setNsigma(nsigma);
			lsaSpace.setType(type);
			
			lsaSpace = lsaSpaceDao.update(lsaSpace);
			if(lsaSpace!=null){
				ret = true;
			}
		}
		
		return ret;		
	}
	
	public Boolean removeLsaSpace(HttpServletRequest request, Integer id){
		LsaSpace lsaSpace = lsaSpaceDao.get(id);
		if(lsaSpace!=null){
			
			//Devo rimuovere il db
			boolean control = lsaSpaceDao.removeDb(lsaSpace.getName());

			return lsaSpaceDao.remove(id)&&control;
		}
		else{
			return false;
		}	
	}
	
	public LsaSpaceAdaptor processLsaSpace(HttpServletRequest request, Integer id){
		LsaSpace lsaSpace = lsaSpaceDao.get(id);
		
		boolean ret = false;
		if(lsaSpace!=null && lsaSpace.getStatus().equalsIgnoreCase(LsaSpaceStatus.Online.name())){
			lsaSpace.setStatus(LsaSpaceStatus.Processing.name());
			lsaSpaceDao.update(lsaSpace);
			
			lsaSpaceDao.clearDb(lsaSpace.getName());
			
			if(lsaSpace.getType().equalsIgnoreCase(LsaSpaceType.ParolaDocumento.name())){
				WordDocumentSpace wordDocumentSpace = new WordDocumentSpace(dbUrl, lsaSpace.getName().toLowerCase().trim(), dbUsername, dbPwd, dbDriver);
				try {
					wordDocumentSpace.processSpace(lsaSpace, pluginPath, lsaSpace.getNsigma());
					ret = true;
				} catch (Throwable e) {
					e.printStackTrace();
				}				
			}else if(lsaSpace.getType().equalsIgnoreCase(LsaSpaceType.ParolaParola.name())){
				WordWordSpace wordWordSpace = new WordWordSpace(dbUrl, lsaSpace.getName().toLowerCase().trim(), dbUsername, dbPwd, dbDriver);
				try {
					wordWordSpace.processSpace(lsaSpace, pluginPath, lsaSpace.getNsigma(), order, maxd);
					ret = true;
				} catch (Throwable e) {
					e.printStackTrace();
				}	
				
				ret = true;
			}
			
			lsaSpace.setStatus(LsaSpaceStatus.Online.name());
			lsaSpaceDao.update(lsaSpace);
			
			if(ret){
				ServletContext context = request.getSession().getServletContext();
				
				Object obj = context.getAttribute(GlobalProperties.sessionKeyCore);
				if(obj!=null && obj instanceof Core){
					String propertiesPath = PluginsSupport.getPluginPropertiesPath(pluginName);
					LsaSupport lsaSupport = (LsaSupport)((Core)obj).getPluginSupportMap().get(LsaSupport.class);
					lsaSupport.loadCompareMap(propertiesPath);
				}
				return new LsaSpaceAdaptor(lsaSpace);
			}
			else{
				return null;
			}
		}
		else{
			return null;
		}	
	}
	
	public LsaSpaceAdaptor getLsaSpaceByName(HttpServletRequest request, String name){
		return new LsaSpaceAdaptor(lsaSpaceDao.getByName(name));
	}

}
