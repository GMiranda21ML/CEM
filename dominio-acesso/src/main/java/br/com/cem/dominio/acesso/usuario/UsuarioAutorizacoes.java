package br.com.cem.dominio.acesso.usuario;

import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoAutorizacoes;

public final class UsuarioAutorizacoes extends AgregadoAutorizacoes<Usuario> {
	private final boolean inativar;
	private final boolean reativar;

	public UsuarioAutorizacoes(boolean incluir, boolean buscar, boolean alterar, boolean excluir, boolean inativar,
			boolean reativar) {
		super(incluir, buscar, alterar, excluir);

		this.inativar = inativar;
		this.reativar = reativar;
	}

	public boolean getInativar() {
		return inativar;
	}

	public boolean getReativar() {
		return reativar;
	}
}
