package org.aitools.programd.server.core.lsa;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileWriter;


import java.util.Properties;

import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;

import org.aitools.programd.server.core.lsa.util.DatabasePD;

import org.aitools.programd.server.core.lsa.util.HashList;

/**
 * La classe permette, dato un database che contiene i documenti mappati nello
 * spazio parola documento, di comparare gli stessi documenti fra loro.
 * 
 * @author Mario Scriminaci
 * @version 0.1
 */
public class CompareDocumentsPD {	
	
	private double competence;
	private DatabasePD databasePD;
	private DenseMatrix vMatrixPD;
	private DenseMatrix vMatrixPDprod;
	
	private HashList<String,int[]> idConcepts;
	
	private String[] concepts;
	
	private int[] documentIdsPD;
	
	private int fristIndex;
		
	private int nsigma;
	
	/**
	 * Il costruttore vuoto setta la soglia di comparazione a 0.5 e richiama
	 * il cotruttore CompareDocumentsPD(double competence)
	 *
	 */
	public CompareDocumentsPD(DatabasePD databasePD){
		this(databasePD, 0.5);
	}
	
	/**
	 * Il cotruttore accetta in ingresso la soglia di comparazione voluta.
	 * 
	 * @param double competence la soglia voluta
	 */
	public CompareDocumentsPD(DatabasePD databasePD, double competence){
		
		this.competence=competence;
		this.databasePD = databasePD;
		documentIdsPD=databasePD.getDocumentIds();
		
		fristIndex=documentIdsPD[0];
		
		nsigma=databasePD.getCodificaDocumento(documentIdsPD[0]).getCoding(databasePD).length;
		
		vMatrixPD=new DenseMatrix(documentIdsPD.length,nsigma);
		
		for(int i=0;i<documentIdsPD.length;i++){
			
			double[] cod=databasePD.getCodificaDocumento(documentIdsPD[i]).getCoding(databasePD);
			
			for(int j=0;j<cod.length;j++){
				vMatrixPD.add(i,j,cod[j]);
			}
		}
		
		vMatrixPDprod=product(vMatrixPD);	
		
		concepts=databasePD.getConcepts();
				
		idConcepts=new HashList<String,int[]>();
		
		for(int i=0;i<concepts.length;i++){
			int[] index1=databasePD.getIdsOfConcept(this.concepts[i]);
			
			idConcepts.add(this.concepts[i],index1);
		}		
	}
	
	/**
	 * Il costruttore invece che leggere dal database le codifiche dei documenti,
	 * riceve in ingresso la matrice gi� pronta e il nome della microteoria
	 * a cui essa � associata
	 * 
	 * @param competence
	 * @param matrix
	 */
	public CompareDocumentsPD(double competence, DenseMatrix matrix, String mtName){
		
		String cycConf="conf/cyc/cyc.properties";
		
		Properties prop=new Properties();
		try{
			prop.load(new FileInputStream(cycConf));
		}
		catch(IOException e){
			System.out.println(e.getMessage());
		}
		
		String directory=prop.getProperty("cyc.document.directory")+"/"+mtName;
		
		File dirMt=new File(directory);
		
		concepts=dirMt.list();
		
		idConcepts=new HashList<String,int[]>();
		int countdoc=0;
		
		for(int i=0;i<concepts.length;i++){
			File dirC=new File(dirMt.getAbsolutePath()+"/"+concepts[i]);
			int indeces[]=new int[dirC.list().length];
			
			for(int j=0;j<indeces.length;j++){
				indeces[j]=countdoc;
				countdoc++;
			}
			idConcepts.add(concepts[i],indeces);
		}
		
		System.out.println("contatore "+countdoc);
		
		this.competence=competence;
		
		vMatrixPD=new DenseMatrix(matrix);
		
		nsigma=matrix.numColumns();
		
		vMatrixPDprod=product(vMatrixPD);		
		
		fristIndex=0;
	}
	
	
	/**
	 * Il metodo ritorna una lista di costanti ognuna delle quali ha, a sua volta, 
	 * una lista contenente tutti i concetti che si sono risultati sotto la soglia
	 * settata effettuando il paragone tra i due centroidi
	 * 
	 * @return HashList<String,HashList<String,Double>> la lista con le relazioni
	 * @see HashList
	 */
	public HashList<String,HashList<String,Double>> generateRelationAvg(){
		
		HashList<String,HashList<String,Double>> relation = new HashList<String,HashList<String,Double>>();
		
		for(int i=0;i<this.concepts.length;i++){
			
			int[] index1=idConcepts.get(this.concepts[i]);
			HashList<String,Double> singrel=new HashList<String,Double>();
			
			for(int j=0;j<this.concepts.length;j++){
				if(j!=i){
					String name=this.concepts[j];

					double media=0;
					
					int[] index2=idConcepts.get(name);
					
					for(int ind1=0;ind1<index1.length;ind1++){
						for(int ind2=0;ind2<index2.length;ind2++){
							double el=this.vMatrixPDprod.get(index1[ind1]-fristIndex,index2[ind2]-fristIndex);
							
							media+=el;
						}
					}
				
					media=media/(index1.length*index2.length);	
					
					if(media>=this.competence){
						singrel.add(name,media);
					}				
				}
			}
			relation.add(this.concepts[i],singrel);			
		}		
		return relation;
	}
	
	
	/**
	 * Il metodo ritorna una lista di costanti ognuna delle quali ha, a sua volta, 
	 * una lista contenente tutti i concetti che si sono risultati sotto la soglia
	 * settata effettuando il paragone tra i due documenti pi� vicini
	 * 
	 * @return HashList<String,HashList<String,Double>> la lista con le relazioni
	 * @see HashList
	 */
	public HashList<String,HashList<String,Double>> generateRelationMax(){
		
		HashList<String,HashList<String,Double>> relation = new HashList<String,HashList<String,Double>>();
		
		for(int i=0;i<this.concepts.length;i++){
			
			int[] index1=idConcepts.get(this.concepts[i]);
			HashList<String,Double> singrel=new HashList<String,Double>();
			
			for(int j=0;j<this.concepts.length;j++){
				if(j!=i){
					String name=this.concepts[j];
					double max=0;
					
					int[] index2=idConcepts.get(name);
					
					for(int ind1=0;ind1<index1.length;ind1++){
						for(int ind2=0;ind2<index2.length;ind2++){
							
							double el=this.vMatrixPDprod.get(index1[ind1]-fristIndex,index2[ind2]-fristIndex);
							
							if(el>max)
								max=el;
						}
					}
										
					if(max>=this.competence){
						singrel.add(name,max);
					}				
				}
			}
			relation.add(this.concepts[i],singrel);			
		}		
		return relation;
	}
	
