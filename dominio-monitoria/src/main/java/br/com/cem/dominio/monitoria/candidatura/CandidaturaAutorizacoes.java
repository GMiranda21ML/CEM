package br.com.cem.dominio.monitoria.candidatura;

import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoAutorizacoes;

public final class CandidaturaAutorizacoes extends AgregadoAutorizacoes<Candidatura> {
	private final boolean candidatar;
	private final boolean cancelar;
	private final boolean registrarEntrevista;
	private final boolean aprovar;
	private final boolean reprovar;

	public CandidaturaAutorizacoes(
			boolean incluir,
			boolean buscar,
			boolean alterar,
			boolean excluir,
			boolean candidatar,
			boolean cancelar,
			boolean registrarEntrevista,
			boolean aprovar,
			boolean reprovar) {
		super(incluir, buscar, alterar, excluir);
		this.candidatar = candidatar;
		this.cancelar = cancelar;
		this.registrarEntrevista = registrarEntrevista;
		this.aprovar = aprovar;
		this.reprovar = reprovar;
	}

	public boolean getCandidatar() {
		return candidatar;
	}

	public boolean getCancelar() {
		return cancelar;
	}

	public boolean getRegistrarEntrevista() {
		return registrarEntrevista;
	}

	public boolean getAprovar() {
		return aprovar;
	}

	public boolean getReprovar() {
		return reprovar;
	}
}
