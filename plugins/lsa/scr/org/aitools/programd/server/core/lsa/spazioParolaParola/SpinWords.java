/**
 * @auth = Giorgio Vassallo SpinWords 4.1
 * modified da agnese
 * usage: - r -w occorrenze.txt -d digrammiNOorder.txt
 * r indica se consideare l'ordine delle parole nel digramma
 * Se � false la matrice viene simmetrica
 */
package org.aitools.programd.server.core.lsa.spazioParolaParola;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import jmp.SparseRowMatrix;
import jmp.io.CoordinateWriter;
import jmp.io.MatrixMarketWriter;
import org.aitools.programd.server.core.lsa.util.TextUtil;

public class SpinWords {
	private int[] wfreq;
	private SortedMap<String, Integer> wordsID;
	private String[] IDwords;
	private int nwords, tnwords;
	private SparseRowMatrix sparsetargetmat;
	public static boolean order = false;
	// constructor
	public SpinWords(String freqFile, String digFile) {
		wordsID = new TreeMap<String, Integer>();
		nwords = TextUtil.countLines(freqFile);
		IDwords = new String[nwords];
		wfreq = new int[nwords];
		tnwords = 0;
		TextUtil.loadFreq(freqFile, wordsID, IDwords, wfreq);
		for (int i = 0; i < nwords; i++) {
			tnwords += wfreq[i];
		}
		sparsetargetmat = createMatrixDigrams(digFile, wordsID, wfreq);
	}
	public void writemat(String fname) {
		MatrixMarketWriter MMW;
		try {
			MMW = new MatrixMarketWriter(new FileWriter(fname));
			MMW.writeMatrix(sparsetargetmat);
			MMW.close();
		} catch (IOException ioe) {
			System.err.println(ioe);
		}
	}
	
public void writemat2(String fname) {
		CoordinateWriter MMW;
		try {
			MMW = new CoordinateWriter(new FileWriter(fname));
			MMW.writeMatrix(sparsetargetmat);
			MMW.close();
		} catch (IOException ioe) {
			System.err.println(ioe);
		}
	}
	public SparseRowMatrix createMatrixDigrams(String fname,
			SortedMap<String, Integer> wordsID, int[] wfreq) {
		SortedMap<SparseIndex, Integer> mat = TextUtil.loadDigrams(fname,
				wordsID);
		SparseRowMatrix sparsemat = new SparseRowMatrix(nwords, nwords);
		for (Map.Entry<SparseIndex, Integer> entry : mat.entrySet()) {
			int i = entry.getKey().si;
			int j = entry.getKey().sj;
			if (i != j) {
				sparsemat.setValue(i, j, Math.sqrt(entry.getValue()));
				//sparsemat.setValue(i, j, entry.getValue());
				if (order == false)
					sparsemat.setValue(j, i, Math.sqrt(entry.getValue()));
				//sparsemat.setValue(j, i, entry.getValue());
			}
		}
		return sparsemat;
	}
	public static double imply(double fA, double fB, double fAB) {
		// return Math.max(fA, fB);
		return fAB;
	}
}
