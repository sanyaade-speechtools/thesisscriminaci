/*
 * Created on 2-mar-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.aitools.programd.server.core.lsa.util;

import jmp.BLAS;
import jmp.DenseVector;
import jmp.SequentialBLAS;
import org.aitools.programd.server.core.lsa.spazioParolaParola.Parola;
import java.text.DecimalFormat;
import java.util.Arrays;


/**
 * Contiene metodi per eseguire calcoli su vettori double[]
 * 
 * @author Augello Agnese Vasile Maria
 */
public class OperazioneArray {
	private static BLAS b;

	/**
	 * Somma gli elementi di un vettore
	 * 
	 * @param vet
	 * @return somma
	 */
	public static double sum(double[] vet) {
		double somma = 0;
		for (int i = 0; i < vet.length; i++) {
			somma = somma + vet[i];
		}
		return (somma);
	}
	/**
	 * Calcola la norma due di un vettore
	 * 
	 * @param vet
	 * @return norma
	 */
	public static double norma2(double[] vet) {
		double norma = 0.0;
		for (int i = 0; i < vet.length; i++) {
			norma = norma + vet[i] * vet[i];
		}
		return Math.sqrt(norma);
	}
	
	public static void normalizza(double[] vet) {
		
		double norma=norma2(vet);
		for (int i = 0; i < vet.length; i++) {
			vet[i]/=norma;
		}
	}
	
