package org.aitools.programd.dao.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.aitools.programd.dao.LsaSpaceDao;
import org.aitools.programd.domain.LsaSpace;

public class LsaSpaceDaoImpl implements LsaSpaceDao {
	
	private String dbUrl;
	private String dbName;
	private String dbUsername;
	private String dbPwd;
	private String dbDriver;
	
	public LsaSpaceDaoImpl(String dbUrl, String dbName, String dbUsername, String dbPwd, String dbDriver){
		super();
		
		this.dbName = dbName;
		this.dbUrl = dbUrl;
		this.dbUsername = dbUsername;
		this.dbPwd = dbPwd;
		this.dbDriver = dbDriver;
	}

	private Connection connect(String url, String db_name, String username, String pwd, String dbDriver) {
		try {
			Class.forName(dbDriver);
			Connection connection;
			connection = DriverManager.getConnection(
					url+ db_name, username,
					pwd);
			return (connection);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public LsaSpace get(Integer id) {
		
		LsaSpace lsaSpace = null;
		String statement = "SELECT l.id, l.name, l.db_url, l.type, l.status, l.nsigma from lsa_space as l where l.id=?";
		Connection connect = null;
		try{
			connect = connect(dbUrl, dbName, dbUsername, dbPwd, dbDriver);
		
			PreparedStatement preparedStatement = connect.prepareCall(statement);
			preparedStatement.setInt(1, id);
			
			ResultSet resultSet = preparedStatement.executeQuery();
			
			if(resultSet.next()){
				lsaSpace = new LsaSpace();
				
				lsaSpace.setId(resultSet.getInt("id"));
				lsaSpace.setName(resultSet.getString("name"));
				lsaSpace.setDbUrl(resultSet.getString("db_url"));	
				lsaSpace.setType(resultSet.getString("type"));
				lsaSpace.setStatus(resultSet.getString("status"));
				lsaSpace.setNsigma(resultSet.getInt("nsigma"));
			}
			
			connect.close();
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeConnection(connect);
		}
		
		return lsaSpace;
	}

	public List<LsaSpace> getAll() {
		List<LsaSpace> list = new ArrayList<LsaSpace>();
		String statement = "SELECT l.id, l.name, l.db_url, l.type, l.status, l.nsigma from lsa_space as l";
		Connection connect = null;
		try{
			connect = connect(dbUrl, dbName, dbUsername, dbPwd, dbDriver);
		
			PreparedStatement preparedStatement = connect.prepareCall(statement);
			
			ResultSet resultSet = preparedStatement.executeQuery();
			
			while(resultSet.next()){
				LsaSpace lsaSpace = new LsaSpace();
				
				lsaSpace.setId(resultSet.getInt("id"));
				lsaSpace.setName(resultSet.getString("name"));
				lsaSpace.setDbUrl(resultSet.getString("db_url"));
				lsaSpace.setType(resultSet.getString("type"));
				lsaSpace.setStatus(resultSet.getString("status"));
				lsaSpace.setNsigma(resultSet.getInt("nsigma"));
				
				list.add(lsaSpace);
			}
			
			connect.close();
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeConnection(connect);
		}
		
		return list;
	}

	public LsaSpace getByName(String name) {
		LsaSpace lsaSpace = null;
		String statement = "SELECT l.id, l.name, l.db_url, l.type, l.status, l.nsigma from lsa_space as l where l.name=?";
		Connection connect = null;
		try{
			connect = connect(dbUrl, dbName, dbUsername, dbPwd, dbDriver);
		
			PreparedStatement preparedStatement = connect.prepareCall(statement);
			preparedStatement.setString(1, name);
			
			ResultSet resultSet = preparedStatement.executeQuery();
			
			if(resultSet.next()){
				lsaSpace = new LsaSpace();
				
				lsaSpace.setId(resultSet.getInt("id"));
				lsaSpace.setName(resultSet.getString("name"));
				lsaSpace.setDbUrl(resultSet.getString("db_url"));		
				lsaSpace.setType(resultSet.getString("type"));		
				lsaSpace.setStatus(resultSet.getString("status"));	
				lsaSpace.setNsigma(resultSet.getInt("nsigma"));			
			}
			
			connect.close();
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeConnection(connect);
		}
		
		return lsaSpace;
	}
	
	public Boolean remove(Integer id) {
		Boolean ret = false;
		String statement = "DELETE FROM lsa_space l WHERE l.id=?";	
		Connection connect = null;
		try{
			connect = connect(dbUrl, dbName, dbUsername, dbPwd, dbDriver);
		
			PreparedStatement preparedStatement = connect.prepareCall(statement);
			preparedStatement.setInt(1, id);
			
			int n = preparedStatement.executeUpdate();
			
			if(n==1){
				ret = true;
			}
			
			connect.close();
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeConnection(connect);
		}

		return ret;
	}

	public LsaSpace save(String name, String dbUrl, String type, String status, Integer nsigma) {
		LsaSpace lsaSpace = null;
		String statement = "INSERT INTO lsa_space(name, db_url, type, status, nsigma) VALUES(?,?,?,?,?)";
		Connection connect = null;
		try{
			connect = connect(this.dbUrl, this.dbName, this.dbUsername, this.dbPwd, this.dbDriver);
		
			PreparedStatement preparedStatement = connect.prepareCall(statement);
			preparedStatement.setString(1, name);
			preparedStatement.setString(2, dbUrl);
			preparedStatement.setString(3, type);
			preparedStatement.setString(4, status);
			preparedStatement.setInt(5, nsigma);
			
			int n = preparedStatement.executeUpdate();
			
			if(n==1){
				String keyStatement = "select currval('lsa_space_id_seq');";
				preparedStatement = connect.prepareStatement(keyStatement);
				ResultSet resultSet = preparedStatement.executeQuery();
				
				if(resultSet.next()){
					int id = resultSet.getInt(1);
					lsaSpace = new LsaSpace();
					lsaSpace.setId(id);
					lsaSpace.setName(name);
					lsaSpace.setDbUrl(dbUrl);
					lsaSpace.setStatus(status);
					lsaSpace.setNsigma(nsigma);					
				}
			}
			
			connect.close();
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeConnection(connect);
		}
		
		return lsaSpace;
	}

	public Boolean createDb(String name, String sql) {
		Boolean ret = false;
		String statement = "CREATE DATABASE "+name;
		Connection connect = null;
		Connection connect2 = null;
		
		try{
			connect = connect(this.dbUrl, this.dbName, this.dbUsername, this.dbPwd, this.dbDriver);
		
			PreparedStatement preparedStatement = connect.prepareCall(statement);
			preparedStatement.executeUpdate();
			connect.close();
			
			//devo eseguire l'sql
			connect2 = connect(this.dbUrl, name, this.dbUsername, this.dbPwd, this.dbDriver);
			PreparedStatement preparedStatement2 = connect2.prepareCall(sql);
			preparedStatement2.executeUpdate();
			connect2.close();			
			
			ret = true;
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeConnection(connect);
			closeConnection(connect2);
		}
		
		return ret;
	}

	public Boolean removeDb(String name) {
		Boolean ret = false;
		String statement = "DROP DATABASE "+name;
		Connection connect = null;
		try{
			connect = connect(this.dbUrl, this.dbName, this.dbUsername, this.dbPwd, this.dbDriver);
		
			PreparedStatement preparedStatement = connect.prepareCall(statement);
			preparedStatement.executeUpdate();
			
			connect.close();
			
			ret = true;
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeConnection(connect);
		}
		
		return ret;
	}

	public LsaSpace update(LsaSpace lsaSpace) {
		LsaSpace ret = null;
		String statement = "UPDATE lsa_space SET name=?, type=?, status=?, db_url=?, nsigma=? WHERE id=?";
		Connection connect = null;
		try{
			connect = connect(this.dbUrl, this.dbName, this.dbUsername, this.dbPwd, this.dbDriver);
		
			PreparedStatement preparedStatement = connect.prepareCall(statement);
			preparedStatement.setString(1, lsaSpace.getName());
			preparedStatement.setString(2, lsaSpace.getType());
			preparedStatement.setString(3, lsaSpace.getStatus());
			preparedStatement.setString(4, lsaSpace.getDbUrl());
			preparedStatement.setInt(5, lsaSpace.getNsigma());
			preparedStatement.setInt(6, lsaSpace.getId());
						
			int n = preparedStatement.executeUpdate();
			
			if(n==1){
				ret = lsaSpace;
			}
			
			connect.close();
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeConnection(connect);
		}
		
		return ret;
	}
	
	public LsaSpace update(String name, Integer nsigma) {
		LsaSpace ret = this.getByName(name);
		if(ret!=null){
			ret.setName(name);
			ret.setNsigma(nsigma);
				
			ret = this.update(ret);
		}
		
		return ret;
	}
	
	private void closeConnection(Connection connection){
		try {
			if(connection!=null && !connection.isClosed()){
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Boolean clearDb(String name) {
		Boolean ret = false;
		String statement1 = "DELETE FROM parole";
		String statement2 = "DELETE FROM documento";
		
		Connection connect = null;
		
		try{
			connect = connect(this.dbUrl, name.trim().toLowerCase(), this.dbUsername, this.dbPwd, this.dbDriver);
		
			PreparedStatement preparedStatement1 = connect.prepareCall(statement1);
			preparedStatement1.executeUpdate();
			PreparedStatement preparedStatement2 = connect.prepareCall(statement2);
			preparedStatement2.executeUpdate();			
			
			connect.close();

			ret = true;
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeConnection(connect);
		}
		
		return ret;
	}

}
