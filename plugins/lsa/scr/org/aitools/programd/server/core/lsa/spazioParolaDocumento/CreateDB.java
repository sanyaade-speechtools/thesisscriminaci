/*
 * Created on 28-feb-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.aitools.programd.server.core.lsa.spazioParolaDocumento;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import org.aitools.programd.server.core.lsa.util.DatabasePD;
import org.aitools.programd.server.core.lsa.util.MMmanager;
import org.aitools.programd.server.core.lsa.util.OperazioneArray;

/**
 * @author Agnese
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class CreateDB {
	
	@SuppressWarnings("unchecked")
	public static void create(String basePath, DatabasePD databasePD){
		List<String> parole = null;
		List<Documento> documenti = null;
		try {
			ObjectInputStream o = new ObjectInputStream(
					new BufferedInputStream(new FileInputStream(basePath+File.separator+"paroleDocumenti.dat")));
			Object ogg = o.readObject();
			if (ogg instanceof List<?>) {
				documenti = (List<Documento>) ogg;
			}
			ogg = o.readObject();
			if (ogg instanceof List<?>) {
				parole = (List<String>) ogg;
			}
			o.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		float[][] U, V;
		float[] S;
		U = MMmanager.loadDMM(basePath+File.separator+"U.mm");
		V = MMmanager.loadDMM(basePath+File.separator+"V.mm");
		S = MMmanager.loadS(basePath+File.separator+"S.mm");
		int nsigma = U[0].length;
		System.out.println(nsigma);
		for (int index = 0; index < parole.size(); index++) {
			String word = parole.get(index);
			double[] vettoreParola = new double[nsigma];
			for (int j = 0; j < nsigma; j++) {
				vettoreParola[j] = (U[index][j] * Math.sqrt(S[j]));
			}
			double moduloParola = 1
					/ OperazioneArray.norma2(vettoreParola);
			vettoreParola = OperazioneArray.moltiplica_vettore_per_fattore(
					vettoreParola, moduloParola);
			databasePD.insertCodificaParola(word, vettoreParola,
					moduloParola);
		}
		for (int index = 0; index < documenti.size(); index++) {
			Documento word = documenti.get(index);
			double[] vettoreDocumento = new double[nsigma];
			for (int j = 0; j < nsigma; j++) {
				vettoreDocumento[j] = (V[index][j] * Math.sqrt(S[j]));
			}
			double moduloDocumento = 1
					/ OperazioneArray.norma2(vettoreDocumento);
			vettoreDocumento = OperazioneArray.moltiplica_vettore_per_fattore(
					vettoreDocumento, moduloDocumento);
			databasePD.insertCodificaDocumento(word.getFilename(),
					word.getClasse(), vettoreDocumento);
		}
		System.out.println("fine memorizzazione nel db");
	}
}
