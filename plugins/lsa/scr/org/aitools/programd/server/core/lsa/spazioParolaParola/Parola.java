package org.aitools.programd.server.core.lsa.spazioParolaParola;

import java.io.Serializable;

public class Parola implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8343181954549307101L;
	public double[] ctx_destro;
	public double[] ctx_sinistro;

	public Parola(double[] ctx_sinistro, double[] ctx_destro) {
		super();
		// TODO Auto-generated constructor stub
		this.ctx_destro = ctx_destro;
		this.ctx_sinistro = ctx_sinistro;
	}
	public double[] getCtx_destro() {
		return ctx_destro;
	}
	public void setCtx_destro(double[] ctx_destro) {
		this.ctx_destro = ctx_destro;
	}
	public double[] getCtx_sinistro() {
		return ctx_sinistro;
	}
	public void setCtx_sinistro(double[] ctx_sinistro) {
		this.ctx_sinistro = ctx_sinistro;
	}
	@Override
	public String toString() {
		return ctx_sinistro + "\n" + ctx_destro;
	}
}
