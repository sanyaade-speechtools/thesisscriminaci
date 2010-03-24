/*
 * Created on 3-mar-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.aitools.programd.server.core.lsa.spazioParolaParola;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.aitools.programd.server.core.lsa.util.DatabasePP;
import org.aitools.programd.server.core.lsa.util.StopWord;

/**
 * @author Agnese
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class DbDocumento {

	public static void process(DatabasePP databasePP, String directory, int dim, String stopwordPath){
		
		File dirBase = new File(directory);
		Set<String> stopwords = null;
		try {
			stopwords = StopWord.createStopWordTable(stopwordPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int classid = 0;
		System.out.println("Starting...");
		for (File dir : dirBase.listFiles()) {
			File[] files = dir.listFiles();
			for (File file : files) {
				Documento doc = new Documento(file.getAbsolutePath(), stopwords,dim);
				databasePP.insertCodificaDocumento(file.getName(),
						dir.getName(),doc.getCoding(databasePP));
			}
			classid++;
		}
	}
}
