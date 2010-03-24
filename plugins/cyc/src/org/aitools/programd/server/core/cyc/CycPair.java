package org.aitools.programd.server.core.cyc;


/**
 *
 * 	Stores a couple of String who represents a CycL formula executed in Cyc
 * 	and its result.
 */

public class CycPair implements java.io.Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3941234746773213399L;

	/** The formula */
	private String formula;
	
	/** The result */
	private String result;
	
	/** The empty string, for convenience */
	private final String EMPTY_STRING = "";
	
	private final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");
	
	
	/** The constructor */
	public CycPair()
	{
		formula = EMPTY_STRING;
		result = EMPTY_STRING;
	}
	
	
	/**
	 * 	Create a CycPair from its paramter.
	 *
	 * 	@param formula 	the formula.
	 * 	@param result 	the result.
	 */
	public CycPair(String formula, String result)
	{
		this.formula = formula.trim();
		this.result = result.trim();
	}
	
	
	/**
	 * 	Create a CycPair from a copy
	 *
	 * 	@param copy	the copy.
	 */	
	public CycPair(CycPair copy)
	{
		formula = copy.formula;
		result = copy.result;
	}
	
	
	/**
	 *	Get the formula of this CycPair.
	 *
	 *	@return the formula of this CycPair.
	 */
	public String getFormula()
	{
		return formula;
	}	
	
	
	/**
	 *	Set the formula of this CycPair.
	 *
	 *	@param formula the formula for this CycPair.
	 */
	public void setFormula(String formula)
	{
		this.formula = formula.trim();
	}	
	
	
	/**
	 *	Get the result of this CycPair.
	 *
	 *	@return the result of this CycPair.
	 */
	public String getResult()
	{
		return result;
	}
		
	
	
	/**
	 *	Set the result of this CycPair.
	 *
	 *	@param result	the result of this CycPair.
	 */
	public void setResult(String result)
	{
		this.result = result.trim();
	}
	
	/**
	 *	Overwrite the toString() method.
	 *
	 *	@return a String representation of this CycPair object.
	 *
	 */
	 @Override
	public String toString()
	 {
	 	return "( " + formula + ", " + LINE_SEPARATOR + result + ")";
	 }
}