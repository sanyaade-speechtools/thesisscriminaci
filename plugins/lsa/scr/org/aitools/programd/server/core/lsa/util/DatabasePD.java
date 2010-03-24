package org.aitools.programd.server.core.lsa.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.aitools.programd.server.core.lsa.spazioParolaDocumento.Documento;

public class DatabasePD {
	private Connection connection;
	private PreparedStatement getWordEncoding;
	private PreparedStatement getDocumentEncoding;
	private PreparedStatement getDocumentIds;
	
	private PreparedStatement getDocumentIdsNumber;
	private PreparedStatement getDocumentNumber;
	private PreparedStatement getConcepts;
	private PreparedStatement getConceptsNumber;
	private PreparedStatement getConceptIds;
	
	private PreparedStatement getDocumentName;
	
	
	private Statement st;
	public DatabasePD(String dbUrl, String dbName, String dbUsername, String dbPwd, String dbDriver) {
		connection = connect(dbUrl, dbName, dbUsername, dbPwd, dbDriver);
		try {
			this.getWordEncoding = connection
					.prepareStatement("SELECT vettore FROM parole WHERE parola = ?");
			this.getDocumentEncoding = connection
					.prepareStatement("SELECT filename,categoria,coding FROM documento WHERE id= ?");
			this.getDocumentIds = connection
					.prepareStatement("SELECT id FROM documento ");
			this.getDocumentNumber = connection
					.prepareStatement("SELECT COUNT(*) FROM documento ");
			
			//aggiunto da Mario Scriminaci
			this.getConcepts = connection
					.prepareStatement("SELECT DISTINCT categoria FROM documento");
			this.getConceptsNumber = connection
					.prepareStatement("SELECT COUNT(DISTINCT categoria) FROM documento");
			
			//aggiunto da Mario Scriminaci
			this.getConceptIds = connection
					.prepareStatement("SELECT id FROM documento WHERE categoria = ?");
			this.getDocumentIdsNumber = connection
					.prepareStatement("SELECT COUNT(*) FROM documento WHERE categoria = ?");
			this.getDocumentName = connection
					.prepareStatement("SELECT filename FROM documento WHERE id = ?");
					
			this.st = connection.createStatement();
			
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
	private Connection connect(String dbUrl, String dbName, String dbUsername, String dbPwd, String dbDriver) {
		try {
			Class.forName(dbDriver);
			Connection connection;
			connection = DriverManager.getConnection(dbUrl+dbName, dbUsername,dbPwd);
			return (connection);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	public double[] getCodificaParola(String parola) {
		try {
			getWordEncoding.setString(1, parola);
			ResultSet rs = getWordEncoding.executeQuery();
			if (rs.next()) {
				double[] ret = (double[]) rs.getArray(1).getArray();
				return ret;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void insertCodificaParola(String parola, double[] vettore,
			double m) {
		try {
			st.execute(queryInserimento(parola, vettore, m));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	private String queryInserimento(String parola, double[] vettore,
			double m) {
		return "INSERT INTO parole (parola,vettore,modulo) VALUES ('"
				+ parola
				+ "','{ "
				+ convertiArray(vettore)
				+ "}','"
				+ m
				+ "');";
	}
	public static String convertiArray(double[] a) {
		String stringa = "";
		for (int i = 0; i < a.length - 1; i++) {
			stringa = stringa + a[i] + ",";
		}
		stringa = stringa + a[a.length - 1];
		return stringa;
	}
	public void insertCodificaDocumento(String filename, String categoria,
			double[] coding) {
		try {
			st.execute(queryInserimentoDocumento(filename, categoria, coding));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	private String queryInserimentoDocumento(String filename, String categoria,
			double[] coding) {
		return "INSERT INTO documento (filename,categoria,coding) VALUES ('"
				+ filename + "','" + categoria + "','{ "
				+ convertiArray(coding) + "}');";
	}
	public Documento getCodificaDocumento(int id) {
		try {
			getDocumentEncoding.setInt(1, id);
			ResultSet rs = getDocumentEncoding.executeQuery();
			if (rs.next()) {				
				return new Documento(rs.getString(1), rs.getString(2),
						(double[]) rs.getArray(3).getArray());				
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public int[] getDocumentIds() {
		ResultSet rs;
		try {
			rs = getDocumentNumber.executeQuery();
			if (rs.next()) {
				int num = rs.getInt(1);
				int[] ids = new int[num];
				rs = getDocumentIds.executeQuery();
				int i = 0;
				while (rs.next())
					ids[i++] = rs.getInt(1);
				return ids;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
//	modifiche fatte da Mario Scriminaci
	/**
	 * Il metodo, dato un concetto (che nel database e' chiamato categoria,
	 * ritorna tutti gli indici dei suoi documenti
	 * 
	 *  @param String concept il nome del concetto
	 *  @return int[] gli indici dei documenti del concetto
	 */
	public int[] getIdsOfConcept(String concept){
		try {
			getConceptIds.setString(1,concept);
			getDocumentIdsNumber.setString(1,concept);
			
			ResultSet rs = getDocumentIdsNumber.executeQuery();
			
			int dim=0;
			if(rs.next())
				dim=rs.getInt(1);
			
			rs=getConceptIds.executeQuery();
			
			int[] array=new int[dim];
			
			for(int i=0;i<dim;i++){
				if(rs.next())
				array[i]=rs.getInt(1);
			}
			
			return array;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	/**
	 * Il metodo ritorna il nome del documento specificato nell'id
	 * 
	 * @param id intero che indica l'indice del documento
	 * @return String il nome del documento
	 */
	public String getDocumentName(int id){
		try{
			getDocumentName.setInt(1,id);
			
			ResultSet rs = getDocumentName.executeQuery();
			
			if(rs.next())
				return rs.getString(1);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	/**
	 * Il metodo ritorna il livello di rilevanza rispetto alla categoria del documento specificato nell'id, qualora non lo fosse
	 * torna 999
	 * 
	 * @param id intero che indica l'indice del documento
	 * @return int il livello di rilevanza del documento del documento
	 */
	public int getDocumentRelevance(int id){
		try{
			getDocumentName.setInt(1,id);
						
			ResultSet rs = getDocumentName.executeQuery();
			
			String s="";
			
			if(rs.next())
				s = rs.getString(1);
			
			if(!s.equals("")){
				return Integer.parseInt(s.substring(0, 3));
			}			
		} catch (Throwable t) {
			//e.printStackTrace();
		}
		return 999;		
	}
	
	/**
	 * il metodo ritorna un array di String contenente, in modo univoco,
	 * tutti i concetti del database (chiamati nello stesso categorie)
	 * 
	 * @return String[]
	 */
	public String[] getConcepts(){
		try {
			ResultSet rs=getConceptsNumber.executeQuery();
			
			String[] concept;
			int dim=0;
			if(rs.next())
				dim=rs.getInt(1);
			
			concept=new String[dim];
			
			rs=getConcepts.executeQuery();
			for(int i=0;i<concept.length&&rs.next();i++){
				concept[i]=rs.getString(1);				
			}

			return concept;
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return null;		
	}
	
	public void closeConnection(){
		try {
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
