package org.aitools.programd.server.core.cyc;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;

import org.opencyc.cycobject.CycFort;
import org.opencyc.cycobject.CycList;

import org.aitools.programd.server.core.cyc.util.CycRelation;
import org.aitools.programd.server.core.cyc.util.HashList;
import org.aitools.programd.server.core.cyc.util.LinkMatrix;
import org.aitools.programd.server.core.cyc.util.WrapperCycAccess;




/**
 * Questa classe permette di creare le matrici rappresentanti i grafi di connessione
 * delle costanti appartenenti a una microteoria
 * 
 * @author Mario Scriminaci
 * @version 0.1
 */
public class CreateMatrix {
	
	private HashList<String,String> allConceptUsedName;
	
	private HashList<String, CycFort> concepts;
	
	private HashList<String, ArrayList<CycRelation>> allConceptsRelation;
	
	private ArrayList<HashList<String, CycFort>> outOfMtConcepts;
	
	private HashList<String, CycFort> predicate;
	
	private HashList<String, LinkMatrix> graphs;
	
	private WrapperCycAccess cycAccess;
	
	private String mt;
	private String mtVocabulary;
	private String hostName;
	private int portNumber;
	
	private int passi=1;
	private int matrixDim;
	private int maxElevate=10;
	
	private Properties propetries;
	private String cycProperties="conf/cyc/cyc.properties";
	
	
	/**
	 * Costruttore, serve inserire la microteoria di cui si vogliono creare le matrici
	 * 
	 * @param String mt deve avere la corrispettiva microteoria Vocabulary
	 */
	public CreateMatrix(String mt)
	{
		try{
			propetries=new Properties();
			propetries.load(new FileInputStream(cycProperties));
		}
		catch(IOException e){
			System.out.println(e.getMessage());
		}
		hostName=propetries.getProperty("cyc.access.hostname");
		portNumber=Integer.parseInt(propetries.getProperty("cyc.access.portnumber"));
		
		
		this.mt=mt;
		
		this.mtVocabulary=mt.substring(0,mt.length()-2)+"VocabularyMt";
		
				
		this.cycAccess=new WrapperCycAccess("BaseKB", hostName, portNumber);		
		
		this.allConceptUsedName=new HashList<String,String>();
		
		this.concepts=new HashList<String,CycFort>();
		
		
		this.allConceptsRelation=new HashList<String, ArrayList<CycRelation>>();
		
		this.predicate=new HashList<String,CycFort>();
		
		this.outOfMtConcepts=new ArrayList<HashList<String, CycFort>>();
		
		this.graphs=new HashList<String, LinkMatrix>();		
		
		//prendo i concetti della microteoria
		this.cycAccess.setMicrotheory(mtVocabulary);
		CycList costants=cycAccess.getCostants();
		this.concepts=cycAccess.convertCycListToHashList(costants);
		
		this.sortConcepts();
		
		this.cycAccess.setMicrotheory(this.mt);
	}
	
	/**
	 * il metodo ritorna l'HashList con tutte le matrici create.
	 * 
	 * @return HashList<String, LinkMatrix> graphs
	 */
	public HashList<String, LinkMatrix> getGraphs()
	{
		return this.graphs;
	}
	
	
	/**
	 * il metodo ritorna un array di String contenente l'elenco ordinato
	 * delle costanti della microteoria cos� come vengono create le matrici
	 * 
	 * @return
	 */
	public String[] getConcepts(){
		
		Object[] a=concepts.getArrayListE1().toArray();
		String[] arrayString=new String[a.length];
		for(int i=0;i<a.length;i++)
			arrayString[i]=(String)a[i];
		
		return arrayString;		
	}
	
	/**
	 * Il metodo restituisce, dopo avere chiamato il metodo build, una matrice
	 * sparsa in cui sono presenti le relazioni tra le varie costanti della microteoria
	 * dove il singolo elemento della matrice (i,j) � calcolato come 1/n dove n rappresenta
	 * il numero di passi del persorso minimo tra i due concetti
	 * 
	 * @return {@link FlexCompRowMatrix}
	 * @see FlexCompRowMatrix
	 */
	public FlexCompRowMatrix getRelationMatrix(){
		
		int numbOfConcepts=concepts.size();
		
		FlexCompRowMatrix matrix=new FlexCompRowMatrix(numbOfConcepts,numbOfConcepts);
		
		FlexCompRowMatrix[] arrayMatrix=new FlexCompRowMatrix[maxElevate];
		
		LinkMatrix hold=new LinkMatrix("",matrixDim);
		
		//sommo tutte le matrici
		for(int i=0;i<graphs.size();i++){
			hold.add(graphs.get(i));
		}
		LinkMatrix holdN_1=new LinkMatrix(hold);
		
		arrayMatrix[0]=hold.getMatrix(numbOfConcepts,numbOfConcepts);
		
		for(int i=1;i<arrayMatrix.length;i++){			
			LinkMatrix hold2=holdN_1.mult(holdN_1,hold);
			
			holdN_1=new LinkMatrix(hold2);
			
			arrayMatrix[i]=hold2.getMatrix(numbOfConcepts,numbOfConcepts);
		}
		
		for(int i=0;i<numbOfConcepts;i++){
			for(int j=0;j<numbOfConcepts;j++){
				double value=0;
				for(int n=1;n<=arrayMatrix.length;n++){
					if(value==0&&arrayMatrix[n-1].get(i,j)>0){
						value=n;						
					}					
				}
				matrix.set(i,j,value);
			}
		}
		
		return matrix;
		
	}
	
