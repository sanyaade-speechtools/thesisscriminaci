package org.aitools.programd.server.core.cyc;

import java.util.Vector;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.aitools.programd.util.FileManager;
//import org.alicebot.server.core.util.Support;


/**
 * CycStore is used to store the CycL formula executed by JAIMLpad and its corresponding
 * result as Strings. Truth values should be converted in TRUE and FALSE words.
 */

public class CycStore
{
	/** The real &quot;warehouse&quot; */
	private static Vector<CycPair> cycPairs = new Vector<CycPair>();
	
	/** The actual size of the CycStore */
	private static int size = 0;
	
	private static String LINE_SEPARATOR = System.getProperty("line.separator", "\n");
	
	
	/**
	 *	Add a couple to the CycStore, that is a formula and its corresponding result.
	 *
	 *	@param formula	the formula to add.
	 *	@param result	the result of the formula.
	 *
	 *	@return a boolean who keeps tracks of the result of the operation.
	 */
	public static boolean addElement(String formula, String result)
	{
		CycPair cyc = new CycPair(formula, result);
		if(cycPairs.add(cyc))
		{
			size++;
			return true;
		}
		else return false;
	}
	
	
	/**
	 * 	Get the element at the specified index in <code>CycPair</code> form.
	 *	
	 *	@param index	the index of the element that must be recovered.
	 *
	 *	@throws StoreOutOfBoundException	if index is negative or CycStore dimension
	 *										is lesser than index.
	 */
	public static CycPair getStoreAt(int index) throws StoreOutOfBoundException
	{
		if(index > -1 && size > index)
		{
			return cycPairs.get(index);
			
		}
		else throw new StoreOutOfBoundException("Index out of bound!");
	}
	
	
	/**
	 *	Get the formula at the specified index (without remove it).
	 *
	 *	@param index	the index of the formula that must be recovered.
	 *
	 *	@return the index-nt formula.
	 *
	 *	@throws StoreOutOfBoundException	if index is negative or CycStore dimension
	 *										is lesser than index.
	 */
	public static String getFormulaAt(int index) throws StoreOutOfBoundException
	{
		if(index > -1 && size > index)
		{
			return cycPairs.get(index).getFormula();
		}
		else throw new StoreOutOfBoundException("Index out of bound!");
	}
	
	
	/**
	 *	Get the result at the specified index (without remove it).
	 *
	 *	@param index	the index of the result that must be recovered.
	 *
	 *	@return the index-nt result.
	 *
	 *	@throws StoreOutOfBoundException	if index is negative or CycStore dimension
	 *										is lesser than index.
	 */
	public static String getResultAt(int index) throws StoreOutOfBoundException
	{
		if(index > -1 && size > index)
		{
			return cycPairs.get(index).getResult();
		}
		else throw new StoreOutOfBoundException("Index out of bound!");
	}
	
	
	/**
	 *	Take care: this metod removes the formula at the specified index TOGHETER
	 *	with its corresponding result.
	 *
	 *	@param index	the index of the formula that has been removed.
	 *
	 *	@return the index-nt formula.
	 *
	 *	@throws StoreOutOfBoundException	if index is negative or CycStore dimension
	 *										is lesser than index.
	 */
	public static String remFormulaAt(int index) throws StoreOutOfBoundException
	{
		if(index > -1 && size > index)
		{
			String formula = cycPairs.remove(index).getFormula();
			size--;
			return formula;
		}
		else throw new StoreOutOfBoundException("Index out of bound!");
	}
	
	
	/**
	 *	Take care: this metod removes the result at the specified index TOGHETER
	 *	with its corresponding formula.
	 *
	 *	@param index	the index of the result that has been removed.
	 *
	 *	@return	the index-nt result.
	 *
	 *	@throws StoreOutOfBoundException	if index is negative or CycStore dimension
	 *										is lesser than index.
	 */
	public static String remResultAt(int index) throws StoreOutOfBoundException
	{
		if(index > -1 && size > index)
		{
			String result = cycPairs.remove(index).getResult();
			size--;
			return result;
		}
		else throw new StoreOutOfBoundException("Index out of bound!");
	}
	
	
	/**
	 *	Remove the CycPair at the specified index.
	 *
	 *	@param index	the index of the CycPair that must be removed.
	 *
	 *	@return the element removed.
	 *
	 *	@throws StoreOutOfBoundException	if index is negative or CycStore dimension
	 *										is lesser than index.
	 */
	public static CycPair removeStoreAt(int index) throws StoreOutOfBoundException
	{
		if(index > -1 && size > index)
		{			
			size--;
			return cycPairs.remove(index);
		}
		else throw new StoreOutOfBoundException("Index out of bound!");
	}
	
	
	/**
	 *	Get the last result stored (without remove it).
	 *
	 *	@return the last result stored.
	 *
	 *	@throws StoreOutOfBoundException	if index is negative or CycStore dimension
	 *										is lesser than index.
	 */
	public static String getLastResult() throws StoreOutOfBoundException
	{
		if(size == 0)
		{
			throw new StoreOutOfBoundException("CycStore is empty!");
		}
		else 
		{
			return cycPairs.get(size - 1).getResult();
		}
	}
	
	
	/**
	 *	Get the last formula stored (without remove it).
	 *
	 *	@return the last formula stored.
	 *
	 *	@throws StoreOutOfBoundException	if index is negative or CycStore dimension
	 *										is lesser than index.
	 */
	public static String getLastFormula() throws StoreOutOfBoundException
	{
		if(size == 0)
		{
			throw new StoreOutOfBoundException("CycStore is empty!");
		}
		else 
		{
			return cycPairs.get(size - 1).getFormula();
		}
	}
	
	
	/**
	 *	Take care: this metod removes the last result TOGHETER
	 *	with its corresponding formula.
	 *
	 *	@return the last result stored.
	 *
	 *	@throws StoreOutOfBoundException	if index is negative or CycStore dimension
	 *										is lesser than index.
	 */
	public static String removeLastResult() throws StoreOutOfBoundException
	{
		if(size == 0)
		{
			throw new StoreOutOfBoundException("CycStore is empty!");
		}
		else 
		{
			return cycPairs.remove(--size).getResult();
		}
	}
	
	
	/**
	 *	Take care: this metod removes the last formula TOGHETER
	 *	with its corresponding result.
	 *
	 *	@return the last formula stored.
	 *
	 *	@throws StoreOutOfBoundException	if index is negative or CycStore dimension
	 *										is lesser than index.
	 */
	public static String removeLastFormula() throws StoreOutOfBoundException
	{
		if(size == 0)
		{
			throw new StoreOutOfBoundException("CycStore is empty!");
		}
		else 
		{
			return cycPairs.get(--size).getFormula();
		}
	}
	
	
	/**
	 *	Get the last CycPair stored (without remove it).
	 *
	 *	@return the last element stored.
	 *
	 *	@throws StoreOutOfBoundException	if index is negative or CycStore dimension
	 *										is lesser than index.
	 */
	public static CycPair getLast() throws StoreOutOfBoundException
	{
		if(size == 0)
		{
			throw new StoreOutOfBoundException("CycStore is empty!");
		}
		else 
		{
			return cycPairs.get(size - 1);
		}
	}
	
	
	/**
	 *	Remove the last CycPair stored.
	 *
	 *	@return the element removed.
	 *
	 *	@throws StoreOutOfBoundException	if index is negative or CycStore dimension
	 *										is lesser than index.
	 */
	public static CycPair removeLast() throws StoreOutOfBoundException
	{
		if(size == 0)
		{
			throw new StoreOutOfBoundException("CycStore is empty!");
		}
		else 
		{
			return cycPairs.remove(--size);
		}
	}
	
