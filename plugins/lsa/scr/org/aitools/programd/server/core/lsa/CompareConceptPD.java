package org.aitools.programd.server.core.lsa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import no.uib.cipr.matrix.DenseMatrix;

import org.aitools.programd.server.core.lsa.util.DatabasePD;
import org.aitools.programd.server.core.lsa.util.HashList;
import org.aitools.programd.server.core.lsa.util.OperazioneArray;
import org.aitools.programd.server.core.lsa.util.StringDouble;


public class CompareConceptPD implements CompareConcept {
	private double competence;
	
	private DatabasePD databasePD;
	
	private HashList<String,int[]> idConcepts;
	private HashList<String, double[]> codConcepts;
	
	private String[] concepts;
	
	private int numConcept;
		
	private int nsigma;
	
	private DenseMatrix vMatrix;
	private DenseMatrix vMatrixProd;
	
	public CompareConceptPD(String dbUrl, String dbName, String dbUsername, String dbPwd, String dbDriver, int nsigma){
		this(dbUrl, dbName, dbUsername, dbPwd, dbDriver, nsigma, 0.5);
	}
	
	public CompareConceptPD(String dbUrl, String dbName, String dbUsername, String dbPwd, String dbDriver, int nsigma, double competence){
		
		this.competence=competence;
		databasePD = new DatabasePD(dbUrl, dbName, dbUsername, dbPwd, dbDriver);
					
		this.nsigma=nsigma;
		
		concepts=databasePD.getConcepts();
		
		numConcept=concepts.length;		
		
		idConcepts=new HashList<String,int[]>();
		
		codConcepts=new HashList<String,double[]>();
		
		vMatrix=new DenseMatrix(numConcept,nsigma);
		
		for(int i=0;i<numConcept;i++){			
			
			int[] index1=databasePD.getIdsOfConcept(this.concepts[i]);
			
			idConcepts.add(this.concepts[i],index1);
			
			double cod[]=new double[nsigma];
			
			//mi calcolo il vettore pesato di tutti i documenti
			for(int j:index1){
				double hcod[]=databasePD.getCodificaDocumento(j).getCoding(databasePD);
				int relevance=databasePD.getDocumentRelevance(j);
				
				hcod=OperazioneArray.moltiplica_vettore_per_fattore(hcod, relevance);
				
				cod=OperazioneArray.somma_vettori(cod, hcod);				
			}		
			//normalizzo
			OperazioneArray.normalizza(cod);
			
			//aggiungo la codifica del concetto alla matrice
			for(int j=0;j<cod.length;j++){
				vMatrix.add(i,j,cod[j]);
			}			
			codConcepts.add(this.concepts[i], cod);			
		}		
		vMatrixProd=product(vMatrix);	
	}	
	
	/**
	 * TODO javadoc
	 * @param concept
	 * @return
	 */
	public List<StringDouble> getQueryRelated(String concept){
		
		return this.getQueryRelated(concept, this.competence);
	}
	
	/**
	 * TODO javadoc
	 * @param concept
	 * @param competence
	 * @return
	 */
	public List<StringDouble> getQueryRelated(String request, double competence){
		
		List<StringDouble> conceptualRelated=new ArrayList<StringDouble>();

		StringTokenizer tokenizer = new StringTokenizer(request," .,'?!\"'_-;:*+#");
		
		double[] cod = new double[nsigma];
		
		Arrays.fill(cod, 0.0);
		
		while(tokenizer.hasMoreElements()){			
			String token = tokenizer.nextToken();
			double[] word = databasePD.getCodificaParola(token);
			if(word!=null){
				cod = OperazioneArray.somma_vettori(cod, word);
			}			
		}
		OperazioneArray.normalizza(cod);
		for(int i=0; i<numConcept; i++){
			
			double[] concept = codConcepts.get(i);
			
			double relation=OperazioneArray.scalar(cod, concept);
			
			if(relation>competence){
				StringDouble sd = new StringDouble();
				sd.name=concepts[i];
				sd.relevance=relation;
				conceptualRelated.add(sd);
			}
		}		

		return conceptualRelated;
		
	}
	
	/**
	 * TODO javadoc
	 * 
	 * @return
	 */
	public String[] getConcepts() {
		return concepts;
	}

	public List<StringDouble> getRelated(String concept) {
		return getRelated(concept, competence);
	}

	public List<StringDouble> getRelated(String concept, double competence) {
		int index=codConcepts.getIndexOf(concept);

		if(index!=-1){

			return getRelated(index, competence);
		}
		else
			return null;
	}
	
	private List<StringDouble> getRelated(int index, double competence){
		
		List<StringDouble> conceptualRelated=new ArrayList<StringDouble>();

		for(int i=0; i<numConcept; i++){
			double relation=vMatrixProd.get(index, i);

			if(relation>competence){
				StringDouble sd = new StringDouble();
				sd.name=concepts[i];
				sd.relevance=relation;
				conceptualRelated.add(sd);
				
			}

		}		

		return conceptualRelated;
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
