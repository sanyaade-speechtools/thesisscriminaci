package org.aitools.programd.server.core.lsa.util;

import java.io.Serializable;

public class Classe implements Serializable {
	private static final long serialVersionUID = -6588124036667353598L;
	private String nome;

	public Classe(String nome) {
		super();
		// TODO Auto-generated constructor stub
		this.nome = nome;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.nome;
	}
	/**
	 * @see java.lang.Object#equals(Object)
	 */
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Classe)) {
			return false;
		}
		Classe rhs = (Classe) object;
		return rhs.nome.equals(this.nome);
	}
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return this.nome.hashCode();
	}
	
	
}