	/**
	 * Costruisce le matrici
	 */
	public void build()
	{
		
		this.findConstantAndPredicate(concepts);//fine primo passo
		
		//successivi passi
		for(int passo=1; passo<passi; passo++)
		{
			this.findConstantAndPredicate(outOfMtConcepts.get(passo-1));			
		}
		//ora in allConceptsRelation ho tutte le relazioni per ogni concetto
		//e in predicate tutti i predicati
		
		//creo una lista con tutti i nomi dei concetti usati
		ArrayList<String> list1=concepts.getArrayListE1();
		for(String a:list1)
			allConceptUsedName.add(a,a);
		
		//le dimensioni delle matrici
		matrixDim=concepts.size();
				
		for(int i=0;i<passi;i++)
		{
			matrixDim+=outOfMtConcepts.get(i).size();
			ArrayList<String> list2=outOfMtConcepts.get(i).getArrayListE1();
			for(String a:list2)
				allConceptUsedName.add(a,a);
		}
		
		System.err.println("Dimensione delle matrici: "+matrixDim);
		
		
		//creo le matrici di relazioni
		for(int i=0;i<predicate.size();i++)
		{
			String predicateName=predicate.get(i).toString();
			if(!(predicateName.equals("definingMt")||predicateName.equals("comment")))
			{
				LinkMatrix matrix=new LinkMatrix(predicateName,matrixDim);
			
				graphs.add(predicateName,matrix);
			}
		}
		
		System.out.println("Sono state create "+predicate.size()+" matrici");
		
		//per i concetti della microteoria sistemo le relazioni nelle matrici
		
		this.fillUpMatrix(allConceptUsedName);
	}
	
	
	/*
	 * Metodo privato che permette di trovare nuove costanti rispetto a una HashList
	 * di costanti gi� note
	 * 
	 *  @param HashList<String, CycFort> concepts le costanti gi� note
	 */
	private void findConstantAndPredicate(HashList<String, CycFort> concepts)
	{
		HashList<String, CycFort> outConst=new HashList<String, CycFort>();
		
		for(int conceptsIndex=0; conceptsIndex<concepts.size(); conceptsIndex++)
		{
			
			CycList list=cycAccess.getLinks(concepts.get(conceptsIndex));
			
			ArrayList<CycRelation> relation=new ArrayList<CycRelation>();
			
			for(int cycListIndex=0; cycListIndex<list.size(); cycListIndex++)
			{
				String assertion=list.get(cycListIndex).toString();
				
				CycRelation rel=new CycRelation(assertion,cycAccess);
				relation.add(rel);
				
				String predicateName=rel.getPredicate().toString();
				
				predicate.add(predicateName, rel.getPredicate());
				
				//inserimento delle costanti nuove della microteoria
				//per la relazione in corso
				for(int relationConstantIndex=0; relationConstantIndex<rel.getNumberOfConstants(); relationConstantIndex++)
				{
					CycFort fort=rel.getConstant(relationConstantIndex);
					if(fort!=null&&isANewConstant(fort.toString()))
					{
						outConst.add(fort.toString(),fort);
					}
					
				}				
			}
			String conceptName=concepts.get(conceptsIndex).toString();
			allConceptsRelation.add(conceptName,relation);
		}		
		outOfMtConcepts.add(outConst);
	}
	
	
	/*
	 * Metodo privato che riempe le matrici secondo l'ordine presente nell'HashList 
	 * in ingresso
	 * 
	 * @param HashList<String,String> conceptsNames ogni coppia deve essere del tipo
	 * <NomeConcetto,NomeConcetto>
	 */
	private void fillUpMatrix(HashList<String,String> conceptsNames)
	{
		for(int i=0;i<allConceptsRelation.size();i++)
		{
			String conceptsName=conceptsNames.get(i);
			ArrayList<CycRelation> relation=allConceptsRelation.get(conceptsName);
			for(int j=0;j<relation.size();j++)
			{
				String predicateName=relation.get(j).getPredicate().toString();
				if(!(predicateName.equals("definingMt")||predicateName.equals("comment")))
				{
					for(int k=0;k<relation.get(j).getNumberOfConstants();k++)
					{
						String constRelation=relation.get(j).getConstant(k).toString();
						int indexConstRelation=conceptsNames.getIndexOf(constRelation);
						graphs.get(predicateName).setElement(i,indexConstRelation,1);
						graphs.get(predicateName).setElement(indexConstRelation,i,1);						
					}
				}					
			}		
		}
	}
	
	
	/*
	 * Metodo privato che permette di scoprire se una costante � gi� stata trovata 
	 * o meno
	 * 
	 * @param String constant il nome della costante
	 * @result booleano vero se � una nuova costante falso altrimenti
	 */
	private boolean isANewConstant(String constant)
	{
		boolean condition=true;
		CycFort fortVerifica=concepts.get(constant);
		if(fortVerifica==null)
		{
			for(int i=0;i<outOfMtConcepts.size();i++)
			{
				fortVerifica=outOfMtConcepts.get(i).get(constant);
				if(fortVerifica!=null)
					condition=false;
			}
		}
		else condition=false;	
		
		return condition;
	}
	
	/*
	 * il metodo serve per ordinare in ordine alfabetico (secondo il codice 
	 * ASCII) i concetti della microteoria in modo di creare le matrici di
	 * collegamenti in modo ordinato
	 * 
	 */
	private void sortConcepts(){
		
		String[] arrayString=getConcepts();
		
		System.out.println(arrayString.length);
		
		Arrays.sort(arrayString);
		
		HashList<String, CycFort> holdConcepts=new HashList<String, CycFort>();
		
		
		
		for(int i=0;i<arrayString.length;i++){
			
			holdConcepts.add(arrayString[i],concepts.get(arrayString[i]));
		}
		
		concepts=holdConcepts;		
	}
	
}
