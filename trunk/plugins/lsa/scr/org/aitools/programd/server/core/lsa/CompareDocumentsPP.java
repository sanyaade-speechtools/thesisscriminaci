package org.aitools.programd.server.core.lsa;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileWriter;

import java.util.Properties;

import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;

import org.aitools.programd.server.core.lsa.util.HashList;

import org.aitools.programd.server.core.lsa.util.DatabasePP;
import org.aitools.programd.server.core.lsa.util.DatabasePD;
import org.aitools.programd.server.core.lsa.util.OperazioneArray;

/**
 * La classe permette, dato un database che contiene i documenti mappati nello
 * spazio parola parola, di comparare gli stessi documenti fra loro.
 * 
 * @author Mario Scriminaci
 * @version 0.1
 */
public class CompareDocumentsPP {
	
	private double competence;	
	private DatabasePP databasePP;
	private DenseMatrix vMatrixPP;
	private DenseMatrix vMatrixPPprod;
	
	private String[] concepts;
	
	private HashList<String,int[]> idConcepts;
	
	private int[] documentIdsPP;
			
	private int nsigma;
	
	private int fristIndex;
	
	
	/**
	 * Il costruttore vuoto setta la soglia di comparazione a 0.5 e richiama
	 * il cotruttore CompareDocumentsPP(double competence)
	 *
	 */
	public CompareDocumentsPP(DatabasePP databasePP){
		this(databasePP, 0.5);
	}
	
	/**
	 * Il cotruttore accetta in ingresso la soglia di comparazione voluta.
	 * 
	 * @param double competence la soglia voluta
	 */
	public CompareDocumentsPP(DatabasePP databasePP, double competence){	
		
		this.competence=competence;
		this.databasePP = databasePP;
		documentIdsPP=databasePP.getDocumentIds();
		
		fristIndex=documentIdsPP[0];
		
		nsigma=databasePP.getCodificaDocumento(documentIdsPP[0]).getCoding(databasePP).length;
		
		vMatrixPP=new DenseMatrix(documentIdsPP.length,nsigma);
		
		for(int i=0;i<documentIdsPP.length;i++){
			
			double[] cod=databasePP.getCodificaDocumento(documentIdsPP[i]).getCoding(databasePP);
			
			for(int j=0;j<cod.length;j++){
				vMatrixPP.add(i,j,cod[j]);
			}
		}
		
		vMatrixPPprod=product(vMatrixPP);	
		
		idConcepts=new HashList<String,int[]>();
		
		for(int i=0;i<concepts.length;i++){
			int[] index1=databasePP.getIdsOfConcept(this.concepts[i]);
			
			idConcepts.add(this.concepts[i],index1);
		}
		
		concepts=databasePP.getConcepts();	
	}
	
	
	/**
	 * Il costruttore invece che leggere dal database le codifiche dei documenti,
	 * riceve in ingresso la matrice gi� pronta e il nome della microteoria
	 * a cui essa � associata
	 * 
	 * @param competence
	 * @param matrix
	 */
	public CompareDocumentsPP(double competence, DenseMatrix matrix, String mtName){
		
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
		
		this.competence=competence;
		
		vMatrixPP=new DenseMatrix(matrix);
		
		nsigma=matrix.numColumns();
		
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
		
		vMatrixPPprod=product(vMatrixPP);		
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
							double el=this.vMatrixPPprod.get(index1[ind1]-fristIndex,index2[ind2]-fristIndex);
							
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
			
			System.out.println(index1.length+" "+this.concepts[i]);
			
			for(int j=0;j<this.concepts.length;j++){
				if(j!=i){
					String name=this.concepts[j];
					double max=0;
					
					int[] index2=idConcepts.get(name);
					
					for(int ind1=0;ind1<index1.length;ind1++){
						for(int ind2=0;ind2<index2.length;ind2++){
							double el=0;
							try{
							el=this.vMatrixPPprod.get(index1[ind1]-fristIndex,index2[ind2]-fristIndex);
							}
							catch(IllegalArgumentException e){
								System.out.println(index1[ind1]+" "+index2[ind2]);
								System.exit(0);
							}
							
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
	public DenseMatrix getProductPP()
	{
		return vMatrixPPprod;
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
	 * @return FlexCompRowMatrix la matrice
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

	/** 
	 * @param double competence
	 */
	public void setCompetence(double competence){
		this.competence=competence;
	}

	/*
	 * Il metodo privato permette di effettuare il prodotto di una DenseMatrix
	 * per la sua trasposta
	 * 
	 * @param m
	 * @return
	 */
	private DenseMatrix product(DenseMatrix m) {
		
		DenseMatrix product = new DenseMatrix(m.numRows(), m.numRows());
		
		int sigma=m.numColumns()/2;
		
		for(int i=0;i<product.numRows();i++){
			double[] vect1=new double[m.numColumns()];
			
			for(int k=0;k<vect1.length;k++)
				vect1[k]=m.get(i,k);
						
			for(int j=0;j<product.numRows();j++){
				double[] vect2=new double[m.numColumns()];
				
				for(int k=0;k<vect2.length;k++)
					vect2[k]=m.get(j,k);
								
				double value=OperazioneArray.cosenquadro(vect1,vect2,sigma);
				
				product.set(i,j,value);
				
			}
		}


		
		return product;
	}
}
