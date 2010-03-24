/*
 * CycSupport.java
 *
 */

package org.aitools.programd.server.core.cyc;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import org.aitools.programd.util.StringKit;
import org.aitools.programd.util.UserError;
import org.opencyc.api.CycAccess;
import org.opencyc.api.CycApiException;
import org.opencyc.api.CycConnection;
import org.opencyc.cycobject.CycConstant;
import org.opencyc.cycobject.CycFort;
import org.opencyc.cycobject.CycList;
import org.opencyc.cycobject.CycVariable;

/**
 *	<p>
 * 	This class provides manifold utilities to the other Classes who uses OpenCyc. 
 *	API implemented in CycSupport consents the interaction with OpenCyc, makes 
 *	uniform the results and simplifies the operations to connect and 
 *	close the connection to the Cyc Server.
 *	</p>
 *	<p>
 *	All assertions must be done by following the sintax of the CycL language, like
 *	instructions of Cycorp advises to do.
 *	</p>
 *
 *	@author Andrea Pergolizzi
 *
 */

public class CycSupport 
{
	//private static final CycSupport mySelf = new CycSupport();
	
	/** Give the access to OpenCyc */
	private CycAccess myAccess = null;
	
	/** Tells if the formula and its results must be stored */
	private static boolean store;
	
	/** The Cyc term false, for convenience */
	private static final String CYC_FALSE = "NIL";
	
	/** The String "false" */
	public static final String FALSE = "FALSE";
	
	/** The String "true" */
	public static final String TRUE = "TRUE";
	
	/** The String "Unknown" */
	public static final String UNKNOWN = "Unknown";
	
	/** The line separator */
	public static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");
    
    /** The host name. */
    //protected static final String CYC_HOST_NAME = CoreSettings.getCycHostName();
    
    //Cyc-mechanism of inference, for convenience.
    public static final int NOTFOUNDED = -1;
    public static final int FIASK = 1;
    public static final int FICOMPLETE = 2;
    public static final int FIFIND = 3;
    public static final int FICREATE = 4;
    public static final int FIFINDORCREATE = 5;
    public static final int FIKILL = 6;
    public static final int FIRENAME = 7;
    public static final int FILOOKUP = 8;
    public static final int FIASSERT = 9;
    public static final int FIUNASSERT = 10;
    public static final int FIEDIT = 11;
    public static final int FIBLAST = 12;
    public static final int FICONTINUELASTASK = 13;
    public static final int FIASKSTATUS = 14;
    public static final int FITMSRECONSIDERGAFS = 15;
    public static final int FITMSRECONSIDERFORMULA = 16;
    public static final int FITMSRECONSIDERMT = 17;
    public static final int FITMSRECONSIDERTERMS = 18;
    public static final int FIHYPOTESIZE = 19;
    public static final int FIPROVE = 20;
    public static final int FIGETERROR = 21;
    public static final int FIGETWARNING = 22;
    
    /** The host name. */
    private String hostName;
    
    /**
     *  Private constructor that initializes the <code>CycSupport</code>
	 *  and, expecially, its CycAccess.
     *
     *  @param hostName    the name of the host where OpenCyc is running
     */
    public CycSupport(String hostname)
    {
    	store = false;
    	this.hostName=hostname;
    	
    }
    
    /**
     *  Returns the host name.
     *
     *  @return the host name
     */
    public String getHostName()
    {
        return hostName;
    }

