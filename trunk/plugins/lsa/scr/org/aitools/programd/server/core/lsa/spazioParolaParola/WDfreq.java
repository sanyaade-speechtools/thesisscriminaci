// WDfreq 0.1
package org.aitools.programd.server.core.lsa.spazioParolaParola;

import gnu.getopt.Getopt;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.SortedMap;

import org.aitools.programd.server.core.lsa.util.TextUtil;

/**
 * @author Prof. Vassallo 
 * Modified by Agnese
 * Lanciare il main
 * 1)per creare il file di occorrenze:
 *  -x -i${folder_prompt} -o occorrenze.txt
 * 2)per creare il file dei digrammi indicando la finetstra:
 *
 * -m 10 -r -d -i${folder_prompt} -o digrammi.txt
 * mettere r per tenere conto dell'ordine delle parole nei dig
 * 
 * Per aumentare l'heap : -Xms512M -Xmx1024M
 * 
 *
 */
public class WDfreq {
	static boolean dig = false;
	static boolean order = false;

	public static void main(String[] args) throws FileNotFoundException {
		int maxd = 2;
		Getopt g = new Getopt("WDfreq", args, "m:r::x::d::i:o:");
		int c;
		String infile = null;
		while ((c = g.getopt()) != -1) {
			switch (c) {
			case 'x':
				dig = false;
				break;
			case 'r':
				order = true;
				break;
			case 'd':
				dig = true;
				break;
			case 'i':
				infile = g.getOptarg();
				break;
			case 'm':
				maxd = Integer.parseInt(g.getOptarg());
				break;
			case 'o':
				String outfile = g.getOptarg();
				System.setOut(new PrintStream(outfile));
				break;
			}
		}
		if (infile == null) {
			System.err
					.println("usage: WDfreq [-m maxdistance][-d][-x] -i <input file> > <output file>");
			System.exit(-1);
		}
		if (!dig) {
			//modifica del codice di Giorgio, Sortedmap anzich� StoredMap
			SortedMap<String, Integer> wf = TextUtil.wordFreq(infile, "stopword.txt");
			TextUtil.printMap(wf, false);
		} else {
			SortedMap<String, Integer> df = TextUtil.digFreq(infile, maxd, order,  "stopword.txt");
			TextUtil.printMap(df, false);
		}
		System.err.println("done.");
	}
}