	/**
	 * Il metodo ritorna una DenseMatrix dei prodotti scalari fra tutti i documenti 
	 * 
	 * @return DenseMatrix
	 */
	public DenseMatrix getProductPD()
	{
		return vMatrixPDprod;
	}
	
	
	/**
	 * Il metodo permette di scrivere su file la DenseMatrix in ingresso;
	 * ATTENZIONE non in formato matrix market!
	 * 
	 * @param DenseMatrix la matrice in ingresso
	 * @param String path il percorso del file di output
	 */
	public void write(DenseMatrix mat,String path){
		
		try{
			FileWriter fout=new FileWriter(path);
			fout.write("%%MatrixMarket matrix array real general");
			fout.write(mat.toString());
			
			fout.flush();
			fout.close();
			
		}
		catch(IOException e){
			System.out.println(e.getMessage());
		}
	}
	
	
	
	/**
	 * Il metodo ritorna la matrice delle relazioni  filtrata dai numeri sotto
	 * la soglia e con ogni vettore riga scalato fra 0 e 1 scegliendo come prodotto
	 * scalare quello fra i vettori documento centroidi. 
	 * 
	 * @return CompRowMatrix la matrice
	 */
	public FlexCompRowMatrix getMatrixAvg(){
		
		FlexCompRowMatrix matrix=new FlexCompRowMatrix(concepts.length,concepts.length);
		
		HashList<String, String> conceptList=new HashList<String, String>();
		
		for(int i=0;i<concepts.length;i++)
			conceptList.add(concepts[i],concepts[i]);
		
		HashList<String,HashList<String,Double>> relation=this.generateRelationAvg();
		
		for(int i=0;i<relation.size();i++){
			
			HashList<String,Double> list=relation.get(i);
			
			double max=0;
			
			for(int j=0;j<list.size();j++){
				double el=list.get(j);
				if(el>max)
					max=el;
			}
			
			
			for(int j=0;j<list.size();j++){
				int k=conceptList.getIndexOf(list.getArrayListE1().get(j));
				double el=list.get(j);// /max;
				
				matrix.set(i,k,el);
			}
			
		}		
		
		return matrix;	
	}
	
	/** 
	 * @param double competence
	 */
	public void setCompetence(double competence){
		this.competence=competence;
	}
	
	/**
	 * Il metodo ritorna la matrice delle relazioni  filtrata dai numeri sotto
	 * la soglia e con ogni vettore riga scalato fra 0 e 1 scegliendo come prodotto
	 * scalare quello fra i vettori documento pi� vicini. 
	 * 
	 * @return CompRowMatrix la matrice
	 */
	public FlexCompRowMatrix getMatrixMax(){
		
		FlexCompRowMatrix matrix=new FlexCompRowMatrix(concepts.length,concepts.length);
		
		HashList<String, String> conceptList=new HashList<String, String>();
		
		for(int i=0;i<concepts.length;i++)
			conceptList.add(concepts[i],concepts[i]);
		
		HashList<String,HashList<String,Double>> relation=this.generateRelationMax();
		
		for(int i=0;i<relation.size();i++){
			
			HashList<String,Double> list=relation.get(i);
			
			double max=0;
			
			for(int j=0;j<list.size();j++){
				double el=list.get(j);
				if(el>max)
					max=el;
			}
			
			
			for(int j=0;j<list.size();j++){
				int k=conceptList.getIndexOf(list.getArrayListE1().get(j));
				double el=list.get(j);// /max;
				
				matrix.set(i,k,el);
			}
			
		}		
		
		return matrix;	
	}
	
	/*
	 * Il metodo privato permette di effettuare il prodotto di una DenseMatrix
	 * per la sua trasposta
	 * 
	 * @param m
	 * @return
	 */
	private DenseMatrix product(DenseMatrix m) {
		
		DenseMatrix mt = new DenseMatrix(m.numColumns(), m.numRows());
		m.transpose(mt);
		DenseMatrix product = new DenseMatrix(m.numRows(), m.numRows());
		m.mult(mt, product);
		return product;
	}	
}