    /**
     * 	Create an access to Cyc. Must be call from all methods who converse with Cyc.
     *
     *	@throw CycSupportError if some Exception occurs
     *
     */
    private void createAccess() throws CycSupportError
    {
    	if(myAccess == null)
    	{
	        try
	        {
	    		if(hostName == null)
	    		{
	    			myAccess = new CycAccess();
	    		}
	    		else
	    		{
	    			int portNumber = CycConnection.DEFAULT_BASE_PORT;
	    			int index = hostName.indexOf(":");
	    			if(index != -1 && (hostName.length() > (index + 1)))
	    			{
	    				try
	    				{
		    				portNumber = Integer.parseInt(hostName.substring(index + 1));
	    				}
	    				catch(NumberFormatException e)
	    				{
	    					portNumber = CycConnection.DEFAULT_BASE_PORT;
	    				}
		    			hostName = hostName.substring(0, index);
	    			}
	    			String localName;
	    			String localIP;
			        try
			        {
			            localName = InetAddress.getLocalHost().getHostName();
			            localIP = InetAddress.getLocalHost().getHostAddress();
			        }
			        catch (UnknownHostException e)
			        {
			            localName = "unknown";
			            localIP = "unknown";
			        }
			        if(localName.equalsIgnoreCase(hostName)) hostName = "localhost";
			        else if(localIP.equals(hostName)) hostName = "localhost";
			        
	            	myAccess = new CycAccess(hostName, portNumber,
					         CycConnection.DEFAULT_COMMUNICATION_MODE,
					         CycAccess.DEFAULT_CONNECTION);
	    		}
	        }
	        catch (CycApiException ce)
	        {
	        	ce.printStackTrace();
	        	throw new CycSupportError("Socket error!");
	        }
	        catch (UnknownHostException ue)
	        {
	        	ue.printStackTrace();
	        	throw new CycSupportError("Unable to find OpenCyc!");
	        }
	        catch (IOException ioe)
	        {
	        	ioe.printStackTrace();
	            throw new CycSupportError(ioe.getMessage() + LINE_SEPARATOR + "I/O error!");
	        }
	        catch(Exception e)
	        {
	        	e.printStackTrace();
	        	throw new CycSupportError(e.getMessage() + LINE_SEPARATOR + "Unknow error!");
	        }
    	}
    	//javax.swing.JOptionPane.showMessageDialog(null, "Ok!");
    }
    
    
    /**
     *	Create a new access to Cyc whit the hostName passed as parameter.
     *
     *	@throw CycSupportError if some Exception occurs.
     *
     */
     public void changeAccess(String hostName) throws CycSupportError
     {
     	if(!hostName.equalsIgnoreCase(getHostName()))
     	{
     		//setHostName(hostName);
     		createAccess();
     	}	
     }
    
    
    /**
     *	Tell to CycStore that the next CycL formula and its result must be stored.
     *	
     */
     public void storeNextFormula()
     {
     	store = true;
     }
    
    
    /*
     *	Return an access to OpenCyc.
     *
     */
    public  CycAccess getAccess()
    {
    	return myAccess;
    }
    
    /**
     * <p>
     * Retrieves the ontology, from an input String.
     * </p>
     * Returns the empty String if doesn't found anything.
     *
     * @param input the input String.
     *
     * @return a String who represents the ontology.
     */
    public String getOntology(String input)
    {
        //A first check
        if(input.indexOf("#$") == -1) return null;
        
        String ontology = input.substring(input.lastIndexOf("#$"));
        //An ontology is delimited from a space or a round bracket
        int spacePos = ontology.indexOf(" ");
        int bracketPos = ontology.indexOf(")");
        if( spacePos != -1 &&  bracketPos!= -1) ontology = ontology.substring(0, (spacePos < bracketPos ? spacePos : bracketPos));
        else if(spacePos != -1) ontology = ontology.substring(0, spacePos);
        else if(bracketPos != -1) ontology = ontology.substring(0, bracketPos);
        return ontology;
    }
    
    /**
     * Retrieves the formula from an input String.
     *
     * @param input the input String.
     *
     * @return a string representing the Formula
     */
    public  String getFormula(String input) throws UserError 
    {
        int apexIndex = input.indexOf('\'');
        if(apexIndex == -1) throw new UserError("Incorrect formula",null);
        //formula will take the result of the process
        String formula = input.substring(apexIndex+1);        
        String ontology = getOntology(formula);
        if(ontology != null) return formula.substring(0, formula.indexOf(ontology));
        else throw new UserError("Missing Ontology!",null);
    }
    
    public  String getContentVariable(String input) throws CycSupportError
    {
    	String result = input.substring(input.indexOf(". ") + 2);
    	return trimBracketAndQuote(result);
    }
    
