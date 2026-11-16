package br.com.cem.dominio.acesso.curso;

import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoAutorizacoes;

public final class CursoAutorizacoes extends AgregadoAutorizacoes<Curso> {
	private final boolean cadastrar;

	public CursoAutorizacoes(boolean incluir, boolean buscar, boolean alterar, boolean excluir, boolean cadastrar) {
		super(incluir, buscar, alterar, excluir);
		this.cadastrar = cadastrar;
	}

	public boolean getCadastrar() {
		return cadastrar;
	}
}
