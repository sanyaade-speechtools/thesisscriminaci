package org.aitools.programd.server.core.cyc.util;

import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;

import org.opencyc.cycobject.CycFort;

import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.io.FileInputStream;

/**
 * Questa classe permette di creare la struttura della microteoria in cartelle
 * e per ogni concetto scrivere il relativo commento e il file SearchWord che contiene
 * le parole da ricercare in wikipedia
 * 
 * @author Mario Scriminaci
 * @version 0.1.1
 *
 */
public class WriteMicrotheory {
	
	private WrapperCycAccess cycAccess;
	private ArrayList<String[]> list;
	private Properties propetries;
	private String cycProperties="conf/cyc/cyc.properties";
	private MtConcepts concept;
	private String mt;
	private String mtVocabulary;
	private String directory;
	private String hostName;
	private int portNumber;
	
	
	/**
	 * Nel costruttore bisogna specificare il nome della microteoria desiderata,
	 * ricordandosi che tale microteoria deve avere la corrispettiva VocabularyMt.
	 * 
	 * @param String mt
	 */
	public WriteMicrotheory(String mt){
		try{
			propetries=new Properties();
			propetries.load(new FileInputStream(cycProperties));
		}
		catch(IOException e){
			System.out.println(e.getMessage());
		}
		directory=propetries.getProperty("cyc.document.directory");
		hostName=propetries.getProperty("cyc.access.hostname");
		portNumber=Integer.parseInt(propetries.getProperty("cyc.access.portnumber"));
		
		this.mt=mt;
		this.mtVocabulary=mt.substring(0,mt.length()-2)+"Vocabulary"+mt.substring(mt.length()-2,mt.length());
		
		cycAccess=new WrapperCycAccess(mtVocabulary, hostName, portNumber);
				
		concept=new MtConcepts(cycAccess.convertCycListToHashList(cycAccess.getCostants()));
		
		list=concept.getWikiList();

	}
	
	/**
	 * Il metodo scrivere nella cartella document/CycMicroteory la microteoria e 
	 * crea le sottocartelle rappresentanti i documenti.
	 */
	public void WriteCostants()
	{
		try
		{									
			File dirMt=new File(directory+"/"+mt);
			dirMt.mkdir();
			
			
			for(int i=0; i<concept.getList().size();i++)
			{
				File file=new File(dirMt.toString()+"/"+concept.getList().get(i).toString());
				file.mkdir();
				
				FileWriter out=new FileWriter(file.toString()+"/999SearchName");
				
				for(int j=0;j<list.get(i).length;j++)
				{
					out.write(list.get(i)[j]);
				}
				
				out.flush();
				out.close();
			}						
		}
		catch(IOException e)
		{
			System.err.println(e.getMessage());
		}
		
	}
	
	/**
	 * Il metodo scrive in ogni sottocartella il commento alla costante.
	 */
	public void WriteComments()
	{
		HashList<String, CycFort> conceptsList=concept.getList();
		
		try
		{
			propetries=new Properties();
			propetries.load(new FileInputStream(cycProperties));
										
			File dirMt=new File(directory+"/"+mt);
			
			for(int i=0;i<conceptsList.size();i++)
			{
				
				String comment=cycAccess.getComment(conceptsList.get(i).toString());
				
				if(!comment.equals("")){
					File file=new File(dirMt.toString()+"/"+conceptsList.get(i).toString());
					FileWriter out=new FileWriter(file.toString()+"/999Comment");
					StringTokenizer token=new StringTokenizer(comment,"$#");

					comment="";

					while(token.hasMoreTokens())
					{
						comment+=token.nextToken();
					}
					out.write(comment);


					out.flush();
					out.close();
				}
			}
		}
		catch(IOException e)
		{
			System.err.println(e.getMessage());
		}			
	}
}
