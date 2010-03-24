package org.aitools.programd.server.core.cyc.util;

import java.net.UnknownHostException;
import java.util.StringTokenizer;

import java.io.IOException;
import java.io.FileWriter;


import java.util.ArrayList;

import org.opencyc.cycobject.CycFort;
import org.opencyc.cycobject.CycList;
import org.opencyc.cycobject.CycVariable;

import org.opencyc.api.CycConnection;
import org.opencyc.api.CycAccess;
import org.opencyc.api.CycApiException;



/**
 * Questa classe � un semplice bridge verso cyc; permette di estrarre costanti
 * di una microteoria, di estrarre il commento di una costante e di compiare in
 * un file di output una CycList. Di default la microteoria di riferimento � la
 * BaseKB; tuttavia � possibile modificarla attraverso il metodo setMicrotheory
 * che accetta in ingresso il nome della microteoria.
 * 
 * @author Mario Scriminaci
 * @version 0.1
 */
public class WrapperCycAccess {

	private CycAccess cycAccess;

	private CycFort microtheory;

	private String mt;
	
	/**
	 * Costruttore vuoto che provvede a creare l'oggetto CycAccess
	 * e pone BaseKB come micreoteoria di riferimento
	 */
	public WrapperCycAccess() {

		this("BaseKB",CycConnection.DEFAULT_HOSTNAME, CycConnection.DEFAULT_BASE_PORT);			
	}
	
	/**
	 * Costruttore sovraccarico che permette di settare la 
	 * microteoria di riferimento
	 * 
	 * @param String mt la microteoria scelta
	 */
	public WrapperCycAccess(String mt, String hostName, int portNumber) {

		try {
			System.out.println("Connessione a Cyc in corso");
			cycAccess = new CycAccess(hostName, portNumber,
			         CycConnection.DEFAULT_COMMUNICATION_MODE,
			         CycAccess.DEFAULT_CONNECTION);
			System.out.println("Connessione Avvenuta");

		} catch (UnknownHostException e) {
			System.out.println(e.getMessage());
		}
		 catch (IOException e) {
				System.out.println(e.getMessage());
		}
		 catch (CycApiException e) {
				System.out.println(e.getMessage());
		}
		this.setMicrotheory(mt);
	}
	
	

