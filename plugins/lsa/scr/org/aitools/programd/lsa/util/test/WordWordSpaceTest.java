package org.aitools.programd.lsa.util.test;

import java.io.FileInputStream;
import java.util.Properties;

import org.aitools.programd.dao.LsaSpaceDao;
import org.aitools.programd.dao.impl.LsaSpaceDaoImpl;
import org.aitools.programd.domain.LsaSpace;
import org.aitools.programd.lsa.util.WordWordSpace;
import org.junit.Test;

public class WordWordSpaceTest {

	private LsaSpaceDao lsaSpaceDao;
	private WordWordSpace wordWordSpace;
	{
		try{
			Properties properties= new Properties();
			properties.load(new FileInputStream("plugins/lsa/plugin.properties"));
		
			String dbName = properties.getProperty("lsa.db_name");
			String dbUrl = properties.getProperty("lsa.db_url");
			String dbUsername = properties.getProperty("lsa.db_username");
			String dbPwd = properties.getProperty("lsa.db_pwd");
			String dbDriver = properties.getProperty("lsa.db_driver");
		
		
			lsaSpaceDao = new LsaSpaceDaoImpl(dbUrl, dbName, dbUsername, dbPwd, dbDriver);
			wordWordSpace = new WordWordSpace(dbUrl, "biologymtfilter", dbUsername, dbPwd, dbDriver);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void processSpaceTest() throws Throwable{
		
		LsaSpace lsaSpace = lsaSpaceDao.getByName("BiologyMtFilter");
			
		wordWordSpace.processSpace(lsaSpace, "/opt/tomcat/webapps/programd/plugins/lsa/", 50, false, 4);
		
	}
	
	
}
