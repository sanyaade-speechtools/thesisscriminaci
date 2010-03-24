package org.aitools.programd.server.core.lsa.spazioParolaDocumento;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.aitools.programd.server.core.lsa.util.DatabasePD;
import org.aitools.programd.server.core.lsa.util.OperazioneArray;

import com.aliasi.util.Files;


public class Documento implements Serializable {
	private static final long serialVersionUID = 6294411033135233505L;
	private int indice;
	private String classe;
	private String filename;
	transient private double[] coding;
	transient List<double[]> codingFrasi;
	transient Set<String> stopwords;
	transient int dim;
	
	public Documento(String filename,String classe,int indice) {
		super();
		this.classe = classe;
		this.filename = filename;
		this.indice=indice;
	}
	public Documento(String filename, Set<String> stopwords, int dim) {
		this.filename = filename;
		this.stopwords = stopwords;
		this.dim = dim;
	}
	public Documento(String filename,String classe,double[] coding) {
		super();
		this.classe = classe;
		this.coding = coding;
		this.filename = filename;
	}

	public String getClasse() {
		return classe;
	}
	public String getFilename() {
		return filename;
	}
	public double[] getCoding(DatabasePD databasePD) {
		if (coding == null)
			this.codifyDocument(databasePD);
		return coding;
	}
	public int getIndice() {
		return indice;
	}
	

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Documento)
		{
			Documento doc=(Documento) obj;
			return ((doc.classe.equalsIgnoreCase(this.classe)) &&
			(doc.filename.equalsIgnoreCase(this.filename)));
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return this.classe.hashCode()*17+this.filename.hashCode()*31;
	}
	

	/*
	 * mi serve per ottenere la codifica di una frase
	 */
	private double[] codifyFrase(DatabasePD databasePD, String testo) {
		double[] textcoded = new double[dim];
		
		StringTokenizer st = new StringTokenizer(testo);
		while (st.hasMoreTokens()) {
			String par = st.nextToken().toLowerCase();
			if (!this.stopwords.contains(par)) {
				double[] parola = databasePD.getCodificaParola(par);
				if (parola != null) {
					textcoded = OperazioneArray.somma_vettori(textcoded,
							parola);
					
				}
			}
		}
		double norma=OperazioneArray.norma2(textcoded);
		if (norma!=0)
			OperazioneArray.moltiplica_vettore_per_fattore(textcoded,(1/norma));	
		return textcoded;
	}
	private void codifyDocument(DatabasePD databasePD) {
		String text;
		try {
			text = Files.readFromFile(new File(this.filename));
			this.coding = this.codifyFrase(databasePD, text);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
