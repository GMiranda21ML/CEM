package br.com.cem.dominio.monitoria.sessao;

import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoAutorizacoes;

public final class SessaoAutorizacoes extends AgregadoAutorizacoes<Sessao> {
	private final boolean cancelar;

	public SessaoAutorizacoes(
			boolean incluir, boolean buscar, boolean alterar, boolean excluir, boolean cancelar) {
		super(incluir, buscar, alterar, excluir);
		this.cancelar = cancelar;
	}

	public boolean getCancelar() {
		return cancelar;
	}
}
