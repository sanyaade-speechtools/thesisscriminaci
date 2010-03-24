package org.aitools.programd.dao.test;

import java.io.FileInputStream;
import java.util.Properties;

import org.aitools.programd.dao.LsaSpaceDao;
import org.aitools.programd.dao.impl.LsaSpaceDaoImpl;
import org.aitools.programd.domain.LsaSpace.LsaSpaceStatus;
import org.aitools.programd.domain.LsaSpace.LsaSpaceType;
import org.junit.Test;

public class LsaSpaceDaoTest {

	private LsaSpaceDao lsaSpaceDao;
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
		
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void saveTest(){
		System.out.println(lsaSpaceDao.save("prova5", "prova url", LsaSpaceType.ParolaParola.name(), LsaSpaceStatus.Online.name(), 100));
	}
	
	@Test
	public void getTest(){
		System.out.println(lsaSpaceDao.get(1));
	}
	
	@Test
	public void getByNameTest(){
		System.out.println(lsaSpaceDao.getByName("prova4"));		
	}
	
	@Test
	public void getAllTest(){
		System.out.println(lsaSpaceDao.getAll());		
	}
	
	@Test
	public void createDbTest(){
		String sql = "CREATE SEQUENCE documento_id_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;CREATE SEQUENCE parole_id_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;CREATE TABLE parole(id int4 NOT NULL DEFAULT nextval('parole_id_seq'::regclass),parola varchar(50),vettore_destro float8[],vettore_sinistro float8[],modulo_vdestro float8,modulo_vsinistro float8,CONSTRAINT pkey PRIMARY KEY(id),CONSTRAINT unke UNIQUE(parola)) WITHOUT OIDS;CREATE TABLE documento(id int4 NOT NULL DEFAULT nextval('documento_id_seq'::regclass),categoria varchar(50),filename varchar(200),coding float8[],CONSTRAINT pk PRIMARY KEY(id),CONSTRAINT documento_categoria_key UNIQUE(categoria,filename))WITHOUT OIDS;";
		System.out.println(lsaSpaceDao.createDb("lsa2", sql));
	}
	
	@Test
	public void removeDb(){
		System.out.println(lsaSpaceDao.removeDb("lsa2"));
	}
}
