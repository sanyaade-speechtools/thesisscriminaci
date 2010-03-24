/*
 * Created on Jun 22, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.aitools.programd.server.core.lsa.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import jmp.DenseRowMatrix;
import jmp.Matrix;
import jmp.io.MatrixMarketReader;

/**
 * @author root
 * 
 */
public class MMmanager {
	public static float[] loadS(String fname) {
		String line, values[];
		BufferedReader BReader = null;
		int nrows = 0, ncols = 0, countlines = 0;
		float[] S = null;
		try {
			BReader = new BufferedReader(new FileReader(fname));
			line = BReader.readLine();
			// %%MatrixMarket matrix array real symmetric
			if ((line == null)
					|| (!line
							.matches("%%MatrixMarket matrix array real[\\s\\w]*"))) {
				System.err.println(fname + ": Bad format for dense MM file");
				System.err.println(line);
			}
			// ricorda il trim
			while ((line = BReader.readLine().trim()).matches("^%")) {
				System.err.println(line);
			}
			line.trim();
			values = line.split("\\s+");
			nrows = Integer.parseInt(values[0]);
			ncols = Integer.parseInt(values[1]);
			if (ncols > 1) {
				System.err.println("ncols > 1");
			}
			S = new float[nrows];
			countlines = 0;
			System.err.println(nrows + " " + ncols);
			while ((line = BReader.readLine()) != null) {
				line.trim();
				S[countlines] = Float.parseFloat(line);
				countlines++;
			}
			BReader.close();
		} catch (FileNotFoundException fne) {
			System.err.println(fname + " " + fne);
		} catch (IOException ioe) {
			System.err.println(fname + " " + ioe);
		}
		if (countlines != (nrows * ncols))
			return null;
		return S;
	}
	public static float[][] loadDMM(String fname) {
		String line, values[];
		BufferedReader BReader = null;
		int nrows = 0, ncols = 0, countlines = 0;
		int i = 0, j = 0;
		float[][] MM = null;
		try {
			BReader = new BufferedReader(new FileReader(fname));
			line = BReader.readLine();
			// %%MatrixMarket matrix array real symmetric
			if ((line == null)
					|| (!line
							.matches("%%MatrixMarket matrix array real[\\s\\w]*"))) {
				System.err.println(fname + ": Bad format for dense MM file");
				System.err.println(line);
			}
			while ((line = BReader.readLine()).matches("^%")) {
				System.err.println(line);
			}
			line.trim();
			values = line.split("\\s+");
			nrows = Integer.parseInt(values[0]);
			ncols = Integer.parseInt(values[1]);
			// nzero = Integer.parseInt(values[2]);
			MM = new float[nrows][ncols];
			countlines = 0;
			i = 0;
			j = 0;
			System.err.println(nrows + " " + ncols);
			while ((line = BReader.readLine()) != null) {
				line.trim();
				j = countlines / nrows;
				i = countlines % nrows;
				MM[i][j] = Float.parseFloat(line);
				countlines++;
				// System.out.println("i "+i+"j "+j+MM[i][j]);
			}
			BReader.close();
		} catch (FileNotFoundException fne) {
			System.err.println(fname + " " + fne);
		} catch (IOException ioe) {
			System.err.println(fname + " " + ioe);
		}
		if (countlines != (nrows * ncols))
			return null;
		return MM;
	}
	public static double[][] loadMM2(String fname) {
		MatrixMarketReader mmio;
		DenseRowMatrix M = null;
		Matrix mat;
		try {
			mmio = new MatrixMarketReader(new FileReader(fname));
			mat = mmio.readMatrix();
			M = new DenseRowMatrix(mat);
		} catch (FileNotFoundException fne) {
			System.err.println(fne);
		} catch (IOException ioe) {
			System.err.println(ioe);
		}
		return M.getData();
	}
	
	public static void writeMM(String fname, float[][] mat) {
		
		System.out.println("%%MatrixMarket matrix array real general");
		int i, j, nrows, ncols;
		ncols = mat[0].length;
		nrows = mat.length;
		System.out.println(nrows + " " + ncols);
		for (j = 0; j < ncols; j++)
			for (i = 0; i < nrows; i++)
				System.out.println(mat[i][j]);
	}
	
	public static void main(String[] args) {
		double[][] jmp;
		float[][] my;
		float[] sigma;
		jmp = MMmanager.loadMM2(args[0]);
		// carica una matrice letta in un file mm in una matrix
		my = MMmanager.loadDMM(args[0]);
		sigma = MMmanager.loadS("S.mm");
		System.err.println("starting");
		// vassallo
		for (int i = 0; i < jmp.length; i++)
			for (int j = 0; j < jmp[0].length; j++)
				System.err.println(jmp[i][j] + " " + (jmp[i][j] - my[i][j]));
		System.out.println("lunghezza totale " + jmp[0].length);
		for (int ns = 0; ns < sigma.length; ns++)
			System.out.println(sigma[ns]);
		/*
		 * for (int cont=0;cont<jmp[0].length;cont ++)
		 * System.out.println(jmp[cont][3]); for (int i = 0; i < jmp.length;
		 * i++) for (int j = 0; j < jmp[0].length; j++)
		 * System.err.println(jmp[i][j]);
		 */
	}
}