    @SuppressWarnings("unchecked")
	public  ArrayList getVariables(Object input) throws CycSupportError
    {
    	if(input instanceof CycList) return (ArrayList)((CycList)input);
    	else if(input instanceof CycVariable)
    	{
    		ArrayList arrayL = new ArrayList();
    		arrayL.add(input);
    		return arrayL;
    	} else throw new CycSupportError("Incorrect Input");
    }
    
    
    /**
     * Retrieves the type of inference module of a SubL instruction.
     *
     * @param input the input String.
     *
     * @return an integer value who representing the type of inference module.
     */
    public  int getType(String input)
    {
        if(input.indexOf("fi-ask") != -1) return FIASK;
        if(input.indexOf("cyc-query") != -1) return FIASK;
        if(input.indexOf("fi-complete") != -1) return FICOMPLETE;
        if(input.indexOf("fi-create") != -1) return FICREATE;
        if(input.indexOf("cyc-create") != -1) return FICREATE;
        if(input.indexOf("fi-find-or-create") != -1) return FIFINDORCREATE;
        if(input.indexOf("cyc-find-or-create") != -1) return FIFINDORCREATE;
        if(input.indexOf("fi-kill") != -1) return FIKILL;
        if(input.indexOf("cyc-kill") != -1) return FIKILL;
        if(input.indexOf("fi-lookup") != -1) return FILOOKUP;
        if(input.indexOf("fi-assert") != -1) return FIASSERT;
        if(input.indexOf("fi-unassert") != -1) return FIUNASSERT;
        if(input.indexOf("fi-edit") != -1) return FIEDIT;
        if(input.indexOf("fi-blast") != -1) return FIBLAST;
        if(input.indexOf("fi-comntinue-last-ask") != -1) return FICONTINUELASTASK;
        if(input.indexOf("fi-ask-status") != -1) return FIASKSTATUS;
        if(input.indexOf("fi-tms-reconsider-formula") != -1) return FITMSRECONSIDERFORMULA;
        if(input.indexOf("fi-tms-reconsider-mt") != -1) return FITMSRECONSIDERMT;
        if(input.indexOf("fi-tms-reconsider-gafs") != -1) return FITMSRECONSIDERGAFS;
        if(input.indexOf("fi-tms-reconsider-terms") != -1) return FITMSRECONSIDERTERMS;
        if(input.indexOf("fi-hypotesize") != -1) return FIHYPOTESIZE;
        if(input.indexOf("fi-prove") != -1) return FIPROVE;
        if(input.indexOf("fi-get-error") != -1) return FIGETERROR;
        if(input.indexOf("fi-get-warning") != -1) return FIGETWARNING;
        if(input.indexOf("fi-complete") != -1) return FICOMPLETE;
        return NOTFOUNDED;
    }
    
    
    /**
     *  Return an ArrayList of String that can be Cyc constatns, running from an input.
     *  
     *  @param input    the input String.
     *  
     *  @return an ArrayList of String that can be Cyc constants.
     */
    @SuppressWarnings("unchecked")
	public  ArrayList makeCycLike(String input)
    {
    	input = input.trim();
    	boolean uppercase = true;
    	for (int index = 0; index < input.length(); index++) 
    	{
    		if(!(input.charAt(index) >= 'A' && input.charAt(index) <= 'Z'))
    			uppercase = false;
    			
    	}
    	if(uppercase) input = input.toLowerCase();
        ArrayList split = StringKit.wordSplit(input);
        ArrayList result = new ArrayList();
        
        int size = split.size();
        //If there are, remove articles;
        String word = null;
        StringBuffer cycLike[] = { new StringBuffer(""), new StringBuffer(""), 
                                    new StringBuffer("") };
        if(size > 1)
        {
            String head = null;
            String tail = null;
            for(int i = 0; i < size; i++)
            {
                word = split.get(i).toString();
                if(!word.equalsIgnoreCase("a") && !word.equalsIgnoreCase("an") && 
                	!word.equalsIgnoreCase("the") && !word.equalsIgnoreCase("and")
                    && !word.equalsIgnoreCase("this") && !word.equalsIgnoreCase("that"))
                {
                	head = word.substring(0,1).toUpperCase();
                	tail = word.substring(1);
                    cycLike[0].append(head);
                    cycLike[0].append(tail);
                    cycLike[1].append(head);
                    cycLike[1].append(tail);
                    cycLike[2].append(head);
                    cycLike[2].append(tail);
                    if(i < (size-1))
                    {
                        cycLike[1].append("-");
                        cycLike[2].append("_");
                    }
                }
            }
            for(int index = 0; index < 3; index++)
            {
                result.add(cycLike[index].toString());
                cycLike[index].append("-TheWord");
                result.add(cycLike[index].toString());
            }
        }
        else
        {
            word = split.get(0).toString();
            result.add(word.substring(0,1).toUpperCase() + word.substring(1));
            result.add(result.get(0).toString() + "-TheWord");
        }
        return result;
    }
    
