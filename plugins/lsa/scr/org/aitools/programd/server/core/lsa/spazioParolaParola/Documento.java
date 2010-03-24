package org.aitools.programd.server.core.lsa.spazioParolaParola;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.aitools.programd.server.core.lsa.util.Classe;
import org.aitools.programd.server.core.lsa.util.DatabasePP;
import org.aitools.programd.server.core.lsa.util.OperazioneArray;
//import org.aitools.programd.server.core.lsa.util.SentenceExtractor;

import com.aliasi.util.Files;

public class Documento {
	String filename;
	Classe classe;
	Set<String> stopwords;
	double[] coding;
	List<double[]> codingFrasi;
	int dim;
	
	public Documento(String filename, Set<String> stopwords, int dim) {
		this.filename = filename;
		this.stopwords = stopwords;
		this.dim = dim;
	}
	public Documento(String filename, String categoria, double[] coding) {
		this.filename = filename;
		this.classe = new Classe(categoria);
		this.coding = coding;
	}
	/*
	 * Calcola la lista di rotori di una frase passata come parametro
	 */
	
	/*
	 * mi serve per ottenere la codifica di una frase
	 */
	private double[] codifyFrase(DatabasePP databasePP, String testo) {
		double[] textcoded = new double[2 * dim];
		double[] sinistro = new double[dim];
		double[] destro = new double[dim];
		StringTokenizer st = new StringTokenizer(testo);
		while (st.hasMoreTokens()) {
			String par = st.nextToken().toLowerCase();
			if (!this.stopwords.contains(par)) {
				Parola parola = databasePP.getCodificaParola(par);//????
				if (parola != null) {
					sinistro = OperazioneArray.somma_vettori(sinistro,
							parola.ctx_sinistro);
					destro = OperazioneArray.somma_vettori(sinistro,
							parola.ctx_destro);
				}
			}
		}
		double normasinistro=OperazioneArray.norma2(sinistro);
		double normadestro=OperazioneArray.norma2(destro);
		if (normasinistro!=0)
			OperazioneArray.moltiplica_vettore_per_fattore(sinistro,(1/normasinistro));
		if (normadestro!=0)
			OperazioneArray.moltiplica_vettore_per_fattore(destro,(1/normadestro));
		for (int index = 0; index < dim; index++) {
			textcoded[index] = sinistro[index];
			textcoded[dim + index] = destro[index];
		}
		return textcoded;
	}
	private void codifyDocument(DatabasePP databasePP) {
		String text;
		try {
			text = Files.readFromFile(new File(this.filename));
			this.coding = this.codifyFrase(databasePP, text);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * codifica frase per frase il documento
	 */
	
	public double[] getCoding(DatabasePP databasePP) {
		if (coding == null)
			this.codifyDocument(databasePP);
		return coding;
	}
	public Classe getClasse() {
		return classe;
	}
	
	public static List<String> getWords(String str, Set<String> stopwords)
	{
		String number = "0123456789";
		String punctuation = " .,;:-+_-'!?()[]\"\\//^*�%&�#@$<>=|~`{}\n\t\b\r";
		BufferedReader buff = new BufferedReader(new StringReader(str));
		String line;
		List<String> words=new ArrayList<String>();
		try {
			while ((line = buff.readLine()) != null) {
				StringTokenizer stream= new StringTokenizer(line, punctuation + number);
				while (stream.hasMoreTokens()) {
					String par=stream.nextToken().toLowerCase();
					if (!stopwords.contains(par))
						words.add(par);
				}
			}
			buff.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return words;
	}
	
}
