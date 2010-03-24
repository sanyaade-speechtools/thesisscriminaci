package org.aitools.programd.server.core.cyc.util;

import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;

/**
 * Questa classe � una rappresentazione del grafo di una relazione Cyc;
 * � formata da una matrice quadrata di dimensioni (sizeXsize) e dal nome
 * della relazione. 
 */
public class LinkMatrix {
	
	private String relation;
	
	private int size;
	
	private FlexCompRowMatrix matrix;
	
	/**
	 * @param String relation il nome della relazione
	 * @param int size la dimensione della matrice (ossia il numero di 
	 * costanti rilevato)
	 */
	public LinkMatrix(String relation, int size)
	{
		this.setRelation(relation);
		
		this.size=size;
				
		this.matrix=new FlexCompRowMatrix(size,size);
	}
	
	/**
	 * Costruttore privato per uso interno alla classe,
	 * fa una copia della LinkMatrix
	 * 
	 */
	public LinkMatrix(LinkMatrix c)
	{
		this.matrix=new FlexCompRowMatrix(c.matrix);
		
		this.size=c.size;
		this.setRelation(c.getRelation());
	}
	/**
	 * Questa funzione eleva a n la matrice e la restituisce in uscita;
	 * bisogna fare attenzione, il metodo non modifica la LinkMatrix!
	 * 
	 * @param int n l'esponente pu� assumere solo valori interi positivi 
	 * @return
	 */
	public LinkMatrix elevate(int n)
	{
		if(n<1)return null;
		else
		{
			LinkMatrix a=new LinkMatrix(this);
				
			for (int i=1;i<n;i++)
			{
				a=this.mult(this,a);
			}		
			return a;
		}		
	}
	
	/**
	 * Somma la matrice in ingresso all'oggetto. Fare attenzione a sommare matrici
	 * con le stesse dimensioni, altrimenti non verr� effettuata la somma.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public LinkMatrix add(LinkMatrix a)
	{
		if(a.getSize()==this.size)
			matrix.add(a.matrix);
		return this;	
	}
	
	/**
	 * Imposta a value il valore dell'elemento della riga m e colonna n
	 * 
	 * @param int m numero di riga
	 * @param int n numero di colonna
	 * @param int value vaolre dell'elemento
	 */	
	public void setElement(int m, int n,int value)
	{
		matrix.set(m,n,value);
	}
	
	/**
	 * ritorna il valore dell'elemento (m,n) dove m � il numero di riga
	 * e n il numero di colonna
	 * 
	 * @param int m numero di riga
	 * @param int n numero di colonna
	 * @return
	 */
	public int getElement(int m, int n)
	{
		return (int)matrix.get(m,n);
	}
	
	/**
	 * Setta il nome della relazione
	 * 
	 * @param String relation il nome della realzione
	 */
	public void setRelation(String relation)
	{
		this.relation=relation;
	}
	
	/**
	 * @return String relation il nome della realzione
	 */
	public String getRelation()
	{
		return relation;
	}
	
	/**
	 * @return int size la dimensione della matrice quadrata
	 */
	public int getSize()
	{
		return this.size;
	}
	
	/**
	 * Il metodo ritorna la matrice della relazione
	 * 
	 * @return CompRowMatrix la matrice sparsa
	 */
	public FlexCompRowMatrix getMatrix()
	{
		return this.matrix;
	}
	
	
	/**
	 * Il metodo ritorna una sotto matrice (mXn) fino alla riga e la
	 * colonna voluta
	 * 
	 * @param m il numero di riga
	 * @param n il numero di colonna
	 * @return CompRowMatrix la sotto matrice voluta
	 */
	public FlexCompRowMatrix getMatrix(int m, int n){
		if(m<matrix.numRows()&&n<matrix.numColumns()){
			FlexCompRowMatrix hold=new FlexCompRowMatrix(m,n);
			for(int i=0;i<m;i++){
				for(int j=0;j<n;j++){
					hold.set(i,j,matrix.get(i,j));
				}				
			}
			return hold;			
		}
		else
			return null;		
	}
	
	/**
	 * Verifica se la matrice � simmetrica
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public boolean isSymmetric()
	{
		boolean symmetry=true;
		
		for(int i=0;i<this.size;i++)
		{
			for(int j=0;j<this.size;j++)
			{
				if(this.matrix.get(i,j)!=this.matrix.get(j,i))
					symmetry=false;
			}
		}
		
		return symmetry;
		
	}
	
	@Override
	public String toString()
	{
		String out="%"+this.relation+"%\n";
		out+=this.matrix.toString();
		
		return out;
		
	}
	
	
	/**
	 * Metodo che fa da wrapper al metodo mult della classe DenseMatrix
	 * @param a
	 * @param b
	 * @return
	 */
	public LinkMatrix mult(LinkMatrix a, LinkMatrix b)
	{
		LinkMatrix c=new LinkMatrix(a.getRelation(),a.getSize());
		
		a.matrix.mult(b.matrix,c.matrix);
		
		return c;
		
	}
}
