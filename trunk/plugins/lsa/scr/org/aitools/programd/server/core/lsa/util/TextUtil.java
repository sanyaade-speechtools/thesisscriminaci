/**
 * @author Prof. Vassallo
 * Modified by Agnese
 *
 */
package org.aitools.programd.server.core.lsa.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import org.aitools.programd.server.core.lsa.spazioParolaParola.SparseIndex;

public class TextUtil {

	public static SortedMap<String, Integer> loadFreq(String fname,
			SortedMap<String, Integer> wordsID, String[] IDwords,
			int wordfreq[]) {
		System.out.println(fname);
		
		String line, word, freq;
		BufferedReader BReader = null;
		String[] words;
		SortedMap<String, Integer> wfreq = new TreeMap<String, Integer>();
		int countlines = 0;
		int ifreq;
		try {
			BReader = new BufferedReader(new FileReader(fname));
			while ((line = BReader.readLine()) != null) {
				words = line.split("\t");
				if (words.length != 2) {
					System.err.println("n. if records != 2 at line "
							+ countlines);
				}
				word = words[1];
				freq = words[0];
				ifreq = Integer.parseInt(freq);
				wfreq.put(word, ifreq);
				if (wordsID != null)
					wordsID.put(word, countlines);
				if (IDwords != null)
					IDwords[countlines] = word;
				if (wordfreq != null)
					wordfreq[countlines] = ifreq;
				countlines++;
			}
			BReader.close();
		} catch (FileNotFoundException fne) {
			System.err.println(fname + " " + fne);
		} catch (IOException ioe) {
			System.err.println(fname + " " + ioe);
		}
		return wfreq;
	}
	public static Map<String, Integer> loadFreq(String fname) {
		Map<String, Integer> wfreq = new TreeMap<String, Integer>();
		String line;
		BufferedReader reader = null;
		String[] words;
		int countlines = 0;
		try {
			reader = new BufferedReader(new FileReader(fname));
			while ((line = reader.readLine()) != null) {
				words = line.split("\t");
				wfreq.put(words[1], Integer.parseInt(words[0]));
				countlines++;
			}
			reader.close();
		} catch (FileNotFoundException fne) {
			System.err.println(fname + " " + fne);
		} catch (IOException ioe) {
			System.err.println(fname + " " + ioe);
		}
		return wfreq;
	}
	public static Map<String, Integer> loadWID(String fname) {
		String line, word, freq;
		BufferedReader BReader = null;
		String[] words;
		Map<String, Integer> wfreq = new TreeMap<String, Integer>();
		int countlines = 0;
		int ifreq;
		try {
			BReader = new BufferedReader(new FileReader(fname));
			while ((line = BReader.readLine()) != null) {
				words = line.split("\t");
				word = words[1];
				freq = words[0];
				ifreq = Integer.parseInt(freq);
				wfreq.put(word, ifreq);
				countlines++;
			}
			BReader.close();
		} catch (FileNotFoundException fne) {
			System.err.println(fname + " " + fne);
		} catch (IOException ioe) {
			System.err.println(fname + " " + ioe);
		}
		return wfreq;
	}
	public static Set<String> loadW1(String fname) {
		String line;
		BufferedReader BReader = null;
		Set<String> wstop = new TreeSet<String>();
		int countlines = 0;
		try {
			BReader = new BufferedReader(new FileReader(fname));
			while ((line = BReader.readLine()) != null) {
				wstop.add(line);
				countlines++;
			}
			BReader.close();
		} catch (FileNotFoundException fne) {
			System.err.println(fname + " " + fne);
		} catch (IOException ioe) {
			System.err.println(fname + " " + ioe);
		}
		return wstop;
	}
	public static SortedMap<SparseIndex, Integer> loadDigrams(String fname,
			SortedMap<String, Integer> wordsID) {
		String line, word1, word2, dfreq;
		BufferedReader BReader = null;
		String[] words;
		SortedMap<SparseIndex, Integer> sparsemat = new TreeMap<SparseIndex, Integer>();
		SparseIndex sindex;
		int freq;
		try {
			BReader = new BufferedReader(new FileReader(fname));
			while ((line = BReader.readLine()) != null) {
				words = line.split("\\s+");
				if (words.length != 3) {
					System.err.println(fname + ": incorrect format");
				}
				word2 = words[2];
				word1 = words[1];
				dfreq = words[0];
				Integer id1 = wordsID.get(word1);
				if (id1 == null) {
					System.err.println(word1 + " not found in word freq file");
				}
				Integer id2 = wordsID.get(word2);
				if (id2 == null) {
					System.err.println(word1 + " not found in word freq file");
				}
				sindex = new SparseIndex(id1, id2);
				freq = Integer.parseInt(dfreq);
				sparsemat.put(sindex, freq);
			}
			BReader.close();
		} catch (FileNotFoundException fne) {
			System.err.println(fname + " " + fne);
		} catch (IOException ioe) {
			System.err.println(fname + " " + ioe);
		}
		return sparsemat;
	}
	public static SortedMap<String, Integer> getSubTree(String word,
			SortedMap<String, Integer> digrams) {
		String from = word + "\t";
		String to = word + "\tzzzzzzzzzzz";
		return digrams.subMap(from, to);
	}
	public static Map<String, Integer> getHashMap(List<String> a) {
		Map<String, Integer> ah = new HashMap<String, Integer>();
		for (String s : a) {
			Integer val = ah.get(s);
			if (val == null)
				ah.put(s, 1);
			else
				ah.put(s, val + 1);
		}
		return ah;
	}
	public static String[] getWords(String line) {
		String line2;
		line2 = line.replaceAll("<.*?>", " $0 ");
		line2.toLowerCase();
		line2 = line2.replaceAll("[{].*?[}]", " $0 ");
		line2 = line2.replaceAll("[{]\\d+[}]", " $0 ");
		line2 = line2.replaceAll("[\\s,.;:()!?]+$", "");
		line2 = line2.replaceAll("^[\\s,.;:()!?]+", "");
		return line2.split("[\\s,.;:()!?]+");
	}
	public static String[] getTokens(String line) {
		String line2;
		line2 = line.replaceAll("\\d+", "_N_");
		line2 = line2.replaceAll("<.*?>", " $0 ");
		line2 = line2.replaceAll("[{].*?[}]", " $0 ");
		line2 = line2.replaceAll("[{]\\d+[}]", " $0 ");
		line2 = line2.replaceAll("'s", " $0");
		line2 = line2.replaceAll("[`\",.;:()!?\\[\\]]", " $0 ");
		line2 = line2.replaceAll("\\s+$", "");
		line2 = line2.replaceAll("^\\s+", "");
		line2 = line2.replaceAll("\\s+", " ");
		line2 = line2.toLowerCase();
		System.err.println(line2);
		return line2.split("\\s+");
	}
	/**
	 * @param mt
	 * @param key
	 *            controllo se la parola o il digramma � gi� presente ed
	 *            incremento le occorrenze, altrimenti setto la occorrenza
	 *            uguale ad 1
	 */
	public static void mapcounter(SortedMap<String, Integer> mt, String key) {
		Integer val = mt.get(key);
		if (val == null)
			mt.put(key, 1);
		else
			mt.put(key, val + 1);
	}
	/**
	 * @author agnese
	 * @param mt
	 * @param key
	 */
	public static void mapcounterNOorder(SortedMap<String, Integer> mt,
			String key) {
		String[] comodo = key.split(" ");
		String diginvertito = comodo[1] + " " + comodo[0];
		Integer val = mt.get(key);
		Integer valinvertito = mt.get(diginvertito);
		if (val != null) {
			//System.err.println("key presente" + key);
			mt.put(key, val + 1);
		} else {
			if (valinvertito != null) {
				//System.err.println("key invertito presente" + diginvertito);
				mt.put(diginvertito, valinvertito + 1);
			} else {
				//System.err.println("lo inserisco" + key);
				mt.put(key, 1);
			}
		}
	}
	public static void getDigrams(SortedMap<String, Integer> digrams,
			SortedMap<String, Integer> digrams2, String line) {
		int i, sizem2;
		String dig1, dig2;
		String[] tokens;
		line = "_x_ " + line + " _x_";
		tokens = getTokens(line);
		sizem2 = tokens.length - 2;
		for (i = 0; i < sizem2; i++) {
			dig1 = tokens[i] + " " + tokens[i + 1];
			dig2 = tokens[i] + " " + tokens[i + 2];
			mapcounter(digrams, dig1);
			mapcounter(digrams2, dig2);
		}
		dig1 = tokens[sizem2] + " " + tokens[sizem2 + 1];
		mapcounter(digrams, dig1);
	}
	public static void getRDigrams(SortedMap<String, Integer> digrams,
			String line, int maxd) {
		int i, couple[];
		String dig1 = null;
		String[] tokens;
		line = "_x_ " + line + " _x_";
		tokens = getTokens(line);
		for (int index = 0; index < tokens.length; index++)
			System.err.println(tokens[index]);
		List<int[]> rdig = combinIndex(tokens.length, Math.min(tokens.length,
				maxd), 2);
		for (i = 0; i < rdig.size(); i++) {
			couple = rdig.get(i);
			dig1 = tokens[couple[0]] + " " + tokens[couple[1]];
			mapcounter(digrams, dig1);
		}
	}
	/**
	 * @author agnese estraggo tutti i token dalla linea corrente, esamino tutti
	 *         i possibili inidci di digrammi chiamando il meotodo combineindex
	 *         e li carico all'interno di una lista di vettori Per ogni elemento
	 *         della lista leggo le parole corrispondenti agli indici e li
	 *         inserisco in una sortedMap. Attenzione : posso scegliere di
	 *         considerare o meno l'ordine delle parole all'interno del
	 *         digramma, chiamando mapcounter, o mapcounterNoorder
	 * @param digrams
	 * @param line
	 * @param maxd
	 */
	public static void getRDigrams2(SortedMap<String, Integer> digrams,
			String line, int maxd,boolean order) {
		int i, couple[];
		String dig1 = null;
		String[] tokens = line.split("\\s+");
		List<int[]> rdig = combinIndex(tokens.length, Math.min(tokens.length,
				maxd), 2);
		for (i = 0; i < rdig.size(); i++) {
			couple = rdig.get(i);
			dig1 = tokens[couple[0]] + " " + tokens[couple[1]];
			// mapcounter mi scrive i digrammi considerando l'ordine
			// mapcounterNOorder scrive i digramminon considerando l'ordine
		
			
			 if (order==true) 
			 { mapcounter(digrams, dig1); } 
			 else
			 mapcounterNOorder(digrams, dig1);
			 
		}
	}
	public static void getDigrams(SortedMap<String, Integer> digrams,
			String line) {
		int i, sizem2;
		String dig1;
		String[] tokens;
		tokens = getTokens(line);
		sizem2 = tokens.length - 1;
		for (i = 0; i < sizem2; i++) {
			dig1 = tokens[i] + " " + tokens[i + 1];
			mapcounter(digrams, dig1);
		}
	}
	/**
	 * @param filename
	 * @param wfreq
	 * @param table_stop
	 * @throws IOException
	 * 
	 * File per file estraggo le parole che non siano stopword, caratteri o
	 * numeri e le memorizzo nella sortedmap
	 */
	public static void addFile(String filename,
			SortedMap<String, Integer> wfreq, Set<String> table_stop)
			throws IOException {
		StringTokenizer stream;
		String number = "0123456789";
		String punctuation = " .,;:-+_-'!?()[]\"\\//^*�%&�#@$<>=|~`{}\n\t\b\r";
		BufferedReader buff = new BufferedReader(new FileReader(filename));
		String line;
		while ((line = buff.readLine()) != null) {
			stream = new StringTokenizer(line, punctuation + number);
			while (stream.hasMoreTokens()) {
				String token = stream.nextToken().toLowerCase().trim();
				if ((token.length() > 2) && !(table_stop.contains(token)))
					mapcounter(wfreq, token);
			}
		}
		buff.close();
	}
	/**
	 * @param folder:
	 *            cartella di cartelle
	 * @param wfreq:
	 *            sortedmap
	 * @param table_stop:
	 *            set di stopword
	 * @throws IOException
	 * 
	 * Richiama ricorsivamente se stessa
	 */
	public static void addFolder(String folder,
			SortedMap<String, Integer> wfreq, Set<String> table_stop)
			throws IOException {
		File dir = new File(folder);
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isDirectory())
				addFolder(file.getAbsolutePath(), wfreq, table_stop);
			else
				addFile(file.getAbsolutePath(), wfreq, table_stop);
		}
	}
	// modifica rispetto al codice di Giorgio, SortedMap anzich� StoredMap
	// riceve una cartella contenente docuemnti o altre cartelle di documenti
	/**
	 * @param cartella
	 * @return
	 */
	public static SortedMap<String, Integer> wordFreq(String cartella, String stopwordPath) {
		SortedMap<String, Integer> wfreq = new TreeMap<String, Integer>();
		Set<String> table_stop;
		try {
			table_stop = StopWord.createStopWordTable(stopwordPath);
			addFolder(cartella, wfreq, table_stop);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return wfreq;
	}
	// per la lettura ricorsiva dei file
	/**
	 * Linea per linea elimino le stopwords e i numeri e passo la linea ripulita
	 * al metodo getRDigrams2
	 * 
	 * @param filename
	 * @param maxd
	 * @param digfreq
	 * @param table_stop
	 * @throws IOException
	 */
	public static void addFileDig(String filename, int maxd,
			SortedMap<String, Integer> digfreq, Set<String> table_stop,boolean order)
			throws IOException {
		StringTokenizer stream;
		String number = "0123456789";
		String punctuation = " .,;:-+_-'!?()[]\"\\//^*�%&�#@$<>=|~`{}\n\t\b\r";
		BufferedReader buff = new BufferedReader(new FileReader(filename));
		String line = "";
		while ((line = buff.readLine()) != null) {
			String line2 = "";
			stream = new StringTokenizer(line, punctuation + number);
			while (stream.hasMoreTokens()) {
				String token = stream.nextToken().toLowerCase().trim();
				if ((token.length() > 2) && !(table_stop.contains(token)))
					line2 += token + ' ';
			}
			getRDigrams2(digfreq, line2, maxd,order);
		}
		buff.close();
	}
	// per la lettura ricorsiva dei file
	/**
	 * @param folder
	 *            la cartella di cartelle
	 * @param maxd
	 *            massima distanza
	 * @param digfreq
	 * @param table_stop
	 * @throws IOException
	 */
	public static void addFolderDig(String folder, int maxd,
			SortedMap<String, Integer> digfreq, Set<String> table_stop,boolean order)
			throws IOException {
		File dir = new File(folder);
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isDirectory())
				addFolderDig(file.getAbsolutePath(), maxd, digfreq, table_stop,order);
			else
				addFileDig(file.getAbsolutePath(), maxd, digfreq, table_stop,order);
		}
	}
	/**
	 * richiama addFolderdig per creare il file di digrammi
	 * 
	 * @param cartella
	 * @param maxd
	 * @return
	 */
	public static SortedMap<String, Integer> digFreq(String cartella, int maxd,
			boolean order, String stopwordPath) {
		SortedMap<String, Integer> digfreq = new TreeMap<String, Integer>();
		Set<String> table_stop;
		try {
			table_stop = StopWord.createStopWordTable(stopwordPath);
			addFolderDig(cartella, maxd, digfreq, table_stop,order);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return digfreq;
	}
	/**
	 * Stampa la mappa parola occorrenza, se keyfirst � uguale a true stampa
	 * prima l'occorrenza e poi la parola
	 * 
	 * @param tm
	 * @param keyFirst
	 */
	public static void printMap(SortedMap<String, Integer> tm, boolean keyFirst) {
		int grandtotal = 0;
		for (SortedMap.Entry<String, Integer> entry : tm.entrySet()) {
			grandtotal += entry.getValue();
			if (keyFirst)
				System.out.println(entry.getKey() + "\t" + entry.getValue());
			else
				System.out.println(entry.getValue() + "\t" + entry.getKey());
		}
		System.out.flush();
		System.err.println(grandtotal);
	}
		
	public static void saveMap(String outFile, SortedMap<String, Integer> tm, boolean keyFirst) throws IOException {
		
		FileWriter fileWriter = new FileWriter(outFile);
		
		int grandtotal = 0;
		for (SortedMap.Entry<String, Integer> entry : tm.entrySet()) {
			grandtotal += entry.getValue();
			if (keyFirst)
				fileWriter.write(entry.getKey() + "\t" + entry.getValue()+"\n");
			else
				fileWriter.write(entry.getValue() + "\t" + entry.getKey()+"\n");
		}
		
		fileWriter.flush();
		fileWriter.close();
		System.err.println(grandtotal);
	}
	
	public static int countLines2(String textfile, Integer nw) {
		String line;
		int count = 0;
		String[] words;
		nw = 0;
		try {
			BufferedReader buff = new BufferedReader(new FileReader(textfile));
			while ((line = buff.readLine()) != null) {
				count++;
				words = getTokens(line);
				nw += words.length;
			}
			buff.close();
		} catch (FileNotFoundException fne) {
			System.err.println(textfile + " " + fne);
		} catch (IOException ioe) {
			System.err.println(textfile + " " + ioe);
		}
		return count;
	}
	public static int countLines(String textfile) {
		int count = 0;
		try {
			BufferedReader buff = new BufferedReader(new FileReader(textfile));
			while ((buff.readLine()) != null) {
				count++;
			}
			buff.close();
		} catch (FileNotFoundException fne) {
			System.err.println(textfile + " " + fne);
		} catch (IOException ioe) {
			System.err.println(textfile + " " + ioe);
		}
		return count;
	}
	public void nostopwords(SortedSet<String> nostopw, String line) {
	}
	public static double SortedMapDistance(SortedMap<String, Integer> m1,
			SortedMap<String, Integer> m2, int m1grandtot, int m2grandtot) {
		int nunion, nintersect = 0;
		double dist;
		for (SortedMap.Entry<String, Integer> entry : m1.entrySet()) {
			Integer val2 = m2.get(entry.getKey());
			if (val2 != null)
				nintersect += entry.getValue() < val2 ? entry.getValue() : val2;
		}
		nunion = m1grandtot + m2grandtot - nintersect;
		dist = nintersect;
		return (dist / nunion);
	}
	/**
	 * crea una lista di vettori a due dimensioni che contengono i possibili
	 * digrammi. Prima consiudera tutti i dirgammi di ampiezza pari a mxd poi
	 * maxd-1, fino ad arrivare a 2
	 * 
	 * @param dim
	 * @param max
	 * @param min
	 * @return
	 */
	public static List<int[]> combinIndex(int dim, int max, int min) {
		// ArrayList
		List<int[]> vettComodo = new ArrayList<int[]>(dim);
		int k, l = 0;
		// ma se max � il minimo tra tokens.lenght e maxd, � ovvio
		// che max � minore o uguale a dim
		if (max > dim) {
			max = dim;
		}
		for (int len = max; len >= min; len--) {
			k = dim - len;
			for (int i = 0; i <= k; i++) {
				l = i + len - 1;
				int[] comodo = new int[2];
				comodo[0] = i;
				comodo[1] = l;
				vettComodo.add(comodo);
			}
		}
		return vettComodo;
	}
	public static List<int[]> combinIndexSenzaOrdine(int dim, int max, int min) {
		List<int[]> vettComodo = new ArrayList<int[]>(dim);
		int k, l = 0;
		if (max > dim) {
			max = dim;
		}
		for (int len = max; len >= min; len--) {
			k = dim - len;
			for (int i = 0; i <= k; i++) {
				l = i + len - 1;
				int[] comodo = new int[2];
				comodo[0] = i;
				comodo[1] = l;
				int[] comodo2 = new int[2];
				comodo[0] = l;
				comodo[1] = i;
				vettComodo.add(comodo);
				vettComodo.add(comodo2);
			}
		}
		return vettComodo;
	}
	public static List<String> getParole(String filename) {
		List<String> parole = new Vector<String>();
		String line;
		BufferedReader BReader;
		try {
			BReader = new BufferedReader(new FileReader(filename));
			int index = 1;
			while ((line = BReader.readLine()) != null) {
				String[] line2 = line.split("\\s+");
				parole.add(line2[1]);
				index++;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return parole;
	}
}