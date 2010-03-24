/*
 * Created on 28-feb-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.aitools.programd.server.core.lsa.spazioParolaParola;

import java.io.File;
import java.util.List;

import org.aitools.programd.server.core.lsa.util.DatabasePP;
import org.aitools.programd.server.core.lsa.util.MMmanager;
import org.aitools.programd.server.core.lsa.util.OperazioneArray;
import org.aitools.programd.server.core.lsa.util.TextUtil;

/**
 * @author Agnese
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class DbParole {
	
	public static void process(DatabasePP databasePP, String wfreqFile, String matrixBasePath){
		List<String> parole = TextUtil.getParole(wfreqFile);
		float[][] U = MMmanager.loadDMM(matrixBasePath+File.separator+"U.mm");
		float[][] V = MMmanager.loadDMM(matrixBasePath+File.separator+"V.mm");
		float[] S = MMmanager.loadS(matrixBasePath+File.separator+"S.mm");
		int nsigma = U[0].length;
		for (int index = 0; index < parole.size(); index++) {
			String word = parole.get(index);
			// System.out.println(word);
			double[] vettore_sinistro = new double[nsigma];
			double[] vettore_destro = new double[nsigma];
			for (int j = 0; j < nsigma; j++) {
				vettore_destro[j] = U[index][j] * Math.sqrt(S[j]);
				vettore_sinistro[j] = V[index][j] * Math.sqrt(S[j]);
			}
			double modulo_vdestro =  1 / OperazioneArray.norma2(vettore_destro);
			double modulo_vsinistro =  1
					/ OperazioneArray.norma2(vettore_sinistro);
			vettore_destro = OperazioneArray.moltiplica_vettore_per_fattore(
					vettore_destro, modulo_vdestro);
			vettore_sinistro = OperazioneArray.moltiplica_vettore_per_fattore(
					vettore_sinistro, modulo_vsinistro);
			databasePP.insertCodificaParola(word, vettore_destro,
					vettore_sinistro, modulo_vdestro, modulo_vsinistro);
		}
		System.out.println("fine memorizzazione nel db");
	}
	
}