	/**
	 * Il metodo ricerca le costanti definite nella microteoria scelta (per
	 * settarla usare setMicrotheory()). Ricordarsi che avere ci� bisogna
	 * settare la corrispendente microteoria Vocabulary della microteoria scelta.
	 * 
	 * @return CycList una lista contenente le costanti
	 */
	@SuppressWarnings("deprecation")
	public CycList getCostants() {
		//((#$definingMt ?X #$MyVocabularyMt)

		CycList risposta = new CycList();

		try {
			CycFort basemt = cycAccess.getKnownConstantByName("BaseKB");

			CycVariable x = new CycVariable("?X");		

			CycList gaf = cycAccess.makeCycList("(#$definingMt ?X #$" + this.getMicrotheory() + ")");

			risposta = cycAccess.askWithVariable(gaf, x, basemt);

		} catch (CycApiException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

		return risposta;

	}
		
	/**
	 * Questo metodo permette di convertire una CycList di CycFort in un
	 * ArrayList di CycFort, di pi� facile accesso e utilizzo.
	 * Attenzione, bisogna essere certi che la CycList contenga realmente
	 * CycFort, altrimenti gli elementi non verranno aggiunti
	 * 
	 * @param CycList list una CycList contenete CycFort
	 * @return ArrayList<CycFort> 
	 */
	public ArrayList<CycFort> convertCycListToArrayList(CycList list) {
		
		StringTokenizer tokens=new StringTokenizer(list.toString(),"#$()\n ");
		
		ArrayList<CycFort> array=new ArrayList<CycFort>();
		
		try {
			while(tokens.hasMoreTokens())
			{
				
				CycFort c=cycAccess.getConstantByName(tokens.nextToken());
				if(c!=null)
					array.add(c);
				
			} 
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
		}
		catch(CycApiException e){

			System.out.println(e.getMessage());
		}
		
		return array;
	}
	
	
	/**
	 * Questo metodo permette di convertire una CycList di CycFort in un
	 * HashMap<String,CycFort>, di pi� facile accesso e utilizzo.
	 * Attenzione, bisogna essere certi che la CycList contenga realmente
	 * CycFort, altrimenti gli elementi non verranno aggiunti
	 * 
	 * @param CycList list una CycList contenete CycFort
	 * @return HashMap<String,CycFort>
	 */
	public HashList<String, CycFort> convertCycListToHashList(CycList list)
	{
		ArrayList<CycFort> arrayList=this.convertCycListToArrayList(list);
		
		HashList<String, CycFort> hashList=new HashList<String, CycFort>();
		
		for(CycFort c:arrayList)
		{
			hashList.add(c.toString(),c);
		}
		
		return hashList;
		
	}
	
	/**
	 * Il metodo permette di ricavare il commento di una costante nella
	 * microteoria selezionata
	 * 
	 * @param CycFort
	 *            concept riceve in ingresso un CycFort
	 * @return String stringa contenente il commento della costante, se non c'�
	 *         commento ritorna no comment
	 * @see org.opencyc.cycobject.CycFort
	 */
	public String getComment(CycFort concept) {

		try {
			String comment = cycAccess.getComment(concept, this.microtheory);
			return (comment);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch(CycApiException e){

			System.out.println(e.getMessage());
		}
		return ("no comment");
	}

	/**
	 * Il metodo permette di ricavare il commento di una costante nella
	 * microteoria selezionata
	 * 
	 * @param String
	 *            concept riceve in ingresso una String contenente il nome della
	 *            costante
	 * @return String stringa contenente il commento della costante, se non c'�
	 *         commento ritorna no comment
	 */
	public String getComment(String concept) {

		try {
			CycFort cost = cycAccess.getKnownConstantByName(concept);

			String comment = this.getComment(cost);

			return (comment);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} 
		catch(CycApiException e){

			System.out.println(e.getMessage());
		}
		return ("no comment");
	}
	
	
	/**
	 * Questo metodo interroga Cyc su un determinato concetto all'interno della
	 * microteoria selezionata 
	 * 
	 * @param CycFort concept il concetto di cui vogliamo le asserzioni
	 * @return CycList contenente le asserzioni riferite al concetto in input
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	public CycList getLinks(CycFort concept){
		
		CycList list=new CycList();
		
		String var[]={"?X","?Y","?Z","?W","?Q"};
		CycVariable[] cycVar=new CycVariable[var.length];
		for(int i=0;i<var.length;i++)
		{
			cycVar[i]=new CycVariable(var[i]);
		}
		
		try {
			for(int k=1;k<var.length;k++)
			{
				for(int i=0;i<=k;i++)
				{
					String sGaf="(";
					CycList varList=new CycList();
					for(int j=0;j<=k;j++)
					{
						if(j==i)
						{
							sGaf+=" #$"+concept.toString();
						}
						else
						{
							sGaf+=" "+var[j];
							varList.add(cycVar[j]);
						}
					}
					sGaf+=")";
					CycList gaf = cycAccess.makeCycList(sGaf);
					list.add(cycAccess.askWithVariables(varList,gaf,microtheory));
				}				
			}	
		} 
		catch (IOException e) {
			System.out.println(e.getMessage());
		}
		catch(CycApiException e){

			System.out.println(e.getMessage());
		}
		
		return list;
	}
	
	
	/**
	 * Il metodo ricerca una costante attraverso il nome
	 * 
	 * @param String constant il nome della costante
	 * @return CycFort la costante, ritorna null se non trova niente
	 */
	public CycFort getConstantByName(String constant)
	{
		CycFort fort=null;
		try
		{
			fort=cycAccess.getConstantByName(constant);
		}
		catch (UnknownHostException e)
		{
			System.out.println(e.getMessage());
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
		catch(CycApiException e){

			System.out.println(e.getMessage());
		}
		return fort;
	}
	
	/**
	 * Il metodo verifica se un CycFort � un predicato
	 * 
	 * @param constant
	 * @return boolean true se la costante � un predicato, false altrimenti
	 */
	public boolean isPredicate(CycFort fort)
	{
		boolean condition=false;
		try
		{
			if(fort!=null)
			condition=cycAccess.isPredicate(fort);
		}
		catch (UnknownHostException e)
		{
			System.out.println(e.getMessage());
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
		}

		catch(CycApiException e){

			System.out.println(e.getMessage());
		}
		return condition;
	}
	

	/**
	 * Il metodo consente di scrivere in forma testuale una CycList
	 * 
	 * @param risposta
	 * @param pathOutput
	 */
	public void writeAnswer(CycList risposta, String pathOutput) {

		StringTokenizer token = new StringTokenizer(risposta.toString()," \"().");

		try {

			FileWriter fileout = new FileWriter(pathOutput);
			while (token.hasMoreTokens()) {
				fileout.write(token.nextToken());
				fileout.write("\n");
			}
			fileout.flush();
			fileout.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

	}

	/**
	 * Ritorna la rappresentazione in Stringa della microteoria scelta
	 * 
	 * @return String
	 */
	public String getMicrotheory() {
		return this.mt;
	}

	/**
	 * Setta la microteoria
	 * 
	 * @param String microtheory
	 */
	public void setMicrotheory(String microtheory) {
		this.mt = microtheory;
		try {
			this.microtheory = cycAccess.getKnownConstantByName(microtheory);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch(CycApiException e){
			System.out.println(e.getMessage());
		}
	}

}