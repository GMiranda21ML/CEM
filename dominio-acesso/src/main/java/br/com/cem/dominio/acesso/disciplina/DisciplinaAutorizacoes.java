package br.com.cem.dominio.acesso.disciplina;

import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoAutorizacoes;

public final class DisciplinaAutorizacoes extends AgregadoAutorizacoes<Disciplina> {
	private final boolean cadastrar;

	public DisciplinaAutorizacoes(boolean incluir, boolean buscar, boolean alterar, boolean excluir,
			boolean cadastrar) {
		super(incluir, buscar, alterar, excluir);
		this.cadastrar = cadastrar;
	}

	public boolean getCadastrar() {
		return cadastrar;
	}
}