    /**
     * Get the first variable in a String who represents a CycList with variables.
     *
     * @param input the String who represent the CycList
     * @return a String who represente the first comment.
     */
    public  String getFirstComment(String input)
    {
        int firstIndex = input.indexOf("\"") + 1;
        if(firstIndex >0)
        {
            int secondIndex = input.substring(firstIndex).indexOf("\"");
            if(secondIndex != -1)
            {
                return input.substring(firstIndex, firstIndex + secondIndex);
            }
            else return "Unknown";
        }
        else return "Unknown";
    }
    
    
	/**
     * Executes a query in the specified microtheory, swhowing variables to substitute.
     * Returns the Cyc terms present in the Cyc KB that match the query applying inference.
     *
     * @param   query   String that contains the Cyc formula (i.e. the query).
     * @param   var   	the unbounds variables in the query for which bindings are sought.
     * @param   mt   	the microtheory in which the query is asked.
     *
     * @return  a list of bindings for the query (<code>null</code> null if doesn't exist).
     *
     * @throw java.net.UnknownHostException  if Cyc server host not found on the network.
     * @throw java.io.IOException  if a data communication error occurs.
     * @throw org.opencyc.api.CycApiException  if the api request results in a Cyc server error.
     *
     */
	@SuppressWarnings("deprecation")
	protected  CycList askQuery(String query, CycVariable var, CycConstant mt)
    							throws IOException, UnknownHostException, CycApiException 
    {
		query = cyclify(query);
		CycList l=new CycList();
		l = myAccess.askWithVariable(myAccess.makeCycList(query), var, mt);//, Integer.MAX_VALUE);
		return l;
	}
    
    
	/**
     * Executes a query in the specified microtheory, swhowing variables to substitute.
     * Returns the Cyc terms present in the Cyc KB that match the query applying inference.
     *
     * @param   query   String that contains the Cyc formula (i.e. the query).
     * @param   var   	the unbounds variables in the query for which bindings are sought.
     * @param   mt   	the microtheory in which the query is asked.
     *
     * @return  a list of bindings for the query (<code>null</code> null if doesn't exist).
     *
     * @throw java.net.UnknownHostException  if Cyc server host not found on the network.
     * @throw java.io.IOException  if a data communication error occurs.
     * @throw org.opencyc.api.CycApiException  if the api request results in a Cyc server error.
     *
     */
	@SuppressWarnings("unchecked")
	public  ArrayList askQuery(String query, String x, String mt)
    							throws IOException, UnknownHostException, CycApiException 
    {
    	createAccess();
		CycConstant newMt = findOrCreateMt(mt);
		CycList result = askQuery(query, new CycVariable(x), newMt);
		return (ArrayList)result;
	}
	
    /**
     *	Find or create the microtehory gave by name
     *
     * @param   mt   the microtheory
     *
     * @return  a CycConstant who represent the microtheory
     *
     * @exception java.net.UnknownHostException  if OpenCyc'host wasn't founded.
     * @exception java.io.IOException  if an I/O error occurs.
     * @exception org.opencyc.api.CycApiException  if occurs an error in communicating with OpenCyc.
     */
    protected  CycConstant findOrCreateMt(String mt)
    				 throws IOException, UnknownHostException,CycApiException {
    	createAccess();
		CycConstant context = myAccess.findOrCreate(mt);
		myAccess.assertIsa(mt,"Microtheory");
		return context;
    }
    
