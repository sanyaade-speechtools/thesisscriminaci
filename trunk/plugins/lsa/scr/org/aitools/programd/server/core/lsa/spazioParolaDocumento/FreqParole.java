package org.aitools.programd.server.core.lsa.spazioParolaDocumento;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

//import net.didion.jwnl.data.IndexWord;
import jmp.SparseRowMatrix;
import jmp.io.MatrixMarketWriter;

import org.aitools.programd.server.core.lsa.util.DatabasePD;
import org.aitools.programd.server.core.lsa.util.StopWord;

public class FreqParole {
	private SortedMap<String, Map<Documento, Integer>> occorrenzeParole;
	private List<Documento> documenti;
	private Set<String> table_stop;
	private DatabasePD databasePd;

	public FreqParole(DatabasePD databasePd, String stopWordsPath) {
		super();
		this.databasePd = databasePd;
		inizializza();
		this.occorrenzeParole = new TreeMap<String, Map<Documento, Integer>>();
		this.documenti = new ArrayList<Documento>();
		try {
			this.table_stop = StopWord.createStopWordTable(stopWordsPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void mapcounter(Documento documento, String word) {
		Map<Documento, Integer> occorrenzeParola = this.occorrenzeParole
				.get(word);
		if (occorrenzeParola == null) {
			occorrenzeParola = new Hashtable<Documento, Integer>();
			occorrenzeParola.put(documento, 1);
			//System.out.println(word);
			this.occorrenzeParole.put(word,occorrenzeParola);
		} else {
			Integer value = occorrenzeParola.get(documento);
			if (value != null)
				occorrenzeParola.put(documento, value + 1);
			else
				occorrenzeParola.put(documento, 1);
		}
	}
	private void addFile(String filename, String classe) throws IOException {
		File f=new File(filename);
		//IndexWord NEW;
		Documento documento = new Documento(f.getName(),classe,this.documenti.size());
		this.documenti.add(documento);
		StringTokenizer stream;
		String number = "0123456789";
		//String punctuation = " .,;:-+_-'!?()[]\"\\//^*�%&�#@$<>=|~`{}\n\t\b\r";
		//Modified by Emilio
		//wordnet usa '_' e '-' per le parole composte;
		String punctuation = " ,;:+!?()[]\"^*�%&�#@$<>=|~`{}\n\t\b\r����'.";
		BufferedReader buff = new BufferedReader(new FileReader(filename));
		String line;
		try{
			while ((line = buff.readLine()) != null) {				
				stream = new StringTokenizer(line, punctuation + number);
				//System.out.println(line);
				//stream = new StringTokenizer(line, " ");
				while (stream.hasMoreTokens()) {					
					String token = stream.nextToken().toLowerCase().trim();				
					//NEW = Dictionary.getInstance().lookupIndexWord(POS.NOUN, token);
					if ((token.length() > 2) && !(table_stop.contains(token))){//&&(NEW!=null))
						//System.out.println(token);
						mapcounter(documento, token);
						
						//mapcounter(documento, NEW.getLemma());
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		buff.close();
	}
	
	public void inizializza(){
		try {
			// initialize JWNL (this must be done before JWNL can be used)
			//JWNL.initialize(new FileInputStream("resources/jwnl13rc3/file_properties.xml"));
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	/**
	 * @param folder:
	 *            cartella di cartelle
	 * @throws IOException
	 * 
	 * Richiama ricorsivamente se stessa
	 */
	private void addFolder(String folder) throws IOException {
		File dir = new File(folder);
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isDirectory()){
				System.out.print(file.getName()+"("+file.listFiles().length+"):");
				addFolder(file.getAbsolutePath());
				System.out.println();
			}
			else{
				addFile(file.getAbsolutePath(), dir.getName());
			}
		}
	}
	
	public void createMatrixFromFolder(String filename,String cartella, String basePath) {
		Map<Documento,Integer> mappa; //temporaneo
		int freq=0;
		//File f;
		//FileOutputStream fos;
		//PrintStream ps=null;
		try {
			//per ottenere le frequenze delle parole
			//f=new File(basePath + "freq.txt");
			//fos=new FileOutputStream(f);
			//ps=new PrintStream(fos);			
			//**************************************
		
			File dir = new File(cartella);
			if (dir.isDirectory())
				addFolder(cartella);
			else
				addFile(cartella,"");
		} catch (IOException e) {
			e.printStackTrace();
		}
		int nwords = this.occorrenzeParole.keySet().size();
		int ndocs = this.documenti.size();
		SparseRowMatrix sparsemat = new SparseRowMatrix(nwords, ndocs);
		int i = 0;
		for (Map.Entry<String, Map<Documento, Integer>> parolaEntry : this.occorrenzeParole
				.entrySet()) {
			//System.out.println(parolaEntry.getKey());
					for (Map.Entry<Documento, Integer> documentoEntry : parolaEntry
							.getValue().entrySet()) {
						int j = documentoEntry.getKey().getIndice();
						//metodo quadrato:
						//metodo radice:						
						sparsemat.setValue(i, j, Math.sqrt(documentoEntry.getValue()));
						//metodo classico: sparsemat.setValue(i, j, documentoEntry.getValue());
						//System.out.println(i +" "+ j +" "+ documentoEntry.getValue() );
					}
					i++;
			
			//**********************modified by emilio			
			mappa = parolaEntry.getValue();
			freq= mappa.size();
			//if((freq>4)&&(freq<50))
			//System.out.println(parolaEntry.getKey() +" " + freq);
			//if((freq>2)&&(freq<30))
				//ps.println(parolaEntry.getKey());
//			************************
		}
		//ps.close();
		MatrixMarketWriter MMW;
		try {
			MMW = new MatrixMarketWriter(new FileWriter(filename));
			MMW.writeMatrix(sparsemat);
			MMW.close();
		} catch (IOException ioe) {
			System.err.println(ioe);
		}
	}
	
	public void createMatrixFromFolder(String filename,String cartella) {
		Map<Documento,Integer> mappa; //temporaneo
		String path = "C:\\Testi\\wordnet3\\";
		int freq=0;
		File f;
		FileOutputStream fos;
		PrintStream ps=null;
		try {
			//per ottenere le frequenze delle parole
			f=new File(path + "freq.txt");
			fos=new FileOutputStream(f);
			ps=new PrintStream(fos);			
			//**************************************
		
			File dir = new File(cartella);
			if (dir.isDirectory())
				addFolder(cartella);
			else
				addFile(cartella,"");
		} catch (IOException e) {
			e.printStackTrace();
		}
		int nwords = this.occorrenzeParole.keySet().size();
		int ndocs = this.documenti.size();
		SparseRowMatrix sparsemat = new SparseRowMatrix(nwords, ndocs);
		int i = 0;
		for (Map.Entry<String, Map<Documento, Integer>> parolaEntry : this.occorrenzeParole
				.entrySet()) {
			//System.out.println(parolaEntry.getKey());
					for (Map.Entry<Documento, Integer> documentoEntry : parolaEntry
							.getValue().entrySet()) {
						int j = documentoEntry.getKey().getIndice();
						//metodo quadrato:
						//metodo radice:						
						sparsemat.setValue(i, j, Math.sqrt(documentoEntry.getValue()));
						//metodo classico: sparsemat.setValue(i, j, documentoEntry.getValue());
						//System.out.println(i +" "+ j +" "+ documentoEntry.getValue() );
					}
					i++;
			
			//**********************modified by emilio			
			mappa = parolaEntry.getValue();
			freq= mappa.size();
			//if((freq>4)&&(freq<50))
			//System.out.println(parolaEntry.getKey() +" " + freq);
			if((freq>2)&&(freq<30))
				ps.println(parolaEntry.getKey());
//			************************
		}
		ps.close();
		MatrixMarketWriter MMW;
		try {
			MMW = new MatrixMarketWriter(new FileWriter(filename));
			MMW.writeMatrix(sparsemat);
			MMW.close();
		} catch (IOException ioe) {
			System.err.println(ioe);
		}
	}
	
	public void saveParoleDocumenti(String filename)
	{
		try {
			ObjectOutputStream o = new ObjectOutputStream(
					new BufferedOutputStream(new FileOutputStream(filename)));
			o.writeObject(new ArrayList<Documento>(this.documenti));
			o.writeObject(new ArrayList<String>(this.occorrenzeParole.keySet()));
			o.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}	
	
	public void getValues(){//legge da una lista di parole in ingresso e ne scrive la frequenza in uscita
		Map<Documento,Integer> mappa;
		String path = "C:\\Testi\\" ;		
		File f;
		String linea;
		FileOutputStream fos;
		PrintStream ps=null;
		try {
			//parole in ingresso
			File f_in = new File(path + "lista.txt");
			FileInputStream fis=new FileInputStream(f_in);
			InputStreamReader isr=new InputStreamReader(fis);
			BufferedReader br=new BufferedReader(isr);	
			
			//frequenze delle parole
			f=new File(path + "freq.txt");
			fos=new FileOutputStream(f);
			ps=new PrintStream(fos);
			do{
				linea=br.readLine();
				if(linea!=null){
					mappa = this.occorrenzeParole.get(linea);
					if(mappa!=null)
						ps.println(linea + " " +mappa.size());
					else
						System.out.println(linea);
				}
			}while(linea!=null);
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
	}
}