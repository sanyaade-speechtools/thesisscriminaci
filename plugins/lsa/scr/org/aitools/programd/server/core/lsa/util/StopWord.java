package org.aitools.programd.server.core.lsa.util;

import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.HashSet;
import java.util.Set;

/**
 * Contiene un metodo per creare una tabella contenente le stopword a partire da
 * un file di testo.
 * 
 * @author Agnese Augello, Maria Vasile
 */
public class StopWord {

	public static Set<String> createStopWordTable(String file)
			throws IOException {
		FileReader reader2;
		reader2 = new FileReader(file);
		StreamTokenizer stream = new StreamTokenizer(reader2);
		int token_type;
		Set<String> table_sw = new HashSet<String>();
		while ((token_type = stream.nextToken()) != (StreamTokenizer.TT_EOF)) {
			if (token_type == StreamTokenizer.TT_WORD) {
				String parola = stream.sval.toLowerCase();
				parola = parola.trim();
				table_sw.add(parola);
			}
		}
		return (table_sw);
	}
}