    /**
     * Put "#$"before all Cyc terms in the sentence.
     *
     * @param   sentence   the sentence to cyclify.
     * @return  a sentence cyclified.
     */
    public  String cyclify(String sentence) {
    	String cyclified = "";
    	int oldstate = 0;
    	int newstate = 0;
    	boolean leggostr = false;
    	char curr;
    	for(int i=0;i<sentence.length();i++) {
    		curr=sentence.charAt(i);
    		if(leggostr) {
    			cyclified=cyclified+curr;
    			if(curr=='\"') leggostr = false;
    		}
    		else {
				oldstate=newstate;
				if(curr=='\"') {leggostr = true;cyclified=cyclified+curr;continue;}
				else if (curr=='('||curr==' '||curr==')'||curr=='\t') newstate=0;
				else if (curr=='0'||curr=='1'||curr=='2'||curr=='3'||curr=='4') newstate=1;
				else if (curr=='5'||curr=='6'||curr=='7'||curr=='8'||curr=='9') newstate=1;
				else if (curr=='?'||(curr=='#'&&sentence.charAt(i+1)=='$'))     newstate=1;
				else                                                  newstate=2;
				if(newstate==0) {cyclified=cyclified+curr;continue;};
				if(newstate==1) {cyclified=cyclified+curr;continue;};
				if(newstate==2&&oldstate==0) {cyclified=cyclified+"#$"+curr;continue;};
				if(newstate==2&&oldstate==1) {cyclified=cyclified+curr;continue;};
				if(newstate==2&&oldstate==2) {cyclified=cyclified+curr;continue;};
			}
    	}
		return cyclified;
    }
    
    
    /**
     * 	Executes a CycL instruction represented by a String.
     *
     *	@param instruction	a String who represents the CyL formula.
     *	
     *	@return the result of the conversation with Cyc in a String format. Converse
     *			directly Cyt term for false in "FALSE" and Cyc term for true in "TRUE".
     */
    public  String executeCycL(String instruction) throws CycSupportError
    {
    	String result = null;
    	
    	createAccess();
    	
    	try
    	{
    		//Execute the CycL formula and convert its result into a String.
    		result = (myAccess.converseObject(instruction)).toString();
    		if(result.equals(CYC_FALSE))
    		{
    			result = FALSE;
    		}
    		else if(trimBracketAndQuote(result).equals(CYC_FALSE))
    		{ 
    			//converseObject, al posto di TRUE, ritorna una CycList con NIL
    			//all'interno, come (NIL)   			
    			result = TRUE;
    		}
    		checkAndStore(instruction, result);
			return result;    		
    	}
    	catch(CycApiException ce)
    	{    		
    		checkAndStore(instruction, "Cyc communication error!"); 
    		throw new CycSupportError("Cyc communication error!");
    	}
    	catch(UnknownHostException oe)
    	{
    		checkAndStore(instruction, "Unable to find OpenCyc!"); 
    		throw new CycSupportError("Unable to find OpenCyc!");
        }
        catch (IOException ioe)
        {
    		checkAndStore(instruction, "I/O error!");
            throw new CycSupportError("I/O error!");
        }
        catch(Exception e)
        {			
    		checkAndStore(instruction, "Unknow error!");
        	throw new CycSupportError("Unknow error!");
        }

    }
    
    
    
    @SuppressWarnings("unchecked")
	public  String executeRandomCycL(String formula) throws CycSupportError
    {
    	
    	createAccess();
    	
    	try
    	{
	    	Object res = myAccess.converseObject(formula);
	    	
	    	//This will store the result
	    	String result = res.toString();
	    	
	    	if(result.equals(CYC_FALSE))
	    	{
	    		result = FALSE;
	    		checkAndStore(formula, result);
	    	}
	    	else if(trimBracketAndQuote(result).equals(CYC_FALSE))
	    	{
	    		result = TRUE;
	    		checkAndStore(formula, result);
	    	}
	    	else if(res instanceof CycList)
	    	{
	    		checkAndStore(formula, result);
	    		CycList cl = ((CycList)res).randomPermutation();
	    		    		
	            ArrayList results = getVariables(cl);
	            if(results.size() > 0) {
	            	result =getContentVariable(results.get(0).toString());
	            }
	            else result = UNKNOWN;
	    	}
	    	return result;
    	}
    	catch(CycApiException ce)
    	{    		
    		ce.printStackTrace();
    		checkAndStore(formula, "Cyc communication error!"); 
    		throw new CycSupportError("Cyc communication error!");
    	}
    	catch(UnknownHostException oe)
    	{
    		checkAndStore(formula, "Unable to find OpenCyc!"); 
    		throw new CycSupportError("Unable to find OpenCyc!");
        }
        catch (IOException ioe)
        {
    		checkAndStore(formula, "I/O error!");
            throw new CycSupportError("I/O error!");
        }
        catch(Exception e)
        {			
    		checkAndStore(formula, "Unknow error!");
        	throw new CycSupportError("Unknow error!");
        }
    }
    
    
    
