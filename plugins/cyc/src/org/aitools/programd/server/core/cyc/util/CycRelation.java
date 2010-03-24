package org.aitools.programd.server.core.cyc.util;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.opencyc.cycobject.CycFort;



/**
 * Questa classe � una rappresentazione di una relazione in cyc;
 * � composta da un predicato e da una serie di costanti
 * 
 * @author Mario Scriminaci
 * @version 0.1
 */
public class CycRelation {
	
	private CycFort predicate;
	
	private ArrayList<CycFort> constants;
	
	
	/**
	 * Il costruttore vuole una stringa rappresentante la relazione
	 * da scomporre e un WrapperCycAccess per la connessione a Cyc
	 * 
	 * @param String assertion
	 * @param WrapperCycAccess cycAccess
	 */
	public CycRelation(String assertion, WrapperCycAccess cycAccess)
	{		
		StringTokenizer tokenizer=new StringTokenizer(assertion,"()#$ ");
		
		predicate=cycAccess.getConstantByName(tokenizer.nextToken());
		
		constants=new ArrayList<CycFort>();
		
		if(!cycAccess.isPredicate(predicate))
		{
			System.err.println("Il primo membro della seguente asserzione "+assertion+
					" non � un predicato, verr� comunque creata la relazione");
			
		}
		
		//se la relazione � un commento evita di creare inutilmente costanti
		if(!predicate.toString().equals("comment"))
		{
			while(tokenizer.hasMoreTokens())
			{
				CycFort cons=cycAccess.getConstantByName(tokenizer.nextToken());
				if(cons!=null&&!cycAccess.isPredicate(cons))
				constants.add(cons);					
			}		
		}
	}
	
	/**
	 * Il metodo ritorna il numero di costanti della relazione
	 * 
	 * @return int il numero di costanti
	 */
	public int getNumberOfConstants()
	{
		return constants.size();
	}
	
	/**
	 * Il metodo ritorna un CycFort rappresentate il predicato della 
	 * relazione
	 * 
	 * @return CycFort predicato della relazione
	 */
	public CycFort getPredicate()
	{
		return predicate;
	}
	
	/**
	 * Il metodo ritorna un CycFort rappresentante la costante corrispettiva
	 * all'indice in input.
	 * 
	 * @param int index
	 * @return CycFort
	 */
	public CycFort getConstant(int index)
	{
		return constants.get(index);
	}
}