	/**
	 * 	Get the size of the CycStore.
	 *
	 *	@return the number of CycPairs stored
	 */
	public static int size()
	{
		return size;
	}
	
	/**
	 *	Serialize this object.
	 *
	 */
	 public static void serialize(String path) throws FileNotFoundException, IOException
	 {
	 	ObjectOutputStream outputStream = new ObjectOutputStream(
	 												new FileOutputStream(path));
	 												
	 	Vector<CycPair> saved = new Vector<CycPair>(cycPairs);
	 	outputStream.writeObject(saved);
	 	outputStream.close();
	 }
	 
	 @SuppressWarnings("unchecked")
	public static void deSerialize(String path) 
	 						throws FileNotFoundException, IOException, ClassNotFoundException
	 {
	 	ObjectInputStream inputStream = new ObjectInputStream( new FileInputStream(path));
	 	
	 	cycPairs.clear();
	 	cycPairs = (Vector<CycPair>) inputStream.readObject();
	 	inputStream.close();
	 }
	 
	 /**
	  *	 Save formulas and corresponding results in a txt file.
	  *
	  *	 @param path the path of the file.
	  *
	  *	 @throw FileNotFoundException
	  *	 @throw IOException
	  *
	  */
	 public static void logCycPairs(String path) throws FileNotFoundException, IOException
	 {
	 	//Support.checkOrCreate(path, "log file for Cyc queries and results" );
		FileManager.checkOrCreate(path, "log file for Cyc queries and results" );
	 	
	 	String toSave = "LOG FOR CYC QUERIES AND ASSERTIONS: (formula, result)" + LINE_SEPARATOR + LINE_SEPARATOR;
	 	if(size > 0)
	 	{
		 	for(int index = 0; index < size; index++)
		 	{
		 		toSave += "N. " + (index + 1) + ": " + LINE_SEPARATOR + 
		 				((cycPairs.get(index))).toString() + LINE_SEPARATOR + LINE_SEPARATOR;
		 	}
	 	}
	 	else
	 	{
	 		toSave += "You didn't save any formula.";
	 	}
	 	
        //Write the output file
        FileOutputStream fout = new FileOutputStream(path);
        for(int j = 0; j < toSave.length(); j++) {
            fout.write(toSave.charAt(j));
        }
        fout.close();	 	
	 }
}