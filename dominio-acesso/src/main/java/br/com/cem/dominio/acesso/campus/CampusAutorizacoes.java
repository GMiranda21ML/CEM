package br.com.cem.dominio.acesso.campus;

import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoAutorizacoes;

public final class CampusAutorizacoes extends AgregadoAutorizacoes<Campus> {
	private final boolean cadastrar;
	private final boolean inativar;
	private final boolean reativar;

	public CampusAutorizacoes(boolean incluir, boolean buscar, boolean alterar, boolean excluir, boolean cadastrar,
			boolean inativar, boolean reativar) {
		super(incluir, buscar, alterar, excluir);
		this.cadastrar = cadastrar;
		this.inativar = inativar;
		this.reativar = reativar;
	}

	public boolean getCadastrar() {
		return cadastrar;
	}

	public boolean getInativar() {
		return inativar;
	}

	public boolean getReativar() {
		return reativar;
	}
}
