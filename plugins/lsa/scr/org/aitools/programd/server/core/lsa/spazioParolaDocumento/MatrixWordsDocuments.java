// WDfreq 0.1
package org.aitools.programd.server.core.lsa.spazioParolaDocumento;

import gnu.getopt.Getopt;

import java.io.FileNotFoundException;

import org.aitools.programd.server.core.lsa.util.DatabasePD;


/**
 * 
 * Per aumentare l'heap : -Xms512M -Xmx1024M
 * 
 *
 */
public class MatrixWordsDocuments {

	public static void main(String[] args) throws FileNotFoundException {
		Getopt g = new Getopt("MatrixWordsDocuments", args, "i:");
		int c;
		String infile = null;
		while ((c = g.getopt()) != -1) {
			switch (c) {
			case 'i':
				infile = g.getOptarg();
				break;
			}
		}
		if (infile == null) {
			System.err
					.println("usage: MatrixWordsDocuments -i <input file>");
			System.exit(-1);
		}
		DatabasePD databasePD = new DatabasePD("jdbc:postgresql://localhost:5432/", "lsa", "chatbot",
				"chatbot", "org.postgresql.Driver");
		FreqParole freq=new FreqParole(databasePD, "stopword.txt");
		freq.createMatrixFromFolder("pd.mm",infile);
		freq.saveParoleDocumenti("paroleDocumenti.dat");
		System.err.println("done.");
	}
}