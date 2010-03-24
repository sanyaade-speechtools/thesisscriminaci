package org.aitools.programd.lsa.util;

import java.io.File;
import java.util.SortedMap;

import org.aitools.programd.domain.LsaSpace;
import org.aitools.programd.server.core.lsa.spazioParolaParola.DbDocumento;
import org.aitools.programd.server.core.lsa.spazioParolaParola.DbParole;
import org.aitools.programd.server.core.lsa.spazioParolaParola.SpinWords;
import org.aitools.programd.server.core.lsa.util.DatabasePP;
import org.aitools.programd.server.core.lsa.util.TextUtil;

public class WordWordSpace {

	private static final String octaveResources = LsaProperties.octaveResourcesDir+File.separator;
	private static final String stopWordsPath = LsaProperties.stopwordPath;
	
	private String dbUrl;
	private String dbName;
	private String dbUsername;
	private String dbPwd;
	private String dbDriver;	
	
	public WordWordSpace(String dbUrl, String dbName, String dbUsername, String dbPwd, String dbDriver){
		this.dbUrl = dbUrl;
		this.dbName = dbName;
		this.dbUsername = dbUsername;
		this.dbPwd = dbPwd;
		this.dbDriver = dbDriver;
	}
	
	public Boolean processSpace(LsaSpace lsaSpace, String pluginPath, int nsv, boolean order, int maxd) throws Throwable{
		
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
				
				String outOccorrenzeFile = basePath+"occorrenze.txt";
				String outDigrammiFile = basePath+"digrammi.txt";
				String outMatrixFile = basePath+"A.mm";
								
				SortedMap<String, Integer> wf = TextUtil.wordFreq(spaceDir.getAbsolutePath(), pluginPath+File.separator+stopWordsPath);
				TextUtil.saveMap(outOccorrenzeFile, wf, false);
				
				SortedMap<String, Integer> df = TextUtil.digFreq(spaceDir.getAbsolutePath(), maxd, order, pluginPath+File.separator+stopWordsPath);
				TextUtil.saveMap(outDigrammiFile, df, false);
				
				SpinWords sw = new SpinWords(outOccorrenzeFile, outDigrammiFile);
				
				sw.writemat(outMatrixFile);
				
				String exec = "cd(\""+basePath+"\");addpath(\""+pluginPath+File.separator+octaveResources+"\");mmsvd(\"A.mm\","+nsv+");";
			
				OctaveSupport.exec(exec);
				
				DatabasePP databasePP = new DatabasePP(dbUrl, dbName, dbUsername, dbPwd, dbDriver);
				
				DbParole.process(databasePP, outOccorrenzeFile, basePath);
				DbDocumento.process(databasePP, spaceDir.getAbsolutePath(), nsv, pluginPath+File.separator+stopWordsPath);
				
				databasePP.closeConnection();

				System.out.println("Done.");
			}
		}
		
		return ret;
	}	
}
