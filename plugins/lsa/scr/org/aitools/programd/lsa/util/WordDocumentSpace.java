package org.aitools.programd.lsa.util;

import java.io.File;

import org.aitools.programd.domain.LsaSpace;
import org.aitools.programd.server.core.lsa.spazioParolaDocumento.CreateDB;
import org.aitools.programd.server.core.lsa.spazioParolaDocumento.FreqParole;
import org.aitools.programd.server.core.lsa.util.DatabasePD;

public class WordDocumentSpace {

	private static final String octaveResources = LsaProperties.octaveResourcesDir+File.separator;
	private static final String stopWordsPath = LsaProperties.stopwordPath;
	
	private String dbUrl;
	private String dbName;
	private String dbUsername;
	private String dbPwd;
	private String dbDriver;	
	
	public WordDocumentSpace(String dbUrl, String dbName, String dbUsername, String dbPwd, String dbDriver){
		this.dbUrl = dbUrl;
		this.dbName = dbName;
		this.dbUsername = dbUsername;
		this.dbPwd = dbPwd;
		this.dbDriver = dbDriver;
	}
	
	public Boolean processSpace(LsaSpace lsaSpace, String pluginPath, int nsv) throws Throwable{
		
		Boolean ret=false;
		
		String spacesDirPath=pluginPath+File.separator+LsaProperties.spacesDir;
		File spacesDir = new File(spacesDirPath);
		
		if(spacesDir.exists()){
			String spaceDirPath = spacesDirPath+File.separator+lsaSpace.getName();
			File spaceDir = new File(spaceDirPath);
		
			if(spaceDir.exists()){
				
				String basePath = pluginPath+File.separator+LsaProperties.varDir+File.separator+lsaSpace.getName()+File.separator;
				File baseDir = new File(basePath);
				
				if(baseDir.exists()){
					baseDir.delete();
				}
				baseDir.mkdirs();
				
				DatabasePD databasePD = new DatabasePD(dbUrl, dbName, dbUsername, dbPwd, dbDriver);
				FreqParole freqParole = new FreqParole(databasePD, pluginPath+File.separator+stopWordsPath);
				
				freqParole.createMatrixFromFolder(basePath+"pd.mm",spaceDir.getAbsolutePath(), basePath);
				freqParole.saveParoleDocumenti(basePath+"paroleDocumenti.dat");
				
				String exec = "cd(\""+basePath+"\");addpath(\""+pluginPath+File.separator+octaveResources+"\");mmsvd(\"pd.mm\","+nsv+");";
				
				OctaveSupport.exec(exec);
							
				CreateDB.create(basePath, databasePD);		
								
				databasePD.closeConnection();	
				System.out.println("Done.");			
			}
		}
		
		return ret;
	}	
}
