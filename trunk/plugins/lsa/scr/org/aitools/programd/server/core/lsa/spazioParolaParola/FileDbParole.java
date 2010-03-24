/*
 * Created on 28-feb-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.aitools.programd.server.core.lsa.spazioParolaParola;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.aitools.programd.server.core.lsa.util.MMmanager;
import org.aitools.programd.server.core.lsa.util.OperazioneArray;
import org.aitools.programd.server.core.lsa.util.TextUtil;

/**
 * @author Agnese
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class FileDbParole {
	private Map<String, Parola> spazio = null;
	public static FileDbParole database = new FileDbParole();

	private FileDbParole() {
		try {
			ObjectInputStream o = new ObjectInputStream(
					new BufferedInputStream(new FileInputStream("parole.dat")));
			Object ogg = o.readObject();
			if (ogg instanceof SortedMap) {
				spazio = (SortedMap<String, Parola>) ogg;
			}
			o.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public Parola getParola(String word) {
		return this.spazio.get(word);
	}
	public static void memorizzaFile() {
		Map<String, Parola> spazio = new HashMap<String, Parola>();
		List<String> parole = TextUtil.getParole("occorrenze.txt");
		float[][] U = MMmanager.loadDMM("U.mm");
		float[][] V = MMmanager.loadDMM("V.mm");
		float[] S = MMmanager.loadS("S.mm");
		
		
		int nsigma = U[0].length;
		System.out.println(nsigma);
		for (int index = 0; index < parole.size(); index++) {
			String word = parole.get(index);
			System.out.println(index);
			// System.out.println(word);
			double[] vettore_sinistro = new double[nsigma];
			double[] vettore_destro = new double[nsigma];
			for (int j = 0; j < nsigma; j++) {
				vettore_destro[j] = U[index][j] * Math.sqrt(S[j]);
				vettore_sinistro[j] = V[index][j] * Math.sqrt(S[j]);
			}
			double modulo_vdestro = 1 / OperazioneArray.norma2(vettore_destro);
			double modulo_vsinistro = 1
					/ OperazioneArray.norma2(vettore_sinistro);
			vettore_destro = OperazioneArray.moltiplica_vettore_per_fattore(
					vettore_destro, modulo_vdestro);
			vettore_sinistro = OperazioneArray.moltiplica_vettore_per_fattore(
					vettore_sinistro, modulo_vsinistro);
			spazio.put(word, new Parola(vettore_sinistro, vettore_destro));
		}
		try {
			ObjectOutputStream o = new ObjectOutputStream(
					new BufferedOutputStream(new FileOutputStream("parole.dat")));
			o.writeObject(spazio);
			o.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("fine memorizzazione nel db");
	}
	public static void main(String[] args) {
	}
}