    /*
     * 	Find the first Cyc Constant that match the input String.
     * 
     * 	@param input the input String
     *
     * 	@return a String who represents a Cyc Constant.
     *
     *	@throw CycSupportError 	if any error occurs.
     */
    @SuppressWarnings("unchecked")
	public  String findFirstMatch(String input) throws CycSupportError
    {
      	createAccess();
    	
    	CycFort result; 	
    	
        ArrayList attempt = makeCycLike(input);
    	int size = attempt.size();
    	
    	for(int index = 0; index < size; index++)
    	{
    		try
    		{
    			result = myAccess.getConstantByName(attempt.get(index++).toString());
    			if(result != null) return result.cyclify();
    		}
    		catch(Exception e)
    		{
    		}
    	}
    	
    	   	
        try
        {
            ArrayList vars = new ArrayList();
            
            //Try CycTerm
            vars = askQuery("(#$denotation #$" + attempt.get(1) + " ?TEXT ?TYPE ?CYCOBJECT)","?CYCOBJECT", "#$EverythingPSC");
            if(vars.size() > 0) if(vars.get(0) != null) return cyclify(vars.get(0).toString());
            
            vars = askQuery("(#$denotationRelatedTo #$" + attempt.get(1) + " ?TEXT ?TYPE ?CYCOBJECT)","?CYCOBJECT", "#$EverythingPSC");
            if(vars.size() > 0) if(vars.get(0) != null) return cyclify(vars.get(0).toString());
            
            //Name look up
            vars = askQuery("(#$nameString \"" + attempt.get(0) + "\" ?CYCOBJECT)"," ?CYCOBJECT", "#$EverythingPSC");
            if(vars.size() > 0) if(vars.get(0) != null) return cyclify(vars.get(0).toString());
            //'#$initialismString'
            vars = askQuery("(#$initialismString ?CYCOBJECT \"" + attempt.get(0) + "\")"," ?CYCOBJECT", "#$EverythingPSC");
            if(vars.size() > 0) if(vars.get(0) != null) return cyclify(vars.get(0).toString());
            
            //'#$abbreviationString-PN'
            vars = askQuery("(#$abbreviationString-PN ?CYCOBJECT  \"" + attempt.get(0) + "\")"," ?CYCOBJECT", "#$EverythingPSC");
            if(vars.size() > 0) if(vars.get(0) != null) return cyclify(vars.get(0).toString());
            //'#$preferredNameString'
            vars = askQuery("(#$preferredNameString ?CYCOBJECT  \"" + attempt.get(0) + "\")"," ?CYCOBJECT", "#$EverythingPSC");
            if(vars.size() > 0) if(vars.get(0) != null) return cyclify(vars.get(0).toString());
            //'#$countryName-LongForm'
            vars = askQuery("(#$countryName-LongForm ?CYCOBJECT  \"" + attempt.get(0) + "\")"," ?CYCOBJECT", "#$EverythingPSC");
            if(vars.size() > 0) if(vars.get(0) != null) return cyclify(vars.get(0).toString());
            //'#$countryName-ShortForm'
            vars = askQuery("(#$countryName-ShortForm ?CYCOBJECT  \"" + attempt.get(0) + "\")"," ?CYCOBJECT", "#$EverythingPSC");
            if(vars.size() > 0) if(vars.get(0) != null) return cyclify(vars.get(0).toString());
            //'#$acronymString'
            vars = askQuery("(#$acronymString ?CYCOBJECT  \"" + attempt.get(0) + "\")"," ?CYCOBJECT", "#$EverythingPSC");
            if(vars.size() > 0) if(vars.get(0) != null) return cyclify(vars.get(0).toString());
            //'#$scientificName'
            vars = askQuery("(#$scientificName ?CYCOBJECT  \"" + attempt.get(0) + "\")"," ?CYCOBJECT", "#$EverythingPSC");
            if(vars.size() > 0) if(vars.get(0) != null) return cyclify(vars.get(0).toString());
            //'#$termStrings'
            vars = askQuery("(#$termStrings ?CYCOBJECT  \"" + attempt.get(0) + "\")"," ?CYCOBJECT", "#$EverythingPSC");
            if(vars.size() > 0) if(vars.get(0) != null) return cyclify(vars.get(0).toString());
            //'#$initialismString'
            vars = askQuery("(#$initialismString ?CYCOBJECT  \"" + attempt.get(0) + "\")"," ?CYCOBJECT", "#$EverythingPSC");
            if(vars.size() > 0) if(vars.get(0) != null) return cyclify(vars.get(0).toString());
            //'#$termStrings-GuessedFromName'
            vars = askQuery("(#$termStrings-GuessedFromName ?CYCOBJECT  \"" + attempt.get(0) + "\")"," ?CYCOBJECT", "#$EverythingPSC");
            if(vars.size() > 0) if(vars.get(0) != null) return cyclify(vars.get(0).toString());
            //'#$prettyName'
            vars = askQuery("(#$prettyName ?CYCOBJECT  \"" + attempt.get(0) + "\")"," ?CYCOBJECT", "#$EverythingPSC");
            if(vars.size() > 0) if(vars.get(0) != null) return cyclify(vars.get(0).toString());
            //'#$nicknames'
            vars = askQuery("(#$nicknames ?CYCOBJECT  \"" + attempt.get(0) + "\")"," ?CYCOBJECT", "#$EverythingPSC");
            if(vars.size() > 0) if(vars.get(0) != null) return cyclify(vars.get(0).toString());
            //'#$preferredTermStrings'
            vars = askQuery("(#$preferredTermStrings ?CYCOBJECT  \"" + attempt.get(0) + "\")"," ?CYCOBJECT", "#$EverythingPSC");
            if(vars.size() > 0) if(vars.get(0) != null) return cyclify(vars.get(0).toString());
            
            // Generic word lookup
            vars = askQuery("(#$and (#$massNumber ?WORD \"" + attempt.get(0)
            				+ "\") (#$or (#$denotation ?WORD ?TEXT ?TYPE ?CYCOBJECT) (#$denotationRelatedTo ?WORD ?TEXT ?TYPE ?CYCOBJECT) ))",
                                "?CYCOBJECT", "#$EverythingPSC");
            if(vars.size() > 0) if(vars.get(0) != null) return cyclify(vars.get(0).toString());
            vars = askQuery("(#$and (#$wordStrings ?WORD \"" + attempt.get(0) + "\") (#$or (#$denotation ?WORD ?TEXT ?TYPE ?CYCOBJECT) (#$denotationRelatedTo ?WORD ?TEXT ?TYPE ?CYCOBJECT) ))",
                                "?CYCOBJECT", "#$EverythingPSC");
            if(vars.size() > 0) if(vars.get(0) != null) return cyclify(vars.get(0).toString());
            
        }
        catch(Exception e)
        {
        }
        //If not founded, return a "cyc-format" text
        return "#$" + attempt.get(0).toString();
    }
    
    

