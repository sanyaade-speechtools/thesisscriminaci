package org.aitools.programd.server.core.cyc.util;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.opencyc.cycobject.CycFort;


/**
 * Questa classe � una rappresentazione dei concetti di una microteoria;
 * � costituita da una HashList formata da String e CycFort.
 * Tramite questa classe si possono trasmormare i concetti al fine
 * di ottenere dei nomi ricercabili in Wikipedia
 * 
 * @see Hashlist
 * 
 * @author Mario Scriminaci
 * @version 0.1
 */
public class MtConcepts {
	
	private HashList<String,CycFort> list;
	
	
	/**
	 * @param HashList<String,CycFort> list la lista dei concetti
	 */
	public MtConcepts(HashList<String,CycFort> list)
	{
		this.list=list;
	}
	
	/**
	 * Il metodo ritorna la HashList dei concetti
	 * 
	 * @return HashList<String,CycFort>
	 */
	public HashList<String,CycFort> getList()
	{
		return this.list;
	}
	
	/**
	 * Il metodo scompone i nomi dei concetti in modo da poterli ricercare in 
	 * Wikipedia
	 * 
	 * @return ArrayList<String[]> una lista che contiene per ogni concetto uno
	 * String Array di parole ricercabili in Wikipedia
	 */
	public ArrayList<String[]> getWikiList()
	{
		ArrayList<String[]> stringList=new ArrayList<String[]>();
		
		for(int i=0;i<list.size();i++)
		{
			String hold=list.get(i).toString();
			stringList.add(this.toWikiString(hold));			
		}
		
		return stringList;
	}
	
	
	/*
	 * Il metodo trasforma la stringa del concetto cyc (esempio 
	 * DegreeGrantingHigherEducationInstitution) in un array di singole 
	 * parole ricercabili in wikipedia (esempio String ret=
	 * {"Degree","Granting","Higher","Education","Institution"}; .
	 * 
	 * @param s
	 * @return
	 */
	private String[] toWikiString(String s)
	{
		StringTokenizer token=new StringTokenizer(s,"-");
		
		String toTokenizer="";
		
		while(token.hasMoreTokens())
		{			
			char[] c=token.nextToken().toCharArray();
			
			
			for(int i=0;i<c.length;i++)
			{
				if(Character.isLowerCase(c[i]))				
					toTokenizer+=c[i];				
				else
					toTokenizer+=" "+c[i];
			}			
		}
		if(toTokenizer.startsWith(" "))
			toTokenizer=toTokenizer.substring(1);
		
		StringTokenizer token2=new StringTokenizer(toTokenizer,"-");
		
		String[] stringArray=new String[token2.countTokens()];
		
		for(int i=0;i<stringArray.length;i++)
			stringArray[i]=token2.nextToken();
		
		return stringArray;					
	}

}
