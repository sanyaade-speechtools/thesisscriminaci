package org.aitools.programd.server.core.lsa;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;
import java.util.Properties;

import no.uib.cipr.matrix.DenseMatrix;

import org.aitools.programd.server.core.lsa.spazioParolaParola.Documento;
import org.aitools.programd.server.core.lsa.util.DatabasePP;
import org.aitools.programd.server.core.lsa.util.StopWord;


/**
 * Questa classe permette di effettuare il foldingIn di una particolare microteoria 
 * gi� scritta attraverso il database Parola Parola 
 * (per modificare il db lsa.util.Database).
 * 
 * @author Mario Scriminaci
 * @version 0.1
 */

public class FoldingInPP {
	
	private DatabasePP databasePP;
	private String mt;
	private String directory;
	private String cycConf="conf/cyc/cyc.properties";
	private String stopwordPath;
	
	private Properties prop;
	
	private int dim;
	
	private DenseMatrix matrix;
	
	private Set<String> stopwords;
	
	/**
	 * Il costruttore accetta in ingresso il nome della microteoria e il valore di 
	 * sigma.
	 * 
	 * @param String mtName il nome della microteoria
	 * @param int dim le dimensioni di sigma
	 */
	public FoldingInPP(DatabasePP databasePP, String mtName,int dim, String stopwordPath){
		this.databasePP = databasePP;
		this.stopwordPath = stopwordPath;
		mt=mtName;
		this.dim=dim;		
		stopwords=null;
		prop=new Properties();
		try{
			prop.load(new FileInputStream(cycConf));
		}
		catch(IOException e){
			System.out.println(e.getMessage());
		}
		
		directory=prop.getProperty("cyc.document.directory")+"/"+mt;
		
		int rows=0;
		File dirBase = new File(directory);		
		for (File dir : dirBase.listFiles()) {
			rows += dir.listFiles().length;
		}
		
		matrix=new DenseMatrix(rows,2*dim);
		
		this.elaborate();		
	}
	
	/*
	 * Il metotodo procede all'elaborazione. 
	 */
	private void elaborate(){
		
		try {
			stopwords = StopWord.createStopWordTable(stopwordPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		File dirBase = new File(directory);
		int numeroClassi = dirBase.listFiles().length;
		
				
		int classid = 0;
		int countdoc=0;
		System.out.println("Starting...");
		for (int i=0;i<dirBase.listFiles().length;i++) {
			File dir=dirBase.listFiles()[i];
			File[] files = dir.listFiles();
			int numfiles = files.length;
			System.out.print(dir.getName() + "(" + classid + "/"
					+ numeroClassi + "/" + numfiles + ")");
			for (int j=0;j<files.length;j++) {
				File file=files[j];
				Documento doc = new Documento(file.getAbsolutePath(), stopwords,dim);
				double[] vettoreDocumento = doc.getCoding(databasePP);
				
				for(int k=0;k<matrix.numColumns();k++){
					matrix.set(countdoc,k,vettoreDocumento[k]);
				}
				System.out.print("-");
				countdoc++;
			}
			System.out.println(" 100 %");
			classid++;
		}	
		
	}
	
	/**
	 * Il metodo ritorna una DenseMatrix nella quale ogni riga i-esima rappresenta la 
	 * codifica del documento i-esimo.
	 * 
	 * @return DenseMatrix la matrice delle codifiche dei documenti
	 */
	public DenseMatrix getMatrix(){
		return matrix;
	}

}
