package org.aitools.programd.server.core.cyc.util;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * TODO migliorare documentazione, poco chiara
 * 
 * Questa classe rappresenta una lista il cui accesso � garantito
 * in due modi, o tramite indice o tramite un altro elemento (arg0)
 * legato all'oggeto a cui vogliamo accedere (come una HashMap). 
 * Si vengono quindi a creare due liste parallele e dipendenti.
 * E' inoltre possibile ottenere un ArrayList della prima lista e rimuovere
 * un oggetto o tramite indice o tramite il primo elemento della prima lista.
 * 
 * @author Mario Scriminaci
 * @version 0.1.1
 */
public class HashList<E1,E2> {
	
	private ArrayList<E1> listE1;
	private ArrayList<E2> listE2;
	private HashMap<E1,Integer> map;
	
	/**
	 * Il costruttore vuoto costruisce la lista, lasciandola vuota.
	 */
	public HashList(){
		
		listE1=new ArrayList<E1>();
		listE2=new ArrayList<E2>();
		map=new HashMap<E1,Integer>();
	}
	
	
	public HashList(HashList<E1, E2> hlist){
		
		listE1=new ArrayList<E1>(hlist.listE1);
		listE2=new ArrayList<E2>(hlist.listE2);
		map=new HashMap<E1,Integer>(hlist.map);
		
	}
	
	/**
	 * Quando si aggiunge un elemento � necessario specificare 
	 * entrambi gli elementi.
	 * 
	 * @param E1 arg0
	 * @param E2 arg1
	 * @return
	 */
	public boolean add(E1 arg0,E2 arg1)
	{
		if(map.get(arg0)==null)
		{
			map.put(arg0,listE2.size());
			listE1.add(arg0);
			listE2.add(arg1);
			return true;
		}
		else
		{
			return false;
		}
		
	}	
	
	/**
	 * Il metodo da la possibilit� di accedere all'oggetto voluto tramite indice
	 * 
	 * @param int index l'indice dell'elemento voluto 
	 * @return E2 l'elemento voluto se esistente, altrimenti null
	 */
	public E2 get(int index)
	{
		if(index<listE2.size()&index>=0)
			return listE2.get(index);
		else
			return null;
	}
	
	/**
	 * Il metodo da la possibilit� di accedere all'oggetto voluto tramite 
	 * l'elemento collegato
	 * 
	 * @param E1 arg0 l'elemento collegato
	 * @return E2 l'elemento voluto se esistente, altrimenti null
	 */
	public E2 get(E1 arg0)
	{
		Object index=map.get(arg0);
		if(index!=null)
			return listE2.get((Integer)index);
		else 
			return null;
	}
	
	/**
	 * Il metodo ritorna l'indice corrispettivo all'elemento in ingresso
	 * 
	 * @param E1 arg
	 * @return int l'indice
	 */
	public int getIndexOf(E1 arg0)
	{
		int index=-1;
		for(int i=0;i<this.size();i++)
		{
			if(listE1.get(i).equals(arg0))
				index=i;
		}
		return index;
	}
	
	/**
	 * La lunghezza della lista
	 * 
	 * @return int size
	 */
	public int size()
	{
		return listE1.size();
	}

	/**
	 * E' possibile rimuovere un oggetto tramite indice
	 * 
	 * @param int index l'indice dell'oggetto da rimuovere
	 * @return boolean true se l'operazione � andata a buon termine.
	 */
	public boolean remove(int index)
	{
		if(index<listE1.size()&&index>=0)
		{
			map.remove(listE1.get(index));
			listE1.remove(index);
			listE2.remove(index);
			return true;
		}
		else
			return false;
	}
	
	/**
	 * E' possibile rimuovere un oggetto tramite l'elemento collegato
	 * 
	 * @param E1 arg0 l'elemento collegato all'oggetto da rimuovere
	 * @return boolean true se l'operazione � andata a buon termine.
	 */
	public boolean remove(E1 arg0)
	{
		Object index=map.get(arg0);
		if(index!=null)
			return this.remove(map.get(arg0));
		else 
			return false;		
	}
	
	/**
	 * Il metodo ritorna un ArrayList di tutti gli elementi colleganti
	 * 
	 * @return ArrayList<E1> una lista ordinata di tutti gli elementi
	 * colleganti
	 */
	public ArrayList<E1> getArrayListE1()
	{
		return listE1;
	}	
	
	/**
	 * TODO javadoc
	 * @param arg0
	 * @param arg1
	 * @return
	 */
	public boolean set(E1 arg0, E2 arg1){
		
		Object index=map.get(arg0);
		if(index!=null)
			return this.set((Integer)index, arg1);			
		else 
			return false;
		
	}
	
	/**
	 * TODO javadoc
	 * @param index
	 * @param arg1
	 * @return
	 */
	public boolean set(int index, E2 arg1){
		
		if(index<this.size()){
			listE2.set(index, arg1);
			return true;
		}
		else return false;
	}
	
}