	/**
	 * Calcola la norma due di un vettore
	 * 
	 * @param vet
	 * @return norma
	 */
	public static double norma2double(double[] vet) {
		double norma = 0.0;
		for (int i = 0; i < vet.length; i++) {
			norma = norma + vet[i] * vet[i];
		}
		return (Math.sqrt(norma));
	}
	/**
	 * Calcola il valore massimo di un vettore
	 * 
	 * @param vet
	 * @return massimo
	 */
	public static double max(double[] vet) {
		double massimo = 0.0;
		double temp = vet[0];
		for (int i = 0; i < vet.length; i++) {
			if (vet[i] > temp) {
				temp = vet[i];
			}
		}
		massimo = temp;
		return (massimo);
	}
	/**
	 * Calcola il valore massimo di un vettore
	 * 
	 * @param vet
	 * @return massimo
	 */
	public static double max2(Object[] vet) {
		double massimo = 0.0;
		Double temporaneo = (Double) vet[0];
		double temp = temporaneo.doubleValue();
		for (int i = 0; i < vet.length; i++) {
			Double temporaneosucc = (Double) vet[i];
			double succ = temporaneosucc.doubleValue();
			if (succ > temp) {
				temp = succ;
			}
			massimo = temp;
		}
		return (massimo);
	}
	/**
	 * Restituisce l'indice dell'elemento massimo del vettore
	 * 
	 * @param vet
	 * @return indexMax
	 */
	public static int indice_max_value(double[] vet) {
		int indexMax = 0;
		double temp = vet[0];
		for (int i = 0; i < vet.length; i++) {
			if (vet[i] > temp) {
				temp = vet[i];
				indexMax = i;
			}
		}
		return (indexMax);
	}
	/**
	 * Calcola il minimo di un vettore
	 * 
	 * @param vet
	 * @return min
	 */
	public static double min(double[] vet) {
		double min = 0.0;
		double temp = vet[0];
		for (int i = 0; i < vet.length; i++) {
			if (vet[i] < temp) {
				temp = vet[i];
			}
		}
		min = temp;
		return (min);
	}
	/**
	 * Calcola il numero di elementi non nulli di un vettore
	 * 
	 * @param vet
	 * @return count: numero di leementi diversi da zero
	 */
	public static int elem_non_nulli(double[] vet) {
		int count = 0;
		for (int i = 0; i < vet.length; i++) {
			if (vet[i] != 0) {
				count++;
			}
		}
		return (count);
	}
	/**
	 * Esegue il prodotto interno di due vettori
	 * 
	 * @param vet1
	 * @param vet2
	 * @return v: prodotto
	 */
	public static double prodotto(double[] vet1, double[] vet2) {
		double v = 0;
		for (int i = 0; i < vet1.length; i++) {
			v = v + (vet1[i] * vet2[i]);
		}
		DecimalFormat df = new DecimalFormat(); 
		df.setMaximumFractionDigits(2); 
		String result = df.format(v); 
//		siccome il metodo format usa la virgola come separatore 
		result = result.replace(',','.'); 
		v = Double.parseDouble(result);
		return (v);
	}
	/**
	 * Stampa a video gli elementi di un vettore
	 * 
	 * @param vet
	 */
	public static void visualizza(double[] vet) {
		for (int i = 0; i < vet.length; i++) {
			System.out.println("vet" + i + vet[i]);
		}
	}
	public static DenseVector converti_jmpvector(float[] vector) {
		double[] vet = new double[vector.length];
		for (int i = 0; i < vector.length; i++)
			vet[i] = vector[i];
		DenseVector vettore = new DenseVector(vet.length);
		b = new SequentialBLAS();
		vettore = (DenseVector) b.setArray(vet, vettore);
		return vettore;
	}
	public static double[] moltiplica_vettore_per_fattore(double[] v1, double f) {
		for (int i = 0; i < v1.length; i++)
			v1[i] = v1[i] * f;
		return v1;
	}
	public static double[] somma_vettori(double[] v1, double[] v2) {
		// System.out.println("dim vet1 "+v1.size()+" dim vet2 "+v2.size());
		// System.out.println("sto iniziando a sommare");
		if (v1.length == v2.length) {
			// System.out.println("i vettori hanno la stessa dim");
			for (int i = 0; i < v1.length; i++) {
				// System.out.println("primo vett "+v1.getValue(i)+"secondo vett
				// "+v2.getValue(i));
				double s = v1[i] + v2[i];
				v1[i] = s;
				// System.out.println("La somma è "+v1.getValue(i));
			}
		}
		// System.out.println("ho finito di sommare");
		return v1;
	}
	public static float prodotto_scalare(DenseVector v1, DenseVector v2) {
		// System.out.println("il primo elemento della query � "+
		// v1.getValue(0)+" e del doc "+ v2.getValue(0));
		b = new SequentialBLAS();
		float prodotto = (float) b.dot(v1, v2);
		return prodotto;
	}
	// esegue il prodotto del contesto sinistro di un vettore con il contesto
	// sinistro
	// dell'altro vettore e fa il quadrato del risultato,
	// analogamente per il contesto destro, infine somma i due risultati
	// ottenuti
	public static double cosenquadro(double[] vet1, double[] vet2, int sigma) {
		double cos1 = 0;
		double cos2 = 0;
		for (int i = 0; i < sigma; i++) {
			cos1 = cos1 + (vet1[i] * vet2[i]);
		}
		cos1 = cos1 * cos1;
		for (int j = sigma; j < vet1.length; j++) {
			cos2 = cos2 + (vet1[j] * vet2[j]);
		}
		cos2 = cos2 * cos2;
		return (cos1 + cos2);

	}
	
	public static double cosenquadro(Parola vet1, Parola vet2) {
		double cos1 = 0;
		double cos2 = 0;
		double[] s1 = vet1.getCtx_sinistro();
		double[] s2 = vet2.getCtx_sinistro();
		double[] d1 = vet1.getCtx_destro();
		double[] d2 = vet2.getCtx_destro();
		for (int i = 0; i < s1.length; i++) {
			cos1 = cos1 + (s1[i] * s2[i]);
		}
		cos1 = cos1 * cos1;
		for (int j = 0; j < d1.length; j++) {
			cos2 = cos2 + (d1[j] * d2[j]);
		}
		cos2 = cos2 * cos2;
		return (cos1 + cos2);

	}
	
	//modified by Emilio Sabatucci
	public static double scalar(double[] vet1, double[] vet2){
		
		double v = 0;
		for (int i = 0; i < vet1.length; i++) {
			v = v + (vet1[i] * vet2[i]);
		}
		if(v<0)		
			return 0.0;
		else if(v == Double.NaN){
			return 0.0;
		}else{
			DecimalFormat df = new DecimalFormat(); 
			df.setMaximumFractionDigits(2); 
			String result = df.format(v*v); 
//			siccome il metodo format usa la virgola come separatore 
			result = result.replace(',','.'); 
			v = Double.parseDouble(result);
			return (v);
		}			
	}

}