    /**
     * End previously opened connection to Cyc.
     *
     */
    public  void endConnection() throws CycSupportError
    {
        try {
            myAccess.close();
        }
        catch(Exception e)
        {	
        	throw new CycSupportError("Unknow error!");
        }
    }
    
    
    /**
     *	If store is true, save the couple (formula, result) into the CycStore.
     *
     *	@param formula	the formula to save.
     *	@param result	its corresponding result.
     *
     */
    private  void checkAndStore(String formula, String result)
    { 
    	//Must I store this couple?
		if(store){
			//Yes, but the next may be not
			store = false;
			//Store this formula and its result.
			CycStore.addElement(formula, result);
		}
    }
    
    /**
	 * 	Simply removes initial and last quotes, spaces and brackets (only in couple)
	 * 	from a String.
	 *
	 *	@param input the String.
	 *
	 *	@return a String cleaned from initial and last quotes, spaces and brackets.
	 */
	public  String trimBracketAndQuote(String input)
	{
		if(input != null)
		{
			int closeBrackets = 0;
			int openBrackets = 0;
			input.trim();
			//Remove initial brackets and quotes
			char c =input.charAt(0);
			while(c == '\"' || c == '(')
			{
				if(input.length() > 1)
				{
					input = input.substring(1);
					c = input.charAt(0);
				}
				else return "";
			}
			
			//Remove last brackets and quotes
			int length = input.length();
			if(length == 0) return "";
			for(int i = 0; i < length; i++) 
			{
				c = input.charAt(i);
				if(c == '(') openBrackets++;
				else if(c == ')') closeBrackets++;
			}
			if(closeBrackets == 0) closeBrackets = Integer.MAX_VALUE;
			c = input.charAt(length - 1);
			while((c == '\"' || c == ')')
					 && closeBrackets > openBrackets)
			{ 
				if(length > 1) 
				{
					input = input.substring(0,--length);
					if(c == ')') closeBrackets--;
					c = input.charAt(length - 1);
				}
				else return "";
			}
			return input.trim();
		}
		else return null;
		
	}


    
}

